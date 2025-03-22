package com.group6.btoproject;

/**
 * BTO Enquiry class.
 * Conversation in a 1-1 QnA.
 * Message and response.
 * {@link BTOEnquiryMessage} message for sender and response.
 */
public class BTOEnquiry {

    private final String id;
    private BTOEnquiryMessage senderMessage;
    private BTOEnquiryMessage responseMessage;

    /**
     * Constructor for Enquiry
     *
     * @param id              id of the enquiry.
     * @param senderMessage   sender message.
     * @param responseMessage response message.
     */
    public BTOEnquiry(String id, BTOEnquiryMessage senderMessage, BTOEnquiryMessage responseMessage) {
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
     * ResponseMessage getter
     *
     * @return {@link #responseMessage}
     */
    public BTOEnquiryMessage getResponseMessage() {
        return responseMessage;
    }

}
