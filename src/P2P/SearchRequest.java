package P2P;

import java.util.Vector;


public class SearchRequest {
	
	private String id, filename;
	private Vector<Integer> parts; //parts missing
	private int size;
	
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
	
	
	
}
