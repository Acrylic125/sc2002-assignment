package com.group6.btoproject;

import com.group6.utils.BashColors;
import com.group6.utils.Storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Storage class for {@link BTOBookingReceipt} objects.
 */
public class BTOBookingReceiptStorage implements Storage<BTOBookingReceipt> {

    private final String filename;

    /**
     * Constructor for BTOBookingReceiptStorage.
     *
     * @param filename The name of the file to store the booking receipts.
     */
    public BTOBookingReceiptStorage(String filename) {
        this.filename = filename;
    }

    /**
     * Loads all booking receipts from the file.
     *
     * @return A list of booking receipts.
     */
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
        } else {
            System.out.println(BashColors.format("[Booking Receipts] No data found in " + filename, BashColors.RED));
        }

        return receipts;
    }

    /**
     * Saves all booking receipts to the file.
     *
     * @param data The list of booking receipts to save.
     */
    @Override
    public void saveAll(List<BTOBookingReceipt> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error saving booking receipts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves a single booking receipt to the file. If a receipt with the same ID
     * already exists, it updates that receipt.
     *
     * @param data The booking receipt to save.
     */
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
