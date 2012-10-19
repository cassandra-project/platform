package eu.cassandra.sim.entities.appliances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.sim.entities.appliances.ConsumptionModel.Tripplet;

public class GUIConsumptionModel {

	ConsumptionModel cons = new ConsumptionModel();


	public GUIConsumptionModel(DBObject obj){

		cons.init(obj);

		cons.status();

	}

	public Double[] getValues(){

		
		ArrayList<Double> temp = new ArrayList<Double>();
		int times = cons.getOuterN();
		if (times == 0)
			times = 2; 

		// Number of repeats
		for (int i = 0; i < times;i++){
			//System.out.println("Time: " + i);
			// Number of patterns in each repeat
			for (int j = 0 ; j < cons.getPatternN();j++){
				//System.out.println("Pattern: " + j);
				ArrayList<Tripplet> tripplets = cons.getPattern(j);
				
				for (int k = 0 ; k < tripplets.size();k++){
					//System.out.println("Tripplet: " + k);
					for (int l = 0; l < tripplets.get(k).d ; l++){
						
						temp.add(tripplets.get(k).p);
						
					}
					
				}
				
			}

		}
		System.out.println("Temp: " + temp.toString());
		
		Double[] result = new Double[temp.size()];
		
		temp.toArray(result);
						
		System.out.println(Arrays.toString(result));
		
		return result;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "{ \"n\" : 4, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 140.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 117.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 73, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";

		DBObject obj = (DBObject)JSON.parse(s); 

		GUIConsumptionModel cm = new GUIConsumptionModel(obj);
		
		Double[] temp = cm.getValues();

		
	}


	private static String readFile( String file ) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String         line = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String         ls = System.getProperty("line.separator");

		while( ( line = reader.readLine() ) != null ) {
			stringBuilder.append( line );
			stringBuilder.append( ls );
		}

		return stringBuilder.toString();
	}
}
