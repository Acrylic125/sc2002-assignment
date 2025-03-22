package com.group6;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.btoproject.HDBOfficerRegistrationStatus;
import com.group6.users.Applicant;
import com.group6.users.UserManager;
import com.group6.users.UserMartialStatus;
import com.group6.views.ExampleView;
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
        final BTOSystem btoSystem = new BTOSystem();

        loadDefaults(btoSystem);

        final ViewContext ctx = new ViewContext(btoSystem, new Scanner(System.in));
        ctx.pushView(new ExampleView());
        System.out.println("App closed!");
    }

    // TEMPORARY! We can conaider loading from file in the futuer.
    public static void loadDefaults(BTOSystem btoSystem) {
        final UserManager userManager = btoSystem.getUsers();
        final BTOProjectManager btoProjectManager = btoSystem.getProjects();

        // Adding John
        Applicant john = new Applicant(UUID.randomUUID().toString(), "S1234567A", "password");
        john.setName("John");
        john.setAge(35);
        john.setMartialStatus(UserMartialStatus.SINGLE);
        userManager.addUser(john);

        // Adding Sarah
        Applicant sarah = new Applicant(UUID.randomUUID().toString(), "T7654321B", "password");
        sarah.setName("Sarah");
        sarah.setAge(40);
        sarah.setMartialStatus(UserMartialStatus.MARRIED);
        userManager.addUser(sarah);

        // Adding Grace
        Applicant grace = new Applicant(UUID.randomUUID().toString(), "S9876543C", "password");
        grace.setName("Grace");
        grace.setAge(37);
        grace.setMartialStatus(UserMartialStatus.MARRIED);
        userManager.addUser(grace);

        // Adding James
        Applicant james = new Applicant(UUID.randomUUID().toString(), "T2345678D", "password");
        james.setName("James");
        james.setAge(30);
        james.setMartialStatus(UserMartialStatus.MARRIED);
        userManager.addUser(james);

        // Adding Rachel
        Applicant rachel = new Applicant(UUID.randomUUID().toString(), "S3456789E", "password");
        rachel.setName("Rachel");
        rachel.setAge(25);
        rachel.setMartialStatus(UserMartialStatus.SINGLE);
        userManager.addUser(rachel);

        // Adding Daniel
        Applicant daniel = new Applicant(UUID.randomUUID().toString(), "T2109876H", "password");
        daniel.setName("Daniel");
        daniel.setAge(36);
        daniel.setMartialStatus(UserMartialStatus.SINGLE);
        userManager.addUser(daniel);

        // Adding Emily
        Applicant emily = new Applicant(UUID.randomUUID().toString(), "S6543210I", "password");
        emily.setName("Emily");
        emily.setAge(28);
        emily.setMartialStatus(UserMartialStatus.SINGLE);
        userManager.addUser(emily);

        // Adding David
        Applicant david = new Applicant(UUID.randomUUID().toString(), "T1234567J", "password");
        david.setName("David");
        david.setAge(29);
        david.setMartialStatus(UserMartialStatus.MARRIED);
        userManager.addUser(david);

        // Adding Michael
        Applicant michael = new Applicant(UUID.randomUUID().toString(), "T8765432F", "password");
        michael.setName("Michael");
        michael.setAge(36);
        michael.setMartialStatus(UserMartialStatus.SINGLE);
        userManager.addUser(michael);

        // Adding Jessica
        Applicant jessica = new Applicant(UUID.randomUUID().toString(), "S5678901G", "password");
        jessica.setName("Jessica");
        jessica.setAge(26);
        jessica.setMartialStatus(UserMartialStatus.MARRIED);
        userManager.addUser(jessica);

        // Adding Project 1
        BTOProject acaciaBreezeYishun = new BTOProject(UUID.randomUUID().toString(), jessica.getId());
        acaciaBreezeYishun.setName("Acacia Breeze");
        acaciaBreezeYishun.setNeighbourhood("Yishun");
        acaciaBreezeYishun.addProjectType(new BTOProjectType("2 Room", 350_000, 2));
        acaciaBreezeYishun.addProjectType(new BTOProjectType("3 Room", 450_000, 3));
        LocalDate start = LocalDate.of(2025, 2, 15);
        LocalDate end = LocalDate.of(2025, 3, 20);

        acaciaBreezeYishun.setApplicationWindow(
                Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        acaciaBreezeYishun.setOfficerLimit(3);
        acaciaBreezeYishun.requestRegisterOfficer(daniel.getId());
        acaciaBreezeYishun.requestRegisterOfficer(emily.getId());
        acaciaBreezeYishun.transitionOfficerRegistrationStatus(daniel.getId(), HDBOfficerRegistrationStatus.SUCCESSFUL);
        acaciaBreezeYishun.transitionOfficerRegistrationStatus(emily.getId(), HDBOfficerRegistrationStatus.SUCCESSFUL);
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
