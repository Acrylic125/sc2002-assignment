package com.group6.views;

import com.group6.utils.BashColors;

import java.util.Scanner;

/**
 * Represents a paginated view containing all the pagination utilities
 * within a paginated view.
 */
public interface PaginatedView extends View {
    /**
     * Returns the last page number.
     *
     * @return The last page number.
     */
    int getLastPage();

    /**
     * Returns the current page number.
     *
     * @return The current page number.
     */
    int getPage();

    /**
     * Sets the current page number.
     *
     * @param page The page number to set.
     */
    void setPage(int page);

    /**
     * Requests the user to input a page number.
     *
     * @param scanner The scanner to read user input.
     * @return True if the page was set successfully, false otherwise.
     */
    default boolean requestPage(Scanner scanner) {
        while (true) {
            System.out.println(BashColors.format("You are currently on page " + getPage() + " / " + getLastPage(),
                    BashColors.BOLD));
            System.out.println("Type the page number you want to go to or empty ('') to go back:");
            String pageStr = scanner.nextLine().trim();
            if (pageStr.isEmpty())
                return false;
            try {
                int page = Integer.parseInt(pageStr);
                if (page >= 1 && page <= getLastPage()) {
                    if (!this.page(page)) {
                        System.out.println(BashColors.format("Invalid page number.", BashColors.RED));
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                        return false;
                    }
                    return true;
                } else {
                    if (getLastPage() <= 0) {
                        System.out.println(BashColors.format("There is only page 0 (i.e. no page).", BashColors.RED));
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                        return false;
                    }
                    System.out.println(BashColors.format(
                            "Invalid page number, page must be between 1 and " + getLastPage() + " (inclusive).",
                            BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                }
            } catch (NumberFormatException ex) {
                System.out.println(BashColors.format("Invalid number, page must be an integer!", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Requests the user to go to the next page.
     *
     * @param scanner The scanner to read user input.
     * @return True if the page was set successfully, false otherwise.
     */
    default boolean requestNextPage(Scanner scanner) {
        if (!this.nextPage()) {
            System.out.println(BashColors.format("You are already on the last page.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return true;
        }
        return true;
    }

    /**
     * Requests the user to go to the previous page.
     *
     * @param scanner The scanner to read user input.
     * @return True if the page was set successfully, false otherwise.
     */
    default boolean requestPrevPage(Scanner scanner) {
        if (!this.prevPage()) {
            System.out.println(BashColors.format("You are already on the first page.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return true;
        }
        return true;
    }

    /**
     * Sets the page number if it is valid.
     *
     * @param page The page number to set.
     * @return True if the page was set successfully, false otherwise.
     */
    default boolean page(int page) {
        if (page >= 1 && page <= getLastPage()) {
            setPage(page);
            return true;
        }
        return false;
    }

    /**
     * Moves to the next page if it exists.
     *
     * @return True if the page was set successfully, false otherwise.
     */
    default boolean nextPage() {
        int page = getPage();
        if (page < getLastPage()) {
            setPage(page + 1);
            return true;
        }
        return false;
    }

    /**
     * Moves to the previous page if it exists.
     *
     * @return True if the page was set successfully, false otherwise.
     */
    default boolean prevPage() {
        int page = getPage();
        if (page > 1) {
            setPage(page - 1);
            return true;
        }
        return false;
    }
}
