package com.scs.pwdHardening;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.scs.pwdHardening.model.Question;
import com.scs.pwdHardening.model.ResponseType;
import com.scs.pwdHardening.model.User;

public class History {

	//public byte[] iv;
	//public byte[] encrypted;
	
	/*public void setIV(byte[] iv){
		this.iv=iv;
	}
	
	public byte[] getIV(){
		return this.iv;
	}
	
	public void setEncrypted(byte[] encrypted){
		this.encrypted=encrypted;
	}
	
	public byte[] getEncrypted(){
		return this.encrypted;
	}*/
	
	/*public void loadHistoryFile(Map<Question, ResponseType> userResponse, User user){
		
		FileOutputStream fos; 
	    DataOutputStream dos;
	    Set<Map.Entry<Question, ResponseType>> set= userResponse.entrySet();
	    
	    try {
          String filename = "C:\\" + user.getUserName() + ".txt";
          
          	     
          RandomAccessFile file= new RandomAccessFile(filename, "rw");
	      String file3=file.toString();
	      
	    	  if(user.getHistoryFile().exists()){
	          file.setLength(1024*1024);
	      String res = "Decryption Successful\n";
	      fos = new FileOutputStream(file3,true);
	      dos=new DataOutputStream(fos);
	      for(Map.Entry<Question, ResponseType> me : set) {
	    	ResponseType response = me.getValue(); 
	    	
	    	res += response.value;  	
	      }
	       res = res + "\n";
	       Integer[] poly = user.getPolynomialCoefficients();
	       byte[] key = poly[0].toString().getBytes();
	      // KeyGenerator kg = KeyGenerator.getInstance(polykey);
	      // Key key = kg.generateKey();
	       byte[] ciphertext = this.encrypt(key, res);
	       dos.write(ciphertext);
		   
	       
	      }else{
	    	  String res = "";
		      fos = new FileOutputStream(file3,true);
		      dos=new DataOutputStream(fos);
		      for(Map.Entry<Question, ResponseType> me : set) {
		    	ResponseType response = me.getValue(); 
		    	res += response.value;  
	      }

		      res = res + "\n";
		      byte[] iv = this.getIV();
		      Cipher c= this.getCipher();
		      
		      String plaintext = decrypt(key, encrypted, iv);
		      if(plaintext.substring(0, 21)=="Decryption Successful");
		      System.out.println("Decryption Successful");
		      
		      byte[] ciphertext = encrypt(key, plaintext);
			  
		      dos.write(ciphertext);
	      }
	      	       		  
	      }
	    catch (IllegalBlockSizeException e) {
		      e.printStackTrace();
		    }
		catch (NoSuchPaddingException f){
			 f.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    }
	}*/
	
	public static byte[] encrypt(BigInteger key, Map<Question, ResponseType> userResponse, User user){
		
		try{
			String res = "Decryption Successful\n";
			Set<Map.Entry<Question, ResponseType>> set= userResponse.entrySet();
		    for(Map.Entry<Question, ResponseType> me : set) {
		    	ResponseType response = me.getValue(); 
		    	res += response.value;  	
		    }
		    res = res + "\n";
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			byte[] unpaddedKey = key.toByteArray();
			byte[] secretKey = new byte[32];
			for(int idx = 0; idx < 32; ++idx){
				secretKey[idx] = idx <= unpaddedKey.length - 1 ? unpaddedKey[0] : (byte)0;
			}
			
			/*byte [] salt = new byte[8];
			new SecureRandom().nextBytes(salt);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec pkSpec = new PBEKeySpec(key.toString().toCharArray(), salt, 1024, 256);
			SecretKey tmp = factory.generateSecret(pkSpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");*/
			
			
			SecretKeySpec keySpec = null;
			keySpec = new SecretKeySpec(secretKey, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
	        byte[] input = res.getBytes();
		    byte[] encrypted = c.doFinal(input);
		    user.setIv(c.getIV());
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
		}
		return null;
	}

	public static String decrypt(byte[] key, byte[] encrypted, byte[] iv){
		
		try{
		IvParameterSpec dps = new IvParameterSpec(iv);
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec newkey= new SecretKeySpec(key, "AES");
		c.init(Cipher.DECRYPT_MODE, newkey, dps);
		
		byte output[] = c.doFinal(encrypted);
		return(new String(output));
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidAlgorithmParameterException e) {
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