package edu.sinhgad.submitassignmentsit;

import android.content.Context;
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

    public static final  String EMAIL = "vivek@submitassignmentsit.xyz";
    public static final String PASSWORD = "qKavPQF8XjDT";

    private Context context;

    private Session session;
    private String email, userName;

    public SendMail(Context context, String email, String userName) {
        this.context = context;
        this.email = email;
        this.userName = userName;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String message = "Hi " + userName + ". \n\nI hope you are having a good day.\n\nThis is Vivek Jaiswal from Submit Assignment SIT." +
                "\nJust wanted to let you know that if you encounter any error please report the error using the Support option provided within the app" +
                " or write an email to support@submitassignmentsit.xyz. Our team will try to resolve the issue as soon as possible." +
                " By reporting the error you will not only get your issue resolved but also help us to make our app run smoothly and error free." +
                "\n\nKind Regards, \nVivek Jaiswal \nSubmit Assignment SIT";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.zoho.in");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");

        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress("Vivek Jaiswal <vivek@submitassignmentsit.xyz>"));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
            mimeMessage.setSubject("Welcome to Submit Assignment SIT.");
            mimeMessage.setText(message);
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
//            e.printStackTrace();
        }

        return null;

    }

}