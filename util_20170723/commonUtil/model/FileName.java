package util.commonUtil.model;

import java.io.File;

import util.commonUtil.ComFileUtil;

public class FileName {
	FileInfo fileInfo;
	
	public FileName(String file) {
		this.fileInfo = ComFileUtil.getFileInfo(file);
	}
	
	public FileName(File file) {
		this.fileInfo = ComFileUtil.getFileInfo(file);
	}
	
	public FileName(String dir, String fileName, String fileExt) {
		this.fileInfo = new FileInfo(dir, fileName, fileExt);
	}
	
	public FileName append(String str) {
		this.fileInfo.setFileName(this.fileInfo.getFileName() + str);
		return this;
	}
	
	public FileName preAppend(String str) {
		this.fileInfo.setFileName(str + this.fileInfo.getFileName());
		return this;
	}
	
	public FileName setExt(String ext) {
		this.fileInfo.setFileExt(ext);
		return this;
	}
	
	public FileName getExt() {
		this.fileInfo.getFileExt();
		return this;
	}
	
	public FileName getExt(boolean needDot) {
		this.fileInfo.getFileExt(needDot);
		return this;
	}

	@Override
	public String toString() {
		return new StringBuilder(fileInfo.getDir()).append(fileInfo.getFileName()).append(fileInfo.getFileExt()).toString();
	}
	
	
}