package com.belitsoft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AmazonApplication {

    public static void main(String[] args) {
        String fileName = "Reviews.csv";
        Map<String, Integer> allWords = new HashMap<>();
        Map<String, Integer> countProducts = new HashMap<>();
        Map<String, Integer> countUsers = new HashMap<>();
        Set<String> onlyText = new HashSet<>();
        int countSkip = 1;
        List<Review> reviews = getArrayReviews(fileName, countSkip);
        while (reviews != null) {
            for (Review review : reviews) {
                //Finding 1000 most commented food items (item ids).
                countProducts.put(review.getProductId(), countProducts.getOrDefault(review.getProductId(), 0) + 1);

                //Finding 1000 most active users (profile names)
                countUsers.put(review.getUserId(), countUsers.getOrDefault(review.getUserId(), 0) + 1);

                //Finding 1000 most used words in the reviews
                String[] split = review.getText().split("\\p{P}?[ \\t\\n\\r]+");
                for (String str : split) {
                    if (str.matches("[a-zA-Z]+")) {
                        str = str.toLowerCase();
                        allWords.put(str, allWords.getOrDefault(str, 0) + 1);
                    }
                }
                onlyText.add(review.getText());
            }

            countSkip+=10000;
            reviews = getArrayReviews(fileName, countSkip);
        }

        Map<String, Integer> countProductsSorted = getSortedMap(countProducts);
        System.out.println("1000 most commented food items:");
        printMap(countProductsSorted);

        Map<String, Integer> countUsersSorted = getSortedMap(countUsers);
        System.out.println("-----------------------------------------------------");
        System.out.println("1000 most active users:");
        printMap(countUsersSorted);

        Map<String, Integer> allWordsSorted = getSortedMap(allWords);
        System.out.println("-----------------------------------------------------");
        System.out.println("1000 most used words in the reviews:");
        printMap(allWordsSorted);

        allWords = null;
        countProducts = null;
        countUsers = null;
        countProductsSorted = null;
        countUsersSorted = null;
        allWordsSorted = null;

        translateReviews(onlyText);
    }

    private static Map<String, Integer> getSortedMap(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private static void printMap(Map<String, Integer> map) {
        int count = 0;
        for (Map.Entry entry : map.entrySet()) {
            count++;
            System.out.println(entry.getKey() + " - " + entry.getValue());
            if (count >= 1000) break;
        }
    }

    private static List<Review> getArrayReviews(String fileName, int countSkip) {
        Path file = Paths.get(fileName);
        List<Review> reviewsList = new ArrayList<>();
        try {
            Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8);
            List<String> collect = lines.skip(countSkip).limit(10000).collect(Collectors.toList());
            if (collect.isEmpty()) return null;
            for (String str : collect) {
                String[] fields = str.split(",", 10);
                reviewsList.add(new Review(fields[0], fields[1], fields[2], fields[9]));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return reviewsList;
    }

    private static void translateReviews(Set<String> reviews) {
        ExecutorService service = Executors.newFixedThreadPool(100);
        AtomicInteger count = new AtomicInteger(0);

        for(String review : reviews)
        {
            count.incrementAndGet();
            Runnable translator = new TranslateReviewThread("en", "fr", count.toString());
            service.execute(translator);
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }
        System.out.println("Finished all threads");
    }
}
