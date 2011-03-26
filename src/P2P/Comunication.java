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

public class Comunication {
	private int mCastCommandPort, mCastDataPort;
	private String mCastAddress;
	private MulticastSocket mSocketCommand, mSocketData ;
	private HashMap<String, SearchRequest> myRequests, getRequests, requests;
//	private Semaphore semData, semCommand;
	public Comunication(int mCastCommandPort, String mCastAddress, int mCastDataPort) throws Exception{

		// Variable Initialization
		this.mCastAddress = mCastAddress;
		this.mCastCommandPort = mCastCommandPort;
		this.mCastDataPort = mCastDataPort;
//		semData = new Semaphore(1,true);
//		semCommand = new Semaphore(1,true);
		myRequests = new HashMap<String, SearchRequest>();
		requests = new HashMap<String, SearchRequest>();
		
		// Socket Initialization
		mSocketCommand = new MulticastSocket(mCastCommandPort);
		mSocketData = new MulticastSocket(mCastDataPort);
		
		// Socket Group join
		mSocketCommand.joinGroup(InetAddress.getByName(mCastAddress));
		mSocketData.joinGroup(InetAddress.getByName(mCastAddress));
		
		// Start Command Listener Thread 
		ReceiveCommandThread r = new ReceiveCommandThread();
		r.start();
		String command = null;
		//Command Line Scanner
		do{
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Scanner scan = new Scanner(System.in);
			System.out.print(":> ");
			command = scan.nextLine();
			System.out.println("command: " + command);
			parseCommand(command);
		}
		while(command.toLowerCase().compareTo("quit") != 0);
		
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
			
			
			//problema com o SHA
			System.out.println("com[1].lenght = " + com[1].length()+ "\n" + myRequests.values().iterator().next().getSha().length() + " => " + com[1].getBytes().length);
			if(myRequests.containsKey(com[1]))
			{
				/*
				 * Caso em que é feito o pedido de um ficheiro
				 * 
				 * Falta ainda considerar que se pode pedir certas partes do ficheiro
				 * 
				 * */
				String msg = "GET " + com[1];
				if(myRequests.containsKey(com[1])){
					if(com.length < 2){
						int nChunks = (int)((myRequests.get(com[1]).getSize()-1)/1024);
						Vector<Integer> parts = new Vector<Integer>();
						for(int i = 0; i <= nChunks; i++)
							parts.add(i);
						SearchRequest tmp = myRequests.remove(com[1]);
						tmp.setParts(parts);
						myRequests.put(tmp.getSha(), tmp);
					}
					else{
						// Caso em que os chunks a obter estão definidos
					}
					sendCommand(msg);
				}
				
				
			}
			else
				System.out.println("no peers available or incorrect search id");
		}
		else
			System.out.println("unknown command: \"" + com[0] + "\"");
	}
	
	private void sendCommand(String message) {
		DatagramPacket packet = null;
		/*try {
			semCommand.acquire();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		*/
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(mCastAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("packet addr = " + addr + " - " + mCastAddress);
		
		packet = new DatagramPacket(message.getBytes(), message.getBytes().length, addr, mCastCommandPort);

//		System.out.println("sending: " + new String(packet.getData()));
		try {
			mSocketCommand.send(packet);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		semCommand.release();
	}
	
	class ReceiveCommandThread extends Thread{
		public ReceiveCommandThread() {
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
				
				/*
				 * Parsing das mensagens de comandos recebidas
				 * 
				 * */
				
				
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
					if(requests.size() > 0 && requests.containsKey(parts[1])){
						System.out.println("parts[3] = " + parts[1].length());

						SearchRequest tmp = requests.remove(parts[1]);
						tmp.setSize(Integer.parseInt(parts[3]));
						tmp.setSha(parts[1]);
						myRequests.put(tmp.getSha(), tmp);
						System.out.println(msg);
					}
				}
				else if(parts[0].toLowerCase().compareTo("get") == 0){
					/*
					 * Recebido um pedido de envio de um ficheiro
					 * 
					 * */
//					
//					for(SearchRequest s : requests.values())
//						System.out.println("id: " + s.getId() + " - " + s.getFilename());
//					if(requests.containsKey(parts[1]))
//						System.out.println("Existe o ficheiro vai ser enviado");
				}
			}
		}
	}
	
	class ReceiveDataThread extends Thread{

		public ReceiveDataThread() {
		}
		
		public void run() {
			System.out.println("Receiving Data");

			while(myRequests.size() > 0){
				byte[] buf = new byte[1088];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					mSocketData.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Chunk c = new Chunk(packet.getData());
				int sha = c.getSHA();
				System.out.println("packet sha = " + sha);
				
				if(myRequests.containsKey("" + sha)){
					// Casos em que se recebe um packet que está nos pedidos de ficheiros pendentes
					SearchRequest tmp = myRequests.remove("" + sha);
					Vector<Integer> parts = tmp.getParts();
					//Retirar das parts que faltam receber desse ficheiro
					if(parts.contains(c.getNumber())){
						parts.remove(c.getNumber());
						tmp.setParts(parts);
				
						try {
							tmp.attachToFile(c);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(!parts.isEmpty())
							myRequests.put(tmp.getSha(), tmp);
						else{
							// Ficheiro já está completo
							;
						}
					}
					//adicionar ao ficheiro temporário para onde se esta a receber a informação
					
					
				}
				
				//Tirar dos pedidos a enviar caso la exista uma ocorrência

					
			}
		}
	}
	
	class SendDataThread extends Thread{
		String idRequest;
		public SendDataThread(String idRequest) {
			this.idRequest = idRequest;
		}
		
		public void run() {
			SearchRequest r = getRequests.get(idRequest);
			while(r.getParts().size() != 0){
				int rnd = new Random().nextInt(r.getParts().size());
				r = myRequests.get(idRequest);
//				DatagramPacket packet = new DatagramPacket(, length)
			}
		}
	}
	
	
}
