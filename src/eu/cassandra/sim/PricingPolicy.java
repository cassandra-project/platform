package eu.cassandra.sim;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.cassandra.server.mongo.MongoPricingPolicy;
import eu.cassandra.sim.utilities.Constants;

public class PricingPolicy {
	
	public final static int SCALAR_ENERGY_PRICING = 0;
	
	public final static int SCALAR_ENERGY_PRICING_TIME_ZONES = 1;
	
	public final static int ENERGY_POWER_PRICING = 2;
	
	public final static int ALL_INCLUSIVE_PRICING = 3;
	
	private String type;
	
	private double fixedCharge;
	
	private int billingCycle;
	
	private double contractedCapacity;
	
	private double contractedEnergy;
	
	private double energyPricing;
	
	private double powerPricing;
	
	private double fixedCost;
	
	private double additionalCost;
	
	private double offpeakPrice;
	
	private ArrayList<Level> levels;
	
	private ArrayList<Offpeak> offpeaks;
	
	public PricingPolicy() {
		billingCycle = 1;
		fixedCharge = 0;
		type = "NoPricing";
	}
	
	public PricingPolicy(DBObject dbo) throws ParseException {
		type = dbo.get("type").toString();
		switch(type) {
			case "ScalarEnergyPricing":
				billingCycle = Integer.parseInt(dbo.get("billingCycle").toString());
				fixedCharge = Double.parseDouble(dbo.get("fixedCharge").toString());
				// Parse levels
				BasicDBList levelsObj = (BasicDBList)dbo.get("levels");
				DBObject levelObj;
				levels = new ArrayList<Level>();
				for(int i = 0; i < levelsObj.size(); i++) {
					levelObj =  (DBObject)levelsObj.get(i);
					double price = Double.parseDouble(levelObj.get("price").toString());
					double level = Double.parseDouble(levelObj.get("level").toString());
					Level l = new Level(price, level);
					levels.add(l);
				}
				break;
			case "ScalarEnergyPricingTimeZones":
				billingCycle = Integer.parseInt(dbo.get("billingCycle").toString());
				fixedCharge = Double.parseDouble(dbo.get("fixedCharge").toString());
				offpeakPrice = Double.parseDouble(dbo.get("offpeakPrice").toString());
				// Parse levels
				BasicDBList levelsObj2 = (BasicDBList)dbo.get("levels");
				DBObject levelObj2;
				levels = new ArrayList<Level>();
				for(int i = 0; i < levelsObj2.size(); i++) {
					levelObj2 =  (DBObject)levelsObj2.get(i);
					double price = Double.parseDouble(levelObj2.get("price").toString());
					double level = Double.parseDouble(levelObj2.get("level").toString());
					Level l = new Level(price, level);
					levels.add(l);
				}
				// Parse timezones
				BasicDBList tzs = (BasicDBList)dbo.get("offpeak");
				DBObject tz;
				offpeaks = new ArrayList<Offpeak>();
				for(int i = 0; i < levelsObj2.size(); i++) {
					levelObj2 =  (DBObject)levelsObj2.get(i);
					String from = levelObj2.get("from").toString();
					String to = levelObj2.get("to").toString();
					Offpeak o = new Offpeak(from, to);
					offpeaks.add(o);
				}
				break;
			case "EnergyPowerPricing":
				billingCycle = Integer.parseInt(dbo.get("billingCycle").toString());
				fixedCharge = Double.parseDouble(dbo.get("fixedCharge").toString());
				contractedCapacity = Integer.parseInt(dbo.get("contractedCapacity").toString());
				energyPricing = Double.parseDouble(dbo.get("energyPrice").toString());
				powerPricing = Double.parseDouble(dbo.get("powerPrice").toString());
				break;
			case "AllInclusivePricing":
				billingCycle = Integer.parseInt(dbo.get("billingCycle").toString());
				fixedCharge = Double.parseDouble(dbo.get("fixedCharge").toString());
				fixedCost = Integer.parseInt(dbo.get("fixedCost").toString());
				additionalCost = Double.parseDouble(dbo.get("additionalCost").toString());
				contractedEnergy = Double.parseDouble(dbo.get("contractedEnergy").toString());
			default:
				break;
		}
	}
	
	public int getBillingCycle() {
		return billingCycle;
	}
	
	public double getFixedCharge() {
		return fixedCharge;
	}
	
	public String getType() {
		return type;
	}
	
	public double calculateCost(double toEnergy, double fromEnergy,
			double toEnergyOffpeak, double fromEnergyOffpeak) {
		double remainingEnergy = toEnergy - fromEnergy;
		double cost = 0;
		switch(type) {
			case "ScalarEnergyPricing":
				cost += fixedCharge;
				for(int i = levels.size()-1; i >= 0; i--) {
					double level = levels.get(i).getLevel();
					double price = levels.get(i).getPrice();
					if(remainingEnergy < level) {
						cost += remainingEnergy * price;
						break;
					} else if(!(level > 0)) {
						cost += remainingEnergy * price;
						break;
					} else {
						remainingEnergy -= level;
						cost += level * price;
					}
				}
				break;
			case "ScalarEnergyPricingTimeZones":
				cost += fixedCharge;
				for(int i = levels.size()-1; i >= 0; i--) {
					double level = levels.get(i).getLevel();
					double price = levels.get(i).getPrice();
					if(remainingEnergy < level) {
						cost += remainingEnergy * price;
						break;
					} else if(!(level > 0)) {
						cost += remainingEnergy * price;
						break;
					} else {
						remainingEnergy -= level;
						cost += level * price;
					}
				}
				double remainingEnergyOffpeak = toEnergyOffpeak - fromEnergyOffpeak;
				cost += remainingEnergyOffpeak * offpeakPrice;
				break;
			case "EnergyPowerPricing":
				cost += fixedCharge;
				cost += remainingEnergy * energyPricing;
				cost += contractedCapacity * powerPricing;
				break;
			case "AllInclusivePricing":
				cost += fixedCharge;
				cost += fixedCost;
				cost += Math.max((remainingEnergy-contractedEnergy),0) * additionalCost;
				break;
			case "NoPricing" :
				break;
			default:
				break;
		}
		return cost;
	}
	
	public boolean isOffpeak(int tick) {
		if(type.equalsIgnoreCase("ScalarEnergyPricingTimeZones")) {
			int minutesInDay = tick % Constants.MIN_IN_DAY;
			Iterator<Offpeak> iter = offpeaks.iterator();
			while(iter.hasNext()) {
				Offpeak o = iter.next();
				String[] fromTokens = o.getFrom().split(":");
				String[] toTokens = o.getTo().split(":");
				int from = Integer.parseInt(fromTokens[0]) * 60 + Integer.parseInt(fromTokens[1]);
				int to = Integer.parseInt(toTokens[0]) * 60 + Integer.parseInt(toTokens[1]);
				if(minutesInDay >= from && minutesInDay < to) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param args
	 * @throws MongoException 
	 * @throws UnknownHostException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws UnknownHostException, MongoException, ParseException {
		// TODO Auto-generated method stub
		String prc_id = "51778737e4b02bc3aca36960";
		DBObject query = new BasicDBObject(); // A query
		query.put("_id", new ObjectId(prc_id));
		Mongo m = new Mongo("localhost");
		DB db = m.getDB("test");
		DBObject pricingPolicy = db.getCollection(MongoPricingPolicy.COL_PRICING).findOne(query);
		PricingPolicy pp = new PricingPolicy(pricingPolicy);
		System.out.println(pp.getType());
		System.out.println(pp.getFixedCharge());
		System.out.println(pp.calculateCost(1500, 0, 0, 0));
	}

}
