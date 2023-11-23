package com.Arjunagi.DoctorApp.services;



import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
@Service
 class MailHandler {
    private static final String from="abdul123arj@gmail.com";
    @Async
     void sendMail(String to,String subject,String message)  {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host","smtp.gmail.com"); //smtp server
        properties.put("mail.smtp.port","465"); //server port
        properties.put("mail.smtp.ssl.enable","true"); //ssl -secure socket layer
        properties.put("mail.smtp.auth","true"); //authentication - auth

        Authenticator mailAut=new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(from, "majravydpzojegwo");
            }
        };

        Session mailSession = Session.getInstance(properties,mailAut);
        MimeMessage mailMessage = new MimeMessage(mailSession);

        try {
            mailMessage.setFrom(from);
            mailMessage.setSubject(subject);
            mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        //    mailMessage.setText(message);
            mailMessage.setContent(message,"text/html");
            Transport.send(mailMessage);
            System.out.println("Mail sent !!!");
        }
        catch(Exception ex)
        {
            System.out.println("Some error while preparing mail body!!!!");
            System.out.println(ex);

        }
    }

}
