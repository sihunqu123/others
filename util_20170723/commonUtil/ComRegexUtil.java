package util.commonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex相关操作
 */
public class ComRegexUtil extends CommonUtil {
	private static final String GREPMARK = "ComRegexUtil";

	public static final String SEPARATOR = File.separator;
	
	public static final String EOLRegex= "(\\r|\\n|\\v)+";

	/**
	 * 用指定replacement替换掉源字符串中匹配的部门(只匹配一次)
	 * 
	 * @param sourceStr
	 *            被用来匹配的源字符串
	 * @param replacement
	 *            用来替换的字符串
	 * @param regex
	 *            去匹配的regex字符串
	 * @return 替换后的字符串
	 */
	public static String replaceByRegex(String sourceStr, Object regex, String replacement) {
		Matcher matcher = Pattern.compile(regex + "").matcher(sourceStr);
		StringBuffer sb = new StringBuffer();
		if (matcher.find()) { // 最多只匹配替换一次
			matcher.appendReplacement(sb, replacement); // 把从"上次替换处"到"当前匹配处"用replacement替换后的字符串输入到sb里去
			// System.out.println(sb.toString());
		}
		// System.out.println(sb.toString());
		matcher.appendTail(sb);// 把最后一次匹配之后的字符串也输入到sb里去
		// StringBuffer sb2 = new StringBuffer();
		// matcher.appendTail(sb2);//把最后一次匹配之后的字符串也输入到sb里去
		// System.out.println(sb2.toString());
		// System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * 用指定replacement替换掉源字符串中匹配的部门(只匹配一次)
	 * 
	 * @param sourceStr
	 *            被用来匹配的源字符串
	 * @param replacement
	 *            用来替换的字符串
	 * @param regex
	 *            去匹配的regex字符串
	 * @return 替换后的字符串
	 */
	public static String replaceAllByRegex(String sourceStr, Object regex, String replacement) {
		// Matcher matcher = Pattern.compile(regex + "",
		// Pattern.MULTILINE).matcher(sourceStr);
		Matcher matcher = Pattern.compile(regex + "", Pattern.DOTALL).matcher(sourceStr);
		if (matcher.find()) { // 最多只匹配替换一次
			sourceStr = matcher.replaceAll(replacement);
		}
		return sourceStr;
	}

	public static String replaceAllLiterally(String sourceStr, String str2Match, String replacement) {
		// Matcher matcher = Pattern.compile(regex + "",
		// Pattern.MULTILINE).matcher(sourceStr);
		Matcher matcher = Pattern.compile(str2Match + "", Pattern.LITERAL).matcher(sourceStr);
		if (matcher.find()) { // 最多只匹配替换一次
			sourceStr = matcher.replaceAll(replacement);
		}
		return sourceStr;
	}

	public static String replaceLiterally(String sourceStr, String str2Match, String replacement) {
		// Matcher matcher = Pattern.compile(regex + "",
		// Pattern.MULTILINE).matcher(sourceStr);
		Matcher matcher = Pattern.compile(str2Match + "", Pattern.LITERAL).matcher(sourceStr);
		if (matcher.find()) { // 最多只匹配替换一次
			sourceStr = matcher.replaceFirst(replacement);
		}
		return sourceStr;
	}

	/**
	 * 从字符串中得到匹配的第一个子字符串
	 * 
	 * @param sourceStr
	 *            被用来匹配的源字符串
	 * @param regex
	 *            去匹配的regex字符串
	 * @return
	 */
	public static String getMatchedString(String sourceStr, Object regex) {
		Matcher matcher = Pattern.compile(regex + "", Pattern.DOTALL).matcher(sourceStr);
		if (matcher.find()) {
			return matcher.group(0);
		}
		return "";
	}
	
	/**
	 * 从字符串中得到匹配的第一个子字符串
	 * 
	 * @param sourceStr
	 *            被用来匹配的源字符串
	 * @param regex
	 *            去匹配的regex字符串
	 * @return
	 */
	public static String getMatchedString(String sourceStr, Object regex, int flags) {
		Matcher matcher = Pattern.compile(regex + "", flags).matcher(sourceStr);
		if (matcher.find()) {
			return matcher.group(0);
		}
		return "";
	}

	/**
	 * 从字符串中得到匹配的第一个子字符串
	 * 
	 * @param sourceStr
	 *            被用来匹配的源字符串
	 * @param regex
	 *            去匹配的regex字符串
	 * @return
	 */
	public static String[] getMatchedStringArr(String sourceStr, Object regex) {
		Matcher matcher = Pattern.compile(regex + "", Pattern.DOTALL).matcher(sourceStr);
		// int resNum = 0;
		// System.out.println(resNum);
		// String[] strArr = new String[];
		int i = 0;
		List<String> list = new ArrayList<String>();
		while (matcher.find() && i++ < 100) {
			// System.out.println("cc: " + matcher.groupCount());
			// System.out.println(matcher.group());
			list.add(matcher.group());

		}
		return list.toArray(new String[list.size()]);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(replaceAllLiterally("abcdefgbchij", "bc", "*"));
		if(true) return;
		String aa = "st\\\\\\\"r\\\"aa\\\\\\\"a23aaaa4\"raaa\\\\\\\\\"sd\\\\\"f";
		System.out.println(aa);
		System.out.println(aa.replaceAll("(?<!(?<!\\\\)(\\\\{2}){0,9999}\\\\{1}(?!\\\\))\"", "\\\\\""));
		String str = ComFileUtil.readFile2String("e:\\download\\youtube\\downloadLog20161127170434.html");
		// System.out.println(ComRegexUtil.getMatchedString(str, "(?<=\\<div
		// id=\"unavailable-submessage\" class=\"submessage\">).+"));
		System.out.println(ComRegexUtil.getMatchedString(str,
				"(?<=<div id=\"unavailable-submessage\" class=\"submessage\">\\s{0,99}+)((?!</div>).)*</div>"));
		// Matcher matcher = Pattern.compile("(?<=\\<div
		// id=\"unavailable-submessage\" class=\"submessage\">).+",
		// Pattern.MULTILINE).matcher(str);
		// if(matcher.find()){
		// System.out.println("result" + matcher.group(0));
		// }
		// System.out.println(ComRegexUtil.getMatchedString(str, "<div
		// id=\"unavailable((?!\"submessage).)+"));
		// System.out.println(ComRegexUtil.getMatchedString(str, "<div
		// id=\"unavailable((?!=\"submessage).)+"));

		// ComLogUtil.printArr(ComRegexUtil.getMatchedStringArr(str, "(?<=<a
		// href=\")/watch\\?v=[^\"]+(?=\"\\s{1,20}class=\")"), "url");
		// ComLogUtil.printArr(ComRegexUtil.getMatchedStringArr(str, "(?<=<a
		// href=\")/watch\\?v=[^\"]+"), "url");
		//
		// System.out.println(ComRegexUtil.getMatchedString("en.00042",
		// "^(\\w|-){2,5}\\.\\d{5}$"));
		//
		//
		try {
			// System.out.println(ComFileUtil.readFile2String(new
			// File("c:\\dddsdf.js")));
			// System.out.println("SELECT id, 'cc\\'c' bb FROM billing_id ");
			// System.out.println("\\\\".length());
			// System.out.println(getMatchedString("sdf/sdfdsfdfdf/dsfds/fds/fffdd=aa",
			// "(?<=/)(?!(.*/.*))[^?/]*"));
			// System.out.println(replaceAllByRegex("cca\\\\ccacca", "\\\\",
			// "11"));
			// String src = "<!--Notes ACF aaaaaaaaaaaaaa\\naaaaa
			// -->sdfsda\\n\\nfsd <!--
			// -->\\n\\nsdfds<!--Notes ACF \\nsdafsdfsdf\\nsdfsdafsd\\n";

			// System.out.println(replaceAllByRegex(src, "<!--((?!-->).)+-->",
			// ""));
			// System.out.println(replaceAllByRegex("dsssfsdfsd", "((?!ss).)+",
			// ""));

			/**
			 * String regex = "(x)(y\\w*)(z)";
			 * 
			 * String input = "exy123z,xy456z"; Pattern p =
			 * Pattern.compile(regex, Pattern.CASE_INSENSITIVE); Matcher m =
			 * p.matcher(input);
			 * 
			 * while (m.find()) { System.out.println(m.group(0)); }
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
