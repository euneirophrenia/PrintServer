package network;
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
	private Folder folder;
	private Store store;
	
	public MailUtils(String protocol, String host, String user, String password) throws MessagingException
	{
		Properties properties = new Properties();
        properties.put("mail.store.protocol", protocol);
        properties.put("mail."+protocol+".host", host);
        //properties.put("mail."+protocol+".port", "995");
        properties.put("mail."+protocol+".starttls.enable", "true");
        
        Session emailSession = Session.getDefaultInstance(properties);
        // emailSession.setDebug(true);
     
		store = emailSession.getStore(protocol+"s");
		store.connect(host, user, password);
		folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

	
	}
	
	public void close()
	{
		try
		{
			store.close();
			folder.close(false);
		} catch (MessagingException e) {
		}
		
	}
	
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
	public List<Message> fetch() throws MessagingException {
		
	        
         Message[] messages = folder.getMessages();
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
         folder.setFlags(messages, f, true);
         return res;
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
