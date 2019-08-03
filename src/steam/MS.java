package steam;



import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/*
 * Implements Steam Master Server Protocol
 * */
public class MS {
	
	private String filter;
	
	public MS(String filter) {
		this.filter = filter;
	}
	
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
	
	private static List<Object[]> parseIP(DatagramPacket packet) {
		ByteBuffer response = ByteBuffer.wrap(packet.getData());
		List<Object[]> IPs = new ArrayList<Object[]>();
		
		response.limit(packet.getLength());	
		response.position(response.position() + 6);
		while(response.hasRemaining()) {
			String IP = String.format("%d.%d.%d.%d", response.get() & 0xFF, response.get() & 0xFF, response.get() & 0xFF, response.get() & 0xFF);
			int port = (int) (response.getShort() & 0xffff) ;
			IPs.add(new Object[] {IP, port});		
			if(IP.equals("0.0.0.0")) break;
		}
		return IPs;
	}
	public List<Object[]> query() throws Exception {
		
		DatagramSocket socket = null;
		String lastIP = new String();
		List<Object[]> IPs = new ArrayList<Object[]>();
		
		InetAddress host = InetAddress.getByName("hl2master.steampowered.com") ;
		socket =  new DatagramSocket();
		
		socket.setSoTimeout(3000);
		
		while(true)
		{
			List<Byte> request = packet(lastIP, this.filter);
			byte[] data = Byte2byte(request);
			
			DatagramPacket packet = new DatagramPacket( data, data.length, host, 27011) ;
	
		    socket.send(packet);
		    packet.setData( new byte[4096] ) ;
		    socket.receive(packet) ;
		        
		    List<Object[]> IPbatch = parseIP(packet);
		    int lastElementIndex = IPbatch.size() - 1;
		    
		    IPs.addAll(IPbatch);
		    lastIP = String.format("%s:%d", IPbatch.get(lastElementIndex)[0],  IPbatch.get(lastElementIndex)[1]);
		
		    if(lastIP.equals("0.0.0.0:0")) {
		    	IPbatch.remove(lastElementIndex);
		    	break;
		    }
		}
		
	    socket.close();
	    return IPs;
	    

	}

}
