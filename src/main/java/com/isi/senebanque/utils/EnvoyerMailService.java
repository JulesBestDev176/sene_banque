package com.isi.senebanque.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnvoyerMailService {
    private static final Logger LOGGER = Logger.getLogger(EnvoyerMailService.class.getName());
    private static final String SMTP_HOST = "sandbox.smtp.mailtrap.io";
    private static final String SMTP_PORT = "2525";
    private static final String EMAIL_FROM = "fallbanque@gmail.com";
    private static final String EMAIL_USERNAME = "acdab68a92f4a4";
    private static final String EMAIL_PASSWORD = "bc723db9f21b4b";

    public static void sendEmail(String to, String subject, String body) {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            LOGGER.info("E-mail envoyé avec succès à " + to);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi de l'e-mail", e);
            if (e instanceof AuthenticationFailedException) {
                LOGGER.severe("Échec d'authentification : Vérifiez vos identifiants Mailtrap");
            }
        }
    }

    public static boolean sendEmailSafe(String to, String subject, String body) {
        try {
            sendEmail(to, subject, body);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de l'envoi de l'email", e);
            return false;
        }
    }


    public static void envoyerMailBienvenue(String email, String nom, String prenom, String username, String password) {
        String subject = "Bienvenue à SeneBanque " + prenom + " " + nom;
        String body = "Cher(e) " + prenom + " " + nom + ",\n\n"
                + "Nous vous souhaitons la bienvenue à SeneBanque. Votre compte a été créé avec succès.\n\n"
                + "Vous pouvez dès maintenant accéder à nos services en ligne.\n\n"
                + "Pour vous connecter, vous pouvez utiliser votre username : " + username + " et votre password : " + password +"\n"
                + "Cordialement,\n"
                + "L'équipe SeneBanque";

        sendEmail(email, subject, body);
    }


    public static void envoyerMailDesactivation(String email, String nom, String prenom) {
        String subject = "Désactivation de votre compte SeneBanque";
        String body = "Cher(e) " + prenom + " " + nom + ",\n\n"
                + "Nous vous informons que votre compte SeneBanque a été désactivé.\n\n"
                + "Si vous pensez qu'il s'agit d'une erreur ou si vous souhaitez réactiver votre compte, "
                + "veuillez contacter notre service client.\n\n"
                + "Cordialement,\n"
                + "L'équipe SeneBanque";

        sendEmail(email, subject, body);
    }


    public static void envoyerMailReactivation(String email, String nom, String prenom) {
        String subject = "Réactivation de votre compte SeneBanque";
        String body = "Cher(e) " + prenom + " " + nom + ",\n\n"
                + "Nous vous informons que votre compte SeneBanque a été réactivé avec succès.\n\n"
                + "Vous pouvez dès maintenant accéder à nouveau à nos services en ligne.\n\n"
                + "Cordialement,\n"
                + "L'équipe SeneBanque";

        sendEmail(email, subject, body);
    }
}
