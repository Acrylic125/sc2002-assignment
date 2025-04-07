package com.group6.views.HDBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.HDBOfficerRegistration;
import com.group6.btoproject.HDBOfficerRegistrationStatus;
import com.group6.users.HDBManager;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ManageOfficerRegistrationsView implements View {
    public View render(ViewContext ctx) {
        Scanner sc = ctx.getScanner();
        BTOSystem system = ctx.getBtoSystem();
        HDBManager manager = (HDBManager) ctx.getUser().orElseThrow();

        System.out.println("\n===== Manage Officer Registrations =====");

        List<BTOProject> myProjects = system.getProjects().getProjects().values().stream()
                .filter(p -> p.getManagerUserId().equals(manager.getId()))
                .collect(Collectors.toList());

        if (myProjects.isEmpty()) {
            System.out.println("You have no projects.");
            System.out.println("Type any key to return.");
            sc.nextLine();
            return new HDBManagerHomeView();
        }

        Map<String, BTOProject> projectMap = new HashMap<>();
        List<HDBOfficerRegistration> pendingRegs = new ArrayList<>();

        for (BTOProject project : myProjects) {
            for (HDBOfficerRegistration reg : project.getHdbOfficerRegistrations()) {
                if (reg.getStatus() == HDBOfficerRegistrationStatus.PENDING) {
                    pendingRegs.add(reg);
                    projectMap.put(reg.getOfficerUserId(), project);
                }
            }
        }

        if (pendingRegs.isEmpty()) {
            System.out.println("No pending officer registrations.");
            System.out.println("Type any key to return.");
            sc.nextLine();
            return new HDBManagerHomeView();
        }

        for (int i = 0; i < pendingRegs.size(); i++) {
            HDBOfficerRegistration reg = pendingRegs.get(i);
            BTOProject p = projectMap.get(reg.getOfficerUserId());
            System.out.printf("%d. User ID: %s | Project: %s (%s)\n",
                    i + 1, reg.getOfficerUserId(), p.getName(), p.getNeighbourhood());
        }

        System.out.print("Select a registration to approve/reject (0 to cancel): ");
        int choice = Integer.parseInt(sc.nextLine());

        if (choice < 1 || choice > pendingRegs.size()) {
            System.out.println("Invalid Choice, Operation Cancelled.");
            return new HDBManagerHomeView();
        }

        HDBOfficerRegistration selected = pendingRegs.get(choice - 1);
        BTOProject project = projectMap.get(selected.getOfficerUserId());

        System.out.print("Approve or Reject (a/r): ");
        String decision = sc.nextLine();

        try {
            if (decision.equalsIgnoreCase("a")) {
                project.transitionOfficerRegistrationStatus(selected.getOfficerUserId(),
                        HDBOfficerRegistrationStatus.SUCCESSFUL);
                System.out.println("Approved successfully.");
            } else if (decision.equalsIgnoreCase("r")) {
                project.transitionOfficerRegistrationStatus(selected.getOfficerUserId(),
                        HDBOfficerRegistrationStatus.UNSUCCESSFUL);
                System.out.println("Rejected successfully.");
            } else {
                System.out.println("Invalid input. No changes made.");
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Type any key to return.");
        sc.nextLine();
        return new HDBManagerHomeView();

    }
}
