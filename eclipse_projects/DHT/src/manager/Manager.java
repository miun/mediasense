package manager;


public class Manager {
	Communication communication;
	
	public static void main(String[] args) {
		new Manager();
	}
	
	Manager() {
		//Create communication object
		communication = new Communication();
		
	}
}
