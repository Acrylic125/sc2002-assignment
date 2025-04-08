package com.group6.views.management;

import java.text.SimpleDateFormat;
import java.util.Scanner;

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

public class EditProjectView implements AuthenticatedView {

    private final BTOProject project;

    private ViewContext ctx;

    /**
     * Constructor for EditProjectView.
     *
     * @param project the project to be edited.
     */
    public EditProjectView(BTOProject project) {
        this.project = project;
    }

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canEditProject();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;

        return showOptions();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format("Edit Project Options", BashColors.BOLD));
            System.out.println("1. Edit Project Name");
            System.out.println("2. Edit Project Neighbourhood");
            System.out.println("3. Edit Project Type");
            System.out.println("4. Edit Project Officer Limit");
            System.out.println("5. Edit Project Application Window");
            System.out.println("6. Edit Project Visibility");
            System.out.println("");
            System.out.println("Type the option (e.g. 1, 2, 3) you want to select or leave empty ('') to cancel.");

            final String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    Optional<String> projectNameOpt = showRequestProjectName();
                    if (projectNameOpt.isEmpty()) {
                        break;
                    }
                    String projectName = projectNameOpt.get();
                    project.setName(projectName);
                    break;
                case "2":
                    Optional<String> neighbourhoodOpt = showRequestProjectNeighbourhood();
                    if (neighbourhoodOpt.isEmpty()) {
                        break;
                    }
                    String neighbourhood = neighbourhoodOpt.get();
                    project.setNeighbourhood(neighbourhood);
                    break;
                case "3":
                    Optional<Collection<BTOProjectType>> projectTypesOpt = showRequestProjectTypes();
                    if (projectTypesOpt.isEmpty()) {
                        break;
                    }
                    Collection<BTOProjectType> projectTypes = projectTypesOpt.get();
                    for (BTOProjectType projectType : projectTypes) {
                        project.addProjectType(projectType);
                    }
                    break;
                case "4":
                    Optional<Integer> officerLimitOpt = showRequestOfficerLimit();
                    if (officerLimitOpt.isEmpty()) {
                        break;
                    }
                    Integer officerLimit = officerLimitOpt.get();
                    project.setOfficerLimit(officerLimit);
                    break;
                case "5":
                    Optional<Date[]> applicationWindowOpt = requestApplicationWindow();
                    if (applicationWindowOpt.isEmpty()) {
                        break;
                    }
                    Date[] applicationWindow = applicationWindowOpt.get();
                    project.setApplicationWindow(applicationWindow[0], applicationWindow[1]);
                    break;
                case "6":
                    Optional<Boolean> visibilityOpt = requestProjectVisibility();
                    if (visibilityOpt.isEmpty()) {
                        break;
                    }
                    Boolean visibility = visibilityOpt.get();
                    project.setVisibleToPublic(visibility);
                    break;
                case "":
                    return null;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
            }
        }
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
                // If the project has the same name.... then obviously it is the same project
                if (projectOpt.get().getId().equals(project.getId())) {
                    return Optional.of(name);
                }
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
            int count = project.getBookedCountForType(_projectType.getId());
            System.out.println(BashColors.format(
                    "Enter the quantity and price of the project type separated by a comma (e.g. 10, 450000) or leave empty ('') tp cancel.",
                    BashColors.BOLD));
            System.out.println(BashColors.format(
                    "NOTE: You can set both quantity and price to 0 (i.e. 0, 0) to remove the project type.",
                    BashColors.LIGHT_GRAY));
            System.out.println(BashColors.format(
                    "NOTE: There are already applicants booked into this project type! You must minimally set the quantity to "
                            + count + ".",
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

            final int quantity = quantityResult.getData().get();
            if (quantity < count) {
                System.out.println(BashColors.format(
                        "There are already " + count
                                + " units booked for this project type, please make sure that the quantity is greater than or equal to the booked count.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            final BTOProjectType projectType = new BTOProjectType(_projectType.getId(), priceResult.getData().get(),
                    quantity);
            return Optional.of(projectType);
        }
    }

    private Optional<Integer> showRequestOfficerLimit() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            int count = project.getManagingOfficerRegistrations().size();
            System.out.println(BashColors.format(
                    "Enter officer limit or leave empty ('') to cancel:", BashColors.BOLD));
            if (count > 0) {
                System.out.println(BashColors.format(
                        "NOTE: There are already " + count
                                + " officers registered for this project! You must minimally set the officer limit to "
                                + count + ".",
                        BashColors.LIGHT_GRAY));
            }
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
            final int officerLimit = result.getData().get();
            if (officerLimit < count) {
                System.out.println(BashColors.format(
                        "There are already " + count
                                + " officers registered for this project, please make sure that the officer limit is greater than or equal to the current officer count.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return result.getData();
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

    private Optional<Boolean> requestProjectVisibility() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter project public visibility (true/false) or leave empty ('') to cancel:", BashColors.BOLD));
            final String visibilityStr = scanner.nextLine().trim();
            if (visibilityStr.isEmpty()) {
                return Optional.empty();
            }
            if (!visibilityStr.equalsIgnoreCase("true") && !visibilityStr.equalsIgnoreCase("false")) {
                System.out.println(BashColors.format(
                        "Invalid project visibility, please type in a valid project visibility.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(Boolean.parseBoolean(visibilityStr));
        }
    }

}
