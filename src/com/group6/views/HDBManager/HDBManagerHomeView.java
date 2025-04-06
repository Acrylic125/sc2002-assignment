package com.group6.views.HDBManager;

import java.util.Scanner;

import com.group6.views.View;
import com.group6.views.ViewContext;

public class HDBManagerHomeView implements View {
    public View render(ViewContext ctx) {
        Scanner sc = ctx.getScanner();

        while (true) {
            System.out.println("\n===== HDB Manager Home =====");
            System.out.println("1. Create BTO Project");
            System.out.println("2. Edit BTO Project");
            System.out.println("3. Delete BTO Project");
            System.out.println("4. Toggle Project Visibility");
            System.out.println("5. View All Projects");
            System.out.println("6. View My Projects");
            System.out.println("7. Manage Officer Registrations");
            System.out.println("8. Manage Applicant Applications");
            System.out.println("9. Handle Withdrawal Requests");
            System.out.println("10. View/Reply to Enquiries");
            System.out.println("11. Generate Reports");
            System.out.println("#. Logout");
            System.out.print("Enter your choice: ");

            String input = sc.nextLine();
            switch (input) {
                case "1":
                    // return new CreateBTOProjectView();
                    break;
                case "2":
                    // return new EditProjectView();
                    break;
                case "3":
                    // return new DeleteProjectView();
                    break;
                case "4":
                    // return new ToggleVisibilityView();
                    break;
                case "5":
                    // return new ViewAllProjectsView();
                    break;
                case "6":
                    // return new ViewMyProjectsView();
                    break;
                case "7":
                    // return new ManageOfficerRegistrationsView();
                    break;
                case "8":
                    // return new ManageApplicationsView();
                    break;
                case "9":
                    // return new HandleWithdrawalsView();
                    break;
                case "10":
                    // return new ViewAndReplyEnquiriesView();
                    break;
                case "11":
                    // return new GenerateReportView();
                    break;
                case "#":
                    System.out.println("Logging out...");
                    return null;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
