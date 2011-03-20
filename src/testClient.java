import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class testClient {

	public static void main(String [] args) throws IOException{
		MulticastSocket msocket = null;
		try {
			msocket = new MulticastSocket(8967);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			msocket.joinGroup(InetAddress.getByName("224.0.2.10"));
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		byte[] buf = new byte [256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		
		System.out.println("receiving");
		
		try {
			msocket.receive(packet);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("receiving....");
		
		
		packet.getAddress();
		String msg = new String(packet.getData());
		System.out.println("mcast received: " + msg);
		
		DatagramPacket packet2 = new DatagramPacket("test response".getBytes(), "test response".getBytes().length, InetAddress.getByName("224.0.2.10"), 8967);
		System.out.println("sending message");
		msocket.send(packet2);
		System.out.println("message sent: " + packet2.getAddress());
	}
}
