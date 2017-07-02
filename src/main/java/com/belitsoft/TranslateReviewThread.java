package com.belitsoft;

public class TranslateReviewThread implements Runnable {
    private String inputLang;
    private String outputLang;
    private String text;

    public TranslateReviewThread(String inputLang, String outputLang, String text) {
        this.inputLang = inputLang;
        this.outputLang = outputLang;
        this.text = text;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Start to translate review №" + text);
        translateText(inputLang, outputLang, text);
        System.out.println(Thread.currentThread().getName() + " End.");
    }

    private String translateText(String inputLang, String outputLang, String text) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
