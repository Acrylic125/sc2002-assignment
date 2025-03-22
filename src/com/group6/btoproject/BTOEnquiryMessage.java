package com.group6.btoproject;

/**
 * Represents a {@link BTOEnquiry} message for sender and response.
 */
public class BTOEnquiryMessage {

    private final String message;
    private final String senderUserId;

    /**
     * Constructor for Message
     *
     * @param message      message content.
     * @param senderUserId sender user id.
     */
    public BTOEnquiryMessage(String message, String senderUserId) {
        this.message = message;
        this.senderUserId = senderUserId;
    }

    /**
     * Message getter
     *
     * @return {@link #message}
     */
    public String getMessage() {
        return message;
    }

    /**
     * SenderUserId getter
     *
     * @return {@link #senderUserId}
     */
    public String getSenderUserId() {
        return senderUserId;
    }

}
