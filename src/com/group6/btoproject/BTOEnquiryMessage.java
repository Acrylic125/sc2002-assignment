package com.group6.btoproject;

import java.io.Serializable;

/**
 * Represents a {@link BTOEnquiry} message for sender and response.
 */
public class BTOEnquiryMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String senderUserId;
    private final String message;
    private final long lastUpdated;

    /**
     * Constructor for Message.
     *
     * @param senderUserId sender user id.
     * @param message      message content.
     * @param lastUpdated  timestamp of when the message was last updated.
     */
    public BTOEnquiryMessage(String senderUserId, String message, long lastUpdated) {
        this.senderUserId = senderUserId;
        this.message = message;
        this.lastUpdated = lastUpdated;
    }

    /**
     * SenderUserId getter.
     *
     * @return {@link #senderUserId}
     */
    public String getSenderUserId() {
        return senderUserId;
    }

    /**
     * Message getter.
     *
     * @return {@link #message}
     */
    public String getMessage() {
        return message;
    }

    /**
     * LastUpdated getter.
     *
     * @return {@link #lastUpdated}
     */
    public long getLastUpdated() {
        return lastUpdated;
    }
}
