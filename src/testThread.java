
public class testThread extends Thread{
	
	int i;
	public testThread(int i) {
		this.i = i;
	}
	
	@Override
	public void run() {
		while(true){
			System.out.println("run: i = " + i);
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
