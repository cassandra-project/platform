package eu.cassandra.sim;

public class Level {
	
	private double price;
	
	private double level;
	
	public Level(double price2, double level2) {
		price = price2;
		level = level2;
	}
	
	public double getPrice() { return price; }
	
	public double getLevel() { return level; }

}
