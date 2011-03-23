
public class test extends Thread{

	static int i;
	
	public static void test(int i){
		while (true){
			System.out.println("test: i = " + i);
			i++;
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		i=2;
		new testThread(i).start();
		test(i);
	}

	
}
