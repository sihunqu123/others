package util.commonUtil;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public final class ConfigManager {

	//private static HashMap<String, String> config = new HashMap<String, String>();

	public ConfigManager() {
	}

	/**
	 * 这里getBundle的参数必须是文件的全路径名.(如:com.util.utilConfig)
	 */
	private static Properties p = new Properties();
	static {
		try {
			p.load(new FileInputStream(ConfigManager.class.getResource("./utilConfig.properties").getPath()));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("init failed");
		}
	}
	
	public static String getString(String key) {
		return p.getProperty(key);
	}
	
	/**
	 * get value as Boolean
	 * @param key
	 * @return true if value is 'true' case insensitively; else false.
	 */
	public static Boolean getBoolean(String key) {
		return "true".equalsIgnoreCase(p.getProperty(key));
	}
	
	public static String[] getArray(String key) {
		return ("" + p.getProperty(key)).split(ComRegexUtil.EOLRegex);
	}
	
	public static List<String> getList(String key) {
		return  new ArrayList<String>(Arrays.asList(getArray(key)));
	}
	
	public static void main(String[] args) {
		System.out.println(getString("ComLogUtil.excludeClasses"));
		System.out.println(ComLogUtil.objToString(getArray("ComLogUtil.excludeClasses")));
		System.out.println(ComLogUtil.objToString(getList("ComLogUtil.excludeClasses")));
		System.out.println(getString("ComLogUtil.redirectLogFile"));
	}
	
	
}
