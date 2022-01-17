package com.univpm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class ApiCall {

	private String url;
	
	public ApiCall (String url) {
		this.url = url;
	}
	public ApiCall () {
		
	}
	
	public JSONObject getData(String url) throws ParseException {
		// TODO Auto-generated method stub
		JSONObject jo = null;
		String data_filter ="";
		String line = "";
		try {
			
			URLConnection openConnection = new URL(url).openConnection();
			InputStream in = openConnection.getInputStream();

			InputStreamReader input = new InputStreamReader( in );
			BufferedReader buf = new BufferedReader( input );
			
			while ( ( line = buf.readLine() ) != null ) {
				data_filter += line;
			}
			
			in.close();
			jo = (JSONObject) JSONValue.parseWithException(data_filter);
		}
		catch (IOException e ) {
			e.printStackTrace();
		}
		return jo;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
