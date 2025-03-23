package com.group6.views;

import java.util.Objects;
import java.util.Scanner;

public class ExampleView implements View {

    @Override
    public void render(ViewContext ctx) {
        while (true) {
            final Scanner scanner = ctx.getScanner();
            System.out.println("Hello! Type something (Or '#' to return: ");
            String str = scanner.nextLine();
            if (Objects.equals(str, "#")) {
                ctx.popView();
                break;
            }
            System.out.println("You typed: " + str);
        }
    }

}
