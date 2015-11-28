package models.service;

import models.User;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import util.VerificationToken;
import views.html.emails.*;

public class EmailService {
    private MailerClient mailerClient;

    public EmailService(play.libs.mailer.MailerClient mailer) {
        mailerClient = mailer;
    }

    /**
     * Send the email for reset the password
     *
     * @param user
     */
    public void sendVerificationToken(User user) {
        Email email = new Email();
        email.setSubject("Reset password");
        email.setFrom("Recetarium <info@recetarium.com>");
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        String body = resetPassword.render(user).body();
        email.setBodyHtml(body);
        email.setBodyText("For reset your password click here: " + user.lostPassToken);
        String id = mailerClient.send(email);
    }

}
