package org.pra.nse.email;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
    void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template, String ...templateArgs);
    void sendAttachmentMessage(String to, String subject, String text, String pathToAttachment, String outputFileName);
}
