package P2P;
import java.io.File;
import java.io.RandomAccessFile;


public class Chunk {
	private byte[] send;
	
	
	public Chunk(byte[] id, int number, File file) throws Exception {
		
		RandomAccessFile in = new RandomAccessFile(file, "r");
		byte[] data = new byte[1024];
		if(1024*number < in.length())
			in.seek(1024*(number));
		else
			in.seek(in.length());
		in.read(data);
		
		send = new byte[1088];
		int i = 0;
		for(i = 0; i < id.length; i++)
			send[i] = id[i];
		System.out.println("id: bytes copied = " + i);
		byte[] n = intToByteArray(number);

		for(i = id.length ; i < n.length + id.length; i++)
			send[i] = n[i - id.length];
		System.out.println("n(" + n.length + "): bytes copied = " + (i - id.length));
		
		for(i = 0; i < data.length ; i++)
			send[i + 64] = data[i];
		System.out.println("data: " + i);
		i = 0;
		System.out.println(new String(send, 64 - i, send.length-65 + i));
		in.close();
	}
	
	public Chunk(byte[] data) {
		this.send = data;
	}
	
	public byte[] getChunkToSend() {
		return send;
	}

	public int byteArrayToInt(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
	}

	public byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
	}
	
	public String getData(){
		return new String(send, 64, 1024);
	}
	
	public int getNumber(){
		
		byte[] num = new byte[32];
		for(int i = 32; i < 64; i++)
			num[i - 32] = send[i];
		int number = byteArrayToInt(num);
		System.out.println("Chunk number decode = " + number);
		return number;
	}

}
