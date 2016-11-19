package main;
import java.util.List;

import javax.mail.Message;

import network.mail.*;

public class Main {

	public static void main(String[] args) 
	{ 
		Utils u = Utils.getIstance();
		List<Message> m=MailUtils.fetch(u.get("host"),u.get("protocol"), u.get("userName"), u.get("passwd"));
		System.out.println("Trovati "+m.size() + " messaggi");
	
		
	}

}
