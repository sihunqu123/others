package util.commonUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * access private field/method
 */
public class ComReflectUtil extends CommonUtil{
	private static final String GREPMARK = "ComReflectUtil";
	
	private static Object  cc;

	public static final String SEPARATOR = File.separator;
	
	/**
	 * get chain field value. e.g: orderDetail.userInfo.username will get the field value 'username' in userInfo, which is in orderDetail.
	 * @param obj
	 * @param fieldName
	 * @return field value
	 * @throws Exception
	 */
	public static Object accessFieldVal(Object obj, String fieldName) throws Exception {
		String[] split = fieldName.split("\\.");
		for(int i = 0; i < split.length; i++) {
			obj = accessFieldValNoChain(obj, ComTypeUtil.toClass(obj), split[i]);
		}
		return obj;
	}
	
	/**
	 * get field value.
	 * @param obj
	 * @param fieldName
	 * @return field value
	 * @throws Exception
	 */
	private static Object accessFieldValNoChain(Object obj, Class class_, String fieldName) throws Exception {
		Field f;
		Object res = null;
		try {
			// all fields(private ~ public) in this class(not including it's superClass/superInterface)
			f = class_.getDeclaredField(fieldName);
			f.setAccessible(true);
		} catch (Exception e) {
			try {
				// all public fields in this class and it's superClass/superInterface
				f = class_.getField(fieldName);
			} catch (Exception e1) {
				for(Class interfaceClass : class_.getInterfaces()) {
					if ((res = accessFieldValNoChain(obj, interfaceClass, fieldName)) != null) return res;
				}
				
				if (!class_.isInterface()) {
		            Class<?> superclass = class_.getSuperclass();
		            if (superclass != null && (res = accessFieldValNoChain(obj, superclass, fieldName)) != null) return res;
		        }
				return null;
//				ComLogUtil.info("Field not found. Existing fields:" + ComLogUtil.objToString(getAllFieldsVisibleInItself(class_)));
//				throw e1;
			}
//			Field[] Fields = obj.getClass().getFields();
//			for(int i = 0; i < Fields.length; i++) {
//				Fields[i].setAccessible(true);
//				System.out.println("Fields[i].getName():" + Fields[i].getName() + " value:" + Fields[i].get(obj));
//				if(Fields[i].getName().equals(fieldName)) {
//					f = Fields[i];
//					break;
//				}
//			}
		}
		return f.get(obj);
	}
	
	
	/**
	 * get the method.
	 * @param obj
	 * @param methodName
	 * @param params
	 * @return method.
	 * @throws Exception
	 */
	public static Method accessMethod(Object obj, String methodName) throws Exception {
		return accessMethod(obj, methodName, new Class[] {});
	}
	
	/**
	 * get the method.
	 * @param obj
	 * @param methodName
	 * @param params
	 * @return method.
	 * @throws Exception
	 */
	public static Method accessMethod(Object obj, String methodName, Class... params) {
		Method method;
		Class class_ = ComTypeUtil.toClass(obj);
		try {
			method = class_.getDeclaredMethod(methodName, params);
			method.setAccessible(true);
		} catch (Exception e) {
			try {
				method = class_.getMethod(methodName, params);
			} catch (Exception e1) {
				//ComLogUtil.info("Method:" + methodName + " not found. Existing methods:" + ComLogUtil.objToString(getAllMethodsVisibleInItself(class_)));
				//throw e1;
				return null;
			}
		}
		return method;
	}
	
	public static List<Method> getAllMethodsVisibleInItself(Class class_) {
		// should new ArrayList, otherwise, java.lang.UnsupportedOperationException
		List<Method> list = new ArrayList(Arrays.asList(class_.getMethods()));
		list.addAll(Arrays.asList(class_.getDeclaredMethods()));
		return list;
	}
	
	public static List<Field> getAllFieldsVisibleInItself(Class class_) {
		// should new ArrayList, otherwise, java.lang.UnsupportedOperationException
		List<Field> list = new ArrayList(Arrays.asList(class_.getFields()));
		list.addAll(Arrays.asList(class_.getDeclaredFields()));
		return list;
	}
	
	public static List<Field> getAllFields(Class class_) {
		List<Field> res = new ArrayList<Field>(Arrays.asList(class_.getDeclaredFields()));
		for(Class interfaceClass : class_.getInterfaces()) {
			res.addAll(getAllFields(interfaceClass));
		}
		
		if (!class_.isInterface()) {
            Class<?> superclass = class_.getSuperclass();
            if (superclass != null) res.addAll(getAllFields(superclass));
        }
		return res;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(getAllFields(ComReflectUtil.class));
			System.out.println(new ComReflectUtil().cc3);
			
			System.out.println(ComLogUtil.objToString(ComReflectUtil.class.getFields()));
			System.out.println(ComLogUtil.objToString(ComReflectUtil.class.getDeclaredFields()));
			System.out.println(ComLogUtil.objToString(ComReflectUtil.class.getSuperclass().getDeclaredFields()));
			
			System.out.println(CommonUtil.log);
			System.out.println(ComReflectUtil.accessFieldVal(new ComReflectUtil(), "grepMark"));
			Field commonUtilGrepMark = ComReflectUtil.class.getSuperclass().getDeclaredField("grepMark");
			commonUtilGrepMark.setAccessible(true);
			System.out.println("oo:" + commonUtilGrepMark.get(ComReflectUtil.class));
			
			Field commonUtilCC4 = ComReflectUtil.class.getSuperclass().getDeclaredField("cc4");
			commonUtilCC4.setAccessible(true);
			System.out.println("cc4:" + commonUtilCC4.get(ComReflectUtil.class));
			
			Field commonUtilCC5 = ComReflectUtil.class.getSuperclass().getDeclaredField("cc5");
			commonUtilCC5.setAccessible(true);
			System.out.println("cc5:" + commonUtilCC5.get(new ComReflectUtil()));
			
			Field commonUtilCC6 = ComReflectUtil.class.getSuperclass().getDeclaredField("cc6");
			commonUtilCC6.setAccessible(true);
			System.out.println("cc6:" + commonUtilCC6.get(new ComReflectUtil()));
			//System.out.println("cc6 static:" + commonUtilCC6.get(ComReflectUtil.class));// error
			
			Method objectToString = accessMethod(Object.class, "toString", new Class[] {});
			System.out.println("objectToString():" + objectToString);
			Method myToString = null;
			int a = 0;
			
			try {
				myToString = accessMethod(ComLogUtil.class, "toString", new Class[] {});
			} catch (Exception e) {
			}
			Method intToString = accessMethod(a, "toString");
			System.out.println("myToString():" + myToString);
			System.out.println("whether they equals:" + (objectToString == myToString));
			System.out.println("IntToString():" + intToString);
			System.out.println("fload toString():" + accessMethod(1.1f, "toString"));
			System.out.println("string toString():" + accessMethod("", "toString"));
			System.out.println("string toString() getDeclaringClass:" + accessMethod("", "toString").getDeclaringClass());
			System.out.println("string toString() .getDeclaringClass() == Object.class:" + (accessMethod("", "toString").getDeclaringClass() == Object.class));
			System.out.println("StringBuffer toString():" + accessMethod(new StringBuffer(), "toString"));
			System.out.println("arr toString():" + accessMethod(new int[] {1, 2}, "toString"));
			System.out.println("arr toString() .getDeclaringClass() == Object.class:" + (accessMethod(new int[] {1, 2}, "toString").getDeclaringClass() == Object.class));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}