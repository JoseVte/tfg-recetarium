package providers;

import models.User;
import models.enums.TypeUser;
import models.service.UserService;
import play.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import views.html.emails.newUser;
import views.html.emails.registration;
import views.html.emails.resetPassword;

import java.util.ArrayList;
import java.util.List;

public class EmailService {
    private final String emailInfo = "Recetarium <info@recetarium.com>";
    private final String url = Play.application().configuration().getString("frontend.url");
    private MailerClient mailerClient;

    public EmailService(play.libs.mailer.MailerClient mailer) {
        mailerClient = mailer;
    }

    /**
     * Send the email for reset the password
     *
     * @param user User
     *
     * @return String
     */
    public String sendVerificationToken(User user) {
        Email email = new Email();
        email.setSubject("Reset password");
        email.setFrom(emailInfo);
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyHtml(resetPassword.render(user, url).body());
        email.setBodyText("For reset your password click here: " + user.lostPassToken);
        return mailerClient.send(email);
    }

    /**
     * Send the email to the new user and the admins
     *
     * @param user User
     *
     * @return String
     */
    public List<String> sendRegistrationEmails(User user) {
        List<String> emails = new ArrayList<String>();
        Email email = new Email();
        email.setSubject("Welcome to Recetarium");
        email.setFrom(emailInfo);
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyHtml(registration.render(user, url).body());
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
