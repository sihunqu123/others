package util.commonUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

import util.commonUtil.interfaces.IJSONArray;
import util.commonUtil.interfaces.IJSONObject;

/**
 * ComCollectionUtil
 */
public class ComCollectionUtil extends CommonUtil{
	
	public final static String METHOD_TOSTRING = "toString";
	
	public final static int TYPE_MAP = 1;
	public final static int TYPE_JSONOBJECT = 101;
	public final static int TYPE_JSONARRAY = 102;
	public final static int TYPE_ARRAY = 3;
	public final static int TYPE_ITERABLE = 4;
	public final static int TYPE_ENUMERATION = 5;
	
	/**
	 * stand for this instance is not a iterative instance.
	 */
	public final static int TYPE_NOT_A_COLLECTION = -1;
	
	/**
	 * get the collection code of an instance.
	 * @param obj an instance or Class of a instance
	 * @return type code.
	 */
	public static int getCollectionType(Object obj) {
		Class<?> class_ = toClass(obj);
		int returnVal = 0;
		if(Map.class.isAssignableFrom(class_)) {
			returnVal =  TYPE_MAP;
		} else if(obj.getClass().isArray()) {
			returnVal =  TYPE_ARRAY;
		} else if(Iterable.class.isAssignableFrom(class_)) {
			returnVal =  TYPE_ITERABLE;
		} else if(class_.isEnum()) {
			returnVal =  TYPE_ENUMERATION;
		}
		else if(IJSONObject.class.isAssignableFrom(class_)) {
			returnVal =  TYPE_JSONOBJECT;
		}
		else if(IJSONArray.class.isAssignableFrom(class_)) {
			returnVal =  TYPE_JSONARRAY;
		}
		else {
			returnVal =  TYPE_NOT_A_COLLECTION;
		}
		return returnVal;
	}
	
	/**
	 * check if it is Map/Array/Collection/enumeration.
	 * @param obj an instance or Class of a instance
	 * @return true if it's Map/Array/Collection/enumeration. Otherwise false.
	 */
	public static boolean isCollectionType(Object obj) {
		return getCollectionType(obj) > 0;
	}
	
	
	public static Class toClass(Object obj) {
		return obj instanceof Class ? (Class) obj : obj.getClass();  
	}
	
	/**
	 * 
	 * @param colletion
	 * @return null is it's not a collection.
	 */
	public static Boolean isCollectionEmpty(Object collection) {
		if(collection == null) return true;
		Boolean returnVal;
		switch (getCollectionType(collection)) {
		case TYPE_MAP:
			returnVal =  ((Map)collection).size() == 0;
			break;
		case TYPE_ARRAY:
			returnVal =  Array.getLength(collection) == 0;
			break;
		case TYPE_ITERABLE:
			returnVal =  !((Iterable)collection).iterator().hasNext();
			break;
		case TYPE_ENUMERATION:
			returnVal =  !((Enumeration)collection).hasMoreElements();
			break;	
		default:
			String errMsg = "typeError! " + ComLogUtil.mimicObjectToStringSimply(collection) + " is not a iterable";
			ComLogUtil.error(errMsg);
			returnVal = null;
		}
		return returnVal;
	}
	
	public static void main(String[] args) {
	}
	
}
