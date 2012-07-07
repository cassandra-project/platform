package eu.cassandra.sim;

import java.io.File;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.entities.people.Activity;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.math.Gaussian;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.FileUtils;
import eu.cassandra.sim.utilities.Params;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Registry;

/**
 * The Observer can simulate up to 4085 years of simulation.
 * 
 * @author kyrcha
 *
 */
public class Observer implements Runnable {
	
	static Logger logger = Logger.getLogger(Observer.class);

	public static Vector<Installation> installations = new Vector<Installation>();
	
	private  PriorityBlockingQueue<Event> queue;

	private int tick = 0;

	private int endTick; 

	private Registry registry;

	public Observer() {
		PropertyConfigurator.configure(Params.LOG_CONFIG_FILE);
	}

	public void simulate() {
		Thread t = new Thread(this);
		try {
			t.start();
	        t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }	
	}

	public  void run() {
		while(tick < endTick) {
			if(tick % Constants.MIN_IN_DAY == 0) {
				logger.info("Day " + ((tick / Constants.MIN_IN_DAY) + 1));
				for(Installation installation : installations) {
					installation.updateDailySchedule(tick, queue);
				}
				logger.info("Daily queue size: " + queue.size() + 
						"(" + SimCalendar.isWeekend(tick) +")");
			}
			while(queue.peek().getTick() == tick) {
				Event e = queue.poll();
				e.apply();
			}
			/*
			 *  Calculate the total power for this simulation step of all the
			 *  installations 
			 */
			float sumPower = 0;
			for(Installation installation : installations) {
				installation.nextStep(tick);
				float power = installation.getPower(tick);
				sumPower += power;
				String name = installation.getName();
				logger.trace("Tick: " + tick + " \t " + "Name: " + name + 
						" \t " + "Power: " + power);
			}
			registry.setValue(tick, sumPower);
			tick++;
		}
	}

	/**
	 * Flush the contents of registries to the file system.
	 */
	public void flush() {
		File folder = new File(Params.REGISTRIES_DIR + 
				Calendar.getInstance().getTimeInMillis() + "/");
		FileUtils.createFolderStucture(folder);
		// Flush installations and appliances
		for(Installation installation : installations) {
			installation.getRegistry().saveRegistry(folder);
		}
		registry.saveRegistry(folder);
	}
	
	public void setup() {
		logger.info("Simulation setup started.");
		// Initialize simulation variables
		int numOfDays = FileUtils.getInt(Params.SIM_PROPS, "days");
		int numOfInstallations = 
				FileUtils.getInt(Params.SIM_PROPS, "installations");
		endTick = Constants.MIN_IN_DAY * numOfDays;
		registry = new Registry("sim", endTick);
		queue = new  PriorityBlockingQueue<Event>(2 * numOfInstallations);
		
		// Read the different kinds of appliances
		String[] appliances = 
				FileUtils.getStringArray(Params.APPS_PROPS, "appliances");
		// Read appliances statistics
		double[] ownershipPerc = new double[appliances.length];
		for(int i = 0; i < appliances.length; i++) {
			ownershipPerc[i] = 
					FileUtils.getDouble(Params.DEMOG_PROPS, 
							appliances[i]+".perc");
		}
		// Create the installations and put appliances inside
		for(int i = 0; i < numOfInstallations; i++) {
			// Make the installation
			Installation inst = new Installation.Builder(i+"").
					registry(new Registry(i+"", endTick)).build();
			// Create the appliances
			for(int j = 0; j < appliances.length; j++) {
				double dice = RNG.nextDouble();
				if(dice < ownershipPerc[j]) {
					Appliance app = 
							new Appliance.Builder(appliances[j], inst).build();
					inst.addAppliance(app);
					logger.trace(i + " " + appliances[j]);
				}
			}
			installations.add(inst);
		}
		// Load possible activities
		String[] activities = 
				FileUtils.getStringArray(Params.ACT_PROPS, "activities");
		// Put persons inside installations along with activities
		for(int i = 0; i < numOfInstallations; i++) {
			Installation inst = installations.get(i);
			int type = (RNG.nextInt() < 0.5) ? 1 : 2;
			Person person = 
					new Person.Builder("Person " + i, type, inst).build();
			inst.addPerson(person);
			for(int j = 0; j < activities.length; j++) {
				String[] appsNeeded = 
						FileUtils.getStringArray(Params.ACT_PROPS, 
								activities[j]+".apps");
				Vector<Appliance> existing = new Vector<Appliance>();
				for(int k = 0; k < appsNeeded.length; k++) {
					Appliance a = inst.applianceExists(appsNeeded[k]);
					if(a != null) {
						existing.add(a);
					}
				}
				if(existing.size() > 0) {
					logger.trace(i + " " + activities[j]);
					double mu = FileUtils.getDouble(
									Params.ACT_PROPS, 
									activities[j]+".startTime.mu."+type);
					double sigma = FileUtils.getDouble(
									Params.ACT_PROPS, 
									activities[j]+".startTime.sigma."+type);
					ProbabilityDistribution start = new Gaussian(mu, sigma);
					start.precompute(0, 1439, 1440);
					
					mu = FileUtils.getDouble(
									Params.ACT_PROPS, 
									activities[j]+".duration.mu."+type);
					sigma = FileUtils.getDouble(
									Params.ACT_PROPS, 
									activities[j]+".duration.sigma."+type);
					ProbabilityDistribution duration = new Gaussian(mu, sigma);
					duration.precompute(1, 1439, 1439);
					
					mu = FileUtils.getDouble(
							Params.ACT_PROPS, 
							activities[j]+".weekday.mu."+type);
					sigma = FileUtils.getDouble(
							Params.ACT_PROPS, 
							activities[j]+".weekday.sigma."+type);
					ProbabilityDistribution weekday = new Gaussian(mu, sigma);
					weekday.precompute(0, 3, 4);
					
					mu = FileUtils.getDouble(
							Params.ACT_PROPS, 
							activities[j]+".weekend.mu."+type);
					sigma = FileUtils.getDouble(
							Params.ACT_PROPS, 
							activities[j]+".weekend.sigma."+type);
					ProbabilityDistribution weekend = new Gaussian(mu, sigma);
					weekend.precompute(0, 3, 4);
					
					Activity a = new Activity.Builder(
							activities[j], 
							start, 
							duration).
							times("weekday", weekday).
							times("weekend", weekend).
							build();
					for(Appliance e : existing) {
						a.addAppliance(e, 1.0);
					}
					person.addActivity(a);
				}
			}
		}
		logger.info("Simulation setup finished.");
	}
	
}
