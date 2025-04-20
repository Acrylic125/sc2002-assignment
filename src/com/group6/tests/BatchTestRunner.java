package com.group6.tests;

public class BatchTestRunner {
    public static void main(String[] args) throws Exception {
        String[] testFiles = {
                "src/com/group6/tests/test1-input.txt",
                "src/com/group6/tests/test2-input.txt",
                "src/com/group6/tests/test3-input.txt",
                "src/com/group6/tests/test4-input.txt",
                "src/com/group6/tests/test5-input.txt",
                "src/com/group6/tests/test6-input.txt",
                "src/com/group6/tests/test7-input.txt",
                "src/com/group6/tests/test8-input.txt",
                "src/com/group6/tests/test9-input.txt",
                "src/com/group6/tests/test10-input.txt",
                "src/com/group6/tests/test11-input.txt",
                "src/com/group6/tests/test12-input.txt",
                "src/com/group6/tests/test13-input.txt",
                "src/com/group6/tests/test14-input.txt",
                "src/com/group6/tests/test15-input.txt",
                "src/com/group6/tests/test16-input.txt",
                "src/com/group6/tests/test17-input.txt",
                "src/com/group6/tests/test18-input.txt",
                "src/com/group6/tests/test19-input.txt",
                "src/com/group6/tests/test20-input.txt",
                "src/com/group6/tests/test21-input.txt",
                "src/com/group6/tests/test22-input.txt",
                "src/com/group6/tests/test23-input.txt",
                "src/com/group6/tests/test24-input.txt",
                "src/com/group6/tests/test25-input.txt",
                "src/com/group6/tests/test26-input.txt",
                "src/com/group6/tests/test27-input.txt",
                "src/com/group6/tests/test28-input.txt",
                "src/com/group6/tests/test29-input.txt",
                "src/com/group6/tests/test30-input.txt",
                "src/com/group6/tests/test31-input.txt",
                "src/com/group6/tests/test32-input.txt",
                "src/com/group6/tests/test33-input.txt",
                "src/com/group6/tests/test34-input.txt",
                "src/com/group6/tests/test35-input.txt",
                "src/com/group6/tests/test36-input.txt",
                "src/com/group6/tests/test37-input.txt",
                "src/com/group6/tests/test38-input.txt",
                "src/com/group6/tests/test39-input.txt"
        };

        for (String inputFile : testFiles) {
            System.out.println("\nRunning test: " + inputFile);
            UITestRunner.main(new String[] { inputFile });
            System.out.println("\nFinished test: " + inputFile + "\n");
            System.out.println("==========================================================");
            System.out.println("========================================================== \n");
        }

        System.out.println(" \n All tests done. \n");
    }
}