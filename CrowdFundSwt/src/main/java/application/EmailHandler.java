package application;

import dbadapter.Donation;
import dbadapter.Project;
import interfaces.IEmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * This class handles sending emails for the application.
 *
 * <p>SMTP username, password, hostname and port has to be set in resources/email.properties file.
 *
 * @author kt
 */
public class EmailHandler implements IEmail {
  private Session session = null;

  public EmailHandler() {
    Properties prop = new Properties();
    Properties myprop = new Properties();
    try {
      myprop.load(
          Thread.currentThread().getContextClassLoader().getResourceAsStream("email.properties"));
      prop.put("mail.smtp.auth", true);
      prop.put("mail.smtp.starttls.enable", "true");
      prop.put("mail.smtp.host", myprop.getProperty("host"));
      prop.put("mail.smtp.ssl.trust", myprop.getProperty("host"));
      prop.put("mail.smtp.port", myprop.getProperty("port"));

      session =
          Session.getInstance(
              prop,
              new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(
                      myprop.getProperty("username"), myprop.getProperty("password"));
                }
              });
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /** Sends a confirmation mail to the project starter of the given project. */
  @Override
  public void sendConfmEmailFR(Project pj) throws MessagingException {
    if (pj.getHash() == null || pj.getHash().isEmpty()) return;

    String url = "http://localhost:8080/confirmfr?h=" + pj.getHash();
    System.out.println("Please confirm the project funding request via the following url:\n" + url);
    sendmail(
        pj.getPsemail(),
        "Confirm Project Creation",
        "Dear "
            + pj.getPsname()
            + ",\n\nPlease confirm the project funding request via the following url:\n"
            + url
            + "\n\nThank you");
  }

  @Override
  public void sendConfmEmailDon(Donation don) throws MessagingException {
    if (don.getHash() == null || don.getHash().isEmpty()) return;

    String url = "http://localhost:8080/confirmdon?h=" + don.getHash();
    System.out.println("Please confirm the project donation request via the following url:\n" + url);
    sendmail(
        don.getSemail(),
        "Confirm Project Donation",
        "Dear "
            + don.getSname()
            + ",\n\nPlease confirm the donation request via the following url:\n"
            + url
            + "\n\nThank you");
  }

  @Override
  public void sendSuccessfulProjectStatus(Project pj, List<Donation> dons) throws MessagingException {
    sendmail(
        pj.getPsemail(),
        "Project Status",
        "Dear "
            + pj.getPsname()
            + ",\n\nThis email is to inform you that your project '"
            + pj.getTitle()
            + "' is successful. You will receive donated money in a short while"
            + "\n\nthank you.");

    for (Donation d : dons) {
        sendmail(d.getSemail(), "Donated Project Status",
                "Dear "
                + d.getSname()
                + ",\n\nThis email is to inform you that the project '"
                + pj.getTitle()
                + "' is successful. Therefore the donated money will be charged from you."
                + "\n\nthank you.");
    }
  }

  @Override
  public void sendFailedProjectStatusProject(Project pj, List<Donation> dons) throws MessagingException {
    sendmail(
        pj.getPsemail(),
        "Project Status",
        "Dear "
            + pj.getPsname()
            + ",\n\nThis email is to inform you that your project '"
            + pj.getTitle()
            + "' has failed to reach the funding limit."
            + "\n\nthank you.");

    for (Donation d : dons) {
      sendmail(d.getSemail(), "Donated Project Status",
              "Dear "
                      + d.getSname()
                      + ",\n\nThis email is to inform you that the project '"
                      + pj.getTitle()
                      + "' has failed. Therefore the donated money will not be charged from you."
                      + "\n\nthank you.");
    }
  }

  /** Helper function to send mail. */
  public void sendmail(String toEmail, String subject, String msg) throws MessagingException {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("crowdfund@localhost"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
    message.setSubject(subject);

    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setContent(msg, "text/plain");

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(mimeBodyPart);

    message.setContent(multipart);

    try {
      Thread.sleep(500); // to avoid rate limiting by mailtrap
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Transport.send(message);
  }
}
