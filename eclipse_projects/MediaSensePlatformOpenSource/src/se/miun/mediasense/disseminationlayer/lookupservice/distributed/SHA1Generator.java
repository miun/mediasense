package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Generator {
	/**
	 * convert the sha1 byte array to a hex string for easier human reading
	 * @param data sha1 key in byte array
	 * @return sha1 key as hex string
	 */
	public static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 
	
	/**
	 * Generate a sha1 from a String..
	 * @param text string to hash
	 * @return hash value as hex string
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
    public static byte[] SHA1(String text) { 
    	try {
    		return SHA1(text.getBytes("iso-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public static byte[] SHA1(byte[] data) {
	    MessageDigest md;

	    try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(data);
		    sha1hash = md.digest();
		    return sha1hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		    return null;
		}
    }
}
