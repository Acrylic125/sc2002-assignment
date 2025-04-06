package com.group6.views;

import com.group6.users.User;
import com.group6.users.UserPermissions;
import com.group6.utils.BashColors;
import com.group6.views.applicant.ApplicantHomeView;
import com.group6.views.hdbofficer.*;

import java.util.Scanner;

public class RoleBasedHomeView implements AuthenticatedView {

    @Override
    public View render(ViewContext ctx, User user) {
        UserPermissions userPermissions = user.getPermissions();
        if (userPermissions.canManageProjects() && userPermissions.canApply()) {
            return new HDBOfficerHomeView(true).render(ctx);
        }
        if (userPermissions.canManageProjects()) {
            return new HDBOfficerManageView(true).render(ctx);
        }
        if (userPermissions.canApply()) {
            return new ApplicantHomeView(true).render(ctx);
        }

        final Scanner scanner = ctx.getScanner();
        System.out.println(BashColors.format("Hmmm looks like you have no permission to do anything. You will be redirected back to the login page.", BashColors.RED));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
        return null;
    }

}
