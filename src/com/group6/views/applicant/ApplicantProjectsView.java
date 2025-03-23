package com.group6.views.applicant;

import java.util.List;
import java.util.Scanner;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.utils.Utils;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantProjectsView implements View {

    private static final int PAGE_SIZE = 3;

    @Override
    public void render(ViewContext ctx) {
        int page = 1;
        while (true) {
            final Scanner scanner = ctx.getScanner();
            final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
            final List<BTOProject> projects = projectManager.getProjects().values().stream().toList();
            projects.sort((a, b) -> a.getName().compareTo(b.getName()));

            for (int i = (page - 1) * PAGE_SIZE; i < Math.min(page * PAGE_SIZE, projects.size()); i++) {
                final BTOProject project = projects.get(i);
                final List<BTOProjectType> types = project.getProjectTypes();
                System.out.println("Project: " + project.getName() + ", " + project.getNeighbourhood());
                System.out.println("ID: " + project.getId());
                System.out.println("Types (No. Units Available / Total No. Units / Price):");
                if (types.size() <= 0) {
                    System.out.println("  No types available");
                } else {
                    for (BTOProjectType type : types) {
                        System.out.println(
                                "  " + type.getId() + " " +
                                        project.getBookedCountForType(type.getId()) + " / " + type.getMaxQuantity()
                                        + " / $" + Utils.formatMoney(type.getPrice()));
                    }
                }

                // project.getProjectTypes().forEach(() -> {
                // System.out.println(" " + type + " (" + project.getUnits().get(type).size() +
                // " / $" + price + ")");
                // });
            }

            String option = scanner.nextLine();
            switch (option) {

            }
        }
    }

}
