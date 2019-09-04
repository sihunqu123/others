package util.commonUtil.model;

import util.commonUtil.ComFileUtil;
import util.commonUtil.ComLogUtil;

public class Logger extends java.util.logging.Logger {
	public String dir;
	public String fileName;
	public String path;
	
	public Logger(String path) {
		super(path, path);
		this.path = path;
	}
	
	public void error(String msg, Exception e) {
		ComLogUtil.error(msg, e);
	}
	
	public void error(String msg) {
		ComLogUtil.error(msg);
	}
	
}