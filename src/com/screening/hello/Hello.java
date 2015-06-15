package com.screening.hello;
/*MuktoSoft ans SureCash Screening
 * 
 * Candidate: Walid Mohammad
 * 
 * */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;


import org.json.JSONException;
import org.json.simple.JSONObject;

@Path("/{marker}")
//marker denotes whether it is greeting or a weather query or a general query 
public class Hello {
@PathParam("marker") String m;

//getDetailedInfo method returns a string containing the exact info from the string extracted 
//from weather or search API
public static String getDetailedInfo(String extract, String match){
	String temp="";
	int start=0;
	int end=0;
	int matchLen=match.length();
	for(int i=0;i<extract.length();i++){
		if(extract.length()>i+matchLen){
			if(extract.substring(i, i+matchLen).equals(match)){
				start=i+matchLen;
				break;
			}
		}
			
	}
	for(int i=start;i<extract.length();i++){
		if(match.equals("<span class=\"_m3b\">")){
			if((extract.charAt(i)=='<' 
				&& extract.charAt(i+1)=='/'
				&& extract.charAt(i+2)=='s'
				&& extract.charAt(i+3)=='p'
				&& extract.charAt(i+4)=='a'
				&& extract.charAt(i+5)=='n'
				&& extract.charAt(i+6)=='>')){
				end=i;
				break;
			}
		}
		else{
			if(extract.charAt(i)==',' || extract.charAt(i)=='}'){
				end=i;
				break;
			}
		}
			
	}
	temp=extract.substring(start,end);
	if(temp.length()>100)temp="";
	return temp;
}
//converts hours+minutes to minutes
public static String getMinutes(String mixed){
	String minutes="";
	String[] strArr=mixed.split(" ");
	if(strArr.length==2){
		int str0=strArr[0].length();
		int str1=strArr[1].length();
		if(strArr[0].endsWith("h") && strArr[1].endsWith("m")){
			strArr[0]=strArr[0].substring(0, str0-1);
			strArr[1]=strArr[1].substring(0, str1-1);
			int hours=Integer.parseInt(strArr[0]);
			int mins=Integer.parseInt(strArr[1]);
			mins=mins+hours*60;
			minutes=String.valueOf(mins);
			minutes=minutes+"m";
		}
	}
	return minutes;
}
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response sayHtmlHello(@QueryParam("q") String userId) throws IOException, JSONException {
	 String response="{\n\t\"answer\": ";
	 String parsed=userId.toLowerCase();
	 //greetings
	 if(m.toLowerCase().equals("greetings")){
		if(parsed.equals("how are you")){
			response = response +  "Hello, Kitty!" + " I'm fine, and you?";		 
		}
		else if(parsed.equals("what is your name") || parsed.equals("who are you")){
		    response = response + "Hello, Kitty!" + " I'm Walid, nice to meet you!";
		}
		else if(parsed.equals("good morning")){
			response = response + "Hello, Kitty!" + " A very good morning to you too!";
		}
		else if(parsed.equals("good evening")){
			response = response + "Hello, Kitty!" + " A very good evening to you too!";
		}
		else if(parsed.equals("good night")){
			response = response + "Hello, Kitty!" + "Good night! Sweet dreams!";
		}
		else{
			response = response + "Hello, Kitty!" + " I'm Walid, nice to meet you!";
		} 
	}
	 //weather
	else if(m.toLowerCase().equals("weather")){
		String[] strArr=parsed.split(" ");
		String cityName=strArr[strArr.length-1];
		String a="http://api.openweathermap.org/data/2.5/weather?q="+cityName;
        URL url = new URL(a);
        URLConnection conn = url.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        String weatherExtract="";
        //weatherExtract holds the result from the API call
        while ((inputLine = br.readLine()) != null) {
        	weatherExtract=weatherExtract+inputLine;
        }
        br.close();
        String kelvin=getDetailedInfo(weatherExtract,"\"temp\":"); //in Kelvin
        float celc=Float.parseFloat(kelvin);
        celc=(float) (celc-273.15);
        String celcius=String.valueOf(celc);
        String humidity=getDetailedInfo(weatherExtract,"\"humidity\":");
        if(parsed.startsWith("what")){
        	if(parsed.contains("temperature")){
        		response=response+celcius+" C or "+ kelvin+" K";
        	}
        	else if(parsed.contains("humidity")){
        		response=response+humidity+"%";
        	}
        	else{
        		response=response+celcius+" C";
        	}
        }
        else if(parsed.startsWith("is")){
        	if(parsed.contains("rain") || parsed.contains("clouds")){
        		response=response+"YES";
        	}
        	else{
        		response=response+"NO";
        	}
        }   
	}
	//general query
	else if(m.toLowerCase().equals("qa")){
		 String url="http://www.google.com/search?q=";
	     String charset="UTF-8";
	     String key=parsed;
	     String query = String.format("%s",URLEncoder.encode(key, charset));
	     URLConnection con = new URL(url+ query).openConnection();
	     con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	     BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	     String inputLine;
	     String resultExtract="";
	     //resultExtract holds the result from the API call
	     while ((inputLine = in.readLine()) != null){
	    	 resultExtract=resultExtract+inputLine;
	     }
	     in.close();
	     String searchResult=getDetailedInfo(resultExtract,"<span class=\"_m3b\">");
	     if(!searchResult.isEmpty()){
	    	if(parsed.contains("how long") && searchResult.contains("h ") && searchResult.contains("m")){
	    		String temp=getMinutes(searchResult);
	    		if(temp.isEmpty()){
	    			response=response+searchResult;  
	    		}
	    		else{
	    			response=response+temp; 
	    		}
	    	 }
	    	 else{
	    		 response=response+searchResult; 
	    	 }  
	     }
	     else{
	    	 response=response+"Your majesty! Jon Snow knows nothing! So do I!";
	     }
	}
	response=response+"\n}";
	return Response.status(200).entity(response).build();
 }
}
