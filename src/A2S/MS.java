package A2S;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

/*
 * Implements Steam Master Server Protocol
 * */
public class MS {
	
	private static List<Byte> packet(String IP, String filter) {
		List<Byte> packet = new ArrayList<Byte>();
		
		packet.add((byte)0x31); // Header
		packet.add((byte)0xFF); // Region Code
		for(byte b : IP.getBytes()) { 
			packet.add((byte)b);
		}
		packet.add((byte)0x00);
		for(byte b : filter.getBytes()) { 
			packet.add((byte)b);
		}
		packet.add((byte)0x00);
		
		return packet;
	}
	// List<Byte> to byte array
	private static byte[] Byte2byte(List<Byte> list) {
		byte[] bytes = new byte[list.size()];
		for(int i = 0; i < list.size(); i++) {
			bytes[i] = list.get(i);
		}
		return bytes;
		
	}
	private static void parseIP(DatagramPacket packet) {
		ByteBuffer response = ByteBuffer.wrap(packet.getData());
		response.limit(packet.getLength());	
		
		while(response.hasRemaining()) {
			String addr = String.format("%d.%d.%d.%d:%d", response.get() & 0xFF, response.get() & 0xFF, response.get() & 0xFF, response.get() & 0xFF, response.getShort());
			System.out.println(addr);
			if(addr.equals("0.0.0.0:0")) break;
		}
	}
	public static void main(String[] args) {
		//System.out.println(packet("0.0.0.0:0", "\\gamedir\\mordhau\\name_match\\official"));
		DatagramSocket socket = null;
		String filter = "\\gamedir\\mordhau\\name_match\\Official Frontline*";
		//"\\gamedir\\mordhau\\name_match\\Official Frontline*"
	
		try {
			InetAddress host = InetAddress.getByName("hl2master.steampowered.com") ;
			socket =  new DatagramSocket();
			List<Byte> request = packet("0.0.0.0:0", filter);
			byte[] data = Byte2byte(request);
			
 	        DatagramPacket packet = new DatagramPacket( data, data.length, host, 27011) ;
	        socket.send(packet);
	        socket.setSoTimeout(1000) ;
	        packet.setData( new byte[4096] ) ;
	        socket.receive(packet) ;
	        
	        parseIP(packet);
	       

	        
	      
			
		}
		catch(Exception e)
		{
			socket.close();
		}
		

	}

}
