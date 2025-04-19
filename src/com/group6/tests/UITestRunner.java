package com.group6.tests;

import java.io.FileInputStream;
import com.group6.Main; // or BTOSystem

public class UITestRunner {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Missing input file path.");
            return;
        }

        FileInputStream testInput = new FileInputStream(args[0]);
        System.setIn(testInput);

        try {
            // Launch your CLI program
            Main.main(new String[0]); // or BTOSystem.start()
        } catch (java.util.NoSuchElementException e) {
            System.out.println("ERR : Input stream ended. Test may have finished early.");
        }
    }
}