package com.group6.btoproject;

import com.group6.utils.Storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BTOBookingReceiptStorage implements Storage<BTOBookingReceipt> {

    private final String filename;

    public BTOBookingReceiptStorage(String filename) {
        this.filename = filename;
    }

    @Override
    public List<BTOBookingReceipt> loadAll() {
        File file = new File(filename);
        List<BTOBookingReceipt> receipts = new ArrayList<>();

        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List<?>) {
                    List<?> list = (List<?>) obj;
                    for (Object item : list) {
                        if (item instanceof BTOBookingReceipt) {
                            receipts.add((BTOBookingReceipt) item);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading booking receipts: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return receipts;
    }

    @Override
    public void saveAll(List<BTOBookingReceipt> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error saving booking receipts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void save(BTOBookingReceipt data) {
        List<BTOBookingReceipt> existingReceipts = loadAll();

        // Check if the receipt with the same ID already exists
        boolean updated = false;
        for (int i = 0; i < existingReceipts.size(); i++) {
            if (existingReceipts.get(i).getId().equals(data.getId())) {
                existingReceipts.set(i, data);
                updated = true;
                break;
            }
        }

        // If not found, add it as a new receipt
        if (!updated) {
            existingReceipts.add(data);
        }

        saveAll(existingReceipts);
    }
}
