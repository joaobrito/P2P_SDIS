package P2P;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Comunication {
	private int mCastPort;
	private String mCastAddress;
	private MulticastSocket mSocketCommand, mSocketData ;
	private static HashMap<String, SearchRequest> myRequests, getRequests, requests;
	private Semaphore semData, semCommand;
	private HashMap<String, File> toReceive;
	public Comunication(int mCastPort, String mCastAddress) throws Exception{

		// Variable Initialization
		this.mCastAddress = mCastAddress;
		toReceive = new HashMap<String, File>();
		this.mCastPort = mCastPort;
		semData = new Semaphore(1,true);
		semCommand = new Semaphore(1,true);
		myRequests = new HashMap<String, SearchRequest>();
		requests = new HashMap<String, SearchRequest>();
		
		// Socket Initialization
		mSocketCommand = new MulticastSocket(mCastPort);
		mSocketData = new MulticastSocket(8966);
		
		// Socket Group join
		mSocketCommand.joinGroup(InetAddress.getByName(mCastAddress));
		mSocketData.joinGroup(InetAddress.getByName(mCastAddress));
		
		// Start Command Listener Thread 
		receiveCommandThread r = new receiveCommandThread();
		r.start();
		String command = null;
		//Command Line Scanner
		do{
			Scanner scan = new Scanner(System.in);
			System.out.print(":> ");
			command = scan.nextLine();
			System.out.println("command: " + command);
			parseCommand(command);
		}
		while(command.toLowerCase().compareTo("quit") == 0);
		
	}
	
	public void parseCommand(String command){
		String[] com = command.split(" ");
		System.out.println("Command = " + command + "\ncenas: " + command.substring(command.indexOf(" ")));
		if (com[0].toLowerCase().compareTo("search") == 0){
			int rndId = Math.abs((new Random()).nextInt());
			String msg = "search id" + rndId + "" + command.substring(command.indexOf(" "));
			requests.put("id" + rndId, new SearchRequest("id" + rndId, command.substring(command.indexOf(" "))));
			sendCommand(msg);
		}
		else if(com[0].toLowerCase().compareTo("get") == 0){
			if(requests.containsKey(com[1]))
			{
				SearchRequest tmp = requests.remove(com[1]);
				myRequests.put(tmp.getId(), tmp);
				String msg = "GET " + com[1];
				sendCommand(msg);
				
			}
			else
				System.out.println("no peers available or incorrect search id");
		}
		else
			System.out.println("unknown command: \"" + com[0] + "\"");
	}
	
	private void sendCommand(String message) {
		DatagramPacket packet = null;
		try {
			semCommand.acquire();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(mCastAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("packet addr = " + addr + " - " + mCastAddress);
		
		packet = new DatagramPacket(message.getBytes(), message.getBytes().length, addr, mCastPort);

//		System.out.println("sending: " + new String(packet.getData()));
		try {
			mSocketCommand.send(packet);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		semCommand.release();
	}
	
	class receiveCommandThread extends Thread{
		public receiveCommandThread() {
		}
		
		public void run() {
			while(true){
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				System.out.println("waiting packet");
				try {
					mSocketCommand.receive(packet);
				} catch (IOException e) {
					System.out.println("Error - Receiving multicast packet");
				}
				
				String msg = new String(packet.getData());
				System.out.println("Message received: " + msg);
				
				String parts[] = msg.split(" ");
//				System.out.println("parts[0] = " + parts[0]);
				
				if(parts[0].toLowerCase().compareTo("search") == 0){
	
					String fName = msg.substring(parts[0].length() + parts[1].length() + 2);
					File f = FileTransfer.searchFile(fName); 
					
					/* Foi encontrado um ficheiro pedido */
					if(f != null){
						requests.put(parts[1], new SearchRequest(parts[1],msg.substring(msg.indexOf(parts[1]))));
						/*Ficheiro pedido existe neste cliente*/
						String message = null;
						try {
							message = "FOUND " + parts[1] + " " + SHACheckSum.getHexFormat(f) + " " + f.length() + " " + f.getName();
						} catch (Exception e) {
							e.printStackTrace();
						}
						sendCommand(message);
					}
					
				}else if (parts[0].toLowerCase().compareTo("found") == 0){
					if(myRequests.size() > 0 && myRequests.containsKey(parts[1])){
						SearchRequest s = myRequests.get(parts[1]);
						System.out.println("parts[3] = " + parts[3]);
						s.setSize(Integer.parseInt(parts[3]));
						s.setId(parts[1]);
						System.out.println(msg);
					}
				}
				else if(parts[0].toLowerCase().compareTo("get") == 0){
					for(SearchRequest s : requests.values())
						System.out.println("id: " + s.getId() + " - " + s.getFilename());
					if(requests.containsKey(parts[1]))
						System.out.println("Existe o ficheiro vai ser enviado");
				}
			}
		}
	}
	
	class receiveData extends Thread{

		SearchRequest req;
		public receiveData(String sha) {
			this.req = myRequests.remove(sha);
		}
		
		public void run() {
			System.out.println("Receiving Data");
/*			try {
				semData.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	*/		
			while(req.getParts().size()> 0){
				byte[] buf = new byte[1088];
				
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					mSocketCommand.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		//			semData.release();
				Chunk c = new Chunk(packet.getData());
				
				
			}
		}
	}
	
	class sendDataThread extends Thread{
		String idRequest;
		public sendDataThread(String idRequest) {
			this.idRequest = idRequest;
		}
		
		public void run() {
			SearchRequest r = getRequests.get(idRequest);
			while(r.getParts().size() != 0){
				int rnd = new Random().nextInt(r.getParts().size());
				r = myRequests.get(idRequest);
			}
		}
	}
	
	
}
