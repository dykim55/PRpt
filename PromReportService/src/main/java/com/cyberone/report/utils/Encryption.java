package com.cyberone.report.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 암호화 클래스 (SHA-512 only)
 * TODO MD2, MD5, SHA, SHA-256, SHA-384 등의 알고리즘 확장성을 고려해야 되는지.
 * 
 * @author hjchoi
 *
 */
@SuppressWarnings("restriction")
public class Encryption {

	private static String sKeyString = "same.cyberone";
	
	/**
	 * 암호화 (SHA-512)
	 * 
	 * @param str 암호화 대상 문자열
	 * @return 암호화 결과 문자열
	 * @throws Exception 
	 */
	public static String encrypt(String str) throws Exception {

		//암호화 결과
		String result = null;
		
		MessageDigest md;
		
		try {
			//알고리즘 지정
			md = MessageDigest.getInstance("SHA-512");

			//문자열을 hash 값으로 변환
			md.update(str.getBytes());
			byte[] bytes = md.digest();
			
			//byte배열을 16진수의 문자열로 변환	
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				String hex = String.format("%02x", b);
				sb.append(hex);
			}
			result =  sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw e;
		}
		
		return result;
	}

	/**
	 * 암호화 (MD5)
	 * 
	 * @param str 암호화 대상 문자열
	 * @return 암호화 결과 문자열
	 * @throws Exception 
	 */
	public static String md5hashkey(String str) throws Exception {

		//암호화 결과
		String result = null;
		
		MessageDigest md;
		
		try {
			//알고리즘 지정
			md = MessageDigest.getInstance("MD5");

			//문자열을 hash 값으로 변환
			md.update(str.getBytes());
			byte[] bytes = md.digest();
			
			//byte배열을 16진수의 문자열로 변환	
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				sb.append(String.format("%02x", b));
			}
			result =  sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw e;
		}
		
		return result;
	}

	/**
	 * 파일 무결성 체크 암호화 (MD5)
	 * 
	 * @param str 암호화 대상 문자열
	 * @return 암호화 결과 문자열
	 * @throws Exception 
	 */
	public static String fileMD5HashKey(String filePath, String fileName) throws Exception {
		InputStream in = null;
		String hex = null;

		if(FileUtil.existsFile(filePath, fileName)){
			try {
				in = new FileInputStream(filePath+fileName);
		
				MessageDigest md = null;
				md = MessageDigest.getInstance("MD5");
	
				byte[] buffer = new byte[5*1024*1024];// 5메가까지 제한 넘어서면 예외발생

				int read;
				while( (read = in.read(buffer)) > 0){
				   md.update(buffer, 0, read);
				}
				
				byte[] bytes = md.digest();
		
				StringBuilder sb = new StringBuilder(2 * bytes.length);
				for (byte b : bytes) {
					sb.append(String.format("%02x", b));
				}
				
				hex = sb.toString();
	
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw e;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw e;
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			} finally {
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
						throw e;
					}
				}
			}
		}
		
		return hex;
	}
	
	public static String AESDecrypt(String text) throws Exception {
		if (text == null || "".equals(text)) {
			return "";
		}
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] keyBytes= new byte[16];
		byte[] b= sKeyString.getBytes("UTF-8");
		int len= b.length;
		if (len > keyBytes.length) len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);
		BASE64Decoder decoder = new BASE64Decoder();
		byte [] results = cipher.doFinal(decoder.decodeBuffer(text));
		return new String(results,"UTF-8");
	}

	public static String AESEncrypt(String text) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] keyBytes= new byte[16];
		byte[] b= sKeyString.getBytes("UTF-8");
		int len= b.length;
		if (len > keyBytes.length) len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
		byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(results);
	}
	
}