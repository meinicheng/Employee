package com.sdbnet.hywy.employee.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.http.impl.auth.UnsupportedDigestAlgorithmException;

import android.util.Log;

public class UtilsJava {

	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String numberChar = "0123456789";

	/**
	 * 返回一个定长的随机字符串(只包含大小写字母、数字)
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 返回一个定长的随机纯字母字符串(只包含大小写字母)
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateMixString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(letterChar.charAt(random.nextInt(letterChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 返回一个定长的随机纯数字字符串
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateNumber(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 返回一个定长的随机纯小写字母字符串
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateLowerString(int length) {
		return generateMixString(length).toLowerCase();
	}

	/**
	 * 返回一个定长的随机纯大写字母字符串
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateUpperString(int length) {
		return generateMixString(length).toUpperCase();
	}

	/**
	 * 生成一个定长的纯0字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return 纯0字符串
	 */
	public static String generateZeroString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append('0');
		}
		return sb.toString();
	}

	/**
	 * 根据数字生成一个定长的字符串，长度不够前面补0
	 * 
	 * @param num
	 *            数字
	 * @param fixdlenth
	 *            字符串长度
	 * @return 定长的字符串
	 */
	public static String toFixdLengthString(long num, int fixdlenth) {
		StringBuffer sb = new StringBuffer();
		String strNum = String.valueOf(num);
		if (fixdlenth - strNum.length() >= 0) {
			sb.append(generateZeroString(fixdlenth - strNum.length()));
		} else {
			throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth
					+ "的字符串发生异常！");
		}
		sb.append(strNum);
		return sb.toString();
	}

	/**
	 * 根据数字生成一个定长的字符串，长度不够前面补0
	 * 
	 * @param num
	 *            数字
	 * @param fixdlenth
	 *            字符串长度
	 * @return 定长的字符串
	 */
	public static String toFixdLengthString(int num, int fixdlenth) {
		StringBuffer sb = new StringBuffer();
		String strNum = String.valueOf(num);
		if (fixdlenth - strNum.length() >= 0) {
			sb.append(generateZeroString(fixdlenth - strNum.length()));
		} else {
			throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth
					+ "的字符串发生异常！");
		}
		sb.append(strNum);
		return sb.toString();
	}

	// public static void main(String[] args) {
	// System.out.println(generateString(15));
	// System.out.println(generateMixString(15));
	// System.out.println(generateNumber(15));
	// System.out.println(generateLowerString(15));
	// System.out.println(generateUpperString(15));
	// System.out.println(generateZeroString(15));
	// System.out.println(toFixdLengthString(123, 15));
	// System.out.println(toFixdLengthString(123L, 15));
	// }

	private static final String LOG_TAG = "MD5";
	private static final String ALGORITHM = "MD5";

	private static char sHexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static MessageDigest sDigest;

	static {
		try {
			sDigest = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG_TAG, "Get MD5 Digest failed.");
			throw new UnsupportedDigestAlgorithmException(ALGORITHM, e);
		}
	}

	// /**
	// * MD5
	// *
	// * @param paramString
	// * @return
	// */
	// private static final String ALGORITHM = "MD5";
	// private static char sHexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
	// '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	//
	// private static String md5(String paramString) {
	//
	// byte[] btyes = paramString.getBytes();
	// byte[] encodedBytes = null;
	// try {
	// encodedBytes = MessageDigest.getInstance(ALGORITHM).digest(
	// btyes);
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// }
	//
	// if (encodedBytes == null || encodedBytes.length <= 0) {
	// return "";
	// }
	//
	// final int size = encodedBytes.length;
	// final char str[] = new char[size * 2];
	// int index = 0;
	// byte b;
	// for (int i = 0; i < size; i++) {
	// b = encodedBytes[i];
	// str[index++] = sHexDigits[b >>> 4 & 0xf];
	// str[index++] = sHexDigits[b & 0xf];
	// }
	// return new String(str);
	// }
	public static String encode(String source) {
		byte[] btyes = source.getBytes();
		byte[] encodedBytes = sDigest.digest(btyes);

		return hexString(encodedBytes);
	}

	public static String hexString(byte[] source) {
		if (source == null || source.length <= 0) {
			return "";
		}

		final int size = source.length;
		final char str[] = new char[size * 2];
		int index = 0;
		byte b;
		for (int i = 0; i < size; i++) {
			b = source[i];
			str[index++] = sHexDigits[b >>> 4 & 0xf];
			str[index++] = sHexDigits[b & 0xf];
		}
		return new String(str);
	}

	// public static void main(String[] args) {
	// // MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
	// System.out.println(MD5.encode(""));
	// // MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
	// System.out.println(MD5.encode("a"));
	// // MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
	// System.out.println(MD5.encode("abc"));
	// }
	public static final String URL_REG_EXPRESSION = "^(https?://)?([a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+)+(/*[A-Za-z0-9/\\-_&:?\\+=//.%]*)*";
	public static final String EMAIL_REG_EXPRESSION = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";
	public static final String MOBILE_REG_EXPRESSION = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0,5-9]))\\d{8}$";

	public static boolean isMobile(String s) {
		if (s == null) {
			return false;
		}
		return Pattern.matches(MOBILE_REG_EXPRESSION, s);
	}

	public static boolean isUrl(String s) {
		if (s == null) {
			return false;
		}
		return Pattern.matches(URL_REG_EXPRESSION, s);
	}

	public static boolean isEmail(String s) {
		if (s == null) {
			return true;
		}
		return Pattern.matches(EMAIL_REG_EXPRESSION, s);
	}

	public static boolean isBlank(String s) {
		if (s == null) {
			return true;
		}
		return Pattern.matches("\\s*", s);
	}

	public static String join(String spliter, Object[] arr) {
		if (arr == null || arr.length == 0) {
			return "";
		}
		if (spliter == null) {
			spliter = "";
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i == arr.length - 1) {
				break;
			}
			if (arr[i] == null) {
				continue;
			}
			builder.append(arr[i].toString());
			builder.append(spliter);
		}
		return builder.toString();
	}

	public static String fromFile(File f) throws IOException {
		InputStream is = new FileInputStream(f);
		byte[] bs = new byte[is.available()];
		is.read(bs);
		is.close();
		return new String(bs);
	}



	public static Object depthClone(Object srcObj) {
		Object cloneObj = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(out);
			oo.writeObject(srcObj);

			ByteArrayInputStream in = new ByteArrayInputStream(
					out.toByteArray());
			ObjectInputStream oi = new ObjectInputStream(in);
			cloneObj = oi.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return cloneObj;
	}

	public static String translate2SessionMessageData(String unixTime) {
		return translate2SessionMessageData(Long.parseLong(unixTime));
	}

	public static String translate2SessionMessageData(long unixTime) {
		Date date = new Date(unixTime);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public static String getCurrentlyDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	public static String getCurrentlyTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return df.format(date);
	}

	public static String getFileStrs(String fileName) {
		if (!new File(fileName).exists()) {
			return null;
		}
		FileInputStream fis = null;
		BufferedReader reader = null;
		String s = null;
		StringBuffer buffer = new StringBuffer();
		try {
			fis = new FileInputStream(fileName);
			reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
			while (true) {
				s = reader.readLine();
				if (s == null)
					break;
				buffer.append(s+"\n");
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally { // 关闭流
			try {
				reader.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}
}
