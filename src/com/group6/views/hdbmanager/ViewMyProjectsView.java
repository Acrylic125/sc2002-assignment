package com.group6.views.hdbmanager;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProject;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ViewMyProjectsView implements View {
    @Override
    public View render(ViewContext ctx) {
        Scanner sc = ctx.getScanner();
        BTOSystem system = ctx.getBtoSystem();
        HDBManager manager = (HDBManager) ctx.getUser().orElseThrow();

        System.out.println("\n===== My BTO Projects =====");

        List<BTOProject> myProjects = system.getProjects().getProjects().values().stream()
                .filter(p -> p.getManagerUserId().equals(manager.getId()))
                .collect(Collectors.toList());

        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
        } else {
            for (BTOProject p : myProjects) {
                System.out.printf("- %s (%s) | %s\n",
                        p.getName(),
                        p.getNeighbourhood(),
                        p.isVisibleToPublic() ? "Public" : "Private");
            }
        }

        System.out.println("\nType any key to return.");
        sc.nextLine();
        return new HDBManagerHomeView();
    }

}
