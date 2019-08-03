package steam;

public class main {

	public static void main(String[] args) throws Exception {
		String filter = "\\gamedir\\mordhau\\name_match\\Official *";
		
		MS ms = new MS(filter);
		
		for(Object[] IP : ms.query()) {
			
			A2S info = new A2S(IP[0].toString(), (int)IP[1]);
			System.out.printf("Name: %s Players: %d%n", info.query().get("name"), info.query().get("players"));
		}
	
	}

}
