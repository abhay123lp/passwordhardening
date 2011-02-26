package com.scs.pwdHardening;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.scs.pwdHardening.model.Question;
import com.scs.pwdHardening.model.ResponseType;
import com.scs.pwdHardening.model.User;

public class History {
	
	public static byte[] encrypt(BigInteger key, Map<Question, ResponseType> userResponse, User user){
		
		try{
			String res = "Decryption Successful\n";
			Set<Map.Entry<Question, ResponseType>> set= userResponse.entrySet();
		    for(Map.Entry<Question, ResponseType> me : set) {
		    	ResponseType response = me.getValue(); 
		    	res += response.value;  	
		    }
		    res = res + "\n";
		    res += "END_OF_FILE";
			
			byte[] unpaddedKey = key.toByteArray();
			byte[] secretKey = new byte[16];
			for(int idx = 0; idx < 16; ++idx){
				secretKey[idx] = idx <= unpaddedKey.length - 1 ? unpaddedKey[idx] : (byte)0;
			}
			
			/*byte [] salt = new byte[8];
			new SecureRandom().nextBytes(salt);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec pkSpec = new PBEKeySpec(key.toString().toCharArray(), salt, 1024, 256);
			SecretKey tmp = factory.generateSecret(pkSpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");*/
			
			
			SecretKeySpec keySpec = null;
			keySpec = new SecretKeySpec(secretKey, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
	        byte[] input = res.getBytes("UTF-8");
	        byte[] paddedInput = new byte[1024];
	        for(int i = 0; i < 1024; ++i){
	        	paddedInput[i] = i <= input.length - 1 ? input[i] : (byte)0;
	        }
		    byte[] encrypted = cipher.doFinal(paddedInput);
		    user.setIv(cipher.getIV());
		    return encrypted;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(byte[] key, byte[] encrypted, byte[] iv){
		
		try{
		//IvParameterSpec dps = new IvParameterSpec(iv);
			
		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		byte[] secretKey = new byte[16];
		for(int idx = 0; idx < 16; ++idx){
			secretKey[idx] = idx <= key.length - 1 ? key[0] : (byte)0;
		}
		SecretKeySpec newkey= new SecretKeySpec(secretKey, "AES");
		
		c.init(Cipher.DECRYPT_MODE, newkey);
		
		byte output[] = c.doFinal(encrypted);
		return(new String(output));
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (BadPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
		finally{
			
		}

	return null;
}
	
}