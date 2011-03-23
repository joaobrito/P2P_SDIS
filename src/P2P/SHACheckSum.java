package P2P;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class SHACheckSum{

	
	public static String getHexFormat(File file) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		FileInputStream fis = new FileInputStream(file);

		byte[] dataBytes = new byte[1024];
		int nread = 0;
		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		};
		byte[] mdbytes = md.digest();
		//convert the byte to hex format
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}

 		/*System.out.println("Hex format : " + sb.toString());
		System.out.println("Hex format length : " + sb.toString().length());
		System.out.println("MD length : " + mdbytes.length);*/
		return sb.toString();
	}
} 