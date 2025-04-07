package com.group6.views.HDBManager;

import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.UUID;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectType;
import com.group6.users.User;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.text.SimpleDateFormat;
import java.util.*;

public class CreateBTOProjectView implements View {
    @Override
    public View render(ViewContext ctx) {
        Scanner sc = ctx.getScanner();
        BTOSystem system = ctx.getBtoSystem();
        User user = ctx.getUser().orElseThrow();

        System.out.println("\n===== Create BTO Project =====");

        System.out.print("Enter project name: ");
        String name = sc.nextLine();

        System.out.print("Enter neighbourhood: ");
        String neighbourhood = sc.nextLine();

        String id = UUID.randomUUID().toString();
        BTOProject project = new BTOProject(id, user.getId());
        project.setName(name);
        project.setNeighbourhood(neighbourhood);

        System.out.print("Enter officer limit: ");
        int officerLimit = Integer.parseInt(sc.nextLine());
        project.setOfficerLimit(officerLimit);

        // Add flat types
        while (true) {
            System.out.print("Add price for this type (or # to stop): ");
            String price = sc.nextLine();
            if (price.equals("#"))
                break;
            int priceInt = Integer.parseInt(price);

            System.out.print("Enter max quantity for this type: ");
            int maxQty = Integer.parseInt(sc.nextLine());

            BTOProjectType type = new BTOProjectType(UUID.randomUUID().toString(), priceInt, maxQty);
            project.addProjectType(type);
        }

        // Set application window
        try {
            System.out.print("Enter application open date (dd-MM-yyyy): ");
            String openStr = sc.nextLine();
            System.out.print("Enter application close date (dd-MM-yyyy): ");
            String closeStr = sc.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date openDate = sdf.parse(openStr);
            Date closeDate = sdf.parse(closeStr);
            project.setApplicationWindow(openDate, closeDate);
        } catch (Exception e) {
            System.out.println("Invalid date format. Application window not set.");
        }

        system.getProjects().addProject(project);

        System.out.println("Project created with ID: " + project.getId());
        System.out.println("Type any key to return to home.");
        sc.nextLine();

        return new HDBManagerHomeView();
    }

}
