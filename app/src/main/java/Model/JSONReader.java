package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

/**
 * Makes a web request using the URL given and then uses external libraries to package the data into JSON to
 * create a JSON object as well as A Map that reads the JSON and connects its key, value pairs into an String, Object
 * Map.
 */
public class JSONReader {
	private Map<String, Object> map = null;
	private JSONObject jsonObject = null;


	public JSONReader(String url_string) {
		URL url = null;
		HttpURLConnection conn = null;
		int responsecode = 0;
		String inline = "";
		
		//Pass the desired URL as an object
		try {
			 url = new URL(url_string);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//Type cast the URL object into a HttpURLConnection object.
		try {
			 conn = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//Set the request type, as in, whether the request to the API is a GET request or a POST request.
		try {
			conn.setRequestMethod("GET");

		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Get the corresponding response code.
		try {
			responsecode = conn.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Now we need to perform a check so that if the response code is not 200
		if(responsecode != 200)
			throw new RuntimeException("HttpResponseCode: " +responsecode);
			else
			{
				Scanner sc = null;
				try {
					sc = new Scanner(url.openStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				while(sc.hasNext())
					{
					inline+=sc.nextLine();
					}
				//System.out.println("\nJSON data in string format");
				//System.out.println(inline);
                                 
				sc.close();
			}
		
		//Change to a JSON Object
		try {
		      this.jsonObject = new JSONObject(inline);
		}catch (JSONException err){
			err.printStackTrace();
		}
		
		
		
		//Print out the JSON object
		//System.out.print(jsonObject);
		
		//Print out part of the JSON
		//System.out.print(jsonObject.toString());
		
	     /*
        * Read JSON from a string into a map
        */
		ObjectMapper mapper = new ObjectMapper();
       try {
           this.map = mapper.readValue(jsonObject.toString(),Map.class);

       } catch (Exception e) {
           e.printStackTrace();
       }
		
	}
	
	public JSONObject getJsonObject(){
		return this.jsonObject; 
	}
        
        public Map<String, Object> getMap(){
            return this.map; 
        }

}