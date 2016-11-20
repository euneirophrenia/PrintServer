package main;
import java.io.File;
import java.util.List;
import javax.mail.Message;


import network.*;

public class Main {
	public static final String error="error";
	public static final String service = "service notice";
	public static final String printing = "printing notice";
	
	public static void main(String[] args) 
	{ 
		Utils u = Utils.getIstance();
		Utils.log("Server starting", service);
		MailUtils m = null ;
		long millis = 60000*Integer.parseInt(u.get("timeBetweenChecks"));
		
		try 
		{
			m = new MailUtils(u.get("protocol"),u.get("host"), u.get("userName"), u.get("passwd"));
			for(;;)
			{
				List<Message> messages = m.fetch();
				Utils.log("Found "+messages.size() + " messages", service);
				if (!messages.isEmpty())
				{
					List<File> attachments = MailUtils.getAttachments(messages);
					Utils.log("Found "+attachments.size()+" files to print", service);
					for (File f: attachments)
					{
						//Utils.log("Printing "+f.getName(), printing);
						PrinterUtils.getInstance().print(f);
					}
				}
				
				Thread.sleep(millis);
			}
		}
		catch(Exception e)
		{
			Utils.log(e.getMessage(), error);
			Utils.log("Server stopped", service);
			m.close();
		}
	}

}
