package P2P;
import java.io.IOException;
import java.net.UnknownHostException;


public class P2P_SDIS extends Thread{

	private static int mcastPort;
	private static int port;
	private static String mcastAddr;
	public static void main(String[] args) throws UnknownHostException, IOException {
		if (args.length != 3){
			System.out.println("java P2P_SDIS_SERVER <mcast port> <mcastAddress>");
			return;
		}
		mcastPort = Integer.parseInt(args[0]);
		port = Integer.parseInt(args[2]);
		mcastAddr = args[1];
		
		try {
			new Comunication(mcastPort, mcastAddr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
