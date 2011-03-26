package P2P;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;


public class SearchRequest {
	
	private String id, filename, sha;
	private Vector<Integer> parts; //parts missing
	private int size;
	private File file;
	
	public SearchRequest(String id, String filename) {
		this.filename = filename;
		this.id = id;
	}
	
	public SearchRequest(String id, String filename, int[] parts) {
		this.filename = filename;
		this.id = id;
	}
	
	public SearchRequest(String message){
		
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setParts(Vector<Integer> parts) {
		this.parts = parts;
	}

	public Vector<Integer> getParts() {
		return parts;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public void attachToFile(Chunk chunk) throws IOException{
		
		if(file == null){
			file = new File("./tmp/" + this.filename);
			if(!file.exists())
				file.createNewFile();
		}
		
		RandomAccessFile rnd = new RandomAccessFile(file, "rw");
		System.out.println("chunk = " + chunk.getNumber());
        rnd.seek(chunk.getNumber() * 1024);
        if(chunk.getNumber()*1024 + 1024 < size)
        	rnd.write(chunk.getData());
        else
        	rnd.write(chunk.getData(), 0, (int) (size - chunk.getNumber()*1024));
//        System.out.println("seek = " + c1.getNumber()*1024 + "\t" + i*1024 + "\nCopied: " + c1.getData().length);
        
	}
	
}
