package com.sgcc.share.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSAUtils {

	public static final String PUBLIC_KEY = "publicKey";
	public static final String PRIVATE_KEY = "privateKey";
	public static final String SB_PUBLIC_KEY = "sb_publicKey";
	public static final String SB_PRIVATE_KEY = "sb_privateKey";

	private static Map<String,byte[]> keyMap = new HashMap<String,byte[]>();
	/**
	 * base64编码
	 * @param data
	 * @return
	 */
	public static String base64Encode(byte[] data) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}

	/**
	 * base64解码
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] base64Decode(String data) throws IOException {
		BASE64Decoder encoder = new BASE64Decoder();
		return encoder.decodeBuffer(data);
	}

	/**
	 * 生成密钥对
	 * @return
	 */
	public static KeyPair getRsaKP() throws Exception {
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance("RSA"); // 创建‘密钥对’生成器
			kpg.initialize(1024, new SecureRandom()); // 指定密钥长度
			KeyPair kp = kpg.genKeyPair(); // 生成‘密钥对
			return kp;
		} catch (Exception e) {
			throw new Exception("失败");
		}
	}

	/**
	 * 还原公钥，X509EncodedKeySpec 用于构建公钥的规范
	 * @param keyBytes
	 * @return
	 */
	public static PublicKey restorePublicKey(byte[] keyBytes) {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
	 * @param keyBytes
	 * @return
	 */
	public static PrivateKey restorePrivateKey(byte[] keyBytes) {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * RSA加密
	 * @param key 公钥
	 * @param data 明文数据
	 * @return
	 */
	public static byte[] rsaEncrypt(PublicKey key, byte[] data)throws Exception {
		try {
			Cipher desCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
			desCipher.init(Cipher.ENCRYPT_MODE, key);
			return desCipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception("RSA加密失败");
		}
	}

	/**
	 * RSA解密
	 * @param key 公钥
	 * @param data   明文数据
	 * @return
	 */
	public static String rsaDecrypt(PrivateKey key, byte[] data)
			throws Exception {
		try {
			Cipher desCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
			desCipher.init(Cipher.DECRYPT_MODE, key);
			return new String(desCipher.doFinal(data),"UTF-8");
		} catch (Exception e) {
			throw new Exception("RSA解密失败");
		}
	}

	
	/**
	 * RSA解密
	 * @param key 公钥
	 * @param data   明文数据
	 * @return
	 */
	public static byte[] rsaDecrypt_Js(PrivateKey key, byte[] data)
			throws Exception {
		try {
			Cipher desCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
			desCipher.init(Cipher.DECRYPT_MODE, key);
			return desCipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception("RSA解密失败");
		}
	}

	/**
	 * 签名
	 * @param key   公钥
	 * @param data  数据
	 * @return
	 */
	public static byte[] sign(PrivateKey key, byte[] data) throws Exception {
		Signature signature = null;
		try {
			signature = Signature.getInstance("SHA1WithRSA");
			signature.initSign(key);
			signature.update(data);
			return signature.sign();
		} catch (Exception e) {
			throw new Exception("签名失败");
		}
	}
	
	
	/**
	 * 签名
	 * @param key   公钥
	 * @param data  数据
	 * @return
	 */
	public static byte[] signSunRsaSign(PrivateKey key, byte[] data) throws Exception {
		Signature signature = null;
		try {
			signature = Signature.getInstance("SHA1withRSA", "SunRsaSign");
			signature.initSign(key);
			signature.update(data);
			return signature.sign();
		} catch (Exception e) {
			throw new Exception("签名失败");
		}
	}
	
	/**
	 * 签名验证
	 * @param key		公钥
	 * @param data		待验证数据
	 * @param signData	签名数据
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(PublicKey key, byte[] data,byte[] signData) throws Exception {
		Signature signature = null;
		try {
			signature = Signature.getInstance("SHA1WithRSA");
			signature.initVerify(key);
			signature.update(data);
			return signature.verify(signData);
		} catch (Exception e) {
			throw new Exception("签名失败");
		}
	}
	
	
	/**
	 * 签名验证
	 * @param oCert		证书
	 * @param data		待验证数据
	 * @param signData	签名数据
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(Certificate oCert, byte[] data,byte[] signData) throws Exception {
		Signature signature = null;
		try {
			signature = Signature.getInstance("SHA1withRSA", "SunRsaSign");
			signature.initVerify(oCert);
			signature.update(data);
			return signature.verify(signData);
		} catch (Exception e) {
			throw new Exception("签名失败");
		}
	}
	
	/**
	 * 根据私钥证书文件获取PrivateKey对象
	 * @param pfxFile	证书路径
	 * @param pfxPwd	证书密码
	 * @return PrivateKey 私钥对象
	 */
	public static PrivateKey GetPvkformPfx(String pfxFile, String pfxPwd) throws Exception{
		FileInputStream fis = null;
		try {
			KeyStore ks = KeyStore.getInstance("PKCS12"); 	//PKCS12
			fis = new FileInputStream(pfxFile);
			char[] nPassword = null;
			if ((pfxPwd == null) || pfxPwd.trim().equals("")){
				nPassword = null;
			}else{
				nPassword = pfxPwd.toCharArray();
			}
			ks.load(fis, nPassword);
			fis.close();
			Enumeration enumas = ks.aliases();
			String keyAlias = null;
			if (enumas.hasMoreElements()){
				keyAlias = (String)enumas.nextElement(); 
			}
			PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
			return prikey;
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("获取私钥对象失败");
		}finally {
			try {
				if(fis != null){
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	
	/**
	 * 签名验证
	 * @param key		公钥
	 * @param data		待验证数据
	 * @param signData	签名数据
	 * @return
	 * @throws Exception
	 */
	public static boolean verify1(byte[] key, byte[] data,byte[] signData) throws Exception {
		Signature signature = null;
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			signature = Signature.getInstance("SHA1WithRSA");
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
			PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
			signature.initVerify(publicKey);
			signature.update(data);
			return signature.verify(signData);
		} catch (Exception e) {
			throw new Exception("签名失败");
		}
	}


	public static Map<String, byte[]> getKeyMap() {
		return keyMap;
	}

	public static void setKeyMap(Map<String, byte[]> keyMap) {
		RSAUtils.keyMap = keyMap;
	}

	public static void main(String[] args) {
		try{
			//文档中第二步骤生成公钥和私钥：
			/*KeyPair kp = getRsaKP();
			PrivateKey priKey = kp.getPrivate();
			PublicKey pubKey = kp.getPublic();
			System.out.println("公钥:\r\n");
			String pubKeyString = base64Encode(pubKey.getEncoded());
			System.out.println(pubKeyString);
			System.out.println("秘钥:\r\n");
			String priKeyString = base64Encode(priKey.getEncoded());
			System.out.println(priKeyString);*/
			String jiami= "测试";
			System.err.println("\r\n加密前："+jiami);
			//java后端方式实现RSA加密，公钥和私钥本案例中有改动，项目中按照实际公钥和私钥进行替换
			//设置公钥
			//PublicKey sb_pubkey = RSAUtils.restorePublicKey(RSAUtils.base64Decode(pubKeyString));
			PublicKey sb_pubkey = RSAUtils.restorePublicKey(RSAUtils.base64Decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLLvjNPfoEjbIUyGFcIFI25Aqhjgazq0dabk/w1DUiUiREmMLRbWY4lEukZjK04e2VWPvKjb1K6LWpKTMS0dOs5WbFZioYsgx+OHD/DV7L40PHLjDYkd4ZWV2EDlS8qcpx6DYw1eXr6nHYZS1e9EoEBWojDUcolzyBXU3r+LDjUQIDAQAB"));
			jiami=base64Encode(rsaEncrypt(sb_pubkey,jiami.getBytes("UTF-8")));
			System.err.println("\r\n加密后："+jiami);
			//java后端进行解密（公钥和私钥本案例中有改动，项目中按照实际公钥和私钥进行替换）：
			//设置私钥
			//PrivateKey prikey = RSAUtils.restorePrivateKey(RSAUtils.base64Decode(priKeyString));
			PrivateKey prikey = RSAUtils.restorePrivateKey(RSAUtils.base64Decode("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIsu+M09+gSNshTIYVwgUjbkCqGO" +
					"BrOrR1puT/DUNSJSJESYwtFtZjiUS6RmMrTh7ZVY+8qNvUrotakpMxLR06zlZsVmKhiyDH44cP8N" +
					"XsvjQ8cuMNiR3hlZXYQOVLypynHoNjDV5evqcdhlLV70SgQFaiMNRyiXPIFdTev4sONRAgMBAAEC" +
					"gYAxe62xKous+sOJaARVQJh6M3EkJt5zGj3wBLAfKxMI9LM4QCdjVcW17+NEu6Djdj+FjXFXfjUC" +
					"YOHVFnS2CCt9ELTPP6Hc8nn0wNpbsacHTQ6XPUqUwZwxmGBmo7h6bNBUTZKiK/ZW+HkDEXXu/niK" +
					"4tMcI1rjh37tcm8yhpdpkQJBAN6fBdFP9gUbx/iYbCqurRHYtIQ5yvf83M2XHzoSjUVXcNkMVHGw" +
					"AcZ7iC6a/ZfBfblnHuxwqAcBmANVu725UWcCQQCgDVG/0k5mi/2MBLxqrbyXmu333aDe0xgEvkYy" +
					"GShYr0oJav7RjIE/myAP+Vi+PGQRaBCro1CAYbUra8MsqZqHAkBnwDcG4Lwoj1T375lhnvy7t4IR" +
					"qZmFT4xcKFT+TI0YRvMpxIxKW5vIM+Q4zDTpl9yPHpcT3EEC2uRMkZUoi4h7AkEAkMJbPR6u0Gsv" +
					"YNPrwqTu3URAlZ374W2V3Lxn6un8JvIsCYafgNIPGINqpWgGDtG6RFAWO9dTeTpmTSaDecLQwwJB" +
					"AIt+TRTz5up2Hq6H4N3vwAJnxFAvVtBSUFOVH8Jt2G0VClmMEazW+6DUAmVBkKvtxMusLTBzid47" +
					"2ZqnOCudzmY="));
			String jiemi=rsaDecrypt(prikey,RSAUtils.base64Decode(jiami));
			System.out.println("\r\n解密后："+jiemi);
		}catch(Exception ex){
		}
	}

	public static String decryptFrontEndCiphertext(String ciphertext) {

		/*R4bliyPssIhGM1VkhllKVnWPaBCH/xupJWNWKZ47KmmYhAeESUPyETJ2tz2SRj6Sx3QU3CpNYWGLUtbmUpWJmZLtt9frHFV+Aslh3uiD0/rsn6C57f8G6xHvM+9OHN3SY11k2PO0+IINYkAPcZWUC4bfn7P15LhherQ1icuArQg=*/

		String plaintext= null;
		try {

			//java后端进行解密（公钥和私钥本案例中有改动，项目中按照实际公钥和私钥进行替换）：
			//设置私钥
			PrivateKey prikey = RSAUtils.restorePrivateKey(RSAUtils.base64Decode("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIsu+M09+gSNshTIYVwgUjbkCqGO" +
					"BrOrR1puT/DUNSJSJESYwtFtZjiUS6RmMrTh7ZVY+8qNvUrotakpMxLR06zlZsVmKhiyDH44cP8N" +
					"XsvjQ8cuMNiR3hlZXYQOVLypynHoNjDV5evqcdhlLV70SgQFaiMNRyiXPIFdTev4sONRAgMBAAEC" +
					"gYAxe62xKous+sOJaARVQJh6M3EkJt5zGj3wBLAfKxMI9LM4QCdjVcW17+NEu6Djdj+FjXFXfjUC" +
					"YOHVFnS2CCt9ELTPP6Hc8nn0wNpbsacHTQ6XPUqUwZwxmGBmo7h6bNBUTZKiK/ZW+HkDEXXu/niK" +
					"4tMcI1rjh37tcm8yhpdpkQJBAN6fBdFP9gUbx/iYbCqurRHYtIQ5yvf83M2XHzoSjUVXcNkMVHGw" +
					"AcZ7iC6a/ZfBfblnHuxwqAcBmANVu725UWcCQQCgDVG/0k5mi/2MBLxqrbyXmu333aDe0xgEvkYy" +
					"GShYr0oJav7RjIE/myAP+Vi+PGQRaBCro1CAYbUra8MsqZqHAkBnwDcG4Lwoj1T375lhnvy7t4IR" +
					"qZmFT4xcKFT+TI0YRvMpxIxKW5vIM+Q4zDTpl9yPHpcT3EEC2uRMkZUoi4h7AkEAkMJbPR6u0Gsv" +
					"YNPrwqTu3URAlZ374W2V3Lxn6un8JvIsCYafgNIPGINqpWgGDtG6RFAWO9dTeTpmTSaDecLQwwJB" +
					"AIt+TRTz5up2Hq6H4N3vwAJnxFAvVtBSUFOVH8Jt2G0VClmMEazW+6DUAmVBkKvtxMusLTBzid47" +
					"2ZqnOCudzmY="));

			byte[] bytes = RSAUtils.base64Decode(ciphertext);

			plaintext = rsaDecrypt(prikey, bytes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return plaintext;
	}
}
