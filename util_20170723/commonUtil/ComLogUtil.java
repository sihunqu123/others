package util.commonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import util.commonUtil.interfaces.IJSONArray;
import util.commonUtil.interfaces.IJSONObject;
import util.commonUtil.json.JSONArray;
import util.commonUtil.json.JSONObject;
import util.commonUtil.loop.FunctionalforEach;
import util.commonUtil.model.Logger;


/**
 * 打印相关操作
 */
public class ComLogUtil extends CommonUtil{
	
	private static final String GREPMARK = "ComLogUtil";
	
	public static final String PRINTHTML = "comPH";

	/**
	 * default stackLevel for user.
	 */
	private static final int STACKLEVEL = 4;
	
	private static PrintStream info = System.out;
	
	private static PrintStream err = System.err;
	
	private static boolean overwriteSYSO = ConfigManager.getBoolean("ComLogUtil.overwriteSYSO");
	
	static {
		if(overwriteSYSO) {
			try {
				info = new PrintStream(new FileOutputStream(new File(
						//ConfigManager.getString("ComLogUtil.redirectLogFile")
						"c:\\Logs\\DoLogs_" + ComDateUtil.getDateAggreated() + ".txt" 
						)
						/**
						 * append or not.
						 * true, then logs will append to log file; false, will empty logs file at every jvm start.
						 */
						, false
						), true, ComFileUtil.UTF8);
				err = info;
				System.setOut(info);
				System.setErr(err);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				error("", e);
			}
		} 
	}
	
	private Map mapTest = new HashMap();
	
	private List listTest = new ArrayList();
	
	public static Logger getLogger(String name) {
		return new Logger(name);
	}
	
	/**
	 * 打印日志error
	 * @param msg 要打印的消息
	 */
//	public static void error(Object msg, Exception e) {
//		printLog(msg.toString(), STACKLEVEL, 1);
//	}
	
	/**
	 * 打印日志error
	 * @param msg 要打印的消息
	 */
	public static void error(Object msg) {
		printLog(msg.toString(), STACKLEVEL, 1);
	}
	
	/**
	 * 打印日志error
	 * @param msg 要打印的消息
	 */
	public static void error(String msg, Exception e) {
		printException(e, msg);
	}

	/**
	 * 打印日志
	 * @param msg 要打印的消息
	 */
	public static void info(Object msg) {
		printLog(msg + "", STACKLEVEL, 0);
	}
	
	/**
	 * 打印日志
	 * Used for commutil inner function only!
	 * @param msg 要打印的消息
	 * @param stackLevelIncrease to add stackLevel so that expected address displayed in log.
	 */
	protected static void info(Object msg, Integer stackLevelIncrease) {
		printLog(msg.toString(), STACKLEVEL + stackLevelIncrease, 0);
	}
	
	/**
	 * get the index first stack that should be print.
	 * @param stes StackTraceElements
	 * @return index
	 */
	private static int printBeginIndex(StackTraceElement[] stes) {
		int stackLevel = 1;
		try {
			while(!ComStrUtil.isBlankOrNull(ComRegexUtil.getMatchedString(stes[++stackLevel].getClassName(), "^(util|my|java)\\.")));
		} catch (Exception e) {
			stackLevel--;
		}
		return stackLevel;
	}
	
	/**
	 * 打印日志
	 * @param msg 要打印的消息
	 * @param callerRange 第几层调用栈
	 */
	public static String printLog(String msg, Integer callerRange, int level) {
		if(!chkPrint(msg)) return "";
		Thread ct = Thread.currentThread();
		StackTraceElement[] stes = ct.getStackTrace();
		//StackTraceElement ste = new Throwable().getStackTrace()[callerRange];
		StackTraceElement ste = stes[printBeginIndex(stes)];
		
		String className = getClassNameFrmSte(ste);
		if(ComStrUtil.isBlankOrNull(className)) {
			//fileName = sysoCallStacks("NoFileName");
			className = "NoClassName";
		}
		String str = new StringBuilder("[").append(ct.getName()).append(":").append(ct.getId())
				.append('|').append(Thread.currentThread().getContextClassLoader())
				.append(']')
				.append('(').append(className).append(".").append(ste.getMethodName()).append("():").append(ste.getLineNumber()).append(") ").append(ComDateUtil.getDateLong()).append(" - ").append(msg).toString();
		if(level == 0) {
			info.println(str);
		} else if(level == 1) {
			err.println(str);
		}
//		if(false) {
//			Logger.getLogger(getPackageNameFrmSte(ste)).logp(Level.WARNING, getClassNameFrmSte(ste), ste.getMethodName(), str);
//		}
		//System.out.println(getPackageNameFrmSte(ste) + "      " + getClassNameFrmSte(ste) + "  " + ste.getMethodName());
		ste = null;
		ct = null;
		stes = null;
		return str;
	}
	
	private static boolean chkPrint(String msg) {
		if(msg.length() > PRINTHTML.length() && PRINTHTML.equals(msg.substring(0, PRINTHTML.length()))) {
			return false;
		}
		return true;
	}
	
	/**
	 * 打印错误栈
	 * @param e 异常
	 */
	public static void printException(Exception e) {
		StackTraceElement [] messages=e.getStackTrace();
		err.print(e.getClass());
		err.println("Case: " + e.getCause());
	   	err.println("Message: " + e.getMessage());
	    for(int i=0;i<messages.length;i++){
//			    System.out.println("ClassName:"+messages[i].getClassName());
//			    System.out.println("getFileName:"+messages[i].getFileName());
//			    System.out.println("getLineNumber:"+messages[i].getLineNumber());
//			    System.out.println("getMethodName:"+messages[i].getMethodName());
		    err.println(messages[i].toString());
	    }
	    messages = null;
	}
	
	/**
	 * 打印错误栈 包含了签名, 便于帅选
	 * @param e 异常
	 */
	public static void printException(Exception e, String grepMark) {
		String dateLong = ComDateUtil.getDateLong();
		int i = 0;
		int length = 0;
		Thread ct = Thread.currentThread();
		//StackTraceElement ste = ct.getStackTrace()[2];
		StringBuilder sb = new StringBuilder("[").append(ct.getName()).append(":").append(ct.getId()).append("]");
		StackTraceElement [] messages=e.getStackTrace();
		grepMark = sb.append("<").append(grepMark).append(">").append(dateLong).append("|").toString();
		//grepMark = "<" + grepMark + ">" + dateLong + "|";
		info.print(grepMark + " " + e.getClass());
		info.println(grepMark + "Case: " + e.getCause());
		info.println(grepMark + "Message: " + e.getMessage());
	   	length = messages.length;
	    for(i = 0; i < length; i++){
//			    System.out.println("ClassName:"+messages[i].getClassName());
//			    System.out.println("getFileName:"+messages[i].getFileName());
//			    System.out.println("getLineNumber:"+messages[i].getLineNumber());
//			    System.out.println("getMethodName:"+messages[i].getMethodName());
	    	info.println(grepMark + messages[i].toString());
	    }

	    ct = null;
	    messages = null;
	}
	
	/**
	 * 打印map
	 * @param 
	 */
//	public static void sysoMap(Map<String, ?> map, String grepMark) {
//		String dateLong = ComDateUtil.getDateLong();
//		
//		grepMark = "<sysoMap" + grepMark + ">" + dateLong + "|";
//		if(ComStrUtil.isBlank(map)) {
//			info.println(grepMark + " map is null");
//			return;
//		} else if(map.isEmpty()) {
//			info.println(grepMark + " map is empty");
//			return;
//		}
//		Iterator<String> it = map.keySet().iterator();
//		String tempKey = "";
//		StringBuilder sysoStr = new StringBuilder(grepMark);
//		while(it.hasNext()) {
//			tempKey = it.next() + "";
//			sysoStr.append(" |key:").append(tempKey).append("; value:").append(map.get(tempKey)).append("|");
//		}
//		//System.out.println(sysoStr.toString());
//		it = null;
//		printLog(sysoStr.toString(), 3, 0);
//	}

//	public static void sysoStringArr(String[] strArr) {
//		for(String str : strArr) {
//			System.out.println(str);
//		}
//	}
	

	/**
	 * print callStacks with simplied class name.
	 * @param grepMark
	 * @return
	 */
	public static String sysoCallStacks(Object grepMark) {
		String dateLong = ComDateUtil.getDateLong();
		Thread ct = Thread.currentThread();
    	StackTraceElement[] stes = ct.getStackTrace();
    	int beginIndex = printBeginIndex(stes);
    	int len = stes.length;
    	StringBuilder sb = new StringBuilder("[").append(ct.getName()).append(":").append(ct.getId()).append("]").append("<").append(grepMark).append(">").append(dateLong).append("|");
    	for(int i = beginIndex; i < len; i++) {//只从调用者开始打
    		sb.append(getClassNameFrmSte(stes[i])).append(".").append(stes[i].getMethodName()).append(":").append(stes[i].getLineNumber()).append("|"); 
    	}
    	System.out.println(sb);
    	
//    	sb = new StringBuilder("//[").append(ct.getName()).append(":").append(ct.getId()).append("]").append("<").append(grepMark).append(">").append(dateLong).append("|");
//    	
//    	for(int i = beginIndex; i < len; i++) {//只从调用者开始打
//    		sb.append(stes[i].getClassName()).append(".").append(stes[i].getMethodName()).append(":").append(stes[i].getLineNumber()).append("|"); 
//    	}
//    	System.out.println(sb);
    	return sb.toString();
	}
	
	/**
	 * print callStacks with qualified class name.
	 * @param grepMark
	 * @return
	 */
	public static String sysoFullCallStacks(Object grepMark) {
		String dateLong = ComDateUtil.getDateLong();
		Thread ct = Thread.currentThread();
    	StackTraceElement[] stes = ct.getStackTrace();
    	int beginIndex = printBeginIndex(stes);
    	int len = stes.length;
    	StringBuilder sb = new StringBuilder("[").append(ct.getName()).append(":").append(ct.getId()).append("]").append("<").append(grepMark).append(">").append(dateLong).append("|");
    	for(int i = beginIndex; i < len; i++) {//只从调用者开始打
    		// stes[i].getClassName() the Class Name of the  Actually code are. 
    		// e.g: A extends B. when a call B's method, stes[i].getClassName() return B's className.  
    		sb.append(stes[i].getClassName()).append(".").append(stes[i].getMethodName()).append(":").append(stes[i].getLineNumber()).append("|"); 
    	}
    	System.out.println(sb);
    	
//    	sb = new StringBuilder("//[").append(ct.getName()).append(":").append(ct.getId()).append("]").append("<").append(grepMark).append(">").append(dateLong).append("|");
//    	
//    	for(int i = beginIndex; i < len; i++) {//只从调用者开始打
//    		sb.append(stes[i].getClassName()).append(".").append(stes[i].getMethodName()).append(":").append(stes[i].getLineNumber()).append("|"); 
//    	}
//    	System.out.println(sb);
    	return sb.toString();
	}
	
	public static String getPackageNameFrmSte(StackTraceElement ste) {
		String fn = ste.getClassName();
		return fn.substring(0, fn.lastIndexOf("."));
	}
	
	public static String getClassNameFrmSte(StackTraceElement ste) {
		String fn = ste.getClassName();
		return fn.substring(fn.lastIndexOf(".") + 1, fn.length());
	}
	
	
	public static String printStackTrace(Thread thread, String grepMark, String nowStr) {
		//thread.
    	StackTraceElement[] ste = thread.getStackTrace();
    	int i = 0;
    	int len = ste.length;
    	StringBuilder sb = new StringBuilder("[").append(thread.getName()).append(":").append(thread.getId()).append("]").append("<").append(grepMark).append(">").append(nowStr).append("|");
    	for(i = 2; i < len; i++) {//只从调用者开始打
    		sb.append(ste[i].getFileName()).append(":").append(ste[i].getLineNumber()).append("|");
    	}
    	System.out.println(sb);
    	ste = null;
    	return sb.toString();
	}
	
	public static void printAllThreadTrace(String grepMark) {
		ComLogUtil.info(grepMark + " thread Start...");
		String nowStr = ComDateUtil.getDateLong();
		Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
		Set<Thread> keySet = allStackTraces.keySet();
		Iterator<Thread> it = keySet.iterator();
		Thread thread = null;
		while(it.hasNext()) {
			thread = it.next();
			ComLogUtil.printStackTrace(thread, "", nowStr);
		}
		ComLogUtil.info(grepMark + " thread end...");
	}
	
	public static void pirntSystemProperty() {
		Properties properties = System.getProperties();
    	Enumeration<Object> keys = properties.keys();
    	String key = null;
    	while(keys.hasMoreElements()) {
    		key = keys.nextElement() + "";
    		ComLogUtil.info(new StringBuilder("key:").append(key).append(" |value:").append(properties.getProperty(key)));
    	}
    	properties = null;
    	keys = null;
	}
	
	private static class ObjectToStringer {
		
		private List<Object> alreadyDoneElements = new ArrayList<Object>();
		
		private List<Class<?>> hashcodeOnlyClasses;
		
		private List<Class<?>> exclude2ExpandClasses;
		
		private final static List<String> excludePkgs = new ArrayList<String>();
		
		private final static List<String> excludeClasses = new ArrayList<String>();
		
		private final static List<String> excludeFatherClasses = new ArrayList<String>();
		
		static {
			/**
			 * itself cannot be in one of those package
			 */
			excludePkgs.addAll(ConfigManager.getList("ComLogUtil.excludePkgs"));
			/**
			 * itself cannot be one of those Class
			 */
			excludeClasses.addAll(ConfigManager.getList("ComLogUtil.excludeClasses"));
			
			/**
			 * itself and it's parant(implemented interfaces and superclasses) cannot be one of those Class
			 */
			excludeFatherClasses.addAll(ConfigManager.getList("ComLogUtil.excludeFatherClasses"));
		}
		
		private static boolean isInExcludeClass(Object obj) {
			Class clazz = obj.getClass();
			String name = clazz.getName();
			
			for(String cls : excludePkgs) {
				if(name.startsWith(cls)) return true; 
			}
			
			for(String cls : excludeClasses) {
				if(name.equals(cls)) return true;
			}
			
			return isInExcludeFatherClass(clazz);
		}
		
		private static boolean isInExcludeFatherClass(Class clazz) {
			String clsName = clazz.getName();
			for(String cls : excludeFatherClasses) {
				if(clsName.startsWith(cls)) return true;
			}
			for(Class interfaceClass : clazz.getInterfaces()) {
				if(isInExcludeFatherClass(interfaceClass)) return true;
			}
			
			if (!clazz.isInterface()) {
	            Class<?> superclass = clazz.getSuperclass();
	            if (superclass != null && isInExcludeFatherClass(superclass)) return true;
	        }
			return false;
		}
		
		private int depth4Collection2Expand = 0;
		private int depth4NoneCollection2Expand = 0;
		
		private ObjectToStringer(int depth4Collection2Expand, int depth4NoneCollection2Expand, List<Class<?>> exclude2ExpandClasses, List<Class<?>> hashcodeOnlyClasses) {
			super();
			this.hashcodeOnlyClasses = hashcodeOnlyClasses;
			this.depth4Collection2Expand = depth4Collection2Expand;
			this.depth4NoneCollection2Expand = depth4NoneCollection2Expand;
			this.exclude2ExpandClasses = exclude2ExpandClasses;
		}
		
		private boolean isShouldExpand(Object obj, int depth4Collection2Expand, int depth4NoneCollection2Expand) {
			if(isListContains(this.alreadyDoneElements, obj) || (this.exclude2ExpandClasses != null && !isListContains(this.exclude2ExpandClasses, obj))) return false;
			if(ComCollectionUtil.isCollectionType(obj)) {
				if(depth4Collection2Expand > 0) {
					return true;
				}
			} else if(depth4NoneCollection2Expand > 0 && !isInExcludeClass(obj)) {
				return true;
			}
			return false;
		}
		
		private boolean isListContains(List list, Object o) {
			int size = list.size();
			if (o == null) {
	            for (int i = 0; i < size; i++)
	                if (list.get(i) == null)
	                    return true;
	        } else {
	            for (int i = 0; i < size; i++)
					try {if (o.equals(list.get(i))) return true;} catch (Exception e) {}
	        }
			return false;
		}
		
		private boolean isHashcodeOnly(Object obj) {
			return this.hashcodeOnlyClasses != null && this.hashcodeOnlyClasses.contains(ComTypeUtil.toClass(obj));
		}
		
		
		private String objToString(Object obj) {
			return this.wrap(obj, this.depth4Collection2Expand, this.depth4NoneCollection2Expand).toString();
		}
		
		private Object wrap(Object obj, int depth4Collection2Expand, int depth4NoneCollection2Expand) {
			Object res = "";
			if(obj != null) {
//				StringBuilder sb = new StringBuilder();
				boolean shouldExpand = isShouldExpand(obj, depth4Collection2Expand, depth4NoneCollection2Expand);
//				if(shouldExpand) this.alreadyDoneElements.add(obj);
				if(ComCollectionUtil.isCollectionType(obj)) {
					if(shouldExpand) {
						if(ComCollectionUtil.isCollectionEmpty(obj)) this.alreadyDoneElements.add(obj);
						res = collectionToString(obj, --depth4Collection2Expand, depth4NoneCollection2Expand);
					} else {
						res = mimicObjectToStringSimply(obj);
					}
//					res = shouldExpand ? collectionToString(obj, --depth4Collection2Expand, depth4NoneCollection2Expand) : mimicObjectToStringSimply(obj);
//					sb.append(shouldExpand ? collectionToString(obj, --depth4Collection2Expand, depth4NoneCollection2Expand) : mimicObjectToStringSimply(obj));
				} else {
					if(this.isHashcodeOnly(obj)) {
//						sb.append(mimicObjectToStringSimply(obj));
						res = mimicObjectToStringSimply(obj);
					} else if(ComTypeUtil.isToStringMeaningful(obj) || !shouldExpand) {
						try {	// sometimes people wrote stupid Exceptional toString().
//							sb.append('"').append(escapeQuotation(obj.toString())) .append('"');
							res = obj.toString();
						} catch (Exception e) {
							excludeClasses.add(obj.getClass().getName());	// exclude Object those guys wrote.
//							sb.append(mimicObjectToStringSimply(obj));
							res = mimicObjectToStringSimply(obj);
						}
					} else {
						this.alreadyDoneElements.add(obj);
//						sb.append(toString4HashcodeObj(obj, depth4Collection2Expand, --depth4NoneCollection2Expand));
						res = wrapHashcodeObj(obj, depth4Collection2Expand, --depth4NoneCollection2Expand);
					}

				}
//				return sb.toString();
			}
			return res;
		}
		
		private JSONObject wrapHashcodeObj(Object obj, int depth4Collection2Expand, int depth4NoneCollection2Expand){
		    Class classType = obj.getClass();
//		    Field[] fields = null;
//		    fields = classType.getDeclaredFields();
		    List<Field> fields = ComReflectUtil.getAllFields(classType);
		    String fName = "";
//		    StringBuilder sb = new StringBuilder("{");
		    JSONObject resJSON = new JSONObject();
		    Method getMethod_ = null;
		    Object value = null;
		    String midLetter = null;
		    String fieldType = "";
//		    boolean isFirstField = true;
		    for(Field f:fields) {
//		    	if(isFirstField) {
//		    		isFirstField = false;
//		    	} else {
//		    		sb.append(", ");
//		    	}
		        f.setAccessible(true);
		        fName = f.getName();
//		        sb.append(fName).append(":");
		        // try is/geter method.
		        fieldType = f.getType().toString();
		        getMethod_ = null;
		        midLetter = fName.substring(0, 1).toUpperCase();
		        try {
		          if(fieldType.equals("boolean")){ // 若是boolean 则没有get方法只有isBoolean方法
		            getMethod_ = classType.getMethod("is" + midLetter + fName.substring(1), new Class[]{});
		          }else{
		            getMethod_ = classType.getMethod("get" + midLetter + fName.substring(1), new Class[]{});
		          }
		          value = getMethod_.invoke(obj, new Object[]{}); // 执行get方法
		        } catch (Exception e) {
//		          error("", e);
		          //if no is/geter method exists, then access it directly.
		          try {
						//value = ComReflectUtil.accessFieldVal(obj, fName);
		        	  value = f.get(obj);
					} catch (Exception e1) {
						error("", e1);
					}
		        } finally {
//		        	resJSON.put(fName, wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
//		        	sb.append(objToString(value, depth4Collection2Expand, depth4NoneCollection2Expand));
		        	pubValIntoJson(resJSON, fName, value, wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
		        }
		    }
//		    sb.append("}");
		    classType = null;
		    fields = null;
		    value = null;
		    getMethod_ = null;
//		    return sb.toString();
		    return resJSON;
		}
		
		private Object pubValIntoJson(IJSONObject jo, String key, Object value, Object wrapValue) {
			if(wrapValue != null && ComCollectionUtil.isCollectionType(wrapValue) && !ComCollectionUtil.isCollectionEmpty(wrapValue)) {
				key += "|" + mimicObjectToStringSimply(value);
			}
			return jo.put(key, wrapValue);
		}
		
		private Object pubValIntoJson(IJSONArray ja, Object value, Object wrapValue) {
			if(wrapValue != null && ComCollectionUtil.isCollectionType(wrapValue) && !ComCollectionUtil.isCollectionEmpty(wrapValue)) {
				ja.put(new JSONObject().put(mimicObjectToStringSimply(value), wrapValue));
			} else {
				ja.put(wrapValue);
			}
			return ja;
		}
		
		/**
		 * 
		 * @param collection
		 * @param depth4Collection2Expand
		 * @param depth4NoneCollection2Expand
		 * @return Object[] {Object:resObject, Boolean:isEmpty}
		 */
		private Object collectionToString(Object collection, int depth4Collection2Expand, int depth4NoneCollection2Expand) {
//			StringBuilder sb = new StringBuilder(mimicObjectToStringSimply(collection));
			Object resObj = null;
			switch (ComCollectionUtil.getCollectionType(collection)) {
				case ComCollectionUtil.TYPE_MAP:
//						sb.append(mapToString((Map<?, ?>) collection, depth4Collection2Expand, depth4NoneCollection2Expand));
					resObj = mapToString(collection, depth4Collection2Expand, depth4NoneCollection2Expand);
					break;
				case ComCollectionUtil.TYPE_ARRAY:
				case ComCollectionUtil.TYPE_ENUMERATION:
				case ComCollectionUtil.TYPE_ITERABLE:
//					try {
					resObj = iterabkleToString(collection, depth4Collection2Expand, depth4NoneCollection2Expand);
//						sb.append(iterabkleToString((Iterable)collection, depth4Collection2Expand, depth4NoneCollection2Expand));
					break;
//					} catch (Exception e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//						return null;
//					}
				default:
					resObj = "TypeError: not a Collection";
//						sb.append("TypeError: not a Collection");
			}
//			return sb.toString();
			return resObj;
		}
		
		private JSONArray iterabkleToString(Object collection, final int depth4Collection2Expand, final int depth4NoneCollection2Expand) {
			final JSONArray jsonArray = new JSONArray();
			FunctionalforEach foreachFn = new FunctionalforEach<Object>(collection) {
				@Override
				public void loop(Object key, Object value, int index, Object collection) {
//					if(index != 0) this.loopLog.append(", ");
//					this.loopLog.append('"').append(this.Key2Str(key)).append('"').append(":").append(objToString(value, depth4Collection2Expand, depth4NoneCollection2Expand));
//					jsonArray.put(wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
					pubValIntoJson(jsonArray, value, wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
				}
			};
			foreachFn.execute();
			//if(foreachFn.getLength() > 0) jsonArray.put("[IterableName]:" + mimicObjectToStringSimply(collection));
			/**
			new FunctionalforEach<Iterable>(collection) {
				
				@Override
				public void loop(Object key, Object value, int index, Iterable collection) {
//					if(index != 0) this.loopLog.append(", ");
//					this.loopLog.append('"').append(this.Key2Str(key)).append('"').append(":").append(objToString(value, depth4Collection2Expand, depth4NoneCollection2Expand));
					jsonArray.put(wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
				}
			}.execute();
			*/
			return jsonArray;
		}
		
		private JSONObject mapToString(Object map, final int depth4Collection2Expand, final int depth4NoneCollection2Expand) {
			final JSONObject jsonObject = new JSONObject();
			FunctionalforEach foreachFn = new FunctionalforEach<Object>(map) {
				
				@Override
				public void loop(Object key, Object value, int index, Object collection) {
//					if(index != 0) this.loopLog.append(", ");
//					this.loopLog.append('"').append(escapeQuotation(this.Key2Str(key))).append('"').append(":").append(objToString(value, depth4Collection2Expand, depth4NoneCollection2Expand));
//					jsonObject.put(this.Key2Str(key), wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
					pubValIntoJson(jsonObject, this.Key2Str(key), value, wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
				}
			};
			foreachFn.execute();
//			if(foreachFn.getLength() > 0) jsonObject.put("[MapName]", mimicObjectToStringSimply(map));
			return jsonObject;
		}
		/**
		private <T> JSONArray arrayToString(final int depth4Collection2Expand, final int depth4NoneCollection2Expand, T collection) throws Exception {
			final JSONArray jsonArray = new JSONArray();
			new FunctionalforEach<T>(collection) {

				@Override
				public void loop(Object key, Object value, int index, T collection) {
//					if(index != 0) this.loopLog.append(", ");
//					this.loopLog.append('"').append(this.Key2Str(key)).append('"').append(":").append(objToString(value, depth4Collection2Expand, depth4NoneCollection2Expand));
					jsonArray.put(wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
				}
			}.executeLog().toString();
			return jsonArray;
		}
		
		
		private JSONArray enumerationToString(Enumeration collection, final int depth4Collection2Expand, final int depth4NoneCollection2Expand) throws Exception {
			final JSONArray jsonArray = new JSONArray();
			
			new FunctionalforEach<Enumeration>(collection) {
				@Override
				public void loop(Object key, Object value, int index, Enumeration collection) {
//					if(index != 0) this.loopLog.append(", ");
//					this.loopLog.append('"').append(this.Key2Str(key)).append('"').append(":").append(objToString(value, depth4Collection2Expand, depth4NoneCollection2Expand));
					jsonArray.put(wrap(value, depth4Collection2Expand, depth4NoneCollection2Expand));
				}
			}.execute();
			return jsonArray;
		}
		*/
	}
	
	
	public static void printMap(Map map, final String grepMark) {
		try {
			new FunctionalforEach<Map>(map) {
				
				@Override
				public void loop(Object key, Object value, int index, Map collection) {
					ComLogUtil.info(new StringBuilder(grepMark).append(" index:").append(index)
											.append(", key:").append(this.Key2Str(key))
											.append(", value:").append(this.Value2Str(value))
					, 3);
				}
			}.execute();
		} catch (Exception e) {
			printException(e, grepMark);
		}

	}
	
	public static void printArr(Object[] arr, final String grepMark) {
		try {
			new FunctionalforEach<Object>(arr) {
				
				@Override
				public void loop(Object key, Object value, int index, Object collection) {
					ComLogUtil.info(new StringBuilder(grepMark).append(" index:").append(index)
											.append(", value:").append(this.Value2Str(value))
					, 3);
				}
				
			}.execute();
		} catch (Exception e) {
			printException(e, grepMark);
		}
	}
	
	public static void printCollection(Collection collection, final String grepMark) {
		try {
			new FunctionalforEach<Collection>(collection) {
				
				@Override
				public void loop(Object key, Object value, int index, Collection collection) {
					ComLogUtil.info(new StringBuilder(grepMark).append(" index:").append(index)
											.append(", value:").append(this.Value2Str(value))
					, 3);
				}
				
			}.execute();
		} catch (Exception e) {
			printException(e, grepMark);
		}
	}
	
	/**
	 * 
	 * @param obj
	 * @return e.g:
	 * {<p>
			GREPMARK: ComLogUtil,<p>
			PRINTHTML: comPH,<p>
			STACKLEVEL: 4,<p>
			info: java.io.PrintStream@4554617c,<p>
			err: java.io.PrintStream@33909752,<p>
			mapTest: HashMap@1975012498[putVal1=putVal2,<p>
				putVal3_list=ArrayList@1028566121[0=listv1,<p>
												1=listv2,<p>
												2=java.util.HashMap@1975012498],<p>
				putVal4=putVal3],<p>
			listTest: java.util.ArrayList@1028566121
		}
	 */
	public static String objToString(Object obj) {
		return objToString(obj, 1, 1, null, null);
	}
	
	public static String objToString(Object obj, int depth4Collection2Expand, int depth4NoneCollection2Expand) {
		return objToString(obj, depth4Collection2Expand, depth4NoneCollection2Expand, null, null);
	}
	
	public static String objToString(Object obj, int depth4Collection2Expand, int depth4NoneCollection2Expand, Class<?>[] exclude2ExpandClasses) {
		return objToString(obj, depth4Collection2Expand, depth4NoneCollection2Expand, exclude2ExpandClasses, null);
	}
	
	public static String objToString(Object obj, int depth4Collection2Expand, int depth4NoneCollection2Expand, Class<?>[] exclude2ExpandClasses, Class<?>[] hashcodeOnlyClasses) {
		return //escapeJavaComment(
				new ObjectToStringer(depth4Collection2Expand, depth4NoneCollection2Expand
				, exclude2ExpandClasses == null ? null : new ArrayList<Class<?>>(Arrays.asList(exclude2ExpandClasses))
				, hashcodeOnlyClasses == null ? null : new ArrayList<Class<?>>(Arrays.asList(hashcodeOnlyClasses))).objToString(obj)
//				)
				;
	}
	
	/**
	 * escape QuotationMark.<br>
	 * replace QuotationMark" after even \		e.g: " -> \"	\\" -> \\\"
	 * and won't QuotationMark" after odd \		e.g: won't replace \"	\\\"
	 * @return
	 */
	public static String escapeQuotation(String str) {
		return str.replaceAll("(?<!(?<!\\\\)(\\\\{2}){0,9999}\\\\{1}(?!\\\\))\"", "\\\\\"");
	}
	
	public static String escapeJavaComment(String str) {
		//return ComRegexUtil.replaceAllLiterally(ComRegexUtil.replaceAllLiterally(str, "//", "\\/\\/"), "/*", "\\/\\*");
		return str.replaceAll("\\/\\/", "\\\\/\\\\/").replaceAll("\\/\\*", "\\\\/\\\\*");
	}
	
	/**
	 * mimic Object.toString();
	 * @param obj
	 * @return getSimpleName + @ + hashcode.
	 */
	public static String mimicObjectToStringSimply(Object obj) {
		return obj == null ? null : obj.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(obj));
	}
	
	/**
	 * mimic Object.toString();
	 * @param obj
	 * @return ClassName + @ + hashcode.
	 */
	public static String mimicObjectToString(Object obj) {
		return obj == null ? null : obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
	}
	
	public static void main(String[] args) {
		String st42re = "aa//ddf sd/*sdf";System.out.println(sysoFullCallStacks("ddd"));
		util.commonUtil.ComLogUtil.info(new String[] {"c", "f", "23"});
//		System.out.println(ComRegexUtil.replaceAllLiterally(ComRegexUtil.replaceAllLiterally(st42re, "//", "\\/\\/"), "/*", "\\/\\*"));
//		System.out.println(st42re.replaceAll("\\/\\/", "\\\\/\\\\/")
//				.replaceAll("\\/\\*", "\\\\/\\\\*")
//				);
//		info("cccaaaaaabbbbb");
//		info("cccaaaaakkkabbbbb");
//		if(true) return;
		int[] intArr = new int[] {1, 32, 45};
//		System.out.println(ComRegexUtil.getMatchedString(":16  Duration: 00:11:11.33, sta", "(?<=Duration:\\s{1,20})[^\\.]+"));
		ComLogUtil logUtil = new ComLogUtil();
		logUtil.listTest.add("li[stv1");
		logUtil.listTest.add("lis[tv2");
		List listTest2 = new ArrayList();
		listTest2.addAll(logUtil.listTest);
		listTest2.add("listVal--------------");
//		System.arraycopy(logUtil.listTest, 0, listTest2, 0, 1);
		logUtil.listTest.add(listTest2);
		logUtil.listTest.add(logUtil.mapTest);
		
//		System.out.println(ComLogUtil.err);
//		System.out.println(mimicObjectToStringSimply(ComLogUtil.err));
		logUtil.mapTest.put("p\"utVa[l1", "put/*Val2");
		logUtil.mapTest.put("p\\\"ut[Val4", "put8?V8*/al3");
		logUtil.mapTest.put("pu\\\\\"tVal3_li]st", logUtil.listTest);
		logUtil.mapTest.put("aa", "cccc");
		logUtil.mapTest.put("aa2", "ccccsdfads");
//		System.out.println(logUtil.listTest.hashCode());
//		System.out.println(list.listTest);
//		System.out.println(list.mapTest);
//		System.out.println(map.entrySet());
		System.out.println(objToString(intArr, 9, 9));
//		System.out.println(objToString(ComLogUtil.err, 9, 9));
//		System.out.println(objToString(logUtil.listTest, 9, 9));
//		System.out.println(objToString(logUtil.mapTest, 9, 9));
		System.out.println(objToString(logUtil));
//		System.out.println(new JSONObject(logUtil).toString(2));
//		System.out.println(objToString(logUtil, 2, 0));
		System.out.println(objToString(logUtil, 9, 9));
		if(true) return;
//		logUtil.ddd(new String[] {""});
		Integer[] ia = new Integer[] {0};
		System.out.println("ia:" + ia);
		System.out.println("ia:" + ia.getClass().getTypeParameters());
		System.out.println("ia:" + ia.getClass().getComponentType());
//		System.out.println("ia:" + logUtil.convert2Arr(ia, ia.getClass().getComponentType()));
		int [] intA = new int[] {1, 2};
		Class<? extends int[]> class1 = intA.getClass();
		System.out.println(class1);
		Object[] cc = ia;
//		Array.
//		Object[]  oi = (Object[])intA;	// compile error
		Object  o = intA;
		
		System.out.println("o:" + o.getClass().getComponentType());
		int[] res = (int [])o;
		System.out.println("res:" + res);
//		System.out.println("o:" + logUtil.convert2Arr(o, o.getClass().getComponentType()));
		
		int[] oarray = (int[]) o;	// OK.
		
		
//		logUtil.ddd(logUtil.convert2Arr(o, o.getClass()));
		System.out.println("ready:" + cc);
		System.out.println("ready:" + ia);
		System.out.println("ready:" + o);
		System.out.println("ready:" + oarray);
//		logUtil.ddd(new Integer[] {0});
		//logUtil.ddd((Integer[])(new int[] {0}));
//		logUtil.ddd((Character[])(new char[] {'d', 'a'}));
//		logUtil.aaa(Arrays.asList(new String[] {""}));
	}
	
}