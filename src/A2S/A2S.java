package A2S;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class A2S {
	/*
	 * Servers to test
	 * 181.214.149.99:27015
	 * 	173.234.24.146:27059
	 * 176.57.135.74:28815
	 * */

	private static String readNullTerminatedString(ByteBuffer data) throws IOException {
		String str = new String();
		int _byte = data.get();
		while(_byte != 0) {
			str = str.concat(String.valueOf((char)_byte));
			_byte = data.get();
		}
		return str;
		
	}
	// Parse incoming server data
	private static Map<String, Object> read(DatagramPacket packet) throws IOException
	{
		Map<String, Object> info = new HashMap<String, Object>();
		ByteBuffer response = ByteBuffer.wrap(packet.getData());
		
		response.position(response.position() + 4); // Skip 4 bytes
		
		info.put("header", (char)response.get());
		info.put("protocol", response.get());
		info.put("name", readNullTerminatedString(response));
		info.put("map", readNullTerminatedString(response));
		info.put("folder", readNullTerminatedString(response));
		info.put("game", readNullTerminatedString(response));
		info.put("id", response.getShort());
		info.put("players", response.get());
		info.put("max-players", response.get());
		info.put("bots", response.get());
		info.put("server-type", (char)response.get());
		info.put("enviroment", (char)response.get());
		info.put("visibility", response.get());
		info.put("vac", response.get());	
		info.put("version", readNullTerminatedString(response));
		response.position(response.position() + 1);
		response = response.order(ByteOrder.LITTLE_ENDIAN);
		info.put("game-port", response.getShort());
		
		
		return info;
  	
	}
	public static Map<String, Object> info(String IP, int port){
		
		DatagramSocket socket = null;
		Map<String, Object> results =  new HashMap<String, Object>();;
	
		try {
			InetAddress host = InetAddress.getByName(IP) ;
			socket =  new DatagramSocket();
		
			byte [] data = {
					(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x54, 
					(byte)0x53, (byte)0x6F, (byte)0x75, (byte)0x72, (byte)0x63,
					(byte)0x65, (byte)0x20, (byte)0x45, (byte)0x6E, (byte)0x67,
					(byte)0x69, (byte)0x6E, (byte)0x65, (byte)0x20, (byte)0x51,
					(byte)0x75, (byte)0x65, (byte)0x72, (byte)0x79, (byte)0x00};
			
	        DatagramPacket packet = new DatagramPacket( data, data.length, host, port ) ;
	        socket.send(packet);
	        socket.setSoTimeout(2000) ;
	         
	        packet.setData( new byte[4096] ) ;
	        socket.receive(packet) ;
	        
	        results = read(packet);

		}
		catch(Exception e)
		{
			socket.close();
		}
		socket.close();
		return results;
		
		
		
	}
		

}