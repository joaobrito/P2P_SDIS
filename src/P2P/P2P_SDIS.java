package P2P;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;


public class P2P_SDIS extends Thread{

	private static int mcastPort;
	private static int port;
	private static String mcastAddr;
	private static MulticastSocket mSocket;
	private static MCastSend mCastSend;
	protected static HashMap<String, SearchRequest> myRequests;
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		if (args.length != 3){
			System.out.println("java P2P_SDIS_SERVER <mcast port> <mcastAddress> <port>");
			return;
		}
		myRequests = new HashMap<String, SearchRequest>();
		mcastPort = Integer.parseInt(args[0]);
		port = Integer.parseInt(args[2]);
		mcastAddr = args[1];
		
		try {
			mSocket = new MulticastSocket(mcastPort);
		} catch (IOException e1) {
			System.out.println("Error - init Multicast Socket");
		}
		mSocket.joinGroup(InetAddress.getByName(mcastAddr));
		mCastSend = new MCastSend(mSocket, mcastAddr, mcastPort);
		
		MCastReiceiveThread threadReiceive = new MCastReiceiveThread(mSocket, mCastSend, myRequests);
		threadReiceive.start();
		Scanner scan = new Scanner(System.in);
		String command;
		do{
			System.out.print(":> ");
			command = scan.nextLine();
			parseCommand(command);
		}
		while(command.compareTo("quit") != 0);	
	}
	
	public static void parseCommand(String command){
		String[] com = command.split(" ");
		if (com[0].toLowerCase().compareTo("search") == 0){
			int rndId = Math.abs((new Random()).nextInt());
			String msg = "search id" + rndId + "" + command.substring(command.indexOf(" "));
			myRequests.put("id" + rndId, new SearchRequest("id" + rndId, command.substring(command.indexOf(" "))));
			mCastSend.sendMessage(msg);
//			System.out.println("msg = " + command.substring(command.indexOf(" ")));
		}
		else if(com[0].toLowerCase().compareTo("get") == 0)
			;
		else
			System.out.println("unknown command: \"" + com[0] + "\"");
	}
}
