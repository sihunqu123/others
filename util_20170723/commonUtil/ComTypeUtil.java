package util.commonUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

/**
 * type
 */
public class ComTypeUtil extends CommonUtil{
	
	public final static String METHOD_TOSTRING = "toString";
	
	
	public static Class toClass(Object obj) {
		return obj instanceof Class ? (Class) obj : obj.getClass();  
	}
	
	public static boolean isToStringMeaningful(Object obj) {
		Class class_ = toClass(obj);
		Method toString = null;
		toString = ComReflectUtil.accessMethod(class_, METHOD_TOSTRING, new Class[]{});
		return toString == null || !(toString.getDeclaringClass() == Object.class);
	}
	
	public static void main(String[] args) {
		System.out.println(Collection.class.isAssignableFrom(ArrayList.class));
		System.out.println("class:" + Class.class.getName());
		int a = 0;
		char b = 'd';
		char c[] = new char[5];
		System.out.println(((Object)a).getClass());
		System.out.println(b);
		System.out.println(c.getClass());
		Package pkg = Package.getPackage("java");
		System.out.println(pkg);
		System.out.println(Object.class.getPackage());
		System.out.println(Object.class.getPackage().getName());
		
//		System.out.println("class:" + C.class.getName());
	}
	
}
