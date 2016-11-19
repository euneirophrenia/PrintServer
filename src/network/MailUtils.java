package network.mail;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeBodyPart;
import main.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class MailUtils 
{
	private static final String basedir = "/tmp/";
	private static final List<String> whiteList=Utils.getIstance().getWhiteList();
	
	
	private static String senderAddress(Message m) throws MessagingException
	{
		String[] x = m.getFrom()[0].toString().split(" ");
		return x[x.length-1].substring(1, x[x.length-1].length()-1);
	}
	
	private static boolean filter(Message m) throws MessagingException
	{
		boolean accepted = !m.isSet(Flag.SEEN);

		accepted&= whiteList.contains(senderAddress(m));
		
		return accepted; 
	}
	public static List<Message> fetch(String host, String storeType, String user, String password) {
	   switch (storeType)
	   {
	   case "pop3": return fetchPOP3(host, user, password);
	   case "imap": return fetchIMAP(host, user, password);
		   default : return new LinkedList<Message>();
		   }
	   }
   
	private static List<Message> fetchPOP3(String host, String user, String password)
   {
      try {
         // create properties field
         Properties properties = new Properties();
         properties.put("mail.store.protocol", "pop3");
         properties.put("mail.pop3.host", host);
         properties.put("mail.pop3.port", "995");
         properties.put("mail.pop3.starttls.enable", "true");
         Session emailSession = Session.getDefaultInstance(properties);
         // emailSession.setDebug(true);

         // create the POP3 store object and connect with the pop server
         Store store = emailSession.getStore("pop3s");

         store.connect(host, user, password);

         // create the folder object and open it
         Folder emailFolder = store.getFolder("INBOX");
         emailFolder.open(Folder.READ_ONLY);

         // retrieve the messages from the folder in an array and print it
         Message[] messages = emailFolder.getMessages();
         List<Message> res = new LinkedList<Message>();
         for (int i=0; i< messages.length; i++)
         {
        	 if (filter(messages[i]))
        	 {
        		 res.add(messages[i]);
        		 System.out.println(messages[i].getSubject());
        	 }
         }
         Flags f = new Flags (Flags.Flag.SEEN);
         emailFolder.setFlags(messages, f, true);

         // close the store and folder objects
         emailFolder.close(false);
         store.close();
         
         return res;

      } catch (NoSuchProviderException e) {
         e.printStackTrace();
      } catch (MessagingException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return new LinkedList<Message>();
   }
   
   
   private static List<Message> fetchIMAP(String imapHost, String user, String password) {
	      try {
	         // create properties field
	         Properties properties = new Properties();
	         properties.put("mail.store.protocol", "imap");
	         /*properties.put("mail.pop3.host", imapHost);
	         properties.put("mail.pop3.port", "995");
	         properties.put("mail.pop3.starttls.enable", "true");*/
	         Session emailSession = Session.getDefaultInstance(properties);
	         // emailSession.setDebug(true);

	         // create the POP3 store object and connect with the pop server
	         Store store = emailSession.getStore("imaps");

	         store.connect(imapHost, user, password);

	         // create the folder object and open it
	         Folder emailFolder = store.getFolder("INBOX");
	         emailFolder.open(Folder.READ_WRITE);

	         // retrieve the messages from the folder in an array and print it
	         Message[] messages = emailFolder.getMessages();
	         List<Message> res = new LinkedList<Message>();
	         for (int i=0; i< messages.length; i++)
	         {
	        	 if (filter(messages[i]))
	        	 {
	        		 res.add(messages[i]);
	        		 //System.out.println(messages[i].getSubject());
	        	 }
	         }
	         Flags f = new Flags (Flags.Flag.SEEN);
	         emailFolder.setFlags(messages, f, true);

	         // close the store and folder objects
	         emailFolder.close(false);
	         store.close();
	         
	         return res;

	      } catch (NoSuchProviderException e) {
	         e.printStackTrace();
	      } catch (MessagingException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return new LinkedList<Message>();
	   }
  
   public static List<File> getAttachments(List<Message> m) throws IOException, MessagingException
   {
	   List<File> attachments = new ArrayList<File>();
	   for (Message message : m) {
	       Multipart multipart = (Multipart) message.getContent();
	       // System.out.println(multipart.getCount());

	       for (int i = 0; i < multipart.getCount(); i++) {
	           BodyPart bodyPart = multipart.getBodyPart(i);
	           if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) && bodyPart.getFileName().isEmpty()) {
	               continue; // dealing with attachments only
	           } 
	           //InputStream is = bodyPart.getInputStream();
	           File f = new File(basedir + bodyPart.getFileName());
	           /*FileOutputStream fos = new FileOutputStream(f);
	           byte[] buf = new byte[4096];
	           int bytesRead;
	           while((bytesRead = is.read(buf))!=-1) {
	               fos.write(buf, 0, bytesRead);
	           }
	           fos.close();*/
	           ((MimeBodyPart)bodyPart).saveFile(f);
	           attachments.add(f);
	       }
	   }
	   return attachments;
   }
 
}
