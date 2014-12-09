/**
 * 
 */
package atlas.cool.rest.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author formica
 * 
 */
@RequestScoped
public class MailSender {

	@Resource(lookup = "java:jboss/mail/Default")
	private Session mailsession;

	@Inject
	private Logger log;

	/**
	 * @param dest
	 * @param content
	 */
	public void sendMail(final String[] dest, final String subject,
			final String content) {

		final Message simpleMessage = new MimeMessage(mailsession);

		if (dest == null) {
			log.log(Level.WARNING, "Destination array is empty or null");
			return;
		}

		InternetAddress fromAddress = null;
		InternetAddress[] toAddress = new InternetAddress[dest.length];
		try {
			fromAddress = new InternetAddress("JBCool-monitor@aiatlas");
			toAddress = new InternetAddress[dest.length];
			for (int i = 0; i < dest.length; i++) {
				if (dest[i] != null) {
					toAddress[i] = new InternetAddress(dest[i]);
				}
			}
		} catch (final AddressException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		try {
			simpleMessage.setFrom(fromAddress);
			simpleMessage.setRecipients(RecipientType.TO, toAddress);
			simpleMessage.setSubject(subject);
			simpleMessage.setText(content);

			Transport.send(simpleMessage);
		} catch (final MessagingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
}
