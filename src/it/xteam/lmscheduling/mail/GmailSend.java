package it.xteam.lmscheduling.mail;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import it.xteam.estrattore.Estrattore;

public class GmailSend {

	final public static String username = "";
	final public static String password = "";
	public static String destinatario = "";
	final static String NOME_MITTENTE = "Skynet";
	final static String OGGETTO_NEWSLETTER = "Immagini settimanali adunanze";
	static String testo = null;

	public static void main(String[] args) {
		sendSingleEmailERROR("CIAO", "CIAO1");
	}

	public static boolean sendSingleEmailOK() {
		destinatario = Estrattore.EMAIL_TO;
		testo = new String("File in allegato");
		Properties props = new Properties();

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", "587");
		props.put("mail.user", username);
		props.put("mail.password", password);
		/*
		 * props.put("mail.smtp.host","smtp.gmail.com"); props.put("mail.smtp.socketFactory.port","587");
		 * props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		 * props.put("mail.smtp.auth","true"); props.put("mail.smtp.port","587");
		 */
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		destinatario = destinatario.trim();
		destinatario = destinatario.toLowerCase();
		try {

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, NOME_MITTENTE));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(Estrattore.EMAIL_CC));
			message.setSubject(OGGETTO_NEWSLETTER);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(testo);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(Estrattore.PATH_OUTPUT + "output.zip");
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("immagini.zip");
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			Transport.send(message);
			System.out.println("Mail Sent Successfully to " + destinatario);
		}
		catch (Exception e) {
			e.printStackTrace();
			return (false);
		}
		return (true);
	}

	public static boolean sendSingleEmailERROR(String subject, String testo) {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, NOME_MITTENTE));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("###@gmail.com"));

			message.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(testo);

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			//messageBodyPart = new MimeBodyPart();

			//DataSource source = new FileDataSource("./images/output.zip");
			//messageBodyPart.setDataHandler(new DataHandler(source));
			//messageBodyPart.setFileName("immagini.zip");
			//multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);

			Transport.send(message);
			System.out.println("Mail Sent Successfully to " + destinatario);
		}
		catch (Exception e) {
			e.printStackTrace();
			return (false);
		}
		return (true);
	}

}