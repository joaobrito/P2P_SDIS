package P2P;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.HashMap;


public class MCastReiceiveThread extends Thread{
	
	MulticastSocket mSocket;
	FileTransfer files;
	MCastSend MCSend;
	HashMap<String, SearchRequest> myRequests;
	
	public MCastReiceiveThread(MulticastSocket mSocket, MCastSend MCSend, HashMap<String, SearchRequest> myRequests) {
		this.myRequests = myRequests;
		this.mSocket = mSocket;
		files = new FileTransfer("/home/joao/Desktop/");
		this.MCSend = MCSend;
	}
	
	public void run() {
		while(true){
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

				/*("received: " + msg);
					myReq
				 * Procura um o ficheiro que foi pedido
				 */
				
				String fName = msg.substring(parts[0].length() + parts[1].length() + 2);
				System.out.println("searching: " + fName);
				File f = FileTransfer.searchFile(fName); /* Foi encontrado um ficheiro pedido */
			
				if(f != null){
					
					/*Ficheiro pedido existe neste cliente*/
					try {
						MCSend.sendMessage("FOUND " + parts[1] + " " + SHACheckSum.getHexFormat(f) + " " + f.length() + " " + f.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}else if (parts[0].toLowerCase().compareTo("found") == 0){
				if(myRequests.containsKey(parts[1])){
					System.out.println("received: " + msg);
					myRequests.get(parts[1]).setExistPeers(true);
				}
			}
		}
	}
	
}
