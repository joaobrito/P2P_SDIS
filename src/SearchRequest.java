
public class SearchRequest {
	
	private String id, filename;
	private int[] parts;
	
	public SearchRequest(String id, String filename) {
		this.filename = filename;
		this.id = id;
	}
	
	public SearchRequest(String id, String filename, int[] parts) {
		this.filename = filename;
		this.id = id;
		this.parts = parts;
	}
	
	public String toString() {
		return id + " " + filename + " " + parts.toString();
	}
	
}
