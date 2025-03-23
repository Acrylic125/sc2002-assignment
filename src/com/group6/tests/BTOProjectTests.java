package com.group6.tests;

import com.group6.btoproject.*;
import com.group6.utils.Utils;

import java.util.UUID;

/**
 * Simple tests for {@link com.group6.btoproject.BTOProject}.
 */
public class BTOProjectTests {

    public static void main(String[] args) {
        BTOProject project = new BTOProject(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        project.addProjectType(new BTOProjectType("2 Room", 15_000, 2));
        project.addProjectType(new BTOProjectType("3 Room", 40_000, 1));
        project.setOfficerLimit(1);

        String[] userIds = {
                "User 1",
                "User 2",
                "User 3",
                "User 4",
                "User 5"
        };

        System.out.println("Checking if can apply:");
        project.requestApply(userIds[0], "2 Room");
        project.requestApply(userIds[1], "2 Room");
        project.requestApply(userIds[2], "3 Room");
        project.requestApply(userIds[3], "2 Room");
        project.getApplications().forEach((app) -> {
            System.out.println("  " + app.getApplicantUserId() + ": " + app.getStatus());
        });
        System.out.println("  Done!");

        System.out.println("Checking if applicant can apply while already having an application (Should error):");
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.requestApply(userIds[0], "2 Room");
        }).getErr().getMessage());
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.requestApply(userIds[1], "2 Room");
        }).getErr().getMessage());
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.requestApply(userIds[2], "2 Room");
        }).getErr().getMessage());
        System.out.println("  Done!");

        System.out.println("Checking if can transition application:");
        String[] applicationIds = {
                project.getActiveApplication(userIds[0]).get().getId(),
                project.getActiveApplication(userIds[1]).get().getId(),
                project.getActiveApplication(userIds[2]).get().getId(),
                project.getActiveApplication(userIds[3]).get().getId(),
        };

        project.transitionApplicationStatus(applicationIds[0], BTOApplicationStatus.SUCCESSFUL);
        project.transitionApplicationStatus(applicationIds[0], BTOApplicationStatus.BOOKED);
        project.transitionApplicationStatus(applicationIds[1], BTOApplicationStatus.SUCCESSFUL);
        project.transitionApplicationStatus(applicationIds[1], BTOApplicationStatus.BOOKED);
        System.out.println("  Done!");

        System.out.println("Checking if can directly transition to BOOKED (Should error):");
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.transitionApplicationStatus(applicationIds[2], BTOApplicationStatus.BOOKED);
        }).getErr().getMessage());
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.transitionApplicationStatus(applicationIds[3], BTOApplicationStatus.BOOKED);
        }).getErr().getMessage());
        System.out.println("  Done!");

        System.out.println("Checking current statuses of all application:");
        project.getApplications().forEach((app) -> {
            System.out.println("  " + app.getApplicantUserId() + ": " + app.getStatus());
        });
        System.out.println("  Done!");

        System.out.println("Checking current approved applicants (status = BOOKED) can register to manage this project (Should error):");
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.requestRegisterOfficer(userIds[0]);
        }).getErr().getMessage());
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.requestRegisterOfficer(userIds[1]);
        }).getErr().getMessage());
        System.out.println("  Done!");

        System.out.println("Checking current unbooked applicants can register to manage this project:");
        project.requestRegisterOfficer(userIds[2]);
        project.requestRegisterOfficer(userIds[4]);
        project.getHdbOfficerRegistrations().forEach((registration) -> {
            System.out.println("  " + registration.getOfficerUserId() + ": " + registration.getStatus());
        });
        System.out.println("  Done!");

        System.out.println("Checking if HDB officer registrants can register (Should error):");
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.requestRegisterOfficer(userIds[2]);
        }).getErr().getMessage());
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.requestRegisterOfficer(userIds[4]);
        }).getErr().getMessage());
        System.out.println("  Done!");

        System.out.println("Checking if HDB officer registrants can be approved:");
        project.transitionOfficerRegistrationStatus(userIds[2], HDBOfficerRegistrationStatus.SUCCESSFUL);
        System.out.println("  Requests (1 successful, 1 pending):");
        project.getHdbOfficerRegistrations().forEach((registration) -> {
            System.out.println("    " + registration.getOfficerUserId() + ": " + registration.getStatus());
        });
        System.out.println("  Managing Registrations (Should filter to only show successful):");
        project.getManagingOfficerRegistrations().forEach((registration) -> {
            System.out.println("    " + registration.getOfficerUserId() + ": " + registration.getStatus());
        });
        System.out.println("  Done!");

        System.out.println("Checking if HDB officer registrants can be approved over limit (Should error):");
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.transitionOfficerRegistrationStatus(userIds[4], HDBOfficerRegistrationStatus.SUCCESSFUL);
        }).getErr().getMessage());
        System.out.println("  Done!");

        System.out.println("Checking if approved HDB officer registrants can have their BTO application approved (Successful) (Should error):");
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.transitionApplicationStatus(applicationIds[2], BTOApplicationStatus.SUCCESSFUL);
        }).getErr().getMessage());
        System.out.println("  Done!");

        System.out.println("Checking if status can be changed to SUCCESSFUL/BOOKED after limit:");
        System.out.println("  Err: " + Utils.tryCatch(() -> {
            project.transitionApplicationStatus(applicationIds[3], BTOApplicationStatus.SUCCESSFUL);
        }).getErr().getMessage());

        System.out.println("Checking if applicant can go from one application to another:");
        project.transitionApplicationStatus(applicationIds[3], BTOApplicationStatus.UNSUCCESSFUL);
        System.out.println("  Modified to Unsuccessful - Should not have active applications: " + project.getActiveApplication(userIds[3]).isEmpty());
        project.requestApply(userIds[3], "3 Room");
        System.out.println("  Reapplied - Should have active applications: " + project.getActiveApplication(userIds[3]).isPresent());
        applicationIds[3] = project.getActiveApplication(userIds[3]).get().getId();
        project.transitionApplicationStatus(applicationIds[3], BTOApplicationStatus.SUCCESSFUL);
        project.getApplications().forEach((app) -> {
            System.out.println("  " + app.getApplicantUserId() + " (" +  app.getTypeId() + "): " + app.getStatus());
        });

        System.out.println("Checking if applications can be withdrawn for ALL applicants:");
        System.out.println("  Applications Before:");
        project.getApplications().forEach((app) -> {
            System.out.println("    " + app.getApplicantUserId() + " (" +  app.getTypeId() + "): " + app.getStatus());
        });
        project.requestWithdrawApplication(applicationIds[0]);
        project.requestWithdrawApplication(applicationIds[1]);
        project.requestWithdrawApplication(applicationIds[2]);
        project.requestWithdrawApplication(applicationIds[3]);
        System.out.println("  Withdrawals (Should all be pending):");
        project.getWithdrawals().forEach((withdrawal) -> {
            System.out.println("    " + withdrawal.getApplicationId() + ": " + withdrawal.getStatus());
        });
        System.out.println("  Applications while pending withdrawals (Should still be the same as Before):");
        project.getApplications().forEach((app) -> {
            System.out.println("    " + app.getApplicantUserId() + " (" +  app.getTypeId() + "): " + app.getStatus());
        });
        project.transitionWithdrawApplicationStatus(applicationIds[0], BTOApplicationWithdrawalStatus.UNSUCCESSFUL);
        project.transitionWithdrawApplicationStatus(applicationIds[1], BTOApplicationWithdrawalStatus.SUCCESSFUL);
        project.transitionWithdrawApplicationStatus(applicationIds[2], BTOApplicationWithdrawalStatus.SUCCESSFUL);
        project.transitionWithdrawApplicationStatus(applicationIds[3], BTOApplicationWithdrawalStatus.SUCCESSFUL);
        System.out.println("  Withdrawals (1 Unsuccessful, 3 Successful):");
        project.getWithdrawals().forEach((withdrawal) -> {
            System.out.println("    " + withdrawal.getApplicationId() + ": " + withdrawal.getStatus());
        });
        System.out.println("  Applications while pending withdrawals (Everyone should be unsuccessful except User 1):");
        project.getApplications().forEach((app) -> {
            System.out.println("    " + app.getApplicantUserId() + " (" +  app.getTypeId() + "): " + app.getStatus());
        });
        System.out.println("  Done!");

        System.out.println("Checking if users can withdraw (User 1 should be able to, User 2 should not (i.e. Should error)):");
        project.requestWithdrawApplication(applicationIds[0]);
        System.out.println("  Err (User 2): " + Utils.tryCatch(() -> {
            project.requestWithdrawApplication(applicationIds[1]);
        }).getErr().getMessage());
        System.out.println("  Done!");

        System.out.println("Checking if users can make enquiries:");
        for (String userId : userIds) {
            project.addEnquiry(
                    BTOEnquiry.create(
                            new BTOEnquiryMessage(userId, "How far from the nearest MRT? (" + userId + ")"),
                            null
                    )
            );
        }
        project.getEnquiries().forEach((enquiry) -> {
            System.out.println("  " + enquiry.getSenderMessage().getSenderUserId() + ": " + enquiry.getSenderMessage().getMessage());
        });
        System.out.println("  Done!");
    }


}
