package com.group6.views.applicant;

import com.group6.users.User;
import com.group6.views.AuthenticatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.Optional;
import java.util.Scanner;

public class ProjectsViewFiltersView implements AuthenticatedView {

    private ViewContext ctx;
    private User user;

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;
        return null;
    }
}
