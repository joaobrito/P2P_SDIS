package P2P;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Comunication {
	private int mcastPort;
	private String mcastAddr;
	private MulticastSocket mSocket;
	private static HashMap<String, SearchRequest> myRequests, requests;
	private Semaphore semData, semCommand;
	private HashMap<String, File> toReceive;
	public Comunication(int mCastPort, String mCastAddress) throws Exception{
		toReceive = new HashMap<String, File>();
		this.mcastPort = mCastPort;
		semData = new Semaphore(1,true);
		semCommand = new Semaphore(1,true);
		myRequests = new HashMap<String, SearchRequest>();
		
		try {
			mSocket = new MulticastSocket(mCastPort);
		} catch (IOException e1) {
			System.out.println("Error - init Multicast Socket");
		}

		mSocket.joinGroup(InetAddress.getByName(mCastAddress));
		
		Scanner scan = new Scanner(System.in);
		String command;
		receiveCommandThread r = new receiveCommandThread();
		r.start();
		do{
			System.out.print(":> ");
			command = scan.nextLine();
			parseCommand(command);
		}
		while(command.compareTo("quit") != 0);
	}
	
	public void parseCommand(String command){
		String[] com = command.split(" ");
		System.out.println("Command = " + command + "\ncenas: " + command.substring(command.indexOf(" ")));
		if (com[0].toLowerCase().compareTo("search") == 0){
			int rndId = Math.abs((new Random()).nextInt());
			String msg = "search id" + rndId + "" + command.substring(command.indexOf(" "));
			myRequests.put("id" + rndId, new SearchRequest("id" + rndId, command.substring(command.indexOf(" "))));
			sendCommand(msg);
		}
		else if(com[0].toLowerCase().compareTo("get") == 0){
			new sendDataThread(com[1]);
		}
		else
			System.out.println("unknown command: \"" + com[0] + "\"");
	}
	
	private void sendCommand(String message){
		DatagramPacket packet = null;
		try {
			semCommand.acquire();
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			packet = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName(mcastAddr), mcastPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		System.out.println("sending: " + new String(packet.getData()));
		try {
			mSocket.send(packet);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		semCommand.release();
	}
	
	class receiveCommandThread extends Thread{
		public receiveCommandThread() {
			// TODO Auto-generated constructor stub
		}
		
		public void run() {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			
			try {
				mSocket.receive(packet);
			} catch (IOException e) {
				System.out.println("Error - Receiving multicast packet");
			}
			
			String msg = new String(packet.getData());
//			System.out.println("Message received: " + msg);
			
			String parts[] = msg.split(" ");
//			System.out.println("parts[0] = " + parts[0]);
			
			if(parts[0].toLowerCase().compareTo("search") == 0){

				/*
				 * Procura um o ficheiro que foi pedido
				 */
				
				String fName = msg.substring(parts[0].length() + parts[1].length() + 2);
				System.out.println("searching: " + fName);
				File f = FileTransfer.searchFile(fName); /* Foi encontrado um ficheiro pedido */
			
				if(f != null){
					/*Ficheiro pedido existe neste cliente*/
					String message = null;
					try {
						message = "FOUND " + parts[1] + " " + SHACheckSum.getHexFormat(f) + " " + f.length() + " " + f.getName();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendCommand(message);
				}
				
			}else if (parts[0].toLowerCase().compareTo("found") == 0){
				if(myRequests.containsKey(parts[1])){
					System.out.println("received: " + msg);
					myRequests.get(parts[1]).setExistPeers(true);
				}
			}
		}
	}
	
	class receiveData extends Thread{

		public receiveData() {
		}
		
		public void run() {
			try {
				semData.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			byte[] buf = new byte[1088];
			
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				mSocket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			semData.release();
			
			Chunk c = new Chunk(packet.getData());
			
		}
	}
	
	class sendDataThread extends Thread{
		String idRequest;
		public sendDataThread(String idRequest) {
			this.idRequest = idRequest;
		}
		
		public void run() {
			SearchRequest r = idRequest).getParts().length;
			while(r.getParts().length != 0){
				int rnd = new Random().nextInt(r.getParts().length);
				r = myRequests.get(idRequest);
			}
		}
	}
	
	
}
