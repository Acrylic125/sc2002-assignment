package com.group6.views;

import java.util.Optional;
import java.util.Scanner;

public interface PaginatedView extends View {
    int getLastPage();

    int getPage();

    void setPage(int page);

    default Optional<Integer> requestPage(Scanner scanner) {
        while (true) {
            System.out.println("You are currently on page " + getPage() + " / " + getLastPage());
            System.out.println("Type the page number you want to go to or empty ('') to go back:");
            String pageStr = scanner.nextLine().trim();
            if (pageStr.isEmpty()) return Optional.empty();
            try {
                int page = Integer.parseInt(pageStr);
                if (page >= 1 && page <= getLastPage()) {
                    return Optional.of(page);
                } else {
                    if (getLastPage() <= 0) {
                        System.out.println("There is only page 0 (i.e. no page).");
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                        return Optional.empty();
                    }
                    System.out.println("Invalid page number, page must be between 1 and "+ getLastPage() + " (inclusive).");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number, page must be an integer!");
                System.out.println("Type anything to continue.");
                scanner.nextLine();
            }
        }
    }

    default boolean page(int page) {
        if (page >= 1 && page <= getLastPage()) {
            setPage(page);
            return true;
        }
        return false;
    }

    default boolean nextPage() {
        int page = getPage();
        if (page < getLastPage()) {
            setPage(page + 1);
            return true;
        }
        return false;
    }

    default boolean prevPage() {
        int page = getPage();
        if (page > 1) {
            setPage(page - 1);
            return true;
        }
        return false;
    }
}
