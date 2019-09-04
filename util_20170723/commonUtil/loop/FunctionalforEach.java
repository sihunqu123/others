package util.commonUtil.loop;

import java.lang.reflect.Array;
import java.util.*;

import util.commonUtil.ComCollectionUtil;
import util.commonUtil.ComLogUtil;
import util.commonUtil.ComTypeUtil;
import util.commonUtil.interfaces.IJSONObject;


/**
 * Class to do functional programming
 * @author tiantc
 *
 */
public abstract class FunctionalforEach<T> {
//	@SuppressWarnings(value={"unchecked"})
//	private Map map;
	
	private T collection;
	
	/**
	 * size of this colletion.
	 */
	private int length;
	
	boolean shouldBreak = false;
	
//	@SuppressWarnings(value={"unchecked"})
//	public FunctionalforEach(Map map) {
//		this.map = map;
//		//this.excute();
//	}
	
	public StringBuilder loopLog = new StringBuilder();
	
	public StringBuilder getLoopLog() {
		return loopLog;
	}

	public FunctionalforEach(T collection) {
		this.collection = collection;
	}

	/**
	 * execute and return this collection
	 * @return collection
	 * @throws Exception
	 */
	final public T execute() {
		Object returnVal = null;
		if(this.collection == null) {
			return null;
		} else {
			switch (ComCollectionUtil.getCollectionType(this.collection)) {
				case ComCollectionUtil.TYPE_MAP:
					returnVal =  this.excuteMap((Map)this.collection);
					break;
				case ComCollectionUtil.TYPE_JSONOBJECT:
					returnVal =  this.excuteMap((IJSONObject)this.collection);
					break;
				case ComCollectionUtil.TYPE_ARRAY:
					returnVal =  this.excuteArray((T)this.collection);
					break;
				case ComCollectionUtil.TYPE_ITERABLE:
					returnVal =  this.excuteIterable((Iterable<?>)this.collection);
					break;
				case ComCollectionUtil.TYPE_ENUMERATION:
					returnVal =  this.excuteEnumeration((Enumeration)this.collection);
					break;	
				default:
					String errMsg = "typeError! " + ComLogUtil.mimicObjectToStringSimply(collection) + " is not a iterable";
					ComLogUtil.error(errMsg);
					return (T)errMsg;
			}
			return (T)returnVal;
		}
	}
	
	/**
	 * execute and return loopLog
	 * @return loogLop
	 * @throws Exception
	 */
	final public StringBuilder executeLog() {
		loopLog.append("{");
		this.execute();
		loopLog.append("}");
		return this.loopLog;
	}
	
	private Map excuteMap(Map map) {
		Set keySet = map.keySet();
		Iterator it = keySet.iterator();
		int index = 0;
		Object key;
		while(it.hasNext()) {
			key = it.next();
			this.length++;
			this.loop(key, map.get(key), index++, this.collection);
		}
		key = null;
		it = null;
		keySet = null;
		return map;
	}
	
	private IJSONObject excuteMap(IJSONObject map) {
		Set<String> keySet = map.keySet();
		Iterator<String> it = keySet.iterator();
		int index = 0;
		String key;
		while(it.hasNext()) {
			key = it.next();
			this.length++;
			this.loop(key, map.get(key), index++, this.collection);
		}
		key = null;
		it = null;
		keySet = null;
		return map;
	}
	
	private T excuteArray(T array) {
		int index;
		this.length = Array.getLength(array);
		for(index = 0; index < this.length; index++) {
			this.loop(index, Array.get(array, index), index, this.collection);
		}
		return array;
	}
	
	private Iterable<?> excuteIterable(Iterable<?> collection) {
		Iterator it = collection.iterator();
		int index = 0;
		while(it.hasNext()) {
			this.length++;
			this.loop(index, it.next(), index++, this.collection);
		}
		return collection;
	}
	
	private Enumeration excuteEnumeration(Enumeration enumeration) {
		int index = 0;
		while(enumeration.hasMoreElements()) {
			this.length++;
			this.loop(index, enumeration.nextElement(), index++, this.collection);
		}
		return enumeration;
	}
	
	/**
	 * How to convert key -> String
	 * U can override this to format you key
	 * @param key
	 * @return String formed key
	 */
	public String Key2Str(Object key) {
		try {
			return key == null ? null : key.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ComLogUtil.mimicObjectToStringSimply(key);
		}
	}
	
	/**
	 * How to convert value -> String
	 * U can override this to format you value
	 * @param value
	 * @return String formed value
	 */
	public String Value2Str(Object value) {
		return value == null ? null : value.toString();
	}
	
	/**
	 * what to do with each entry and value
	 * @param key key for Map; Or index for array.
	 * @param value
	 * @param index
	 */
	public abstract void loop(Object key, Object value, int index, T collection);

	/**
	 * Do not call this before loop() is done.
	 * @return 
	 */
	public int getLength() {
		return length;
	}
	
}
