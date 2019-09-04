package util.commonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串相关操作
 */
public class ComStrUtil extends CommonUtil{
	private static final String GREPMARK = "ComStrUtil";

	/**
	 * 判读字符串是否为空
	 * @author TianChen
	 * @createdTime 2013-8-29 10:32am
	 * @param String str
	 * @return true:为空; false:不为空
	 */
	public static boolean isBlank(Object obj) {
		if(obj == null || obj.toString().trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判读字符串是否为空或者为字符串"null"
	 * @author TianChen
	 * @createdTime 2013-8-29 10:32am
	 * @param String str
	 * @return true:为空; false:不为空
	 */
	public static boolean isBlankOrNull(Object obj) {
		return isBlank(obj) || "null".equalsIgnoreCase(obj.toString().trim());
	}
	
	/**
	 * 得到一个随机的数字字母字符串
	 * @param length 返回字符串的长度
	 * @return 随机数字字母字符串
	 */
	public static final String getRandomString(int length) {
		Random randGen = null;
		char[] numbersAndLetters = null;
		
		if (length < 1) {
			return null;
		}
		if (randGen == null) {
			randGen = new Random();
			numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
					+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		}
		int len = numbersAndLetters.length;
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(len)];
		}
		randGen = null;
		numbersAndLetters = null;
		return new String(randBuffer);
	}
	
	/**
	 * 得到一个随机的数字字符串
	 * @param length 返回字符串的长度
	 * @return 随即数字字符串
	 */
	public static final String getRandomNumString(int length) {
		Random randGen = null;
		char[] numbersAndLetters = null;
		
		if (length < 1) {
			return null;
		}
		if (randGen == null) {
			randGen = new Random();
			numbersAndLetters = ("0123456789").toCharArray();
		}
		int len = numbersAndLetters.length;
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(len)];
		}
		randGen = null;
		numbersAndLetters = null;
		return new String(randBuffer);
	}   
	
	/**
	 * sourceStr.replace with replacement literally only once.
	 * @param sourceStr
	 * @param literalString
	 * @param replacement
	 * @return
	 */
	public static String replaceLiteral(String sourceStr, String literalString, String replacement) {
		Matcher matcher = Pattern.compile(literalString, Pattern.LITERAL).matcher(sourceStr);
	    StringBuffer sb = new StringBuffer();
	    if(matcher.find()){ // 最多只匹配替换一次
	      matcher.appendReplacement(sb, replacement); // 把从"上次替换处"到"当前匹配处"用replacement替换后的字符串输入到sb里去
	    }
	    matcher.appendTail(sb);// 把最后一次匹配之后的字符串也输入到sb里去
	    return sb.toString();
	}
	
	/**
	 * sourceStr.replaceAll with replacement literally
	 * @param literalString
	 * @param replacement
	 * @return
	 */
	public static String replaceAllLiteral(String sourceStr, String literalString, String replacement) {
		Matcher matcher = Pattern.compile(literalString, Pattern.LITERAL).matcher(sourceStr);
	    if(matcher.find()){ // 最多只匹配替换一次
	      sourceStr = matcher.replaceAll(replacement);
	    }
	    return sourceStr;
	}
	
	/**
	 * String to Array literally
	 * @param str
	 * @param splitMark
	 * @return
	 */
	public static String[] splitLiterally(String str, String splitMark) {
		return Pattern.compile("" + splitMark, Pattern.LITERAL).split(str);
	}
	
	/**
	 * String to list literally
	 * @param str
	 * @param splitMark
	 * @return
	 */
	public static List<String> str2ListLiterally(String str, String splitMark) {
		return new ArrayList<String>(Arrays.asList(splitLiterally(str, splitMark)));
	}
	
	public static void main(String[] args) {
	    System.out.println(replaceLiteral("abcdefghijbckl", "bc", "*"));
	    System.out.println(replaceAllLiteral("abcdefghijbckl", "bc", "*"));
	    System.out.println(ComLogUtil.objToString(str2ListLiterally("ab$cd:ef\\ghi//k[lmn", "\\")));
	    
	}
}