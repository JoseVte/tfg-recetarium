package models.service;

import models.User;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import util.VerificationToken;

public class EmailService {
    
    final String username = "jvortsromero@gmail.com";
    final String password = "josevte1";
    private MailerClient mailerClient;

    public EmailService(play.libs.mailer.MailerClient mailer) {
        mailerClient = mailer;
    }

    public void sendVerificationToken(User user, VerificationToken token) {
        Email email = new Email();
        email.setSubject("Reset password");
        email.setFrom("Recetarium <info@recetarium.com>");
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyText("For reset your password click here: " + user.lostPassToken);
        System.out.println(email.toString());
        String id = mailerClient.send(email);
        System.out.println(id);
    }

}
