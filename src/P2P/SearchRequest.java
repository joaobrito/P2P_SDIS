package P2P;


public class SearchRequest {
	
	private String id, filename;
	private int[] parts;	
	private boolean existPeers;
	
	public SearchRequest(String id, String filename) {
		this.filename = filename;
		this.id = id;
		this.setExistPeers(false);
	}
	
	public SearchRequest(String id, String filename, int[] parts) {
		this.filename = filename;
		this.id = id;
	}
	
	public void setExistPeers(boolean hasPeers) {
		this.existPeers = hasPeers;
	}

	public boolean existPeers() {
		return existPeers;
	}
	
	public String getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public void setParts(int[] parts) {
		this.parts = parts;
	}

	public int[] getParts() {
		return parts;
	}
	
	
}
