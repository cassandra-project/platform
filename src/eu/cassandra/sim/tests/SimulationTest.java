package eu.cassandra.sim.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Vector;

import org.junit.Test;
import org.mockito.Mock;



import eu.cassandra.sim.entities.appliances.Appliance;
import eu.cassandra.sim.entities.appliances.ConsumptionModel;
import eu.cassandra.sim.entities.installations.Installation;
import eu.cassandra.sim.entities.people.Person;
import eu.cassandra.sim.utilities.RNG;

import eu.cassandra.sim.Simulation;
import eu.cassandra.sim.SimulationParams;


public class SimulationTest {
	
	@Mock SimulationParams simulationWorld;
	@Mock String scenario ="{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" : 140.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 117.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 0.0, \"d\" : 73, \"s\": 0.0}]},{ \"n\" : 1, \"values\" : [ {\"p\" : 14.0, \"d\" : 20, \"s\": 0.0}, {\"p\" : 11.0, \"d\" : 18, \"s\": 0.0}, {\"p\" : 5.0, \"d\" : 73, \"s\": 0.0}]}]}";
	@Mock Installation inst1= new Installation.Builder("1", "HouseholdInst", "A household installation", "Low Voltage Installation").build();
	@Mock Installation inst2=new Installation.Builder("2", "IndustrialInst", "An industrial installation", "Medium Voltage Installation").build();
	@Mock Installation[] p=new Installation[2];
	
	
	@Test
	public void SimulationTest() throws Exception {
		RNG.init();
		
		Appliance app1=new Appliance.Builder("Appid1","App1", "App1Desc", "App1Type", inst1, new ConsumptionModel(scenario), 1f, true).build();
		Appliance app2=new Appliance.Builder("Appid2","App2", "App2Desc", "App2Type", inst1, new ConsumptionModel(scenario), 1f, true).build();
		Person person1=new Person.Builder("Personid1","Person1", "Person1Desc", "Person1Type", inst1).build();
		Person person2=new Person.Builder("Personid2","App2", "App2Desc", "App2Type", inst1).build();
		p[0]=inst1;
		p[1]=inst2;
		
		
		Simulation sim=mock(Simulation.class);
		when(sim.getEndTick()).thenReturn(7);
		when(sim.getInstallation(0)).thenReturn(p[0]);
		
		when(sim.getCurrentTick ()).thenReturn(0);
		when(sim.getSimulationWorld ()).thenReturn(simulationWorld);
		sim.setup();
		sim.run();
		
		 assertEquals(7, sim.getEndTick());
		 assertEquals(p[0], sim.getInstallation(0));
		 assertEquals(0, sim.getCurrentTick ());
		 assertEquals(simulationWorld, sim.getSimulationWorld ());
	}
	
	

}
