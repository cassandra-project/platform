package eu.cassandra.sim.entities.appliances;

import java.util.ArrayList;

import com.mongodb.DBObject;

import eu.cassandra.sim.entities.appliances.ConsumptionModel.Tripplet;

public class GUIConsumptionModel {

	private ConsumptionModel cons = new ConsumptionModel();

	/**
	 * 
	 * @param obj
	 */
	public GUIConsumptionModel(DBObject obj){
		cons.init(obj);
		//cons.status();
	}

	/**
	 * 
	 * @return
	 */
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
		Double[] result = new Double[temp.size()];
		temp.toArray(result);
		return result;

	}
}
