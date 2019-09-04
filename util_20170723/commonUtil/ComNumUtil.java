package util.commonUtil;

import java.math.BigDecimal;

/**
 * 数字相关操作
 */
public class ComNumUtil extends CommonUtil{
	private static final String GREPMARK = "ComNumUtil";

	/**
	 * 把数值型的Double,int,或String转成非科学计数法的,保留了 @scale 位的字符串(scale后的四舍五入)
	 * @Param num 数值型的对象
	 * @param scale 小数点后保留的位数 .若为 0, 则返回int的字符串. 为负数时则对正数部分的也四舍五入.
	 * @return String 非科学计数法的,保留了 @scale 位的字符串
	 */
	public static String number2String(Object num, Integer scale) {
		if("".equals(num + "") || num == null ) {
			return "0.00";
		}

		return new BigDecimal(Double.parseDouble(num + "")).setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString();
	}
	

	/**
	 * 取整, 四舍五入
	 * @param num 数字
	 * @return Integer
	 */
	public static Integer number2Integer(Object num) {
		return Integer.parseInt(number2String(num, 0));
	}
	
	/**
	 * 把char转化成int
	 * @param charNum
	 * @return
	 */
	public static Integer char2Int(char charNum) {
		return Integer.parseInt(String.valueOf(charNum));
	}
	
	/**
	 * 判断一个字符串是否都为有效数字Double类型或int类型
	 * @param str 字符串
	 * @return 返回true或者false
	 * 
	 */
	public static Boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	
}