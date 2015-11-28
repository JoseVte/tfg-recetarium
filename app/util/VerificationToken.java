package util;

import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;

import play.Play;

public class VerificationToken {
    private static final int DEFAULT_EXPIRY_TIME_IN_MINS = Play.application().configuration().getInt("lostPassword.expiry.minutes");
    private final String token;
    private Date expiryDate;
    private boolean verified;
    public VerificationToken() {
        this.token = UUID.randomUUID().toString();
        this.expiryDate = calculateExpiryDate(DEFAULT_EXPIRY_TIME_IN_MINS);
    }

    public VerificationToken(String token, Date expire) {
        this.token = token;
        this.expiryDate = expire;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getToken() {
        return token;
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        DateTime now = new DateTime();
        return now.plusMinutes(expiryTimeInMinutes).toDate();
    }

    public boolean hasExpired() {
        DateTime tokenDate = new DateTime(getExpiryDate());
        return tokenDate.isBeforeNow();
    }
}