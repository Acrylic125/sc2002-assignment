package com.group6.views.applicant;

import java.util.*;
import java.util.function.Supplier;

import com.group6.btoproject.BTOEnquiry;
import com.group6.btoproject.BTOEnquiryMessage;
import com.group6.btoproject.BTOProject;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

/**
 * View for the applicant to view their enquiries.
 */
public class ProjectEnquiryView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private final BTOProject project;
    private final Supplier<List<BTOEnquiry>> enquiriesSupplier;

    private List<BTOEnquiry> enquiries;
    private ViewContext ctx;
    private User user;
    private int page = 1;
    private boolean canRespond;

    /**
     * Constructor for the ProjectEnquiryView.
     *
     * @param project         the project
     * @param enquiriesSupplier the supplier for the list of enquiries
     * @param canRespond      whether the user can respond to enquiries
     */
    public ProjectEnquiryView(BTOProject project, Supplier<List<BTOEnquiry>> enquiriesSupplier, boolean canRespond) {
        this.project = project;
        this.enquiriesSupplier = enquiriesSupplier;
        this.enquiries = enquiriesSupplier.get();
        this.canRespond = canRespond;
    }

    /**
     * Constructor for the ProjectEnquiryView.
     *
     * @param project         the project
     * @param enquiriesSupplier the supplier for the list of enquiries
     */
    public ProjectEnquiryView(BTOProject project, Supplier<List<BTOEnquiry>> enquiriesSupplier) {
        this.project = project;
        this.enquiriesSupplier = enquiriesSupplier;
        this.enquiries = enquiriesSupplier.get();
    }

    /**
     * @return the last page
     */
    @Override
    public int getLastPage() {
        int size = enquiries.size();
        if (size % PAGE_SIZE == 0) {
            return size / PAGE_SIZE;
        }
        return size / PAGE_SIZE + 1;
    }

    /**
     * Set page
     */
    @Override
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the current page
     */
    @Override
    public int getPage() {
        return page;
    }

    /**
     * View renderer.
     *
     * @param ctx  view context
     * @param user authenticated user
     * @return next view
     */
    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        showOptions();
        return null;
    }

    /**
     * Show the enquiries.
     */
    private void showEnquiries() {
        final Scanner scanner = ctx.getScanner();
        System.out.println(BashColors
                .format(project.getName() + ", " + project.getNeighbourhood() + " - Enquiries", BashColors.BOLD));
        System.out.println("Enquiry ID | Message | Response");
        if (enquiries.isEmpty()) {
            System.out.println(BashColors.format("(No enquiries found)", BashColors.LIGHT_GRAY));
            return;
        }
        int lastIndex = Math.min(page * PAGE_SIZE, enquiries.size());
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

    /**
     * Show the options for the user.
     */
    private void showOptions() {
        final Scanner scanner = ctx.getScanner();

        Utils.joinStringDelimiter(new ArrayList<>(), ", ", " or ");
        final List<String> options = new ArrayList<>();
        if (canRespond) {
            options.add("'r' to respond");
        }
        options.add("'v' to view");
        options.add("'a' to add");
        options.add("'e' to edit");
        options.add("'d' to delete");
        options.add("'n' to go to next page");
        options.add("'p' to go to previous page");
        options.add("'page' to go to a specific page");
        options.add("leave empty ('') to go back");

        final String optionsStr = Utils.joinStringDelimiter(
                options,
                ", ",
                " or ");
        while (true) {
            showEnquiries();
            System.out.println();
            System.out.println("Page " + page + " / " + getLastPage() + " - " + optionsStr + ":");

            String opt = scanner.nextLine().trim();
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
                    this.requestNextPage(scanner);
                    break;
                case "p":
                    this.requestPrevPage(scanner);
                    break;
                case "page":
                    this.requestPage(scanner);
                    break;
                case "":
                    return;
                case "r":
                    if (canRespond) {
                        showRespond();
                        break;
                    }
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    /**
     * Show the view enquiry.
     */
    private void showViewEnquiry() {
        final UserManager userManager = ctx.getBtoSystem().getUserManager();
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
            if (!enquiry.getSenderMessage().getSenderUserId().equals(user.getId())) {
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
        System.out.println("Last Updated: " + Utils.formatToDDMMYYYYWithTime(new Date( enquiry.getSenderMessage().getLastUpdated())));
        System.out.println("Message: " + enquiry.getSenderMessage().getMessage());
        Optional<BTOEnquiryMessage> responseOpt = enquiry.getResponseMessage();
        if (responseOpt.isPresent()) {
            BTOEnquiryMessage response = responseOpt.get();
            System.out.println("Response: " + response.getMessage());
            Optional<User> responderOpt = userManager.getUser(response.getSenderUserId());
            if (responderOpt.isPresent()) {
                System.out.println("Responder: " + responderOpt.get().getName());
            } else {
                System.out.println("Responder: " + BashColors.format("(Unknown)", BashColors.LIGHT_GRAY));
            }
            System.out.println("Responded on: " + Utils.formatToDDMMYYYYWithTime(new Date(response.getLastUpdated())));
        } else {
            System.out.println("Response: " + BashColors.format("(No Response)", BashColors.LIGHT_GRAY));
        }
        System.out.println();
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the respond enquiry.
     */
    private void showRespond() {
        final Scanner scanner = ctx.getScanner();
        BTOEnquiry enquiry;
        while (true) {
            System.out.println(BashColors.format("What would you like to respond?", BashColors.BOLD));
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
            if (enquiry.getResponseMessage().isPresent()) {
                System.out.println(BashColors.format("This enquiry has already been responded to. You may not respond to it.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            break;
        }

        // Then ask for the new message.
        System.out.println(BashColors
                .format("What would you like to respond, \"" + enquiry.getSenderMessage().getMessage()
                        + "\" with? Leave empty ('') to cancel:", BashColors.BOLD));
        String newMessage = scanner.nextLine().trim();
        if (newMessage.isEmpty()) {
            return;
        }
        enquiry.setResponseMessage(new BTOEnquiryMessage(user.getId(), newMessage, System.currentTimeMillis()));
        this.enquiries = enquiriesSupplier.get();
        System.out.println(BashColors.format("Message responded!", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the edit enquiry.
     */
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
        enquiry.setSenderMessage(new BTOEnquiryMessage(user.getId(), newMessage, System.currentTimeMillis()));
        this.enquiries = enquiriesSupplier.get();
        System.out.println(BashColors.format("Message updated!", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the add enquiry.
     */
    private void showAddEnquiry() {
        final Scanner scanner = ctx.getScanner();
        System.out.println(
                BashColors.format("What would you like to enquire?: Leave empty ('') to cancel:", BashColors.BOLD));
        String opt = scanner.nextLine().trim();
        if (opt.isEmpty()) {
            return;
        }
        project.addEnquiry(BTOEnquiry.create(
                new BTOEnquiryMessage(user.getId(), opt, System.currentTimeMillis()),
                null));
        this.enquiries = enquiriesSupplier.get();
        System.out.println(BashColors.format("Message sent!", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the delete enquiry.
     */
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
        this.enquiries = enquiriesSupplier.get();
        System.out.println(BashColors.format("Message deleted", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

}
