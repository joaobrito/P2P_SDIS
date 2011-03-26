package P2P;


public class P2P_SDIS extends Thread{

	private static int mCastCommandPort, mCastDataPort;
	private static String mcastAddr;
	public static void main(String[] args)  {
		if (args.length != 3){
			System.out.println("java P2P_SDIS_SERVER <mcast command port> <mcastAddress> <mcast data port>");
			return;
		}
		mCastCommandPort = Integer.parseInt(args[0]);
		mcastAddr = args[1];
		mCastDataPort = Integer.parseInt(args[2]);
		
//		port = Integer.parseInt(args[2]);
		
		
		try {
			new Comunication(mCastCommandPort, mcastAddr, mCastCommandPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
