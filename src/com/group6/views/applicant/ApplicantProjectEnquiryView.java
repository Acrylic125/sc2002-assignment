package com.group6.views.applicant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOEnquiry;
import com.group6.btoproject.BTOEnquiryMessage;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.HDBManager;
import com.group6.users.HDBOfficer;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantProjectEnquiryView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private User user;
    private int page = 1;
    private BTOProject project;

    // Used by HDBOfficer and HDB Manager
    private final boolean filterUserEnquiries;

    public ApplicantProjectEnquiryView() {
        this(false);
    }

    public ApplicantProjectEnquiryView(boolean filterUserEnquiries) {
        this.filterUserEnquiries = filterUserEnquiries;
    }

    @Override
    public int getLastPage() {
        int size = project.getEnquiries().size();
        if (size % PAGE_SIZE == 0) {
            return size / PAGE_SIZE;
        }
        return size / PAGE_SIZE + 1;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        final Optional<BTOProject> projectOpt = showRequestProject();
        if (projectOpt.isEmpty()) {
            return null;
        }

        this.project = projectOpt.get();

        showOptions();
        return null;
    }

    private Optional<BTOProject> showRequestProject() {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        while (true) {
            System.out.println(BashColors.format(
                    "Type in the project id you want to enquire, or leave empty ('') to cancel:", BashColors.BOLD));
            final String projectId = scanner.nextLine().trim();
            if (projectId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOProject> projectOpt = projectManager.getProject(projectId);
            if (projectOpt.isEmpty()) {
                System.out.println(
                        BashColors.format("Project not found, please type in a valid project id.", BashColors.BOLD));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            return projectOpt;
        }
    }

    private void showEnquiries() {
        final Scanner scanner = ctx.getScanner();
        System.out.println(BashColors
                .format(project.getName() + ", " + project.getNeighbourhood() + " - Your Enquiries", BashColors.BOLD));
        System.out.println("Enquiry ID | Message | Response");
        List<BTOEnquiry> enquiries = project.getEnquiries().stream()
                .filter((enquiry) -> {
                    if (!filterUserEnquiries && (user instanceof HDBOfficer || user instanceof HDBManager)) {
                        return true;
                    }
                    return enquiry.getSenderMessage().getSenderUserId().equals(user.getId());
                })
                .toList();
        if (enquiries.isEmpty()) {
            System.out.println(BashColors.format("No enquiries found.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }
        int lastIndex = Math.min(page * PAGE_SIZE, enquiries.size());
        enquiries = new ArrayList<>(enquiries);
        // Render the projects in the page.
        for (int i = (page - 1) * PAGE_SIZE; i < lastIndex; i++) {
            BTOEnquiry enquiry = enquiries.get(i);
            Optional<BTOEnquiryMessage> response = enquiry.getResponseMessage();
            if (response.isPresent()) {
                System.out.println(enquiry.getId() + " | " + enquiry.getSenderMessage().getMessage() + " | "
                        + response.get().getMessage());
            } else {
                System.out.println(
                        enquiry.getId() + " | " + enquiry.getSenderMessage().getMessage() + " | "
                                + BashColors.format("(No Response)", BashColors.LIGHT_GRAY));
            }
        }
    }

    private void showOptions() {
        final Scanner scanner = ctx.getScanner();
        while (true) {
            showEnquiries();
            System.out.println("");
            System.out.println(
                    "Page " + page + " / " + getLastPage() +
                            " - Type 'v' to view, 'a' to add, 'd' to delete, 'e' to edit, 'n' to go to next page, 'p' to go to  previous page, 'page' to go to a specific page, or leave empty ('') to go back:  ");
            String opt = scanner.nextLine();
            switch (opt) {
                case "v":
                    showViewEnquiry();
                    break;
                case "a":
                    showAddEnquiry();
                    break;
                case "d":
                    showDeleteEnquiry();
                    break;
                case "e":
                    showEditEnquiry();
                    break;
                case "n":
                    if (!this.nextPage()) {
                        System.out.println(BashColors.format("You are already on the last page.", BashColors.RED));
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                    }
                    break;
                case "p":
                    if (!this.prevPage()) {
                        System.out.println(BashColors.format("You are already on the first page.", BashColors.RED));
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                    }
                    break;
                case "page":
                    Optional<Integer> pageOpt = this.requestPage(scanner);
                    if (pageOpt.isEmpty()) {
                        break;
                    }
                    if (!this.page(pageOpt.get())) {
                        System.out.println(BashColors.format("Invalid page number.", BashColors.RED));
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                    }
                    break;
                case "":
                    return;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    private void showViewEnquiry() {
        final UserManager userManager = ctx.getBtoSystem().getUsers();
        final Scanner scanner = ctx.getScanner();

        BTOEnquiry enquiry;
        while (true) {
            System.out.println(BashColors.format("What would you like to view?", BashColors.BOLD));
            System.out.println("Type the enquiry id, or leave empty ('') to cancel:");
            String opt = scanner.nextLine().trim();
            if (opt.isEmpty()) {
                return;
            }
            Optional<BTOEnquiry> enquiryOpt = project.getEnquiries().stream()
                    .filter((_enquiry) -> _enquiry.getId().equals(opt))
                    .findFirst();
            if (enquiryOpt.isEmpty()) {
                System.out.println(BashColors.format("Enquiry not found. Please type in a valid id.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            enquiry = enquiryOpt.get();
            if (!enquiry.getSenderMessage().getSenderUserId().equals(user.getId())
                    || !(!filterUserEnquiries && (user instanceof HDBOfficer || user instanceof HDBManager))) {
                System.out.println(BashColors.format(
                        "You are not the sender of this enquiry, you may not view it. Please type in a valid id.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            break;
        }

        System.out.println("Enquiry ID: " + enquiry.getId());
        System.out.println("Message: " + enquiry.getSenderMessage().getMessage());
        Optional<BTOEnquiryMessage> response = enquiry.getResponseMessage();
        if (response.isPresent()) {
            System.out.println("Response: " + response.get().getMessage());
            Optional<User> responderOpt = userManager.getUser(response.get().getSenderUserId());
            if (responderOpt.isPresent()) {
                System.out.println("Responder: " + responderOpt.get().getName());
            } else {
                System.out.println("Responder: " + BashColors.format("(Unknown)", BashColors.LIGHT_GRAY));
            }
        } else {
            System.out.println("Response: " + BashColors.format("(No Response)", BashColors.LIGHT_GRAY));
        }
    }

    private void showEditEnquiry() {
        final Scanner scanner = ctx.getScanner();
        BTOEnquiry enquiry;
        while (true) {
            System.out.println(BashColors.format("What would you like to edit?", BashColors.BOLD));
            System.out.println("Type the enquiry id, or leave empty ('') to cancel:");
            String opt = scanner.nextLine().trim();
            if (opt.isEmpty()) {
                return;
            }
            Optional<BTOEnquiry> enquiryOpt = project.getEnquiries().stream()
                    .filter((_enquiry) -> _enquiry.getId().equals(opt))
                    .findFirst();
            if (enquiryOpt.isEmpty()) {
                System.out.println(BashColors.format("Enquiry not found. Please type in a valid id.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            enquiry = enquiryOpt.get();
            if (!enquiry.getSenderMessage().getSenderUserId().equals(user.getId())) {
                System.out.println(BashColors
                        .format("You are not the sender of this enquiry. Please type in a valid id.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            if (enquiry.getResponseMessage().isPresent()) {
                System.out.println(BashColors.format("This enquiry has already been responded to. You may not edit it.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            break;
        }

        // Then ask for the new message.
        System.out.println(BashColors
                .format("What would you like to change the message, \"" + enquiry.getSenderMessage().getMessage()
                        + "\" to? Leave empty ('') to cancel:", BashColors.BOLD));
        String newMessage = scanner.nextLine().trim();
        if (newMessage.isEmpty()) {
            return;
        }
        enquiry.setSenderMessage(new BTOEnquiryMessage(user.getId(), newMessage));
        System.out.println(BashColors.format("Message updated!", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    private void showAddEnquiry() {
        final Scanner scanner = ctx.getScanner();
        System.out.println(
                BashColors.format("What would you like to enquire?: Leave empty ('') to cancel:", BashColors.BOLD));
        String opt = scanner.nextLine().trim();
        if (opt.isEmpty()) {
            return;
        }
        project.addEnquiry(BTOEnquiry.create(
                new BTOEnquiryMessage(user.getId(), opt),
                null));
        System.out.println(BashColors.format("Message sent!", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    private void showDeleteEnquiry() {
        final Scanner scanner = ctx.getScanner();
        BTOEnquiry enquiry;
        while (true) {
            System.out.println(BashColors.format("What would you like to delete?", BashColors.BOLD));
            System.out.println("Type the enquiry id, or leave empty ('') to cancel:");
            String opt = scanner.nextLine().trim();
            if (opt.isEmpty()) {
                return;
            }
            Optional<BTOEnquiry> enquiryOpt = project.getEnquiries().stream()
                    .filter((_enquiry) -> _enquiry.getId().equals(opt))
                    .findFirst();
            if (enquiryOpt.isEmpty()) {
                System.out.println(BashColors.format("Enquiry not found. Please type in a valid id.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            enquiry = enquiryOpt.get();
            if (!enquiry.getSenderMessage().getSenderUserId().equals(user.getId())) {
                System.out.println(BashColors
                        .format("You are not the sender of this enquiry. Please type in a valid id.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            if (enquiry.getResponseMessage().isPresent()) {
                System.out.println(BashColors
                        .format("This enquiry has already been responded to. You may not delete it.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            break;
        }

        project.deleteEnquiry(enquiry.getId());
        System.out.println(BashColors.format("Message deleted", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

}
