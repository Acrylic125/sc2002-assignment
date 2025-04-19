package com.group6.tests;

public class BatchTestRunner {
    public static void main(String[] args) throws Exception {
        String[] testFiles = {
                "src/com/group6/tests/test1-input.txt",
                "src/com/group6/tests/test2-input.txt"
        };

        for (String inputFile : testFiles) {
            System.out.println("Running test: " + inputFile);
            UITestRunner.main(new String[] { inputFile });
            System.out.println("inished test: " + inputFile + "\n");
            System.out.println("==========================================================");
            System.out.println("==========================================================");
        }

        System.out.println("All tests done.");
    }
}