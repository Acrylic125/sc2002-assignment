package com.group6.views.applicant;

import com.group6.btoproject.BTOBookingReceipt;
import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.*;

/**
 * View for the applicant to view their booking receipts.
 */
public class ApplicantReceiptsView implements AuthenticatedView, PaginatedView {

    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private int page = 1;
    private List<BTOBookingReceipt> receipts = new ArrayList<>();

    /**
     * @return the last page
     */
    @Override
    public int getLastPage() {
        int size = receipts.size();
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
        this.receipts = ctx.getBtoSystem().getProjectManager().getBookingReceipts(user.getId());

        return showOptions();
    }

    /**
     * Show the receipts.
     */
    private void showReceipts() {
        System.out.println(BashColors.format("My Booking Receipts", BashColors.BOLD));
        if (receipts.isEmpty()) {
            System.out.println(BashColors.format("(No Bookings Found)", BashColors.LIGHT_GRAY));
            System.out.println();
            return;
        }

        final int lastIndex = Math.min(page * PAGE_SIZE, receipts.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;

        for (int i = firstIndex; i < lastIndex; i++) {
            final BTOBookingReceipt receipt = receipts.get(i);
            System.out.println("Project Name: " + receipt.getProjectName() + ", " + receipt.getProjectNeighbourhood());
            System.out.println("Project ID: " + receipt.getProjectId());
            System.out.println("Project Type: " + receipt.getTypeID().getName());
            System.out.println("Applicant: " + receipt.getApplicantName() + " (" + BashColors.format(receipt.getNric(), BashColors.LIGHT_GRAY) + ")");
            System.out.println("Price: $" + Utils.formatMoney(receipt.getPrice()));
            System.out.println("Booked on: " + Utils.formatToDDMMYYYY(new Date(receipt.getDateOfBooking())));
            System.out.println();
        }
    }

    /**
     * Show options available.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showReceipts();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
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
                    return null;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

}
