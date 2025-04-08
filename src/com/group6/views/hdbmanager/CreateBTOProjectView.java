package com.group6.views.hdbmanager;

import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.UUID;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.btoproject.BTOProjectTypeID;
import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.utils.TryCatchResult;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.*;

public class CreateBTOProjectView implements AuthenticatedView {

    private ViewContext ctx;

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canCreateProject();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;

        Scanner sc = ctx.getScanner();
        BTOSystem system = ctx.getBtoSystem();

        List<BTOProject> activeManagerProjects = system.getProjects().getProjects().values().stream()
                .filter(p -> p.isApplicationWindowOpen())
                .toList();
        if (activeManagerProjects.size() >= 1) {
            System.out.println(BashColors.format(
                    "You are currently managing projects that are open, you may not create projects.", BashColors.RED));
            System.out.println("Type anything to continue.");
            sc.nextLine();
            return null;
        }

        System.out.println(BashColors.format("Create BTO Project", BashColors.BOLD));
        Optional<String> projectNameOpt = showRequestProjectName();
        if (projectNameOpt.isEmpty()) {
            return null;
        }
        String projectName = projectNameOpt.get();

        Optional<String> neighbourhoodOpt = showRequestProjectNeighbourhood();
        if (neighbourhoodOpt.isEmpty()) {
            return null;
        }
        String neighbourhood = neighbourhoodOpt.get();

        Optional<Integer> officerLimitOpt = showRequestOfficerLimit();
        if (officerLimitOpt.isEmpty()) {
            return null;
        }
        int officerLimit = officerLimitOpt.get();

        Optional<Collection<BTOProjectType>> projectTypesOpt = showRequestProjectTypes();
        if (projectTypesOpt.isEmpty()) {
            return null;
        }
        Collection<BTOProjectType> projectTypes = projectTypesOpt.get();

        Optional<Date[]> applicationWindowOpt = requestApplicationWindow();
        if (applicationWindowOpt.isEmpty()) {
            return null;
        }
        Date[] applicationWindow = applicationWindowOpt.get();
        if (applicationWindow.length != 2) {
            System.out.println(
                    BashColors.format("Invalid application window, unhandled. Falling back...", BashColors.RED));
            System.out.println("Type anything to continue.");
            sc.nextLine();
            return null;
        }

        Date openDate = applicationWindow[0];
        Date closeDate = applicationWindow[1];

        BTOProject project = new BTOProject(UUID.randomUUID().toString(), user.getId());
        project.setName(projectName);
        project.setNeighbourhood(neighbourhood);
        project.setOfficerLimit(officerLimit);
        for (BTOProjectType type : projectTypes) {
            project.addProjectType(type);
        }
        project.setApplicationWindow(openDate, closeDate);
        project.setVisibleToPublic(false);

        if (Utils.tryCatch(() -> {
            system.getProjects().addProject(project);
        }).getErr().isPresent()) {
            System.out
                    .println(BashColors.format("Failed to create project, unhandled. Falling back...", BashColors.RED));
            System.out.println("Type anything to continue.");
            sc.nextLine();
            return null;
        }

        System.out.println(BashColors.format("Project created with ID: " + project.getId(), BashColors.GREEN));
        System.out.println("Type anything to continue.");
        sc.nextLine();
        return null;
    }

    private Optional<String> showRequestProjectName() {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter project name or leave empty ('') to cancel:", BashColors.BOLD));
            final String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOProject> projectOpt = projectManager.getProjectByName(name);
            if (projectOpt.isPresent()) {
                System.out.println(BashColors.format(
                        "Project with name already exists, please type in a valid project name.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(name);
        }
    }

    private Optional<String> showRequestProjectNeighbourhood() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter neighbourhood or leave empty ('') to cancel:", BashColors.BOLD));
            final String neighbourhood = scanner.nextLine().trim();
            if (neighbourhood.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(neighbourhood);
        }
    }

    private Optional<Integer> showRequestOfficerLimit() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter officer limit or leave empty ('') to cancel:", BashColors.BOLD));
            final String officerLimitStr = scanner.nextLine().trim();
            if (officerLimitStr.isEmpty()) {
                return Optional.empty();
            }
            TryCatchResult<Integer, Throwable> result = Utils.tryCatch(() -> Integer.parseInt(officerLimitStr));
            if (result.getErr().isPresent()) {
                System.out.println(
                        BashColors.format("Invalid officer limit, please type in a valid number.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return result.getData();
        }
    }

    private Optional<Collection<BTOProjectType>> showRequestProjectTypes() {
        final Scanner scanner = ctx.getScanner();

        final BTOProjectTypeID[] allAvailableTypes = BTOProjectTypeID.values();
        final Map<BTOProjectTypeID, BTOProjectType> projectTypeMap = new HashMap<>();

        while (true) {
            System.out.println(BashColors.format("Specify project types", BashColors.BOLD));
            System.out.println("Types (Total No. Units / Price):");
            if (allAvailableTypes.length == 0) {
                System.out.println(BashColors.format("(No available types)", BashColors.LIGHT_GRAY));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return Optional.empty();
            }

            for (BTOProjectTypeID typeID : allAvailableTypes) {
                final BTOProjectType projectType = projectTypeMap.get(typeID);
                if (projectType == null) {
                    System.out.println("  " + typeID.getName() + " 0 / $0.00");
                } else {
                    System.out.println("  " + typeID.getName() + " " + projectType.getMaxQuantity() + " / $"
                            + Utils.formatMoney(projectType.getPrice()));
                }
            }

            System.out.println("Type the type (e.g. '" + allAvailableTypes[0].getName()
                    + "') or leave empty ('') to cancel:");

            final String selectedTypeStr = scanner.nextLine().trim();
            if (selectedTypeStr.isEmpty()) {
                return Optional.empty();
            }

            final BTOProjectTypeID selectedType = Arrays.stream(allAvailableTypes)
                    .filter(type -> type.getName().equalsIgnoreCase(selectedTypeStr))
                    .findFirst()
                    .orElse(null);
            if (selectedType == null) {
                System.out.println(BashColors.format("Invalid type, please type in a valid type.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            BTOProjectType projectType = projectTypeMap.get(selectedType);
            if (projectType == null) {
                projectType = new BTOProjectType(selectedType, 0, 0);
            }
            Optional<BTOProjectType> modifiedProjectTypeOpt = requestProjectTypeEdit(projectType);
            if (modifiedProjectTypeOpt.isEmpty()) {
                continue;
            }
            projectType = modifiedProjectTypeOpt.get();
            if (projectType.getMaxQuantity() == 0 && projectType.getPrice() == 0) {
                projectTypeMap.remove(selectedType);
                System.out.println(BashColors.format(
                        "Project type " + selectedType.getName() + " removed.", BashColors.GREEN));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            projectTypeMap.put(selectedType, projectType);
            System.out.println(BashColors.format(
                    "Project type " + selectedType.getName() + " updated.", BashColors.GREEN));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }
    }

    private Optional<BTOProjectType> requestProjectTypeEdit(BTOProjectType _projectType) {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter the quantity and price of the project type separated by a comma (e.g. 10, 450000) or leave empty ('') tp cancel.",
                    BashColors.BOLD));
            System.out.println(BashColors.format(
                    "NOTE: You can set both quantity and price to 0 (i.e. 0, 0) to remove the project type.",
                    BashColors.LIGHT_GRAY));
            final String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return Optional.empty();
            }
            final String[] parts = input.split(",");
            if (parts.length != 2) {
                System.out.println(BashColors.format(
                        "Invalid input, please type in the quantity and price separated by a comma.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            TryCatchResult<Integer, Throwable> quantityResult = Utils.tryCatch(() -> Integer.parseInt(parts[0].trim()));
            TryCatchResult<Integer, Throwable> priceResult = Utils.tryCatch(() -> Integer.parseInt(parts[1].trim()));
            if (quantityResult.getErr().isPresent() || priceResult.getErr().isPresent()) {
                System.out.println(BashColors.format(
                        "Invalid input, please type in the quantity and price separated by a comma.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            final BTOProjectType projectType = new BTOProjectType(_projectType.getId(), priceResult.getData().get(),
                    quantityResult.getData().get());
            return Optional.of(projectType);
        }
    }

    private Optional<Date[]> requestApplicationWindow() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter the openning and closing date of the project in DD/MM/YYYY format, separated by a comma (e.g. 1/1/2025, 2/2/2025) or leave empty ('') tp cancel.",
                    BashColors.BOLD));
            System.out.println(BashColors.format(
                    "NOTE: openning and closing date are inclusive meaning openning starts at 00:00 of the day and closing ends at 23:59 of the day.",
                    BashColors.LIGHT_GRAY));
            final String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return Optional.empty();
            }
            final String[] parts = input.split(",");
            if (parts.length != 2) {
                System.out.println(BashColors.format(
                        "Invalid input, please type in the openning and closing date separated by a comma.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date openDate = null;
            Date closeDate = null;
            try {
                openDate = sdf.parse(parts[0].trim());
                closeDate = sdf.parse(parts[1].trim());
                // Set open date to 00:00 of the day
                openDate = new Date(openDate.getTime() - openDate.getTime() % (24 * 60 * 60 * 1000));
                // Set close date to 23:59 of the day
                closeDate = new Date(
                        closeDate.getTime() + (24 * 60 * 60 * 1000) - closeDate.getTime() % (24 * 60 * 60 * 1000));
            } catch (Exception e) {
                System.out.println(BashColors.format(
                        "Invalid input, please type in the openning and closing date in DD/MM/YYYY format separated by a comma.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            if (openDate.after(closeDate)) {
                System.out.println(BashColors.format(
                        "Invalid input, please make sure that the openning date is before the closing date.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(new Date[] { openDate, closeDate });
        }
    }

}
