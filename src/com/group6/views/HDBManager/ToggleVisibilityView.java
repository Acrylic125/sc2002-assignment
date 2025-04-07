package com.group6.views.HDBManager;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProject;
import com.group6.users.HDBManager;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ToggleVisibilityView implements View {
    @Override
    public View render(ViewContext ctx) {
        Scanner sc = ctx.getScanner();
        BTOSystem system = ctx.getBtoSystem();
        HDBManager manager = (HDBManager) ctx.getUser().orElseThrow();

        System.out.println("\n===== Toggle Project Visibility =====");

        List<BTOProject> myProjects = system.getProjects().getProjects().values().stream()
                .filter(p -> p.getManagerUserId().equals(manager.getId()))
                .collect(Collectors.toList());

        if (myProjects.isEmpty()) {
            System.out.println("You have no projects.");
            System.out.println("Type any key to return.");
            sc.nextLine();
            return new HDBManagerHomeView();
        }

        for (int i = 0; i < myProjects.size(); i++) {
            BTOProject p = myProjects.get(i);
            System.out.printf("%d. %s (%s) - Currently %s\n", i + 1, p.getName(), p.getNeighbourhood(),
                    p.isVisibleToPublic() ? "Public" : "Private");
        }

        System.out.print("Select a project to toggle visibility (or 0 to cancel): ");
        int choice = Integer.parseInt(sc.nextLine());

        if (choice < 1 || choice > myProjects.size()) {
            System.out.println("Invalid Choice, Operation Cancelled.");
            return new HDBManagerHomeView();
        }

        BTOProject selected = myProjects.get(choice - 1);
        selected.setVisibleToPublic(!selected.isVisibleToPublic());
        System.out.printf("Project \"%s\" is now %s.\n", selected.getName(),
                selected.isVisibleToPublic() ? "Public" : "Private");

        System.out.println("Type any key to return.");
        sc.nextLine();
        return new HDBManagerHomeView();
    }

}
