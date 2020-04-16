package application;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dbadapter.Donation;
import dbadapter.Project;
import interfaces.IEmail;

/**
 * This class handles sending emails for the application.
 * 
 * Environment variables EMAILUSER (smtp username) and EMAILPASS (smtp password)
 * has to be set in order to send emails. SMTP hostname and port has to be set
 * in web.xml file.
 * 
 * @author kt
 *
 */
public class EmailHandler implements IEmail {
  private Session session;

  public EmailHandler() {
//    Properties prop = new Properties();
//    prop.put("mail.smtp.auth", true);
//    prop.put("mail.smtp.starttls.enable", "true");
//    try {
//      InitialContext ic = new InitialContext();
//      String host = (String) ic.lookup("java:comp/env/ejb/email/host");
//      prop.put("mail.smtp.host", host);
//      prop.put("mail.smtp.ssl.trust", host);
//      int port = (int) ic.lookup("java:comp/env/ejb/email/port");
//      prop.put("mail.smtp.port", String.valueOf(port));
//
//    } catch (NamingException e) {
//      System.err.println(e.getMessage());
//    }
//
//    session = Session.getInstance(prop, new Authenticator() {
//      @Override
//      protected PasswordAuthentication getPasswordAuthentication() {
//        return new PasswordAuthentication(System.getenv("EMAILUSER"), System.getenv("EMAILPASS"));
//      }
//    });

  }

  /**
   * Sends a confirmation mail to the project starter of the given project.
   * 
   * @throws MessagingException
   */
  @Override
  public void sendConfmEmailFR(Project pj) {
    if (pj.getHash() == null || pj.getHash().isEmpty())
      return;

    String url = "http://localhost:8080/confirmfr?h=" + pj.getHash();
    System.out.println("Please confirm the project funding request via following url:\n" + url);
    // sendmail(pj.getPsemail(), "Confirm Project Creation",
    // "Please confirm the project funding request via following url:\n" + url);
  }

  @Override
  public void sendConfmEmailDon(Donation don) {
    // TODO Auto-generated method stub

  }

  /**
   * Helper function to send mail.
   */
  private void sendmail(String toEmail, String subject, String msg) throws MessagingException {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("crowdfund@localhost.lan"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
    message.setSubject(subject);

    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setContent(msg, "text/html");

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(mimeBodyPart);

    message.setContent(multipart);

    Transport.send(message);
  }

}
