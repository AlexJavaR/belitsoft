package com.belitsoft;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AmazonApplication {

    public static void main(String[] args) {
        String fileName = "Reviews.csv";
        List<Review> reviewsList = getArrayFromCSV(fileName, false);
        Set<Review> reviewSet;
//        for (Review r : reviewsList) {
//            System.out.println(r.toString());
//        }

        if (reviewsList != null) {
            System.out.println(reviewsList.size());
            reviewSet = new HashSet<>(reviewsList);
            System.out.println(reviewSet.size());

            //Finding 1000 most commented food items (item ids).
            Map<String, Integer> countProducts = reviewSet.stream().collect(Collectors.groupingBy(Review::getProductId, Collectors.summingInt(Review::getCount)));
            Map<String, Integer> countProductsSorted = getSortedMap(countProducts);
            System.out.println("1000 most commented food items:");
            printMap(countProductsSorted);

            //Finding 1000 most active users (profile names)
            Map<String, Integer> countUsers = reviewSet.stream().collect(Collectors.groupingBy(Review::getUserId, Collectors.summingInt(Review::getCount)));
            Map<String, Integer> countUsersSorted = getSortedMap(countUsers);

            System.out.println("-----------------------------------------------------");
            System.out.println("1000 most active users:");
            printMap(countUsersSorted);
        }

        //Finding 1000 most used words in the reviews
        reviewsList = getArrayFromCSV(fileName, true);
        if (reviewsList != null) {
            //reviewSet = new HashSet<>(reviewsList);
            Map<String, Integer> allWords = new HashMap<>();
            for (Review r : reviewsList) {
                String[] split = r.getText().split("\\p{P}?[ \\t\\n\\r]+");
                for (String str : split) {
                    if (str.matches("[a-zA-Z]+")) {
                        str = str.toLowerCase();
                        allWords.put(str, allWords.getOrDefault(str, 0) + 1);
                    }
                }
            }

            Map<String, Integer> allWordsSorted = getSortedMap(allWords);
            System.out.println("-----------------------------------------------------");
            System.out.println("1000 most used words in the reviews:");
            printMap(allWordsSorted);

//            List<String> allTexts = reviewSet.stream().map(Review::getText).sorted(Comparator.comparing(String::length)).collect(Collectors.toList());
//            for (String str : allTexts) {
//                System.out.println(str.length());
//            }
        }
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

    private static List<Review> getArrayFromCSV(String fileName, boolean withText) {
        List<Review> reviewsList = new ArrayList<>();
        CSVParser parser = null;
        Review review;
        try {
            parser = new CSVParser(new FileReader(fileName), CSVFormat.EXCEL.withHeader());
            for (CSVRecord record : parser) {
                if (withText) {
                    review = new Review(record.get("Id"), record.get("Text"));
                } else {
                    review = new Review(record.get("Id"), record.get("ProductId"), record.get("UserId"));
                }
                reviewsList.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //closing the parser
                parser.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reviewsList;
    }
}
