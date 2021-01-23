package org.pra.nse.email;

import org.pra.nse.ApCo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Profile("dev")
@Component
public class EmailServiceMock implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceMock.class);

    private final JavaMailSender emailSender;

    public EmailServiceMock(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template, String... templateArgs) {
        String text = String.format(template.getText(), (Object[]) templateArgs);
        sendSimpleMessage(to, subject, text);
    }

    @Override
    public void sendAttachmentMessage(String to, String subject, String text, String pathToAttachment, String outputFileName) {
        LOGGER.info("Mailing | Subject=[{}], Path=[{}]", subject, pathToAttachment);
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            //dynamic file being attached
            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment(subject, file);

            //static file being attached
            if(outputFileName != null) {
                String dateString = "-"+subject.replace(outputFileName+"-","").replace(ApCo.REPORTS_FILE_EXT,"");
                String staticFileNameWithPath = pathToAttachment.replace(dateString,"");
                FileSystemResource staticFile = new FileSystemResource(new File(staticFileNameWithPath));
                helper.addAttachment(outputFileName + ApCo.REPORTS_FILE_EXT, staticFile);
            }

            //--
            //emailSender.send(message);
            LOGGER.info("Mailed  | Successfully - {}", outputFileName);
        } catch (MessagingException e) {
            LOGGER.error("Mailing | Error {}", e);
        }

    }

}
