package P2P;
import java.io.File;
import java.io.FilenameFilter;


public class FileTransfer {
	
	private static File dir;
	public FileTransfer(String folder) {
		dir = new File(folder);
	}
	
	public static void listFiles(){
		File[] fileList = dir.listFiles();
		for(File s : fileList)
			System.out.println(s.getName() + "\tsize = " + s.length());
	}
	
	public static File searchFile(String fileName){
		dir = new File("/home/joao/Desktop");
		
		for(File f: dir.listFiles()){
			if((f.getName()).compareTo(fileName.trim()) == 0){
//				System.out.println("Ficheiro encontrado");
				return f; 
			}
//			System.out.println(f.getName() + " = " + fileName + "\t=> " + (f.getName()).compareTo(fileName));
			
		}
		return null;
	}
	
	public static void main(String[] args){
		dir = new File("/home/joao/Desktop");
		listFiles();
		if(args.length > 0)
			System.out.println(searchFile(args[0]));
	}
}
