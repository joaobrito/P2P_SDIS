package P2P;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;


public class MCastReceiveData extends Thread{
	
	MulticastSocket mSocket;
	DatagramPacket packet;
	HashMap<String, SearchRequest> myRequests;
	
	public MCastReceiveData(HashMap<String, SearchRequest> myRequests) throws IOException {
		mSocket = new MulticastSocket(8966);
		this.myRequests = myRequests;
	}
	
	@Override
	public void run() {
		try {
			mSocket.joinGroup(InetAddress.getByName("224.0.2.10"));
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true){
			while(!myRequests.isEmpty()){
				byte[] buf = new byte[1280];
				packet = new DatagramPacket(buf, buf.length);
				//falta processar msg
				
			}
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
