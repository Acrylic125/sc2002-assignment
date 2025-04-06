package com.group6.views.HDBManager;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProject;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ViewAllProjectsView implements View {
    @Override
    public View render(ViewContext ctx) {
        Scanner sc = ctx.getScanner();
        BTOSystem system = ctx.getBtoSystem();

        System.out.println("\n===== All BTO Projects =====");

        List<BTOProject> allProjects = system.getProjects().getProjects().values().stream()
                .collect(Collectors.toList());

        if (allProjects.isEmpty()) {
            System.out.println("No projects found.");
        } else {
            for (BTOProject p : allProjects) {
                System.out.printf("- %s (%s) | Manager: %s | %s\n",
                        p.getName(),
                        p.getNeighbourhood(),
                        p.getManagerUserId(),
                        p.isVisibleToPublic() ? "Public" : "Private");
            }
        }

        System.out.println("\nType any key to return.");
        sc.nextLine();
        return new HDBManagerHomeView();
    }

}
