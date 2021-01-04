package com.zhao.common.utils;

import com.zhao.common.exception.BusinessException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String Bit32(String SourceString) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(SourceString.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			throw new BusinessException(e.getMessage());
		}
	}

	public static String Bit16(String SourceString){
		return Bit32(SourceString).substring(8, 24);
	}
	
	/**
	 * 生成文件的MD5值
	 * @param bytes 字节数组
	 * @return
	 */
	public static String getFileMD5(byte[] bytes) {
		MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(bytes, 0, bytes.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
	}

	public static String getMD5Two(byte[] bytes) {
		try {
			String str;
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes, 0, bytes.length);
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
			return str;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 生成文件的md5值，有一个原生方法可以直接获取：DigestUtils.md5Hex
	 * @param inputStream 输入流
	 * @return
	 */
	public static String getFileMD5(InputStream inputStream) {
		if (inputStream == null) {
            return null;
        }
        MessageDigest digest = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
			throw new RuntimeException(e);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
	}
	
	/**
	 * 生成文件的md5值
	 * @param file 文件
	 * @return
	 */
	public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        try {
			return getFileMD5(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
