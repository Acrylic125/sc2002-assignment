package com.group6.views;

import java.util.Objects;
import java.util.Scanner;

public class ExampleView implements View {

    @Override
    public View render(ViewContext ctx) {
        while (true) {
            final Scanner scanner = ctx.getScanner();
            System.out.println("Hello! Type something (Or '#' to return: ");
            String str = scanner.nextLine();
            if (Objects.equals(str, "#")) {
                System.out.println("Closing...");
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return null;
            }
            System.out.println("You typed: " + str);
        }
    }

}
