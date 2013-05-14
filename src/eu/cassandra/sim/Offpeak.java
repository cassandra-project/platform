package eu.cassandra.sim;

public class Offpeak {
	
	private double from;
		
	private double to;
		
	public Offpeak(int afrom, int ato) {
		from = afrom;
		to = ato;
	}
		
	public double getFrom() { return from; }
		
	public double getTo() { return to; }

}
