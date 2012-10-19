package eu.cassandra.sim.entities.appliances;

import java.util.ArrayList;
import java.util.Arrays;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.server.mongo.MongoConsumptionModels;
import eu.cassandra.sim.entities.Entity;

/**
 * This class stores the variables of a consumption model. It has no 
 * functionality except parsing a JSON string and return the values on demand.
 * 
 * @author kyrcha
 *
 */
public class ConsumptionModel extends Entity {
	
	/** How many times the patterns repeat */
	private int outerN;
	
	/** The number of patterns */
	private int patternN;
	
	/** Total duration of the consumption model */
	private int totalDuration;
	
	/** How many times each pattern runs */
	private int[] n;
	
	/** Total duration per pattern */
	private int[] patternDuration;
	
	/** An array storing the consumption patterns */
	private ArrayList[] patterns;
	
	private String model;
	
	public ConsumptionModel() {}
	
	public ConsumptionModel(String amodel) {
		model = amodel;
		DBObject modelObj = (DBObject) JSON.parse(model);
		init(modelObj);
	}
	
	public void init (DBObject modelObj) {

		outerN = ((Integer)modelObj.get("n")).intValue();
		BasicDBList patternsObj = (BasicDBList)modelObj.get("params");
		patternN = patternsObj.size();
		patterns = new ArrayList[patternN];
		n = new int[patternN];
		patternDuration = new int[patternN];
		for(int i = 0; i < patternN; i++) {
			n[i] = ((Integer)((DBObject)patternsObj.get(i)).get("n")).intValue();
			BasicDBList values = ((BasicDBList)((DBObject)patternsObj.get(i)).get("values"));
			int tripplets = values.size();
			patterns[i] = new ArrayList<Tripplet>(tripplets);
			for(int j = 0; j < tripplets; j++) {
				Tripplet t = new Tripplet();
				try {
					t.p = ((Double)((DBObject)values.get(j)).get("p")).doubleValue();
				} catch(ClassCastException e) {
					t.p = (double)((Integer)((DBObject)values.get(j)).get("p")).intValue();
				} 
				t.d = ((Integer)((DBObject)values.get(j)).get("d")).intValue();
				patternDuration[i] += t.d; 
				totalDuration += (n[i] * t.d);
				try {
					t.s = ((Double)((DBObject)values.get(j)).get("s")).doubleValue();
				} catch(ClassCastException e) {
					t.s = (double)((Integer)((DBObject)values.get(j)).get("s")).intValue();
				}
				patterns[i].add(t);
			}
		}

	}
	
	public int getTotalDuration() { return totalDuration; }
	
	public int getOuterN() { return outerN; }
	
	public int getPatternN() { return patternN; }
	
	public int getN(int i) { return n[i]; }
	
	public int getPatternDuration(int i) { return patternDuration[i]; }
	
	public ArrayList getPattern(int i) { return patterns[i]; }
	
	class Tripplet {
		double p,s;
		int d;
		public Tripplet() {
			p = s = 0;
			d = 0;
		}
	}
	
	public static void main(String[] args) {
		String s = "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 140.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 117.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 73, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
		ConsumptionModel cm = new ConsumptionModel(s);
		// TODO [TEST] check is parsing is done correctly
	}

	public void status(){
		
		System.out.println("Outer N:" + outerN);
		System.out.println("Total Duration:" + totalDuration);
		System.out.println("Number of Patterns:" + patternN);
		System.out.println("Pattern Times: " + Arrays.toString(n) );
		System.out.println("Pattern Durations: " + Arrays.toString(patternDuration) );
		
	}
	
	@Override
	public BasicDBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject();
		obj.put("name", name);
		obj.put("description", description);
		obj.put("app_id", parentId);
		obj.put("model", model);
		return obj;
	}

	@Override
	public String getCollection() {
		return MongoConsumptionModels.COL_CONSMODELS;
	}
	
}
