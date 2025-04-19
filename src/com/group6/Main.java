package com.group6;

import com.group6.btoproject.*;
import com.group6.users.*;
import com.group6.utils.BashColors;
import com.group6.views.MenuView;
import com.group6.views.ViewContext;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main entry point for the BTO application.
 */
public class Main {

    /**
     * Entry to the application.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        System.out.println("=========================");
        System.out.println(
                BashColors.format("IF YOU SEE LOADING ERRORS, MAKE SURE THE STORAGE FILES EXIST WITHIN " + System.getProperty("user.dir"), BashColors.BOLD)
        );
        System.out.println("=========================");
        System.out.println();
        final UserStorage userStorage = new UserStorage(
                "applicants.txt",
                "officers.txt",
                "managers.txt"
        );
        final BTOProjectStorage projectStorage = new BTOProjectStorage(
                "projects.txt"
        );
        final BTOBookingReceiptStorage receiptsStorage = new BTOBookingReceiptStorage(
                "booking-receipts.txt"
        );

        final BTOProjectManager projectManager = new BTOProjectManager(projectStorage, receiptsStorage);
        projectManager.setProjects(projectStorage.loadAll());
        projectManager.setReceipts(receiptsStorage.loadAll());

        final UserManager userManager = new UserManager(userStorage);
        userManager.setUsers(userStorage.loadAll());
        final BTOSystem btoSystem = new BTOSystem(projectManager, userManager);

        final ViewContext ctx = new ViewContext(btoSystem, new Scanner(System.in));
        ctx.startFromView(new MenuView());

        // Save on close
        userStorage.saveAll(new ArrayList<>(userManager.getUsers().values()));
        projectStorage.saveAll(new ArrayList<>(projectManager.getProjects().values()));
        receiptsStorage.saveAll(new ArrayList<>(projectManager.getBookingReceipts()));
    }

}