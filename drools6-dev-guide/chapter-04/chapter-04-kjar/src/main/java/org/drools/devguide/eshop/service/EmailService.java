package org.drools.devguide.eshop.service;

import java.util.ArrayList;
import java.util.List;

public class EmailService {

    private List<String> sentMessages = new ArrayList<String>();
    
    public void sendEmail(String message) {
        System.out.println("email service received a message: " + message);
        sentMessages.add(message);
    }
    
    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }
}
