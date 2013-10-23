package eu.cassandra.sim.entities.external;

import javax.ws.rs.core.MediaType;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import eu.cassandra.sim.utilities.Constants;

public class ThermalModule {
	
	private String uri = "http://at.climavem.com:8080/Integration_v1/rest/cassandra_integration";
	
	private String features = "\"features\": \"HysteresisModel\",";
	
	private String modelInitState = "\"model_init_state\": {" +
				"\"SS_disturbi1_CSTATE\": [5, 6, 6, 5, 4, 4, 6, 7, 7, 7, 8, 8]," +
				"\"SS_fc1_CSTATE\": [5, 6, 6, 5, 4, 4, 6, 7, 7, 7, 8, 8]," +
				"\"SS_lame1_CSTATE\": [5, 6, 6, 5, 4, 4, 6, 7, 7, 7, 8, 8]" +
			"},";
	
	private String desiredTempSchedule = "\"desired_temp_schedule\": [23.4, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5],";
	
	private String pricing = "\"pricing\": [0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08]";
	
	private String opening = "\"opening_time\": 8.0,";
	
	private String closing = "\"closing_time\": 21.0,";
	
	private String modelInputs = "\"model_inputs\": {" +
				opening +
				closing +
				desiredTempSchedule + 
				pricing +
			"}";
	
	private double[] currentPowerValues = new double[Constants.MIN_IN_DAY];
	
	public ThermalModule() { }
	
	public ThermalModule(DBObject obj, double[] touPricing) {
		try {
			String openingTime = (String)obj.get("opening_time");
			String closingTime = (String)obj.get("closing_time");
			double openingValue = Integer.parseInt(openingTime.split("\\:")[0]);
			double closingValue = Integer.parseInt(closingTime.split("\\:")[0]);
			if(openingTime.contains("PM")) {
				openingValue += 12.0;
			}
			if(closingTime.contains("PM")) {
				closingValue += 12.0;
			}
			uri = (String)obj.get("web_service_url");
			String type = (String)obj.get("type");
			switch(type) {
				case "single_single_nodr":
					features = "\"features\": \"HysteresisModel\",";
					break;
				case "six_single_nodr":
					features = "\"features\": \"HysteresisModel\",";
					break;
				case "six_five_nodr":
					features = "\"features\": \"SplitRange\",";
					break;
				case "six_onoff_nodr":
					features = "\"features\": \"HysteresisModel\",";
					break;
				case "single_mpc_nodr":
					features = "\"features\": \"HysteresisModel\",";
					break;
				case "six_mpc_nodr":
					features = "\"features\": \"HysteresisModel\",";
					break;
				default:
					features = "\"features\": \"HysteresisModel\",";
					break;
			}
			desiredTempSchedule = "\"desired_temp_schedule\":" + obj.get("desired_temp_schedule").toString() + ",";
			pricing = "\"pricing\":" + doubleToString(touPricing);
			opening = "\"opening_time\": " + openingValue + ",";
			closing = "\"closing_time\": " + closingValue + ",";
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String doubleToString(double[] values) {
		if(values.length == 0) return "[]";
		String s = new String();
		s += "[" + values[0];
		for(int i = 1; i < values.length; i++) {
			s += "," + values[i];
		}
		s += "]";
		return s;
	}
	
	public ThermalModule(String auri, 
			String afeatures, 
			String amodelInitState, 
			String amodelInputs,
			String adesiredTempSchedule,
			String apricing) {
		uri = auri;
		features = afeatures;
		modelInitState = amodelInitState;
		modelInputs = amodelInputs;
		desiredTempSchedule = adesiredTempSchedule;
		pricing = apricing;
	}
	
	public void nextStep() {
		String response = getResponse();
		modelInitState = getNextModelInitState(response);
		currentPowerValues = getPowerConsumption(response);
	}
	
	public double getPower(int tick) {
		return currentPowerValues[tick % Constants.MIN_IN_DAY];
	}
	
	public String getResponse() {
		String stringResponse = null;
		try { 
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			Client client = Client.create(clientConfig);
			client.setConnectTimeout(10000);
			client.setReadTimeout(10000);
			String JSONRequest = "{" + features + modelInitState + modelInputs + "}";
			WebResource webResource = client.resource(uri);
	 
			ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(ClientResponse.class,JSONRequest);
	 
			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			stringResponse = response.getEntity(String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringResponse;
	}
	
	public static String getNextModelInitState(String response) {
		DBObject o = (DBObject)JSON.parse(response);
		DBObject model_final_state = (DBObject)o.get("model_final_state");
		return "\"model_init_state\":" + model_final_state.toString() + ",";
	}
	
	public static double[] getPowerConsumption(String response) {
		double[] d = new double[1440];
		DBObject o = (DBObject)JSON.parse(response);
		BasicDBList list = (BasicDBList)o.get("sim_outputs");
		for(Object lo : list) {
			double power = ((Double)((DBObject)lo).get("P")).doubleValue();
			int min = ((int)((Double)((DBObject)lo).get("time")).doubleValue())/60;
			d[min-1] = power;
		}
		return d;
	}
	
	public static void main(String[] args) {
		try { 
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			Client client = Client.create(clientConfig);
			client.setConnectTimeout(10000);
			client.setReadTimeout(10000);
			
			String JSONRequest = "{" +
					  "\"features\": \"HysteresisModel\"," +
					  "\"model_init_state\": {" +
					  				"\"SS_disturbi1_CSTATE\": [5, 6, 6, 5, 4, 4, 6, 7, 7, 7, 8, 8]," +
					  				"\"SS_fc1_CSTATE\": [5, 6, 6, 5, 4, 4, 6, 7, 7, 7, 8, 8]," +
					  				"\"SS_lame1_CSTATE\": [5, 6, 6, 5, 4, 4, 6, 7, 7, 7, 8, 8]" +
					  			"}," +
					  "\"model_inputs\": {" +
					  			"\"opening_time\": 8.0," +
					  			"\"closing_time\": 21.0," +
								"\"desired_temp_schedule\": [23.4, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5, 24.5],"+
								"\"pricing\": [0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08, 0.08, 0.07, 0.08]" +
				  			"}" +
					"}";
			//WebResource webResource = client.resource("http://131.175.16.246:8080/Integration_v1/rest/cassandra_integration");
			WebResource webResource = client.resource("http://at.climavem.com:8080/Integration_v1/rest/cassandra_integration");
			ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(ClientResponse.class,JSONRequest);
	 
			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
	 
			String output = response.getEntity(String.class);
	 
			System.out.println("Output from Server .... \n");
			System.out.println(output);
			DBObject o = (DBObject)JSON.parse(output);
			System.out.println(getNextModelInitState(o.toString()));
			BasicDBList list = (BasicDBList)o.get("sim_outputs");
			System.out.println(list.size());
//			System.out.println(getPowerConsumption(output));
			for(Object lo : list) {
				double power = ((Double)((DBObject)lo).get("P")).doubleValue();
				int min = ((int)((Double)((DBObject)lo).get("time")).doubleValue())/60;
				System.out.println(min + " " + power);
			}
		  } catch (Exception e) {
	 
			e.printStackTrace();
	 
		  }
	 
		}

}
