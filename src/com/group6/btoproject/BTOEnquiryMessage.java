package com.group6.btoproject;

import java.io.Serializable;

/**
 * Represents a {@link BTOEnquiry} message for sender and response.
 */
public class BTOEnquiryMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String senderUserId;
    private final String message;

    /**
     * Constructor for Message.
     *
     * @param senderUserId sender user id.
     * @param message      message content.
     */
    public BTOEnquiryMessage(String senderUserId, String message) {
        this.senderUserId = senderUserId;
        this.message = message;
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

}
