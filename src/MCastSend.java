import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class MCastSend {

	private MulticastSocket socket;
	private DatagramPacket packet;
	private byte[] buf;
	private String address;
	private int mPort;
	public MCastSend(MulticastSocket socket, String address, int mPort) throws IOException {
		this.socket = socket;
		this.address = address;
		this.mPort = mPort;
		
		
	}
	
	public void sendMessage(String message) {
		
		try {
			packet = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName(address), mPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket.send(packet);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
