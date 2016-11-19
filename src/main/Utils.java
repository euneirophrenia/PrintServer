package main;
import org.json.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	private static final String file="settings.json";
	private static Utils istance=null;
	private JSONObject properties;
	
	private Utils() throws IOException, JSONException
	{	
		BufferedReader b = new BufferedReader(new FileReader(new File(file)));
		StringBuilder s = new StringBuilder();
		String line=b.readLine();
		while (line!=null)
		{
			s.append(line);
			line=b.readLine();
		}
		
		b.close();
		properties= new JSONObject(s.toString());
		
	}
	
	public String get(String key)
	{
		try {
			return properties.getString(key);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getWhiteList()
	{
		List<String> res = new ArrayList<String>();
		String all = this.get("whiteList");
		all=all.substring(1, all.length()-1);
		String[] tokens = all.split(",");
		for (int i=0; i< tokens.length; i++)
		{
			res.add(tokens[i]);
		}
		
		return res;
	}
	
	public static Utils getIstance()
	{
		if (istance==null)
			try {
				istance = new Utils();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return istance;
	}
	
}
