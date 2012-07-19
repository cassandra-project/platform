package eu.cassandra.server.threads;

public class DemoThread implements Runnable {
	
	String message;
	
	int a;
	
	public DemoThread() { }
	
	public DemoThread(String amessage) {
		message = amessage;
	}
	
	public int getA() {
		return a;
	}
	
	public void run() {
		int a = 0;
		for(int i = 0; i < 5000000; i++) {
			for(int j = 0; j < 1000; j++) {
				for(int k = 0; k < 1000; k++) {
					double b = Math.pow(2, 2);
				}
			}
			a++;
			if((i % 100) == 0) {
				System.out.println("Hello from a thread! " + 
						a + " - " + 
						message
						);
			}
		}
	}

}
