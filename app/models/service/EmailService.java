package models.service;

import java.util.ArrayList;
import java.util.List;

import models.TypeUser;
import models.User;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import views.html.emails.*;

public class EmailService {
    private MailerClient mailerClient;
    private final String emailInfo = "Recetarium <info@recetarium.com>";

    public EmailService(play.libs.mailer.MailerClient mailer) {
        mailerClient = mailer;
    }

    /**
     * Send the email for reset the password
     *
     * @param user
     *
     * @return String
     */
    public String sendVerificationToken(User user) {
        Email email = new Email();
        email.setSubject("Reset password");
        email.setFrom(emailInfo);
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyHtml(resetPassword.render(user).body());
        email.setBodyText("For reset your password click here: " + user.lostPassToken);
        return mailerClient.send(email);
    }

    /**
     * Send the email to the new user and the admins
     *
     * @param user
     *
     * @return String
     */
    public List<String> sendRegistrationEmails(User user) {
        List<String> emails = new ArrayList<String>();
        Email email = new Email();
        email.setSubject("Welcome to Recetarium");
        email.setFrom(emailInfo);
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyHtml(registration.render(user).body());
        emails.add(mailerClient.send(email));

        email = new Email();
        email.setSubject("New user registered");
        email.setFrom(emailInfo);
        email.setBodyHtml(newUser.render(user).body());
        for (User admin : UserService.where("type", TypeUser.ADMIN.toString())) {
            email.addTo(admin.firstName + " " + admin.lastName + " <" + admin.email + ">");
        }
        emails.add(mailerClient.send(email));

        return emails;
    }

}
