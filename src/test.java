import java.util.Vector;


public class test extends Thread{

	static int i;
	
	
	public static void main(String[] args) {
		Vector<Integer> vec = new Vector<Integer>();
		vec.add(2);
		vec.add(6);
		vec.add(5);
		
		System.out.println("Contains 5 = " + vec.contains(5));
		System.out.println("Contains 7 = " + vec.contains(7));
		System.out.println("Contains 9 = " + vec.contains(9));
		
	}

	
}

