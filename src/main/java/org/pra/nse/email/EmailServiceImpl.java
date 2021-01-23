package org.pra.nse.email;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDate;

//@Profile("prod")
//@Profile("!dev")
@Component
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender emailSender;

    public EmailServiceImpl(JavaMailSender emailSender) {
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
        //TODO use cross cutting to send emails
        LocalDate fileForDate = DateUtils.getLocalDateFromPath(pathToAttachment);
        if(fileForDate == null) {
            fileForDate = DateUtils.getLocalDateFromPath(pathToAttachment, NseCons.AB_FILE_NAME_DATE_REGEX, NseCons.AB_FILE_NAME_DATE_FORMAT);
        }
        if (!ApCo.EMAIL_ENABLED ) {
            LOGGER.info("Mailing | disabled, mailing requests ignored");
            return;
        } else if (to.contains("manish") && !ApCo.EMAIL_ENABLED_FOR_MANISH) {
            LOGGER.info("Mailing | disabled for Manish, mailing requests ignored");
            return;
        } else if (to.contains("pradeep") && !ApCo.EMAIL_ENABLED_FOR_PRADEEP) {
            LOGGER.info("Mailing | disabled for Pradeep, mailing requests ignored");
            return;
        } else if (to.contains("shweta") && !ApCo.EMAIL_ENABLED_FOR_SHUVI) {
            LOGGER.info("Mailing | disabled for Shuvi, mailing requests ignored");
            return;
        } else if(fileForDate.compareTo(ApCo.EMAIL_FROM_DATE) < 0) {
            LOGGER.info("Mailing | skipped, file is for younger date then configured - {}", ApCo.EMAIL_FROM_DATE);
            return;
        }

        LOGGER.info("Mailing | Subject=[{}], To=[{}], Path=[{}]", subject, to, pathToAttachment);
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
            emailSender.send(message);
            LOGGER.info("Mailed  | Successfully - {}", outputFileName == null ? subject : outputFileName);
        } catch (MessagingException e) {
            LOGGER.error("Mailing | Error {}", e);
        }
    }

}
