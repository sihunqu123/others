package util.commonUtil;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
// import java.math.BigDecimal;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 普通的工具类
 *
 * @author TianChen
 *
 */
public abstract class CommonUtil {
   public static Logger log = Logger.getLogger(CommonUtil.class.getName());
   
   private static String grepMark = "CommonUtil";
   
   public static int  cc;
   public static int  cc2;
   public int cc3;
   private static int cc4;
   private int cc5 = 5;
   private String cc6 = "cc6";

  static{
    initialLogs();
  }

  public static void initialLogs(){
    openLogs(ConfigManager.getArray("CommonUtil.openLogs"));
  }

  public static void openLogs(String[] names){
    // Logger.getLogger(name).setLevel(Level.FINEST);
    Field f = null;
    Logger logger = null;
    for(String name:names){
      logger = Logger.getLogger(name);
      logger.setLevel(Level.FINEST);
      // ComLogUtil.info(name + " set over");
      /*
       * try { if(!Class.forName(name).isLocalClass()) { ComLogUtil.info(name + " skip"); continue;
       * } } catch (ClassNotFoundException e) { ComLogUtil.info(name + " :" + e.getMessage());
       * continue; }
       *
       * try { f = Class.forName(name).getDeclaredField("_logger"); f.setAccessible(true);
       *
       * f.set(null, Proxy.newProxyInstance(Logger.class.getClassLoader(), new Class[]
       * {Logger.class}, new InvocationHandler() {
       *
       * public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
       *
       * System.out.println("before invoke I am doing something"); // 转调具体目标对象的方法 Object obj = null;
       * try{ obj = method.invoke(proxy, args); }catch(Exception e){
       * System.out.println(e.getMessage()); }finally{ // 在转调具体目标对象之后，可以执行一些功能处理 System.out.println(
       * "after invoke I am doing something");
       *
       * } return obj; }
       *
       * })); ComLogUtil.info(name + " done"); } catch (Exception e) { // TODO Auto-generated catch
       * block e.printStackTrace(); }
       */


    }
    // Logger.getLogger("").
    // java.util.logging.SimpleFormatter
  }

  /**
   * 把对象里面的String域trim了后再放回去（若对象的String类型的变量的值为null， 则将其设置为""）
   *
   * @param 待转化的对象(对象的类型任意)
   * @returnu 把每个域都tirm()了过后的对象
   */
  public static Object trimObject(Object obj){
    Class classType = obj.getClass();
    Field[] fields = classType.getDeclaredFields();
    int length = fields.length;
    String fName = "";
    Method getMethod_ = null;
    Method setMethod_ = null;
    String midLetter = "";
    for(Field f:fields){
      // log.info("type: " + f.getType());
      if(f.getType().toString().equals("class java.lang.String")){ // 只trim类型为String类型的
        f.setAccessible(true); // ************很重要
        fName = f.getName();
        midLetter = fName.substring(0, 1).toUpperCase();
        try{
          getMethod_ = classType.getMethod("get" + midLetter + fName.substring(1), new Class[]{});
          setMethod_ = classType.getMethod("set" + midLetter + fName.substring(1), new Class[]{f.getType()});
          Object value = getMethod_.invoke(obj, new Object[]{});
          // CommonUtil.log.info(value);
          // 防止空指针. 若是null, 就给他赋一个"";
          if(ComStrUtil.isBlank(value)){
            value = "";
          }else{
            value = value.toString().trim();
          }
          // CommonUtil.log.info(value);
          setMethod_.invoke(obj, new Object[]{value});
        }catch(Exception e){
          // CommonUtil.log.error("trim时错误!");
          classType = null;
          fields = null;
          getMethod_ = null;
          setMethod_ = null;
          e.printStackTrace();
        }

      }
    }
    classType = null;
    fields = null;
    getMethod_ = null;
    setMethod_ = null;
    return obj;
  }

  /**
   * 根据传入的Entity来生成这个实体的查询语句(列出了这个实体的所有列) 要求:实体Entity里面的成员变量名要与数据库的完全一样(不分大小写)
   *
   * @param Object obj 实体
   * @reurn String sql 查询语句
   */
  public static String getSelectSql(Object obj){
    Class classType = obj.getClass();
    Field[] fields = classType.getDeclaredFields();
    String fName = "";
    String sql = " select ";
    for(Field f:fields){
      fName = f.getName();
      sql += fName + ",";
    }
    sql = sql.substring(0, sql.length() - 1) + " from " + classType.getSimpleName();
    classType = null;
    fields = null;
    return sql;
  }


  /**
   * 从key-value对的对象中得到里面的value
   *
   * @param key_value 对象
   * @param key key键
   * @return
   */
  // public static String getStrValueFrmKVObj(HttpServletRequest request, String key) throws
  // Exception {
  // String res = request.getParameter(key);
  // return ComStrUtil.isBlank(res) ? "" : res.trim();
  // }

  /**
   * 从key-value对的对象中得到里面的value
   *
   * @param key_value 对象
   * @param key key键
   * @return
   */
  public static String getStrValueFrmKVObj(Map map, String key) throws Exception{
    String res = map.get(key) + "";
    return ComStrUtil.isBlank(res) ? "" : res.trim();
  }

  /**
   * 从key-value对的对象中得到里面的value(忽略key的大小写)
   *
   * @param key_value 对象
   * @param key key键
   * @return
   */
  // public static String getStrValueFrmKVObjIgnoreCaption(HttpServletRequest request, String key)
  // throws Exception {
  // return getStrValueFrmKVObjIgnoreCaption(request.getParameterMap(), key);
  // }

  /**
   * 从key-value对的对象中得到里面的value(忽略key的大小写)
   *
   * @param key_value 对象
   * @param key key键
   * @return
   */
  public static String getStrValueFrmKVObjIgnoreCaption(Map map, String key) throws Exception{
    String res = map.get(key) + "";
    if(ComStrUtil.isBlank(res)){ // 若本来取不到才考虑忽略大小写
      key = key.toLowerCase(); // 先全部转小写好比较
      Iterator it = map.keySet().iterator(); // 遍历map的key
      String tempKey = ""; // 用来存临时的next key
      while(it.hasNext()){
        tempKey = it.next() + ""; // 临时的next key
        if(key.equalsIgnoreCase(tempKey)){ // 将key转小写后于转小写后的str比较
          it = null;
          return map.get(tempKey) + ""; // 若想等, 则将map里的这个key的value返回
        }
      }
      it = null;
      return ""; // 若还是没有找到匹配的,则放回空"".
    }else{ // 若本身就能取到, 就直接取起走了.
      return res;
    }
  }

  /**
   * 从key-value对的对象中得到里面的value(忽略key的大小写)
   *
   * @param key_value 对象
   * @param key key键
   * @return
   */
  // public static String getStrValueFrmKVObjIgnoreCaptionAndNull(HttpServletRequest request, String
  // key) throws Exception {
  // String value = request.getParameter(key);
  // return ComStrUtil.isBlankOrNull(value) ? "" : value;
  // }

  /**
   * 从key-value对的对象中得到里面的value(忽略key的大小写)
   *
   * @param key_value 对象
   * @param key key键
   * @return
   */
  public static String getStrValueFrmKVObjIgnoreCaptionAndNull(Map map, String key) throws Exception{
    String res = getStrValueFrmKVObjIgnoreCaption(map, key);
    return ComStrUtil.isBlankOrNull(res) ? "" : res;
  }

  /**
   * 得到当前的jsp页面名字(不包含参数)
   *
   * @param request
   * @return
   */
  // public static String getJspFileName(HttpServletRequest request) {
  // String sp = request.getServletPath();
  // return sp.substring(sp.lastIndexOf("/") + 1, sp.indexOf(".jsp") + 4);
  // }
  //
  /**
   * 得到当前的jsp页面名字
   *
   * @param hasParam 是否需要参数
   * @param request
   * @return
   */
  // public static String getJspFileName(HttpServletRequest request, boolean hasParam) {
  // String sp = request.getServletPath();
  // if(hasParam){
  // return sp.substring(sp.lastIndexOf("/") + 1); //需要参数
  // } else {
  // return sp.substring(sp.lastIndexOf("/") + 1, sp.indexOf(".jsp") + 4); //不要参数
  // }
  //
  // }

  /**
   * 把一个数组转成以特定字符分割的字符串
   *
   * @param arr 数组
   * @param splitStr 分隔符
   * @return 合成后的字符串
   */
  public static String getStrFrmArr(String[] arr, String splitStr){
    int length = arr.length;
    if(length == 0){
      return "";
    }
    String str = "";
    for(int i = 0; i < length; i++){
      str += arr[i] + splitStr;
    }
    return str.substring(0, str.length() - 1);
  }

  /**
   * 取得当前request的相对路径(如:"/application/web/memberCenter/")
   *
   * @return 相对路径String
   * @throws Exception
   */
  // public static String getCurrentPath(HttpServletRequest request) throws Exception {
  // return ComFileUtil.getNoFilePathFrmPath(request.getContextPath() + request.getServletPath());
  // }


  /**
   * 根据已知Object(已经设好值了的),来生成并返回一个往数据库插入这个Object的sql语句
   *
   * @param obj 已经设置好值的实体(要求:全部字段必须为String类型.)
   * @return sql 生成的sql插入语句.
   * @throws Exception
   */
  public static String getInsertSqlFromObj(Object obj) throws Exception{
    trimObject(obj); // 首先去掉语句收尾的空格
    Class classType = obj.getClass();
    Field[] fields = classType.getDeclaredFields();
    int length = fields.length;
    String fName = "";
    String sql = "insert into " + classType.getSimpleName() + "(";
    String valuesSql = "values(";
    Method getMethod_ = null;
    Object value = null;
    String midLetter = null;
    String fieldType = "";
    // System.out.println(sql);
    // if(true) {return "";}
    for(Field f:fields){
      f.setAccessible(true); // ************很重要
      fName = f.getName();
      midLetter = fName.substring(0, 1).toUpperCase();
      getMethod_ = null;
      fieldType = f.getType().toString();
      // System.out.println(fieldType);
      try{
        if(fieldType.equals("boolean")){ // 若是boolean 则没有get方法只有isBoolean方法
          getMethod_ = classType.getMethod("is" + midLetter + fName.substring(1), new Class[]{});
        }else{
          getMethod_ = classType.getMethod("get" + midLetter + fName.substring(1), new Class[]{});
        }

        value = getMethod_.invoke(obj, new Object[]{});
        if(ComStrUtil.isBlank(value)){
          continue;
        } // 如果为空值,则不插入
        sql += fName + ","; // 在要插入的域中加一个
        if(fieldType.equals("class java.lang.String")){ // java.lang.String类型
          valuesSql += "'" + value + "',";
        }else if(fieldType.equals("class java.util.Date")){ // java.util.sql类型
          valuesSql += "'" + ComDateUtil.getFormatDateStr(null, (Date) value) + "',";
        }else if(fieldType.equals("int") || fieldType.equals("class java.lang.Integer")){ // int
                                                                                          // 或Integer
                                                                                          // 类型
          valuesSql += value + ",";
        }else if(fieldType.equals("class java.lang.Double") || fieldType.equals("float")){ // java.lang.Double或float类型
          valuesSql += value + ",";
        }else if(fieldType.equals("boolean")){ // boolean类型
          valuesSql += value + ",";
        }

      }catch(Exception e){
        // CommonUtil.log.error("拼接sql时错误!");
        classType = null;
        fields = null;
        value = null;
        getMethod_ = null;
        throw e;
      }finally{

      }
    }
    sql = sql.substring(0, sql.length() - 1) + ") " + valuesSql.substring(0, valuesSql.length() - 1) + ") ";
    classType = null;
    fields = null;
    value = null;
    getMethod_ = null;
    return sql;
  }

  /**
   * 根据已知Object(已经设好值了的),来生成并返回一个往数据库插入这个Object的PrepareSql语句(也就是有?的那种语句)
   *
   * @param obj 已经设置好值的实体(要求:全部字段必须为String类型.)
   * @return sql 生成的PrepareSql插入语句.
   * @throws Exception
   */
  public static Map getInsertPrepareSqlAndParamFromObj(Object obj) throws Exception{
    ArrayList<Object> paraList = new ArrayList<Object>();
    Map resMap = new HashMap<String, Object>();
    Class classType = obj.getClass();
    Field[] fields = classType.getDeclaredFields();
    int length = fields.length;
    String fName = "";
    String sql = "insert into " + classType.getSimpleName() + "(";
    String valuesSql = "values(";
    Method getMethod_ = null;
    Object value = null;
    String midLetter = null;
    String fieldType = "";
    // System.out.println(sql);
    // if(true) {return "";}
    for(Field f:fields){
      if(f.getType().toString().equals("class java.lang.String")){
        f.setAccessible(true); // ************很重要
        fName = f.getName();
        sql += fName + ",";
        midLetter = fName.substring(0, 1).toUpperCase();
        getMethod_ = null;
        fieldType = f.getType().toString();
        try{
          if(fieldType.equals("boolean")){ // 若是boolean 则没有get方法只有isBoolean方法
            getMethod_ = classType.getMethod("is" + midLetter + fName.substring(1), new Class[]{});
          }else{
            getMethod_ = classType.getMethod("get" + midLetter + fName.substring(1), new Class[]{});
          }
          getMethod_ = classType.getMethod("get" + midLetter + fName.substring(1), new Class[]{});
          value = getMethod_.invoke(obj, new Object[]{}); // 执行get方法
          if(ComStrUtil.isBlank(value)){
            continue;
          } // 如果为空值,则不插入
          valuesSql += "?,";
          paraList.add(value);
        }catch(Exception e){
          // CommonUtil.log.error("拼接sql时错误!");
          classType = null;
          fields = null;
          value = null;
          getMethod_ = null;
          throw e;
        }finally{

        }
      }
    }
    sql = sql.substring(0, sql.length() - 1) + ") " + valuesSql.substring(0, valuesSql.length() - 1) + ") ";
    classType = null;
    fields = null;
    value = null;
    getMethod_ = null;
    resMap.put("sql", sql);
    resMap.put("list", paraList);
    return resMap;
  }

  /**
   *
   * @param conn dao.getHibSessionFactory().openSession().connection()
   * @param obj 要插入的实体
   * @throws SQLException
   */
  public static void insertObjPrepareStat(Connection conn, Object obj) throws Exception{
    Map map = null;
    List list = null;
    PreparedStatement preState = null;
    try{
      map = getInsertPrepareSqlAndParamFromObj(obj);
      String sql = map.get("sql").toString();
      list = (List) map.get("list");
      preState = conn.prepareStatement(sql);
      if(!ComStrUtil.isBlank(list)){
        for(int i = 1; i <= list.size(); i++){
          preState.setObject(i, list.get(i - 1));
        }
      }
      preState.execute();
    }catch(Exception e){
      e.printStackTrace();
      throw e;
    }finally{
      preState.close();
      map.clear();
      map = null;
      list = null;
    }

  }

  /**
   * 把map里面的与obj对应的属性set进去, 从而把map转为实体Entity 要求:map里面Key(必须全部为小写)要与obj里面的成员变量名相对应(忽略大小写)
   *
   * @param Object obj 实体
   * @reurn String sql 查询语句
   */
  public static Object getEntityFromMap(Map map, Object obj) throws Exception{
    Class<? extends Object> classType = obj.getClass();
    Field[] fields = classType.getDeclaredFields();
    String fName = "";
    String midLetter = null;
    Method setMethod_ = null;
    for(Field f:fields){
      f.setAccessible(true); // ************很重要
      fName = f.getName();
      midLetter = fName.substring(0, 1).toUpperCase();
      try{
        setMethod_ = classType.getMethod("set" + midLetter + fName.substring(1), new Class[]{String.class});
        setMethod_.invoke(obj, new Object[]{map.get(fName.toLowerCase())});
      }catch(Exception e){
        fields = null;
        classType = null;
        setMethod_ = null;
        throw e;
      }
    }
    fields = null;
    classType = null;
    setMethod_ = null;

    return obj;
  }

  /**
   * 验证邮箱地址
   *
   * @return true 正确; false 错误
   */
  public static boolean chkMailAddress(String str){
    if(ComStrUtil.isBlank(str)){
      return false;
    }
    // 没转义的原始正则^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$
    if(str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")){
      return true;
    }else{
      return false;
    }
  }

  /**
   * 验证手机号码
   *
   * @return true 正确; false 错误
   */
  public static boolean chkPhoneNum(String str){
    if(ComStrUtil.isBlank(str)){
      return false;
    }
    // 没转义的原始正则(^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$)|(^((\(\d{3}\))|(\d{3}\-))?(1[358]\d{9})$)
    if(str.matches(
        "(^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$)|(^((\\(\\d{3}\\))|(\\d{3}\\-))?(1[358]\\d{9})$)")){
      return true;
    }else{
      return false;
    }
  }

  /***
   * MD5加码 生成32位md5码
   *
   * @param inStr 待加密字符串
   * @return 32位加密后的字符串
   */
  public static String string2MD5(String inStr){
    MessageDigest md5 = null;
    try{
      md5 = MessageDigest.getInstance("MD5");
    }catch(Exception e){
      System.out.println(e.toString());
      e.printStackTrace();
      return "";
    }
    char[] charArray = inStr.toCharArray();
    byte[] byteArray = new byte[charArray.length];

    for(int i = 0; i < charArray.length; i++)
      byteArray[i] = (byte) charArray[i];
    byte[] md5Bytes = md5.digest(byteArray);
    StringBuffer hexValue = new StringBuffer();
    for(int i = 0; i < md5Bytes.length; i++){
      int val = (md5Bytes[i]) & 0xff;
      if(val < 16) hexValue.append("0");
      hexValue.append(Integer.toHexString(val));
    }
    return hexValue.toString();
  }

  /**
   * 返回length个c字符串的重复叠加组合
   *
   * @param c 要重复的字符串
   * @param length 重复的次数
   * @return 重复后的字符串
   */
  public static String getDuplicateChar(String c, int length){
    int i = 0;
    String resString = "";
    for(i = 0; i < length; i++){
      resString += c;
    }
    return resString;
  }

  /**
   * 把一个字符串的某个范围覆盖成想要的字符串. 如:replaceRegion4String("0123456789", "*", 1,3) 返回 "0*3456789"
   *
   * @param originalString 原字符串
   * @param coverString 用来覆盖的字符串
   * @param beginPlace 开始覆盖的位置
   * @param endPlace 结束覆盖的位置
   * @return
   * @throws Exception
   */
  public static String replaceRegion4String(String originalString, String coverString, int beginPlace, int endPlace)
      throws Exception{
    if(beginPlace > endPlace){
      throw new Exception("结束位置不能比开始位置小   beginPlace: " + beginPlace + " > endPlace : " + endPlace);
    }
    // System.out.println("ddddddd: originalString.substring(0, beginPlace):" +
    // originalString.substring(0, beginPlace));
    // System.out.println("ddddddd: coverString:" + coverString);
    // System.out.println("ddddddd: originalString.substring(endPlace, originalString.length():" +
    // originalString.substring(endPlace, originalString.length()));
    // System.out.println("beginPlace : " + beginPlace + " endPlace" + endPlace);
    return originalString.substring(0, beginPlace) + coverString
        + originalString.substring(endPlace, originalString.length());
  }

  /**
   * 在源String中count指定字符String的数量.
   *
   * @param originalStr 源String
   * @param subStr 要匹配的字符串
   * @return 匹配字符串数量
   * @throws Exception
   */
  public static int countSubstringInString(String originalStr, String subStr) throws Exception{
    int count = 0;
    String bonus = "";
    if(ComStrUtil.isBlank(subStr)){
      throw new Exception("要匹配的字符串不能为空!!");
    }
    if(subStr.indexOf("(") > -1 || subStr.indexOf(")") > -1 || subStr.indexOf("\\") > -1 || subStr.indexOf(".") > -1){ // 如果包含了需要转移的字符
      bonus = "\\";
    }
    // System.out.println("**originalStr : " + originalStr);
    // System.out.println("**subStr : " + subStr);
    // System.out.println(originalStr.indexOf(subStr));
    while(originalStr.indexOf(subStr) > -1){
      // System.out.println("subStr : " + subStr);
      count++;
      originalStr = originalStr.replaceFirst(bonus + subStr, "");
      if(count > 1000){
        throw new Exception("Wrong!");
      }
    }
    return count;
  }

  /**
   * 把map转成dto实体里去
   *
   * @param map //待转的map
   * @param classType //dto实体的class
   * @return 转换好的dto
   * @throws Exception
   */
  public static Object map2Dto(Map<String, Object> map, Class classType) throws Exception{
    Object obj = null; // 要返回的实例
    try{
      obj = classType.newInstance();
    }catch(Exception e1){
      System.out.println("实例化失败");
      throw new Exception("实例化失败");
    }
    Object value = null; // 临时存map的value
    String fName = ""; // 成员变量名
    String midLetter = ""; // 驼峰规则中间的大写字母
    Method setMethod_ = null; // set方法
    Field[] fields = classType.getDeclaredFields(); // 得到指定类的所有成员变量,包括private的.
    int fieldLength = fields.length; // 成员变量的数量
    int i = 0; // 用于for循环
    try{
      for(i = 0; i < fieldLength; i++){
        fName = fields[i].getName(); // 取得成员变量名
        value = getStrValueFrmKVObj(map, fName); // 在map中取得对应的value
        if(ComStrUtil.isBlank(value)){ // 若map里没有这个key, 则跳过
          continue;
        }
        fields[i].setAccessible(true); // 必须要的
        midLetter = fName.substring(0, 1).toUpperCase(); // 得到方法中间的大写字母
        setMethod_ = classType.getMethod("set" + midLetter + fName.substring(1), new Class[]{fields[i].getType()}); // 得到setMethod
        // System.out.println("aaaaa: fields[i].getClass()" +
        // fields[i].getClass().getCanonicalName() + " " + fields[i].getName() + " bbbbbb: " +
        // fields[i].getModifiers() + " : " + fields[i].getType() + " ddddd: " +
        // fields[i].getGenericType());
        value = convertObject2AppointedType(value, fields[i]);
        setMethod_.invoke(obj, new Object[]{value}); // 执行set方法, 给成员变量赋值
      }
    }catch(Exception e){
      System.out.println(fields[i].getName());
      throw e;
    }finally{
      value = null;
      setMethod_ = null;
      fields = null;
    }
    return obj;
  }

  public static Object convertObject2AppointedType(Object obj, Field field) throws Exception{
    String typeName = field.getType().toString();
    // System.out.println(typeName);
    /** */
    if(typeName.equals("class java.lang.String")){ // java.lang.String类型
      return obj + "";
    }else if(typeName.equals("class java.util.Date")){ // java.util.sql类型
      return ComDateUtil.ObjToDate(obj + "");
    }else if(typeName.equals("int") || typeName.equals("class java.lang.Integer")){ // int 或Integer
                                                                                    // 类型
      return Integer.parseInt(obj + "");
    }else if(typeName.equals("class java.lang.Double") || typeName.equals("double")){ // java.lang.Double或float类型
      // System.out.println("**************typeName: " + typeName + " " + Double.parseDouble(obj +
      // "") + " ddd " + Double.parseDouble(obj + "") + "");
      return Double.parseDouble(obj + "");
    }else if(typeName.equals("float") || typeName.equals("class java.lang.Float")){ // java.lang.Double或float类型
      // System.out.println("**************typeName: " + typeName + " " + Double.parseDouble(obj +
      // "") + " ddd " + Double.parseDouble(obj + "") + "");
      return Float.parseFloat(obj + "");
    }else if(typeName.equals("boolean") || typeName.equals("class java.lang.Boolean")){ // boolean类型
      return Boolean.parseBoolean(obj + "");
    }else if(typeName.equals("class java.sql.Timestamp")){ // boolean类型
      return Timestamp.valueOf(obj + "");
    }else{
      System.out.println("未能识别的类型: " + typeName);
      return obj;
    }
  }
  
  public static <T> List<T> Array2List(T[] arr) { //public static <T> List<T> Array2List(T arr[]) { this works too.
	return new ArrayList<T>(Arrays.asList(arr));
  }

  /**
   * 判断是否有关键字, 防止sql注入.
   *
   * @param valueString
   * @throws Exception
   */
  public static void IsHavingKeyWord(String valueString) throws Exception{
    if(!ComStrUtil.isBlank(valueString)){
      valueString = valueString.toLowerCase();
      if(valueString.indexOf("insert ") >= 0 || valueString.indexOf("update ") >= 0 || valueString.indexOf("'") >= 0
          || valueString.indexOf("delete ") >= 0 || valueString.indexOf("drop ") >= 0
          || valueString.indexOf("create ") >= 0 || valueString.indexOf("truncate ") >= 0
          || valueString.indexOf("alter ") >= 0 || valueString.indexOf("function ") >= 0
          || valueString.indexOf("select ") >= 0 || valueString.indexOf("<script ") >= 0
          || valueString.indexOf("<iframe") >= 0 || valueString.indexOf("<img") >= 0){
        throw new Exception("不能包含insert、update、delete、drop、create、truncate、alter、function、select、'  等字符！");
      }
    }
  }

  /**
   * 计算出时间共有多少秒. eg. 00:01:01 -> 61
   *
   * @param time 58:01:02 这样的时间
   * @return 秒数
   */
  public static Integer computSeconds(String time){
    return new Time(time).getComputeSeconds();
  }

  // public static String getUserIpInfo(HttpServletRequest request) {
  // return request.getRemoteAddr() + "|" + request.getRemoteHost() + ":" + request.getRemotePort()
  // + "|" + request.getRemoteUser();
  // }


  public static void main(String[] args) throws Exception{

    Calendar calendar = Calendar.getInstance();
    // calendar.setTime(new Date());
    // String time = df.format(calendar.getTime());
    System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
    System.out.println(calendar.get(Calendar.MINUTE));
    System.out.println(calendar.getTime());
    // HashedMap aaa = new HashedMap();
    // aaa.put("2333", "ffffff");
    // aaa.put("233sas", "ffffffff");
    // JSONObject jresult = new JSONObject();
    // jresult.put("errno", "0");
    // jresult.put("list", aaa.keySet());
    // System.out.println(jresult);
    // System.out.println(replaceByRegex("...avi", "\\.*", "."));
    // System.out.println(replaceByRegex("...avi", "ccc", "."));
    // System.out.println(addDot2FileNameExtension(""));
    /**
     * //输出: one dog two dogs in the yard Pattern p = Pattern.compile("cat"); Matcher m = p.matcher(
     * "one cat two cats in the yard"); StringBuffer sb = new StringBuffer(); while (m.find()) {
     * m.appendReplacement(sb, "dog"); System.out.println(sb.toString()); } m.appendTail(sb);
     * System.out.println(sb.toString());
     */

    // System.out.println(getRandomString(9));
    /**
     * String s = "{\"internal_1\": [{\"version\": 4,\"addr\": \"192.160.1.11\"}]}"; String regex =
     * ".+?\\[(.+?)\\].+?"; Pattern pattern = Pattern.compile(regex); Matcher matcher =
     * pattern.matcher(s); if (matcher.matches()) { String group = matcher.group(1);
     * System.out.println(group); }else { System.out.println("no matches!!"); }
     */

    // System.out.println("uname=15117942760&passwd=123456&rememberUname=on&login=%E7%99%BB%E5%BD%95".replaceFirst("(?<=uname=)[^&]*(?=&)",
    // "XXXXXXXX"));
    // System.out.println("uname=15117942760&passwd=123456&rememberUname=on&login=%E7%99%BB%E5%BD%95".replaceFirst("(?<=passwd=)[^&]*(?=&)",
    // "XXXXXXXX"));
    // System.out.println(StrToDate("2014-08-31 17:50:07.3r4r"));

    // System.out.println(CommonUtil.convertDateWithDifference("2014-02-02 03:03:03", new Integer[]
    // {0,0,10}));


    Map hMap = new HashMap();
    String[] strArr = new String[]{"1", "2"};
    hMap.put("test", strArr);
    strArr[1] = "999";
    // System.out.println(((String[])(hMap.get("test")))[1]);

    // jsonArray.contains(o)

    /**
     * System.out.println(DateToStr(StrToDate("2014--./02/03")));
     * System.out.println(DateToStr(StrToDate("2014--./02/--03 02:05:06")));
     * System.out.println(DateToStr(StrToDate("2014-/2/03 020506")));
     * System.out.println(DateToStr(StrToDate("20141203020588")));
     * System.out.println(DateToStr(StrToDate("201412030205")));
     * System.out.println(DateToStr(StrToDate("2014120399")));
     * System.out.println(DateToStr(StrToDate("20149903"))); System.out.println(DateToStr(StrToDate(
     * "20140003 778899"))); System.out.println(DateToStr(StrToDate("1122")));
     */
    /**
     *
     *
     * Matcher matcher = null; Pattern pattern = null;
     *
     * String s = ""; String regex = "";
     *
     * s = "2014-08-08 01:01:01"; regex =
     * "^(19|20)\\d{2}[/\\s\\-\\.]*\\d{2}[/\\s\\-\\.]*\\d{2}[\\s]"; pattern =
     * Pattern.compile(regex); System.out.println(pattern); matcher = pattern.matcher(s); if
     * (matcher.find()) { String group = matcher.group(0); System.out.println(group); }else {
     * System.out.println("no matches!!"); }
     *
     * //s = "2014-08-08 01:01:01"; regex = " *\\d{1,2}:\\d{1,2}:\\d{1,2}"; pattern =
     * Pattern.compile(regex); System.out.println(pattern); matcher = pattern.matcher(s); if
     * (matcher.find()) { String group = matcher.group(0); System.out.println(group); }else {
     * System.out.println("no matches!!"); }
     */


    // System.out.println(StrToDate("2014-03-03 02:00:00.0"));
    // compareDate("2014-02-02 02:02:02", "2013-01-03 03:03:03");
    // System.out.println(getDateFrmFormat("yyyy-MM-dd HH:mm:ss", new Integer[]{1,-2,3,2}));
    SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try{
      java.util.Date begin = dfs.parse("1970-01-01 00:00:00:00");
      long time = 129000;
      java.util.Date end = new java.util.Date(time * 1000 + begin.getTime());
      String endStr = dfs.format(end);
      // System.out.println(endStr);
    }catch(ParseException e){
      e.printStackTrace();
    }
    // System.out.println(ObjToDate("2014-02-02 02:02:02").getTime() - ObjToDate("2014-02-02
    // 02:02:03").getTime()) ;
    // System.out.println(convertBirth2Age("1991-02-01 07:05:06")[0]);
    // System.out.println(" order by sddd asc".replaceFirst("order\\s+by", ""));

    // String ddd = "(";
    // ddd= "\\" + "(";
    // System.out.println("fdadfsd(fgbfdgfd)".replaceFirst(ddd, ""));

    // CustomerAccount
    /// System.out.println(" select a.detail_id, a.create_time, a.amount, a.fee_name, a.fee_type,
    // a.fee_detail from t_platform_account_detail a ;".matches(".+;+\\s*$"));
    // CustomerAccount cus = (CustomerAccount) map2Dto(map, CustomerAccount.class);
    // System.out.println(cus);
    // System.out.println(replaceRegion4String("0123456789", "ddddd", 1,3));
    // String sql = "select a.worker_id, a.customer_id,convert(varchar,a.worker_birthday,23) as
    // worker_birthday,a.worker_description,a.worker_name,a.worker_degree,a.worker_photo_addr,f.company_name,(select
    // e.service_name from t_service_type e where c.small_service_id = e.service_id) as
    // service_name,convert(decimal(18,2),round(c.service_cost,2)) as
    // service_cost,c.service_frequence,datediff(YEAR,a.worker_birthday,GETDATE()) as
    // age,g.collectioner,convert(varchar,g.collection_time,23) collection_time from
    // t_collection4needer g left join t_worker a on a.customer_id=g.customer_id and
    // a.worker_id=g.worker_id left join t_worker_skill c on c.id_= (select top 1 d.id_ from
    // t_worker_skill d where a.customer_id=d.customer_id and a.worker_id=d.worker_id) left join
    // t_enterprise_detail f on f.customer_id=a.customer_id where g.collectioner='CU00000001' order
    // by g.collection_time desc";
    // System.out.println("original: " + sql);
    // System.out.println(getCountSql(sql));
    // System.out.println(countSubstringInString("asdfsasadasf", ""));
    // System.out.println(validateTimeFormat("2013-12-32"));
    // System.out.println(getInsertSqlFromObj(test));
    // System.out.println(string2MD5(" "));
    // CommonUtil.trim(memo);
    // System.out.println(memo);

    // System.out.println(CommpareTime("2013-09-08", "2013-09-09"));

    // System.out.println(getDataLong());

    // System.out.println(getRandomString(10));

    // System.out.println(delPercentage(" dd% d %d "));

    /**
     * WebOrder webOrder = new WebOrder(); webOrder.setCs_time(" ddddd "); trimObject(webOrder);
     */
    // System.out.println(getDateLongPlus1Hour());
    // System.out.println(get2DotStrFrmDouble(new Double(3.0)));
    // System.out.println(getJspFileName(null,false));
    // System.out.println(isInteger("333.00"));


    // System.out.println("sdfsad阿打发手动阀dsafsadfad".toUpperCase());

    // System.out.println(Integer.parseInt("0"));
    // int aa = Integer.parseInt("0");
    // System.out.println(aa);


    // System.out.println(getStrFrmArr(new String[]{"abc", "sdf", "uio"}, "$"));
    // System.out.println(validateTimeFormat("2014-02-13 "));
    // System.out.println(getFileNameFrmPath("D:\\vincent\\workspace\\jydAll\\webapp\\res\\xml\\city.xml"));
    // System.out.println("abcde".endsWith("cde"));
    // HttpServletRequest request = null;
    // System.out.println("ddd" + getAttr(request,"dd"));
    // System.out.println("dddddd: " + chkPhoneNum("15320357998"));
    // System.out.println(parseDouble(""));

  }

  public static class Time{
    public int hour;
    public int minute;
    public int second;

    public Time(int hour, int minute, int second){
      super();
      this.hour = hour;
      this.minute = minute;
      this.second = second;
    }

    public int getHour(){
      return hour;
    }

    public void setHour(int hour){
      this.hour = hour;
    }

    public int getMinute(){
      return minute;
    }

    public void setMinute(int minute){
      this.minute = minute;
    }

    public int getSecond(){
      return second;
    }

    public void setSecond(int second){
      this.second = second;
    }

    public Time(Object time){
      super();
      Date datetime = null;
      try{
        datetime = ComDateUtil.ObjToDate(time);
      }catch(Exception e){
        ComLogUtil.printException(e, "TimeError");
      }
      this.hour = datetime.getHours();
      this.minute = datetime.getMinutes();
      this.second = datetime.getSeconds();
    }

    public Integer getComputeSeconds(){
      return hour * 3600 + minute * 60 + second;
    }

    public Integer getComputeMinutes(){
      return hour * 60 + minute;
    }

    public Integer getComputeHours(){
      return hour;
    }

  }

  public void forEach(Map map){


  }


}
