package edu.sinhgad.submitassignmentsit;

import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask<Void, Void, Void> {

    private Session session;
    private String toEmail, userName, message, fromEmail, password, subject, setFrom;

    public SendMail(String toEmail, String userName, String message, String fromEmail, String password, String subject) {
        this.toEmail = toEmail;
        this.userName = userName;
        this.message = message;
        this.fromEmail = fromEmail;
        this.password = password;
        this.subject = subject;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String completeMessage = "Hi " + userName + "," + message;
        if(fromEmail.equals("vivek@submitassignmentsit.xyz")) {
            setFrom = "Vivek Jaiswal <vivek@submitassignmentsit.xyz>";
        } else if(fromEmail.equals("support@submitassignmentsit.xyz")) {
            setFrom = "SAS support <support@submitassignmentsit.xyz>";
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.zoho.in");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");

        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(setFrom));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(toEmail)));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(completeMessage);
            Transport.send(mimeMessage);
        } catch (MessagingException ignored) {}

        return null;

    }

}