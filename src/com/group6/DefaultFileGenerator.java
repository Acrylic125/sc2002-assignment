package com.group6;

import com.group6.btoproject.*;
import com.group6.users.*;
import com.group6.utils.BashColors;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * DefaultFileGenerator is a class that generates default files for the application.
 */
public class DefaultFileGenerator {

    public static void main(String[] args) {
        System.out.println(
                BashColors.format("Saving default files to " + System.getProperty("user.dir"), BashColors.BOLD)
        );
        final UserStorage userStorage = new UserStorage(
                "applicants.txt",
                "officers.txt",
                "managers.txt"
        );
        final BTOProjectStorage projectStorage = new BTOProjectStorage(
                "projects.txt"
        );
        final BTOBookingReceiptStorage receiptsStorage = new BTOBookingReceiptStorage(
                "booking-receipts.txt"
        );

        final BTOProjectManager projectManager = new BTOProjectManager(projectStorage, receiptsStorage);
        final UserManager userManager = new UserManager(userStorage);

        BTOSystem btoSystem = new BTOSystem(projectManager, userManager);
        loadUsers(btoSystem);
        loadProjects(btoSystem);

        userStorage.saveAll(new ArrayList<>(userManager.getUsers().values()));
        projectStorage.saveAll(new ArrayList<>(projectManager.getProjects().values()));
        receiptsStorage.saveAll(new ArrayList<>(projectManager.getBookingReceipts()));
    }

    public static void loadUsers(BTOSystem btoSystem) {
        final UserManager userManager = btoSystem.getUserManager();

        User john = new RoleBasedUser(UserRole.APPLICANT, UUID.randomUUID().toString(), "John", "S1234567A", 35, UserMaritalStatus.SINGLE, "password");
        User sarah = new RoleBasedUser(UserRole.APPLICANT, UUID.randomUUID().toString(), "Sarah", "T7654321B", 40, UserMaritalStatus.MARRIED, "password");
        User grace = new RoleBasedUser(UserRole.APPLICANT, UUID.randomUUID().toString(), "Grace", "S9876543C", 37, UserMaritalStatus.MARRIED, "password");
        User james = new RoleBasedUser(UserRole.APPLICANT, UUID.randomUUID().toString(), "James", "T2345678D", 30, UserMaritalStatus.MARRIED, "password");
        User rachel = new RoleBasedUser(UserRole.APPLICANT, UUID.randomUUID().toString(), "Rachel", "S3456789E", 25, UserMaritalStatus.SINGLE, "password");
        User emily = new RoleBasedUser(UserRole.OFFICER, UUID.randomUUID().toString(), "Emily", "S6543210I", 28, UserMaritalStatus.SINGLE, "password");
        User daniel = new RoleBasedUser(UserRole.OFFICER, UUID.randomUUID().toString(), "Daniel", "T2109876H", 36, UserMaritalStatus.SINGLE, "password");
        User david = new RoleBasedUser(UserRole.OFFICER, UUID.randomUUID().toString(), "David", "T1234567J", 29, UserMaritalStatus.MARRIED, "password");
        User michael = new RoleBasedUser(UserRole.MANAGER, UUID.randomUUID().toString(), "Michael", "T8765432F", 36, UserMaritalStatus.SINGLE, "password");
        User jessica = new RoleBasedUser(UserRole.MANAGER, UUID.randomUUID().toString(), "Jessica", "S5678901G", 26, UserMaritalStatus.MARRIED, "password");

        userManager.registerUser(john);
        userManager.registerUser(sarah);
        userManager.registerUser(grace);
        userManager.registerUser(james);
        userManager.registerUser(rachel);
        userManager.registerUser(emily);
        userManager.registerUser(daniel);
        userManager.registerUser(david);
        userManager.registerUser(michael);
        userManager.registerUser(jessica);
    }

    public static void loadProjects(BTOSystem btoSystem) {
        final UserManager userManager = btoSystem.getUserManager();
        final BTOProjectManager btoProjectManager = btoSystem.getProjectManager();

        User jessica = userManager.getUserByNRIC("S5678901G").get();
        User daniel = userManager.getUserByNRIC("T1234567J").get();
        User emily = userManager.getUserByNRIC("S6543210I").get();
//        User sarah = userManager.getUserByNRIC("T7654321B").get();

        // Adding Project 1
        BTOProject acaciaBreezeYishun = new BTOProject(UUID.randomUUID().toString(), jessica.getId());
        acaciaBreezeYishun.setName("Acacia Breeze");
        acaciaBreezeYishun.setNeighbourhood("Yishun");
        acaciaBreezeYishun.setProjectType(new BTOProjectType(BTOProjectTypeID.S_2_ROOM, 350_000, 2));
        acaciaBreezeYishun.setProjectType(new BTOProjectType(BTOProjectTypeID.S_3_ROOM, 450_000, 3));
        LocalDate start = LocalDate.of(2025, 2, 15);
        LocalDate end = LocalDate.of(2025, 3, 20);

        acaciaBreezeYishun.setApplicationWindow(
                Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        acaciaBreezeYishun.setOfficerLimit(3);
        btoProjectManager.addProject(acaciaBreezeYishun);
        btoProjectManager.requestRegisterOfficer(acaciaBreezeYishun.getId(), daniel.getId());
        btoProjectManager.requestRegisterOfficer(acaciaBreezeYishun.getId(), emily.getId());
        btoProjectManager.transitionOfficerRegistrationStatus(acaciaBreezeYishun.getId(), daniel.getId(), HDBOfficerRegistrationStatus.SUCCESSFUL);
        btoProjectManager.transitionOfficerRegistrationStatus(acaciaBreezeYishun.getId(), emily.getId(), HDBOfficerRegistrationStatus.SUCCESSFUL);

//        BTOProject someBtoProject = new BTOProject(UUID.randomUUID().toString(), jessica.getId());
//        someBtoProject.setName("Some BTO Project");
//        someBtoProject.setNeighbourhood("Palau NTU");
//        someBtoProject.setProjectType(new BTOProjectType(BTOProjectTypeID.S_2_ROOM, 350_000, 2));
//        someBtoProject.setProjectType(new BTOProjectType(BTOProjectTypeID.S_3_ROOM, 450_000, 2));
//
//        someBtoProject.setApplicationWindow(
//                Date.from(LocalDate.of(2025, 2, 15).atStartOfDay(ZoneId.systemDefault()).toInstant()),
//                Date.from(LocalDate.of(2025, 4, 20).atStartOfDay(ZoneId.systemDefault()).toInstant()));
//        someBtoProject.setOfficerLimit(3);
//
//        btoProjectManager.addProject(someBtoProject);
//        btoProjectManager.requestApply(someBtoProject.getId(), sarah.getId(), BTOProjectTypeID.S_3_ROOM);
//        btoProjectManager.requestWithdrawApplication(someBtoProject.getId(), someBtoProject.getActiveApplication(sarah.getId()).get().getId());
//        btoProjectManager.requestRegisterOfficer(someBtoProject.getId(), emily.getId());
//        btoProjectManager.transitionOfficerRegistrationStatus(someBtoProject.getId(), emily.getId(), HDBOfficerRegistrationStatus.SUCCESSFUL);
    }

}
