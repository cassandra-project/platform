/*   
   Copyright 2011-2012 The Cassandra Consortium (cassandra-fp7.eu)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package eu.cassandra.sim;

import java.io.File;

import java.util.Calendar;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.entities.people.Activity;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.math.Gaussian;
import eu.cassandra.sim.math.GaussianMixtureModels;
import eu.cassandra.sim.math.ProbabilityDistribution;
import eu.cassandra.sim.math.Uniform;
import eu.cassandra.sim.utilities.Constants;
import eu.cassandra.sim.utilities.FileUtils;
import eu.cassandra.sim.utilities.Params;
import eu.cassandra.sim.utilities.RNG;
import eu.cassandra.sim.utilities.Registry;

/**
 * The Simulation class can simulate up to 4085 years of simulation.
 * 
 * @author Kyriakos C. Chatzidimitriou (kyrcha [at] iti [dot] gr)
 * 
 */
public class Simulation implements Runnable
{

  static Logger logger = Logger.getLogger(Simulation.class);

  private Vector<Installation> installations;

  private PriorityBlockingQueue<Event> queue;

  private int tick = 0;

  private int endTick;

  private Registry registry;

  public Collection<Installation> getInstallations ()
  {
    return installations;
  }

  public Installation getInstallation (int index)
  {
    return installations.get(index);
  }

  public int getCurrentTick ()
  {
    return tick;
  }

  public int getEndTick ()
  {
    return endTick;
  }

  public Registry getRegistry ()
  {
    return registry;
  }

  public void simulate ()
  {
    Thread t = new Thread(this);
    try {
      t.start();
      t.join();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void run ()
  {
    while (tick < endTick) {
      // If it is the beginning of the day create the events
      if (tick % Constants.MIN_IN_DAY == 0) {
        logger.info("Day " + ((tick / Constants.MIN_IN_DAY) + 1));
        for (Installation installation: installations) {
          installation.updateDailySchedule(tick, queue);
        }
        logger.info("Daily queue size: " + queue.size() + "("
                    + SimCalendar.isWeekend(tick) + ")");
      }

      Event top = queue.peek();
      while (top != null && top.getTick() == tick) {
        Event e = queue.poll();
        e.apply();
        top = queue.peek();
      }

      /*
       *  Calculate the total power for this simulation step for all the
       *  installations.
       */
      float sumPower = 0;
      for (Installation installation: installations) {
        installation.nextStep(tick);
        float power = installation.getPower(tick);
        sumPower += power;
        String name = installation.getName();
        logger.trace("Tick: " + tick + " \t " + "Name: " + name + " \t "
                     + "Power: " + power);
      }
      registry.setValue(tick, sumPower);
      tick++;
    }
  }

  /**
   * Flush the contents of registers to the file system.
   */
  public void flush ()
  {
    File folder =
      new File(Params.REGISTRIES_DIR + Calendar.getInstance().getTimeInMillis()
               + "/");
    FileUtils.createFolderStucture(folder);
    // Flush installations and appliances
    for (Installation installation: installations) {
      installation.getRegistry().saveRegistry(folder);
    }
    registry.saveRegistry(folder);
  }

  public void setup () throws Exception
  {
    logger.info("Simulation setup started.");
    installations = new Vector<Installation>();

    int numOfDays = FileUtils.getInt(Params.SIM_PROPS, "days");
    endTick = Constants.MIN_IN_DAY * numOfDays;
    registry = new Registry("Total", endTick);

    // Check type of setup
    String setup = FileUtils.getString(Params.SIM_PROPS, "setup");
    if (setup.equalsIgnoreCase("static")) {
      staticSetup();
    }
    else if (setup.equalsIgnoreCase("dynamic")) {
      dynamicSetup();
    }
    else {
      throw new Exception("Problem with setup property in "
                               + Params.SIM_PROPS);
    }
    // Load possible activities
    String[] activities =
      FileUtils.getStringArray(Params.ACT_PROPS, "activities");
    // Put persons inside installations along with activities
    int typesOfPersons = FileUtils.getInt(Params.ACT_PROPS, "person-types");
    for (int i = 0; i < installations.size(); i++) {
      Installation inst = installations.get(i);
      int type = RNG.nextInt(typesOfPersons) + 1;
      Person person = new Person.Builder("Person " + i, type, inst).build();
      inst.addPerson(person);

      for (int j = 0; j < activities.length; j++) {
        String[] appsNeeded =
          FileUtils.getStringArray(Params.ACT_PROPS, activities[j] + ".apps");
        Vector<Appliance> existing = new Vector<Appliance>();
        for (int k = 0; k < appsNeeded.length; k++) {
          Appliance a = inst.applianceExists(appsNeeded[k]);
          if (a != null) {
            existing.add(a);
          }
        }

        if (existing.size() > 0) {
          logger.trace(i + " " + activities[j]);
          double mu = 0, sigma = 0, from = 0, to = 0;
          ProbabilityDistribution start = null, duration = null, weekday = null, weekend =
            null;
          double[] means, sigmas, pi;
          String distribution = "";

          /* ==========Start Time Distribution========== */

          distribution =
            FileUtils.getString(Params.ACT_PROPS, activities[j]
                                                  + ".startTime.distribution."
                                                  + type);

          switch (distribution) {
          case ("normal"):
            mu =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".startTime.mu." + type);
            sigma =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".startTime.sigma."
                                                    + type);
            start = new Gaussian(mu, sigma);
            start.precompute(0, 1439, 1440);
            break;

          case ("uniform"):
            from =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".startTime.start."
                                                    + type);
            to =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".startTime.end." + type);
            start = new Uniform(from, to);
            start.precompute(from, to, (int) to + 1);
            break;

          case ("mixture"):
            means =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".startTime.means."
                                                         + type);
            sigmas =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".startTime.sigmas."
                                                         + type);

            pi =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".startTime.pi."
                                                         + type);
            start = new GaussianMixtureModels(pi.length, pi, means, sigmas);
            start.precompute(0, 1439, 1440);
            break;

          default:
            System.out.println("Non existing start time distribution type");
          }

          System.out.println("Start Time Distribution");
          start.status();

          /* ==========Duration Distribution========== */

          distribution =
            FileUtils.getString(Params.ACT_PROPS, activities[j]
                                                  + ".duration.distribution."
                                                  + type);

          switch (distribution) {
          case ("normal"):

            mu =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".duration.mu." + type);
            sigma =
              FileUtils
                      .getDouble(Params.ACT_PROPS, activities[j]
                                                   + ".duration.sigma." + type);

            duration = new Gaussian(mu, sigma);
            duration.precompute(0, 1439, 1440);
            break;

          case ("uniform"):
            from =
              FileUtils
                      .getDouble(Params.ACT_PROPS, activities[j]
                                                   + ".duration.start." + type);
            to =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".duration.end." + type);
            duration = new Uniform(from, to);
            duration.precompute(from, to, (int) to + 1);
            break;

          case ("mixture"):
            means =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".duration.means."
                                                         + type);
            sigmas =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".duration.sigmas."
                                                         + type);

            pi =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".duration.pi."
                                                         + type);
            duration = new GaussianMixtureModels(pi.length, pi, means, sigmas);
            duration.precompute(0, 1439, 1440);
            break;

          default:
            System.out.println("Non existing duration distribution type");
          }

          System.out.println("Duration Distribution");
          duration.status();

          /* ==========Weekday Times Distribution========== */

          distribution =
            FileUtils.getString(Params.ACT_PROPS, activities[j]
                                                  + ".weekday.distribution."
                                                  + type);

          switch (distribution) {
          case ("normal"):

            mu =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".weekday.mu." + type);
            sigma =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".weekday.sigma." + type);
            weekday = new Gaussian(mu, sigma);
            weekday.precompute(0, 1439, 1440);

            break;
          case ("uniform"):
            from =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".weekday.start." + type);
            to =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".weekday.end." + type);
            weekday = new Uniform(from, to);
            weekday.precompute(from, to, (int) to + 1);
            break;

          case ("mixture"):
            means =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".weekday.means."
                                                         + type);
            sigmas =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".weekday.sigmas."
                                                         + type);

            pi =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".weekday.pi."
                                                         + type);
            weekday = new GaussianMixtureModels(pi.length, pi, means, sigmas);
            weekday.precompute(0, 1439, 1440);
            break;

          default:
            System.out.println("Non existing duration distribution type");
          }

          System.out.println("Weekday Distribution");
          weekday.status();

          /* ==========Weekend Times Distribution========== */

          distribution =
            FileUtils.getString(Params.ACT_PROPS, activities[j]
                                                  + ".weekend.distribution."
                                                  + type);

          switch (distribution) {
          case ("normal"):

            mu =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".weekend.mu." + type);
            sigma =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".weekend.sigma." + type);
            weekend = new Gaussian(mu, sigma);
            weekend.precompute(0, 1439, 1440);
            break;
          case ("uniform"):
            from =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".weekend.start." + type);
            to =
              FileUtils.getDouble(Params.ACT_PROPS, activities[j]
                                                    + ".weekend.end." + type);
            weekend = new Uniform(from, to);
            weekend.precompute(from, to, (int) to + 1);
            break;

          case ("mixture"):
            means =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".weekend.means."
                                                         + type);
            sigmas =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".weekend.sigmas."
                                                         + type);

            pi =
              FileUtils.getDoubleArray(Params.ACT_PROPS, activities[j]
                                                         + ".weekend.pi."
                                                         + type);
            weekend = new GaussianMixtureModels(pi.length, pi, means, sigmas);
            weekend.precompute(0, 1439, 1440);
            break;

          default:
            System.out.println("Non existing duration distribution type");
          }

          System.out.println("Weekend Distribution");
          weekend.status();

          Activity act =
            new Activity.Builder(activities[j], start, duration)
                    .times("weekday", weekday).times("weekend", weekend)
                    .build();
          for (Appliance e: existing) {
            act.addAppliance(e, 1.0 / existing.size());
          }
          person.addActivity(act);
        }
      }
    }
    logger.info("Simulation setup finished.");
  }

  public void staticSetup ()
  {
    // Initialize simulation variables
    String[] namesOfInstallations =
      FileUtils.getStringArray(Params.SIM_PROPS, "installations");
    int numOfInstallations = namesOfInstallations.length;
    queue = new PriorityBlockingQueue<Event>(2 * numOfInstallations);
    for (int i = 0; i < numOfInstallations; i++) {
      // Make the installation
      Installation inst =
        new Installation.Builder(i, i + "").registry(new Registry(i + "",
                                                                  endTick))
                .build();
      // Create the appliances
      String[] instApps =
        FileUtils.getStringArray(Params.DEMOG_PROPS, namesOfInstallations[i]);
      for (int j = 0; j < instApps.length; j++) {
        Appliance app = new Appliance.Builder(instApps[j], inst).build();
        inst.addAppliance(app);
        logger.trace(i + " " + instApps[j]);
      }
      installations.add(inst);
    }
  }

  public void dynamicSetup ()
  {
    // Initialize simulation variables
    int numOfInstallations =
      FileUtils.getInt(Params.SIM_PROPS, "installations");
    queue = new PriorityBlockingQueue<Event>(2 * numOfInstallations);
    // Read the different kinds of appliances
    String[] appliances =
      FileUtils.getStringArray(Params.APPS_PROPS, "appliances");
    // Read appliances statistics
    double[] ownershipPerc = new double[appliances.length];
    for (int i = 0; i < appliances.length; i++) {
      ownershipPerc[i] =
        FileUtils.getDouble(Params.DEMOG_PROPS, appliances[i] + ".perc");
    }
    // Create the installations and put appliances inside
    for (int i = 0; i < numOfInstallations; i++) {
      // Make the installation
      Installation inst =
        new Installation.Builder(i, i + "").registry(new Registry(i + "",
                                                                  endTick))
                .build();
      // Create the appliances
      for (int j = 0; j < appliances.length; j++) {
        double dice = RNG.nextDouble();
        if (dice < ownershipPerc[j]) {
          Appliance app = new Appliance.Builder(appliances[j], inst).build();
          inst.addAppliance(app);
          logger.trace(i + " " + appliances[j]);
        }
      }
      installations.add(inst);
    }
  }

}
