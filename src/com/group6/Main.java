package com.group6;

import com.group6.btoproject.*;
import com.group6.users.*;
import com.group6.utils.BashColors;
import com.group6.views.MenuView;
import com.group6.views.ViewContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    /**
     * Entry to the application.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        System.out.println("=========================");
        System.out.println(
                BashColors.format("IF YOU SEE LOADING ERRORS, MAKE SURE THE USER FILES EXIST WITHIN " + System.getProperty("user.dir"), BashColors.BOLD)
        );
        System.out.println("=========================");
        System.out.println("");
        final UserStorage userStorage = new UserStorage(
                "applicants.txt",
                "officers.txt",
                "managers.txt"
        );
        final BTOProjectManager projectManager = new BTOProjectManager();
        final UserManager userManager = new UserManager(userStorage);
        userManager.setUsers(userStorage.loadAllUsers());
        final BTOSystem btoSystem = new BTOSystem(projectManager, userManager);

        loadDefaults(btoSystem);

        final ViewContext ctx = new ViewContext(btoSystem, new Scanner(System.in));
        User user = btoSystem.getUsers().getUser("d608792e-9778-4708-a813-c1cf7127546d").get();
        BTOProject project = btoSystem.getProjects().getProject("hello world").get();
        BTOEnquiry enquiry = BTOEnquiry.create(
                new BTOEnquiryMessage(user.getId(), "Hello world!"),
                new BTOEnquiryMessage(user.getId(), "Responded!")
        );
        project.addEnquiry(
                enquiry
        );
        ctx.startFromView(new MenuView());

        // Save on close
        userStorage.saveAllUsers(userManager.getUsers());
    }

    // TEMPORARY! We can conaider loading from file in the futuer.
    public static void loadDefaults(BTOSystem btoSystem) {
        final UserManager userManager = btoSystem.getUsers();
        final BTOProjectManager btoProjectManager = btoSystem.getProjects();

        User jessica = userManager.getUser("73cc8ce3-60f0-4d0b-9d2e-a91fecf91ded").get();
        User daniel = userManager.getUser("b4b3f882-2f6a-44d5-9472-2b8c21568524").get();
        User emily = userManager.getUser("c72a0ad1-a32a-466a-b6c6-5c9fe535a8f3").get();
        User sarah = userManager.getUser("e7c709b7-22a4-411a-9ec1-c191b7fc729b").get();

        // Adding Project 1
        BTOProject acaciaBreezeYishun = new BTOProject("hello world", jessica.getId());
        acaciaBreezeYishun.setName("Acacia Breeze");
        acaciaBreezeYishun.setNeighbourhood("Yishun");
        acaciaBreezeYishun.addProjectType(new BTOProjectType(BTOProjectTypeID.S_2_ROOM, 350_000, 2));
        acaciaBreezeYishun.addProjectType(new BTOProjectType(BTOProjectTypeID.S_3_ROOM, 450_000, 3));
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

        BTOProject someBtoProject = new BTOProject(UUID.randomUUID().toString(), jessica.getId());
        someBtoProject.setName("Some BTO Project");
        someBtoProject.setNeighbourhood("Palau NTU");
        someBtoProject.addProjectType(new BTOProjectType(BTOProjectTypeID.S_2_ROOM, 350_000, 2));
        someBtoProject.addProjectType(new BTOProjectType(BTOProjectTypeID.S_3_ROOM, 450_000, 2));

        someBtoProject.setApplicationWindow(
                Date.from(LocalDate.of(2025, 2, 15).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.of(2025, 4, 20).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        someBtoProject.setOfficerLimit(3);

        btoProjectManager.addProject(someBtoProject);
        btoProjectManager.requestApply(someBtoProject.getId(), sarah.getId(), BTOProjectTypeID.S_3_ROOM);
        btoProjectManager.requestWithdrawApplication(someBtoProject.getId(), someBtoProject.getActiveApplication(sarah.getId()).get().getId());
        btoProjectManager.requestRegisterOfficer(someBtoProject.getId(), emily.getId());
        btoProjectManager.transitionOfficerRegistrationStatus(someBtoProject.getId(), emily.getId(), HDBOfficerRegistrationStatus.SUCCESSFUL);

    }

    // public static List<User> parseUserCSV(String filePath) {
    // List<User> users = new LinkedList<>();
    // String line;
    //
    // try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
    // // Skip the header line
    // line = br.readLine();
    //
    // while ((line = br.readLine()) != null) {
    // // Split the line by comma
    // String[] data = line.split(",");
    //
    // if (data.length >= 5) {
    // String name = data[0].trim();
    // String nric = data[1].trim();
    // int age = Integer.parseInt(data[2].trim());
    // String maritalStatus = data[3].trim();
    // String password = data[4].trim();
    //
    // User user = new User(name, nric, password);
    // users.add(user);
    // }
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // } catch (NumberFormatException e) {
    // System.err.println("Error parsing age: " + e.getMessage());
    // }
    //
    // return users;
    // }

}