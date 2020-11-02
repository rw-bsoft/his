/*
 * @(#)EncryptException.java Created on 2011-7-15 下午4:09:51
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.security;

import java.security.Key;
import javax.crypto.Cipher;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class DESCoder {

	public static final String ALGORITHM = "DES";

	private static String strDefaultKey = "national";

	private Cipher encryptCipher = null;

	private Cipher decryptCipher = null;

	/**
	 * 默认构造方法，使用默认密钥
	 * 
	 * @throws Exception
	 */
	public DESCoder() throws EncryptException {
		this(strDefaultKey.getBytes());
	}

	/**
	 * 指定密钥构造方法
	 * 
	 * @param strKey
	 *            指定的密钥
	 * @throws Exception
	 */
	public DESCoder(byte[] bKey) throws EncryptException {
		// Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = getKey(bKey);
		try {
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);

			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 加密字节数组
	 * 
	 * @param arrB
	 *            需加密的字节数组
	 * @return 加密后的字节数组
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] arrB) throws EncryptException {
		try {
			return encryptCipher.doFinal(arrB);
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 加密字符串
	 * 
	 * @param strIn
	 *            需加密的字符串
	 * @return 加密后的字符串,以16进制字符串形式返回。
	 * @throws Exception
	 */
	public String encrypt(String strIn) throws EncryptException {
		return EncryptUtil.byteArr2HexStr(encrypt(strIn.getBytes()));
	}

	/**
	 * 解密字节数组
	 * 
	 * @param arrB
	 *            需解密的字节数组
	 * @return 解密后的字节数组
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] arrB) throws EncryptException {
		try {
			return decryptCipher.doFinal(arrB);
		} catch (Exception e) {
			throw new EncryptException(e);
		}
	}

	/**
	 * 解密字符串
	 * 
	 * @param strIn
	 *            需解密的字符串
	 * @return 解密后的字符串,以16进制字符串形式返回。
	 * @throws Exception
	 */
	public String decrypt(String strIn) throws EncryptException {
		return new String(decrypt(EncryptUtil.hexStr2ByteArr(strIn)));
	}

	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 * 
	 * @param arrBTmp
	 *            构成该字符串的字节数组
	 * @return 生成的密钥
	 * @throws java.lang.Exception
	 */
	private Key getKey(byte[] arrBTmp) {
		// 创建一个空的8位字节数组（默认值为0）
		byte[] arrB = new byte[8];

		// 将原始字节数组转换为8位
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}

		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
		return key;
	}
}
