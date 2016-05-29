package providers;

import models.User;
import models.enums.TypeUser;
import models.service.UserService;
import play.Play;
import play.i18n.Messages;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import views.html.emails.*;

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
        email.setSubject(Messages.get("email.reset-password.title"));
        email.setFrom(emailInfo);
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyHtml(resetPassword.render(user, url).body());
        email.setBodyText(Messages.get("email.reset-password.body-text", url + "/reset/password/" + user.lostPassToken));
        return mailerClient.send(email);
    }

    /**
     * Send the email for reset the password
     *
     * @param user User
     *
     * @return String
     */
    public String sendChangedPassword(User user) {
        Email email = new Email();
        email.setSubject(Messages.get("email.changed-password.title"));
        email.setFrom(emailInfo);
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyHtml(changedPassword.render(url).body());
        email.setBodyText(Messages.get("email.changed-password.body-text", url + "/login"));
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
        email.setSubject(Messages.get("email.registration.title"));
        email.setFrom(emailInfo);
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyHtml(registration.render(user, url).body());
        email.setBodyText(Messages.get("email.registration.body-text", url + "/active/" + user.validationEmailToken));
        emails.add(mailerClient.send(email));

        email = new Email();
        email.setSubject(Messages.get("email.new-user.title"));
        email.setFrom(emailInfo);
        email.setBodyHtml(newUser.render(user).body());
        email.setBodyText(Messages.get("email.new-user.body-text", user.username, user.email, user.getFullName()));
        for (User admin : UserService.where("type", TypeUser.ADMIN.toString())) {
            email.addTo(admin.firstName + " " + admin.lastName + " <" + admin.email + ">");
        }
        emails.add(mailerClient.send(email));

        return emails;
    }

    /**
     * Send the email when an user actives the account
     *
     * @param user User
     *
     * @return String
     */
    public  List<String> sendAccountActivated(User user) {
        List<String> emails = new ArrayList<String>();
        Email email = new Email();
        email.setSubject(Messages.get("email.activation.title"));
        email.setFrom(emailInfo);
        email.addTo(user.firstName + " " + user.lastName + " <" + user.email + ">");
        email.setBodyHtml(activation.render(url).body());
        email.setBodyText(Messages.get("email.activation.body-text"));
        emails.add(mailerClient.send(email));

        email = new Email();
        email.setSubject(Messages.get("email.new-user-activated.title"));
        email.setFrom(emailInfo);
        email.setBodyHtml(newUserActivated.render(user).body());
        email.setBodyText(Messages.get("email.new-user-activated.body-text", user.username, user.email, user.getFullName()));
        for (User admin : UserService.where("type", TypeUser.ADMIN.toString())) {
            email.addTo(admin.firstName + " " + admin.lastName + " <" + admin.email + ">");
        }
        emails.add(mailerClient.send(email));

        return emails;
    }

}
