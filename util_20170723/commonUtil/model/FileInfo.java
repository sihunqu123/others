package util.commonUtil.model;

import util.commonUtil.ComFileUtil;

public class FileInfo {
	public String dir;
	public String fileName;
	/**
	 * file extension including dot. e.g: .txt  .java
	 */
	public String fileExt;
	
	public FileInfo(String dir, String fileName, String fileExt) {
		this.dir = dir;
		if(!dir.endsWith(ComFileUtil.SEPARATOR)) this.dir += ComFileUtil.SEPARATOR;
		
		this.fileName = fileName;
		this.setFileExt(fileExt);
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileExt() {
		return fileExt;
	}
	
	public String getFileExt(boolean needDot) {
		return needDot ? fileExt : fileExt.substring(1);
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt.startsWith(".") ? fileExt : ('.' + fileExt); 
	}

	@Override
	public String toString() {
		return "FileInfo [dir=" + dir + ", fileName=" + fileName + ", fileExt=" + fileExt + "]";
	}
	
	
}