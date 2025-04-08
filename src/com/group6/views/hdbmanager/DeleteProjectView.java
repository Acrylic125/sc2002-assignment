package com.group6.views.hdbmanager;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProject;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class DeleteProjectView implements View {
    @Override
    public View render(ViewContext ctx) {
        Scanner sc = ctx.getScanner();
        BTOSystem system = ctx.getBtoSystem();
        HDBManager manager = (HDBManager) ctx.getUser().orElseThrow();

        System.out.println("\n===== Delete Project =====");

        List<BTOProject> myProjects = system.getProjects().getProjects().values().stream()
                .filter(p -> p.getManagerUserId().equals(manager.getId()))
                .collect(Collectors.toList());

        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to delete.");
            System.out.println("Type any key to return.");
            sc.nextLine();
            return new HDBManagerHomeView();
        }

        for (int i = 0; i < myProjects.size(); i++) {
            BTOProject p = myProjects.get(i);
            System.out.printf("%d. %s (%s)\n", i + 1, p.getName(), p.getNeighbourhood());
        }

        System.out.print("Select a project to delete (or 0 to cancel): ");
        int choice = Integer.parseInt(sc.nextLine());

        if (choice < 1 || choice > myProjects.size()) {
            System.out.println("Cancelled.");
            return new HDBManagerHomeView();
        }

        BTOProject selected = myProjects.get(choice - 1);

        System.out.print("Are you sure you want to delete \"" + selected.getName() + "\"? (yes/no): ");
        String confirm = sc.nextLine();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return new HDBManagerHomeView();
        }

        system.getProjects().getProjects().remove(selected.getId());
        System.out.println("Project deleted successfully.");

        System.out.println("Type any key to return.");
        sc.nextLine();
        return new HDBManagerHomeView();
    }

}
