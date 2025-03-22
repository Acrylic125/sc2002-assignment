package com.group6.btoproject;

import java.util.Optional;
import java.util.UUID;

/**
 * BTO Enquiry class.
 * Conversation in a 1-1 QnA.
 * Message and response.
 * {@link BTOEnquiryMessage} message for sender and response.
 */
public class BTOEnquiry {
    /**
     *
     * @param senderMessage sender message
     * @param responseMessage response message
     * @return a BTOEnquiry instance
     */
    public static BTOEnquiry create(BTOEnquiryMessage senderMessage, BTOEnquiryMessage responseMessage) {
        return new BTOEnquiry(UUID.randomUUID().toString(), senderMessage, responseMessage);
    }

    private final String id;
    private BTOEnquiryMessage senderMessage;
    private BTOEnquiryMessage responseMessage;

    /**
     * Constructor for Enquiry
     *
     * @param id              id of the enquiry.
     * @param senderMessage   sender message.
     * @param responseMessage response message. NULLABLE!
     */
    private BTOEnquiry(String id, BTOEnquiryMessage senderMessage, BTOEnquiryMessage responseMessage) {
        this.id = id;
        this.senderMessage = senderMessage;
        this.responseMessage = responseMessage;
    }

    /**
     * Id getter
     *
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * SenderMessage getter
     *
     * @return {@link #senderMessage}
     */
    public BTOEnquiryMessage getSenderMessage() {
        return senderMessage;
    }

    /**
     *
     * @param senderMessage sender message
     */
    public void setSenderMessage(BTOEnquiryMessage senderMessage) {
        this.senderMessage = senderMessage;
    }

    /**
     * ResponseMessage getter
     *
     * @return {@link #responseMessage}
     */
    public Optional<BTOEnquiryMessage> getResponseMessage() {
        return Optional.ofNullable(responseMessage);
    }

    /**
     *
     * @param responseMessage response message. NULLABLE!
     */
    public void setResponseMessage(BTOEnquiryMessage responseMessage) {
        this.responseMessage = responseMessage;
    }
}
