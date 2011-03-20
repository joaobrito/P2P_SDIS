import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;


public class MCastReiceiveThread extends Thread{
	
	MulticastSocket mSocket;
	FileTransfer files;
	MCastSend MCSend;
	public MCastReiceiveThread(MulticastSocket mSocket, MCastSend MCSend) {
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
			System.out.println("Message received: " + msg);
			
			String parts[] = msg.split(" ");
			System.out.println("parts[0] = " + parts[0]);
			if(parts[0].toLowerCase().compareTo("search") == 0){
				String fName = msg.substring(parts[0].length() + parts[1].length() + 2);
				System.out.println("searching: " + fName);
				File f = FileTransfer.searchFile(fName); 
				if(f != null)
					try {
						MCSend.sendMessage("FOUND " + parts[1] + " " + SHACheckSum.getHexFormat(f) + " " + f.length() + " " + f.getName());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					System.out.println("File not found");
			}
		}
	}
	
}
