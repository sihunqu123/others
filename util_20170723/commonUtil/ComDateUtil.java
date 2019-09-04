package util.commonUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 日期相关操作
 */
public class ComDateUtil extends CommonUtil{
  private static final String GREPMARK = "ComDateUtil";
  /**
   * datetime format字符串
   */
  public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
  /**
   * date format字符串
   */
  public static final String DEFAULT_Date_FORMAT = "yyyy-MM-dd";
  
  public static final String DEFAULT_AGGREGATE_FORMAT = "yyyyMMddHHmmss";

  /**
   * datetime SimpleDateFormat
   */
  public static java.text.SimpleDateFormat getDefaultFormat(){
    return new SimpleDateFormat(DEFAULT_FORMAT);
  }

  /**
   * date SimpleDateFormat
   */
  public static java.text.SimpleDateFormat getDefaultDateFormat(){
    return new SimpleDateFormat(DEFAULT_Date_FORMAT);
  }
  
  /**
   * date DEFAULT_AGGREGATE_FORMAT
   */
  public static java.text.SimpleDateFormat getDefaultAggregateDateFormat(){
    return new SimpleDateFormat(DEFAULT_AGGREGATE_FORMAT);
  }

  /**
   * 取得短的(只包含年月日)时间.
   * 
   * @return
   */
  public static String getDateShort(){
    return getDefaultDateFormat().format(new Date());
  }

  /**
   * 取得长的(包含时分秒)时间.
   * 
   * @return
   */
  public static String getDateLong(){
    return getDefaultFormat().format(new Date());
  }
  
  /**
   * 取得长的(包含时分秒)时间.
   * 
   * @return
   */
  public static String getDateAggreated(){
    return getDefaultAggregateDateFormat().format(new Date());
  }

  /**
   * 比较两String时间的先后
   * 
   * @param beginTime String类型的开始时间
   * @param endTime String类型的结束时间
   * @return 只有在endTime > beginTime 才返回true
   * @throws Exception
   */
  public static boolean isAfter(Object beginTime, Object endTime) throws Exception{

    Date left = ObjToDate(beginTime);
    Date right = ObjToDate(endTime);
    boolean res = left.before(right);
    // info("CommpareTime: " + CommonUtil.getDateLong() + " CommpareTime: beginTime:" + beginTime +
    // " endTime:" + endTime + " left:" + left + " right:" + right + " res:" + res);
    return res;
    // return ObjToDate(beginTime).before(ObjToDate(endTime));
  }

  /**
   * 比较后一个时间dateObj2比前一个时间dateObj1晚多少. compareDate(dateObj1,dateObj2)[0]
   * 就是dateObj2比dateObj1晚的年数(Integer类型的)
   * 
   * @param dateObj1
   * @param dateObj2
   * @return Integer[] {year, month, day, hour, minute, second, isAfter}
   *         最后一个数isAfter代表第二个日期是否比第一个日期要晚(晚->1. 早 -> 0)
   * @throws Exception
   */
  public static Integer[] compareDateDiffer(Object dateObj1, Object dateObj2) throws Exception{
    Date date1 = ObjToDate(dateObj1);
    Date date2 = ObjToDate(dateObj2);
    int year = date2.getYear() - date1.getYear();
    int month = date2.getMonth() - date1.getMonth();
    int day = date2.getDate() - date1.getDate();
    int hour = date2.getHours() - date1.getHours();
    int minute = date2.getMinutes() - date1.getMinutes();
    int second = date2.getSeconds() - date1.getSeconds();
    int isAfter = date2.after(date1) ? 1 : 0; // 第二个日期是否比第一个日期要晚(晚->1. 早 -> 0)
    if(month < 0){
      year -= 1;
      month += 12;
    }
    if(day < 0){
      month -= 1;
      day += 30;
    }
    if(hour < 0){
      day -= 1;
      hour += 24;
    }
    if(minute < 0){
      hour -= 1;
      minute += 60;
    }
    if(second < 0){
      minute -= 1;
      second += 60;
    }
    // StringBuffer sb = new StringBuffer();
    // sb.append(year + "年").append(month + "月").append(day + "天").append(hour + "小时").append(minute
    // + "分钟").append(second + "秒");
    // System.out.println(sb);
    return new Integer[]{year, month, day, hour, minute, second, isAfter};
  }

  /**
   * 按照传入的格式取得时间(格式含义: "yyyy-MM-dd HH:mm:ss" -- 对应 "年-月-日 时:分:秒") 若format为空, 则默认为
   * "yyyy-MM-dd HH:mm:ss"
   * 
   * @return 字符串格式的时间
   */
  public static String getFormatDateStr(String format){
    return getFormatDateStr(format, (Date) null, null);
  }

  /**
   * 按照传入的格式取得时间(格式含义: "yyyy-MM-dd HH:mm:ss" -- 对应 "年-月-日 时:分:秒") 若format为空, 则默认为
   * "yyyy-MM-dd HH:mm:ss"
   * 
   * @return 字符串格式的时间
   */
  public static String getFormatDateStr(String format, Date date){
    return getFormatDateStr(format, date, null);
  }

  /**
   * 按照传入的格式取得时间(格式含义: "yyyy-MM-dd HH:mm:ss" -- 对应 "年-月-日 时:分:秒") 若format为空, 则默认为
   * "yyyy-MM-dd HH:mm:ss"
   * 
   * @return 字符串格式的时间
   * @throws Exception
   */
  public static String getFormatDateStr(String format, String date) throws Exception{
    return getFormatDateStr(format, ObjToDate(date), null);
  }


  /**
   * 按照传入的格式取得时间, 并且根据要求修正日期(格式含义: "yyyy-MM-dd HH:mm:ss" -- 对应 "年-月-日 时:分:秒")
   * 
   * @param format String类型的日期样式
   * @param date 待修正日期
   * @param difference Integer[]类型, difference[0-5] 分别代表 年月日时分秒的修正. 正数为+,负数为减
   * @return 修正后的字符串格式的时间
   */
  public static String getFormatDateStr(String format, Date date, Integer[] difference){
    GregorianCalendar res = new GregorianCalendar(); // 日期调整类
    res.setTime(date == null ? new Date() : date);
    if(difference != null){
      int len = difference.length; // 修正数组的长度
      if(len > 0){ // 若修正数组有第一个成员, 则是用于修正年份的
        res.add(GregorianCalendar.YEAR, difference[0]);
        if(len > 1){ // 若修正数组有第二个成员, 则是用于修正月份的
          res.add(GregorianCalendar.MONTH, difference[1]);
          if(len > 2){
            res.add(GregorianCalendar.DATE, difference[2]);
            if(len > 3){
              res.add(GregorianCalendar.HOUR, difference[3]);
              if(len > 4){
                res.add(GregorianCalendar.MINUTE, difference[4]);
                if(len > 5){
                  res.add(GregorianCalendar.SECOND, difference[5]);
                }
              }
            }
          }
        }
      }
    }
    return (ComStrUtil.isBlank(format) ? getDefaultFormat() : new SimpleDateFormat(format)).format(res.getTime());
  }

  /**
   * 按照传入的格式取得时间, 并且根据要求修正日期(格式含义: "yyyy-MM-dd HH:mm:ss" -- 对应 "年-月-日 时:分:秒")
   * 
   * @param format String类型的日期样式
   * @param date 待修正日期
   * @param difference Integer[]类型, difference[0-5] 分别代表 年月日时分秒的修正. 正数为+,负数为减
   * @return 修正后的字符串格式的时间
   * @throws Exception
   */
  public static String getFormatDateStr(String format, String date, Integer[] difference) throws Exception{
    return getFormatDateStr(format, ObjToDate(date), difference);
  }

  /**
   * 字符串转换成日期
   * 
   * @param str "yyyy-MM-dd HH:mm:ss" 格式的日期字符串
   * @return date
   * @throws Exception
   */
  public static Date StrToDate(String str) throws Exception{
    // Thread ct = Thread.currentThread();
    // String sb = new
    // StringBuilder("[").append(ct.getName()).append(":").append(ct.getId()).append("]").toString();

    if(ComStrUtil.isBlank(str)){
      throw new Exception("dateString日期不能为空[DateStringNullException]");
    }
    String dateTimeString = ""; // 年月日 时分秒
    dateTimeString = ComRegexUtil.getMatchedString(str, "^\\s*\\d{13}l?\\s*$");
    if(!ComStrUtil.isBlank(dateTimeString)){ // 说明是long型的时间
      return new Date(Long.parseLong(dateTimeString.replace("l", "")));
    }
    // str = str.trim();
    str = " " + str.trim() + " "; // 加上收尾空格, 便于Regex区分
    String dateString = ""; // 年月日
    String timeString = ""; // 时分秒


    // 匹配 yyyyMMddHHmmss, yyyyMMddHHmm, yyyyMMddHH
    dateTimeString = ComRegexUtil.getMatchedString(str, "[\\s](\\d{14}|\\d{12}|\\d{10})[\\s]");
    if(!ComStrUtil.isBlank(dateTimeString)){
      dateString = ComRegexUtil.getMatchedString(dateTimeString, "[\\s]\\d{8}");
      timeString = dateTimeString.replaceFirst("[\\s]\\d{8}", "");
    }
    // 匹配 HHmmss HHmm HH
    if(ComStrUtil.isBlank(timeString)){
      timeString = ComRegexUtil.getMatchedString(str, "[\\s](\\d{6}|\\d{4}|\\d{2})[\\s]");
    }
    // System.out.println("dateTimeString:" + dateTimeString);
    // 匹配 yyyy{各种分隔符}MM{各种分隔符}dd 其中 MM 和 dd可以只有一位
    if(ComStrUtil.isBlank(dateString)){
      // 取得年月日. 要求必须有分隔符, 所以月, 日 可以只有一位, 不用02这种
      dateString =
          ComRegexUtil.getMatchedString(str, "[\\s](19|20)\\d{2}[/|\\s|\\-|\\.]+\\d{1,2}[/|\\s|\\-|\\.]+\\d{1,2}[\\s]");
    }
    // 匹配 yyyy{可以没有分隔符}MM{可以没有分隔符}dd 其中 MM 和 dd可以必须是2位
    if(ComStrUtil.isBlank(dateString)){
      // 取得年月日. 要求可以没有分隔符(如:20140808), 所以月, 日 必须有2位, 不能用02这种
      dateString =
          ComRegexUtil.getMatchedString(str, "[\\s](19|20)\\d{2}[/|\\s|\\-|\\.]*\\d{2}[/|\\s|\\-|\\.]*\\d{2}[\\s]");
    }

    // 匹配 HH:mm:ss 其中 HH mm 和 ss可以只有1位 如 2:1:3
    if(ComStrUtil.isBlank(timeString)){
      timeString = ComRegexUtil.getMatchedString(str, "[\\s]\\d{1,2}:\\d{1,2}:\\d{1,2}(?!(^\\.))");
    }

    if(ComStrUtil.isBlank(dateString) && ComStrUtil.isBlank(timeString)){
      throw new Exception("dateString日期格式错误[DateStringEormatErrorException]");
    }

    // 若没有, 这设置默认值
    if(ComStrUtil.isBlank(dateString)){
      // 若没有年月日字符串, 则默认为1970-01-01
      dateString = "1970-01-01";
    }
    if(ComStrUtil.isBlank(timeString)){
      timeString = "00:00:00";
    }

    // info(nowTime + " dateString: " + dateString);
    // info(nowTime + " timeString: " + timeString);
    dateString = dateString.trim();
    timeString = timeString.trim();
    // 防止 2014--02//02 这种多个分隔符出现
    dateString = dateString.replaceAll("/+|\\s+|\\-+|\\.+", "-").replaceAll("-{2}", "-");
    String[] dateArr = dateString.split("-");
    String[] timeArr = timeString.split(":");
    if(dateArr.length > 1){
      dateTimeString = dateArr[0] + "-" + (dateArr[1].length() == 2 ? dateArr[1] : "0" + dateArr[1]) + "-"
          + (dateArr[2].length() == 2 ? dateArr[2] : "0" + dateArr[2]);
    }else{
      dateTimeString = dateString.substring(0, 4) + "-" + dateString.substring(4, 6) + "-" + dateString.substring(6);

    }
    dateTimeString += " "; // 中间的空格
    if(timeArr.length > 1){
      dateTimeString += (timeArr[0].length() == 2 ? timeArr[0] : "0" + timeArr[0]) + ":"
          + (timeArr[1].length() == 2 ? timeArr[1] : "0" + timeArr[1]) + ":"
          + (timeArr[2].length() == 2 ? timeArr[2] : "0" + timeArr[2]);
    }else{
      if(timeString.length() == 6){
        dateTimeString += timeString.substring(0, 2) + ":" + timeString.substring(2, 4) + ":" + timeString.substring(4);
      }else if(timeString.length() == 4){
        dateTimeString += timeString.substring(0, 2) + ":" + timeString.substring(2, 4) + ":00";
      }else if(timeString.length() == 2){
        dateTimeString += timeString.substring(0, 2) + ":00:00";
      }
    }
    Date res = getDefaultFormat().parse(dateTimeString);
    // info(nowTime + " dateTimeString: " + dateTimeString + " format.parse(dateTimeString):" +
    // res);
    return res;
  }

  /**
   * Object)(字符串或Date)转换成日期
   * 
   * @param str "yyyy-MM-dd HH:mm:ss" 格式的日期字符串
   * @return date
   * @throws Exception
   */
  public static Date ObjToDate(Object obj) throws Exception{
    String className = obj.getClass().getName();
    // System.out.println(className);
    if("java.lang.String".equals(className)){
      return StrToDate(obj + "");
    }else if("java.util.Date".equals(className)){
      return (Date) obj;
    }else if("java.lang.Long".equals(className)){
      return new Date((Long) obj);
    }else{
      throw new Exception("请输入正确的起始日期");
    }
  }

  /**
   * 取得长的(时分秒始终置为0)时间.
   * 
   * @return
   */
  public static String getDateLongZero(){
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    Calendar calendar = Calendar.getInstance();
    // calendar.setTime(new Date());
    String time = df.format(calendar.getTime());
    df = null;
    calendar = null;
    return time;
  }

  /**
   * 通过出生日期,计算年龄,精确到秒 convertBirth2Age(birth)[0]就是年龄(Integer的)
   * 
   * @param obj java.util.Date或java.lang.String型的时间.
   * @return Integer[] {year, month, day, hour, minute, second}
   * @throws Exception
   */
  public static Integer[] convertBirth2Age(Object obj) throws Exception{
    Date date = ObjToDate(obj);
    Date today = new Date();
    if(today.before(date)){ // 如果出生日期比现在还晚
      throw new Exception("出生日期不对.");
    }
    int year = today.getYear() - date.getYear();
    int month = today.getMonth() - date.getMonth();
    int day = today.getDate() - date.getDate();
    int hour = today.getHours() - date.getHours();
    int minute = today.getMinutes() - date.getMinutes();
    int second = today.getSeconds() - date.getSeconds();
    if(month < 0){
      year -= 1;
      month += 12;
    }
    if(day < 0){
      month -= 1;
      day += 30;
    }
    if(hour < 0){
      day -= 1;
      hour += 24;
    }
    if(minute < 0){
      hour -= 1;
      minute += 60;
    }
    if(second < 0){
      minute -= 1;
      second += 60;
    }
    // StringBuffer sb = new StringBuffer();
    // sb.append(year + "岁").append(month + "月").append(day + "天").append(hour + "小时").append(minute
    // + "分钟").append(second + "秒");
    // System.out.println(sb);
    return new Integer[]{year, month, day, hour, minute, second};
  }

  /**
   * 把日期转化成当天最晚的时间. 如:2014-05-05 -> 2014-05-05 23:59:59
   * 
   * @param date
   * @return
   * @throws Exception
   */
  public static String toDayEndString(Object date) throws Exception{
    return getFormatDateStr("yyyy-MM-dd", ObjToDate(date)) + " 23:59:59";
  }

  /**
   * 判断2个时间是不是同一天
   * 
   * @param left
   * @param right
   * @return
   * @throws Exception
   */
  public static Boolean isSameDay(Object left, Object right) throws Exception{
    return getFormatDateStr(DEFAULT_Date_FORMAT, ObjToDate(left))
        .equals(getFormatDateStr(DEFAULT_Date_FORMAT, ObjToDate(right))) ? true : false;
  }

  /**
   * 判断日期是不是 date. 即 没有time. 如:2015-01-01 return true; 2015-01-01 01:01:01 return false;
   */
  public static boolean isDate(String date){
    return ComStrUtil
        .isBlank(ComRegexUtil.getMatchedString(date, "^(19|20)\\d{2}[/|\\s|\\-|\\.]*\\d{2}[/|\\s|\\-|\\.]*\\d{2}$"))
            ? false : true;
  }

  /**
   * 判断日期是不是 date. 即 没有time. 如:2015-01-01 return true; 2015-01-01 01:01:01 return false;
   */
  public static boolean isDatetime(String date){
    return ComStrUtil.isBlank(ComRegexUtil.getMatchedString(date,
        "^(19|20)\\d{2}[/|\\s|\\-|\\.]*\\d{2}[/|\\s|\\-|\\.]*\\d{2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) ? false : true;
  }

  public static void main(String[] args) throws Exception{
    // System.out.println(isDate("20150129"));
    // System.out.println(new Date().getTime());
    // System.out.println(StrToDate("2015-02-02 01:01:01.7"));
    // System.out.println(StrToDate("20150102020101"));
    // System.out.println(ObjToDate(System.currentTimeMillis()));
    System.out.println(System.currentTimeMillis());
    System.out.println(getFormatDateStr(null, ObjToDate(System.currentTimeMillis())));
    System.out.println(new Date(System.currentTimeMillis()));
    System.out.println(myTest2("1458812170088l"));
    // ComLogUtil.info("return :" + myTest(30));
  }

  private static boolean myTest(int refreshPeriod) throws Exception{
    Date lastAccessTime = ObjToDate(getFormatDateStr(null, "2016-03-24 01:01:01"));
    System.out.println("refreshPeriod:" + refreshPeriod);
    if(refreshPeriod < 0) return false;

    System.out.println("lastAccessTime before" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastAccessTime));
    GregorianCalendar res = new GregorianCalendar();
    res.setTime(lastAccessTime);
    res.add(GregorianCalendar.SECOND, refreshPeriod);
    System.out.println("lastAccessTime after" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(res.getTime()));
    // ComLogUtil.info("compareto" + res.compareTo(ObjToDate(getFormatDateStr(null, "2015-03-24
    // 01:01:15"))));
    return res.getTime().after(ObjToDate(getFormatDateStr(null, "2016-03-24 01:01:31")));
  }

  private static boolean myTest2(String lastAccessTimeStr) throws Exception{
    try{
      int refreshPeriod = 30;
      // get RefreshPeriod

      System.out.println("refreshPeriod:" + refreshPeriod);
      if(refreshPeriod < 0) return false;
      if(refreshPeriod == 0) return true;

      // get lastAccessTime
      Date lastAccessTime;
      Matcher matcher = Pattern.compile("^\\s*\\d{13}l?\\s*$").matcher(lastAccessTimeStr);
      if(matcher.find()){
        lastAccessTime = new Date(Long.parseLong(matcher.group(0).replace("l", "")));
      }else{ // lastAccessTime value improper.
        System.out.println("lastAccessTime value improper:" + lastAccessTimeStr);
        return false;
      }

      System.out.println("lastAccessTime before" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastAccessTime));
      GregorianCalendar res = new GregorianCalendar();
      res.setTime(lastAccessTime);
      res.add(GregorianCalendar.SECOND, refreshPeriod);
      System.out.println("lastAccessTime after" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(res.getTime()));
      return res.getTime().before(new Date());
    }catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }
}
