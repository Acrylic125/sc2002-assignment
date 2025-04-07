package com.group6.views.HDBManager;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProject;
import com.group6.users.HDBManager;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class EditProjectView implements View {
    @Override
    public View render(ViewContext ctx) {
        Scanner sc = ctx.getScanner();
        BTOSystem system = ctx.getBtoSystem();
        HDBManager manager = (HDBManager) ctx.getUser().orElseThrow();

        System.out.println("\n===== Edit BTO Project =====");

        List<BTOProject> myProjects = system.getProjects().getProjects().values().stream()
                .filter(p -> p.getManagerUserId().equals(manager.getId()))
                .collect(Collectors.toList());

        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to edit.");
            System.out.println("Type any key to return.");
            sc.nextLine();
            return new HDBManagerHomeView();
        }

        for (int i = 0; i < myProjects.size(); i++) {
            BTOProject p = myProjects.get(i);
            System.out.printf("%d. %s (%s)\n", i + 1, p.getName(), p.getNeighbourhood());
        }

        System.out.print("Select a project to edit (or 0 to cancel): ");
        int choice = Integer.parseInt(sc.nextLine());

        if (choice < 1 || choice > myProjects.size()) {
            System.out.println("Cancelled.");
            return new HDBManagerHomeView();
        }

        BTOProject selected = myProjects.get(choice - 1);

        System.out.print("Enter new name (leave blank to keep \"" + selected.getName() + "\"): ");
        String name = sc.nextLine();
        if (!name.isBlank())
            selected.setName(name);

        System.out.print("Enter new neighbourhood (leave blank to keep \"" + selected.getNeighbourhood() + "\"): ");
        String hood = sc.nextLine();
        if (!hood.isBlank())
            selected.setNeighbourhood(hood);

        System.out.print("Enter new officer limit (or -1 to keep " + selected.getOfficerLimit() + "): ");
        int limit = Integer.parseInt(sc.nextLine());
        if (limit >= 0)
            selected.setOfficerLimit(limit);

        System.out.println("Project updated successfully.");
        System.out.println("Type any key to return.");
        sc.nextLine();

        return new HDBManagerHomeView();
    }
}
