package P2P;

public class Chunk {
	private byte[] send;
	
	
	public Chunk(byte[] id, int number, byte[] data) {
		 
		send = new byte[1088];
		int i = 0;
		for(i = 0; i < id.length; i++)
			send[i] = id[i];
		System.out.println("id: bytes copied = " + i);
		byte[] n = intToByteArray(number);
		for(i = i ; i < n.length + id.length; i++)
			send[i] = n[i - id.length];
		System.out.println("n(" + n.length + "): bytes copied = " + (i - id.length));
		for(i = 0; i < data.length ; i++)
			send[i + 64] = data[i];
		System.out.println("data: " + i);
		i = 0;
		System.out.println(new String(send, 64 - i, send.length-65 + i));
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

}
