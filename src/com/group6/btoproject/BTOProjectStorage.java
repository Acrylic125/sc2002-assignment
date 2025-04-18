package com.group6.btoproject;

import com.group6.utils.BashColors;
import com.group6.utils.Storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BTOProjectStorage implements Storage<BTOProject> {

    private final String filename;

    public BTOProjectStorage(String filename) {
        this.filename = filename;
    }

    @Override
    public List<BTOProject> loadAll() {
        File file = new File(filename);
        List<BTOProject> projects = new ArrayList<>();

        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List<?>) {
                    List<?> list = (List<?>) obj;
                    for (Object item : list) {
                        if (item instanceof BTOProject) {
                            projects.add((BTOProject) item);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(BashColors.format("Error loading data: " + e.getMessage(), BashColors.RED));
            }
        } else {
            System.out.println(BashColors.format("[BTO Projects] No data found in " + filename, BashColors.RED));
        }

        return projects;
    }

    @Override
    public void saveAll(List<BTOProject> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println(BashColors.format("Error saving data: " + e.getMessage(), BashColors.RED));
        }
    }

    @Override
    public void save(BTOProject data) {
        List<BTOProject> existingProjects = loadAll();

        // Check if the project with the same ID already exists
        boolean updated = false;
        for (int i = 0; i < existingProjects.size(); i++) {
            if (existingProjects.get(i).getId().equals(data.getId())) {
                existingProjects.set(i, data);
                updated = true;
                break;
            }
        }

        // If not found, add it as a new project
        if (!updated) {
            existingProjects.add(data);
        }

        saveAll(existingProjects);
    }
}
