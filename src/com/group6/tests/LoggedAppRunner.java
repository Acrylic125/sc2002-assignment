package com.group6.tests;

import java.io.*;
import com.group6.Main;

public class LoggedAppRunner {
    public static void main(String[] args) throws Exception {
        System.out.print("Enter test number: ");
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        String testNum = consoleReader.readLine();

        // File to log everything typed
        BufferedWriter inputLogger = new BufferedWriter(
                new FileWriter("src/com/group6/tests/" + "test" + testNum + "-input.txt"));

        // Create a background thread that listens to user input and logs it
        PipedOutputStream inputToApp = new PipedOutputStream();
        PipedInputStream appInput = new PipedInputStream(inputToApp);
        System.setIn(appInput);

        Thread inputLoggerThread = new Thread(() -> {
            try {
                String line;
                while ((line = consoleReader.readLine()) != null) {
                    inputLogger.write(line + System.lineSeparator());
                    inputLogger.flush();

                    inputToApp.write((line + System.lineSeparator()).getBytes());
                    inputToApp.flush();
                }
            } catch (IOException e) {
                System.out.println("Logger error: " + e.getMessage());
            }
        });

        inputLoggerThread.start();

        // Run the actual application
        Main.main(new String[0]);

        inputLogger.close();
    }
}