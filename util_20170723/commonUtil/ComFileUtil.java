package util.commonUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.SortedMap;

import util.commonUtil.model.FileInfo;
import util.commonUtil.model.FileName;

/**
 * 文件相关操作
 */
public class ComFileUtil extends CommonUtil{
	
	private static final String GREPMARK = "ComFileUtil";

	public static final String SEPARATOR = File.separator;
	
	/**
	 * file separator for regex use.
	 */
	public static final String SEPARATORREG = File.separator.equals("\\\\") ? "\\\\" : File.separator;
	
	
	
	public static final String UTF8 = "utf-8";
	/**
	 * 取有后缀的文件名 regex
	 */
	private static final String REGEXFILENAMEEX = "(?<=/?)(?!(.*/.*))[^?/]*";
	/**
	 * 取有后缀的文件名 regex
	 */
	private static final String REGEXFILENAMEEX_Windows = "(?<=\\\\?)(?!(.*\\\\.*))[^?\\\\]*";
	/**
	 * 取没有后缀的文件名 regex
	 */
	private static final String REGEXFILENAMENOEX = "(?<=/?)(?!(.*/.*))[^?/\\.]*";
	/**
	 * 取没有后缀的文件名 regex
	 */
	private static final String REGEXFILENAMENOEX_Windows = "(?<=\\\\?)(?!(.*\\\\.*))[^?\\\\\\.]*";
	
	
	
	public static String getSeparator() {
		return SEPARATOR;
	}

	/**
	 * 取有后缀的文件名 regex
	 */
	public static String getRegexfilenameex() {
		return File.separator.equals("\\") ? REGEXFILENAMEEX_Windows : REGEXFILENAMEEX;
	}
	/**
	 * 取没有后缀的文件名 regex
	 */
	public static String getRegexfilenamenoex() {
		return File.separator.equals("\\") ? REGEXFILENAMENOEX_Windows : REGEXFILENAMENOEX;
	}


    /**
     * copy file
     * @param srcFileName
     * @param destFileName 
     * @throws IOException
     */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        byte[] byteBuf = null;
        InputStream fis = null;
        OutputStream fos = null;
        try {
        	if(srcFile.exists()){	//若源文件存在
        		fis = new FileInputStream(srcFile);
        		fos = new FileOutputStream(destFile);
        		byteBuf = new byte[1024];
	            int readByteNumber = -1;
	            while((readByteNumber = fis.read(byteBuf)) > -1) {
	            	fos.write(byteBuf, 0, readByteNumber);
	            }
	            fos.flush();
        	} else {
        		throw new FileNotFoundException(" soureFile not found! ");
        	}
        } finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
            byteBuf = null;
        }
    }

    /**
     * 删除文件或文件夹(慎用!!!! 若文件名写掉了只剩一个文件夹, 则该文件夹会被删掉!!!)
     * @param file 要删除的文件或目录
     * @return true: 删除了文件或目录; false: 文件或文件夹不存在.
     * @throws Exception 
     */
    public static Boolean delFileAndFolder(File file) throws Exception {
        if(!file.exists()) return false;	//若文件或文件夹不存在
        if (file.isDirectory()) {// 判断是文件还是目录
            if (file.listFiles().length == 0) {// 若目录下没有文件则直接删除
            	file.delete();
            } else {// 若有, 则把文件放进数组，并判断是否有下级目录
                File delFile[] = file.listFiles();	//得到目录下一层的所有文件
                int i = file.listFiles().length;
                for (int j = 0; j < i; j++) {	//遍历每一个文件
                    if (delFile[j].isDirectory()) {	//若是目录
                    	delFileAndFolder(new File(delFile[j].getAbsolutePath()));// 递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();//若是文件则删除文件
                }
            }
        } else if(file.isFile()){ //若是文件
        	delFile(file);
        } else {
        	throw new Exception("该文件既不是文件夹也不是文件");
        }
        return true;
    }
    
    /**
     * 删除文件
     * @param file
     * @throws Exception
     * @return true: 成功删除了文件; false: 要删除的文件不存在.
     */
    public static Boolean delFile(File file) throws Exception {
    	if(!file.exists()) return false;
    	if(file.isDirectory()) {
    		throw new Exception(" 要删除的只能是文件, 不能是目录(目录的话, 请调用delFileAndFolder()方法)");
    	} else if(file.isFile()) {
    		file.delete();
    	} else {
    		throw new Exception("该文件既不是文件夹也不是文件");
    	}
    	return true;
    }
    
   /** 
    * 更改文件名字
	* @param oldFile 旧文件, 待更改的文件
	* @param newFile 更改后的文件 
	* @param forceCover 若新目录下存在和转移文件具有相同文件名的文件时，是否覆盖新目录下文件，cover=true将会覆盖原文件，否则不操作 
	* @throws Exception 
	*/ 
    public static void changeDirectory(File oldFile, File newFile,boolean forceCover) throws Exception{
    	if(!oldFile.exists()) {
    		throw new Exception(" 源文件不存在");
    	}
        if(newFile.exists()){  //若在待转移目录下，已经存在待转移文件
            if(forceCover) {	//若轻质覆盖
            	oldFile.renameTo(newFile);
            } else {
            	throw new Exception(" 改文件已经存在了");   
            }
        } else {   
        	oldFile.renameTo(newFile);  
        }   
	}
    
    /**
	 * 给Object(String 或 File) 创建路径
	 * @param fileOrPath
	 * @throws Exception
	 */
	public static void mkDirs(Object fileOrPath) throws Exception {
		String pathStr = "";
		if("java.lang.String".equals(fileOrPath.getClass().getName())){//如果是String
			pathStr = fileOrPath + "";
		} else if("java.io.File".equals(fileOrPath.getClass().getName())) {//如果是File
			pathStr = ((File)fileOrPath).getPath();
		} else {
			throw new Exception(" Path type error!");
		}
		//判断路径存不存在,若不存在则新建路径
		File file = new File(getNoFilePathFrmPath(pathStr));
		if(!file.exists()) {
			file.mkdirs();
		}
	}
	
	/**
	 * 从包含文件名的路径String中取得没有文件名的path(路径最后面没有分隔符) 如 "e:/nn/" 返回 e:/nn
	 * @param path 没有文件名的路径
	 * @return 文件名
	 * @throws Exception 
	 */
	public static String getNoFilePathFrmPath(String path) throws Exception {
		File file = convert2File(path);
		if(file.isFile()) {
			return file.getParent();
		} else if(file.isDirectory()) {
			return file.getPath();
		} else {	//还有可能Path是像"/application/web/memberCenter/"这样的相对路劲, 这个我们也得兼容
//			info("file.getAbsolutePath():" + file.getAbsolutePath());
//			info("file.getPath():" + file.getPath());
//			info("file.getParent():" + file.getParent());
			return path.substring(0, getLastSlashIndex(path));
		}
	}
	
	/**
	 * 把Object(String 或  File) 转换才File
	 * @param path Object(String 或  File)
	 * @return File
	 * @throws Exception
	 */
	private static File convert2File(Object path) {
		if("java.lang.String".equals(path.getClass().getName())){//如果是String
			return new File(path + "");
		} else if("java.io.File".equals(path.getClass().getName())) {//如果是File
			return (File)path;
		} else {
			throw new RuntimeException("file type error!");
//			throw new IOException("file type error!");
		}
	}
    
	/**
	 * 得到文件名 e.g F:\\download\\bbb\\aa.txt -> aa.txt
	 * @param path
	 * @param hasExtension 是否要扩展名
	 * @return 
	 * @throws Exception
	 */
	public static String getFileName(File path, Boolean hasExtension) {
		return getFileName(path.getPath(), hasExtension);
	}
	
	/**
	 * 得到文件名 e.g F:\\download\\bbb\\aa.txt -> aa.txt
	 * @param path
	 * @param hasExtension 是否要扩展名
	 * @return 
	 * @throws Exception
	 */
	public static String getFileName(String path, Boolean hasExtension) {
		return ComRegexUtil.getMatchedString(path, hasExtension ? getRegexfilenameex() : getRegexfilenamenoex());
	}
	
	/**
   * get file extension
   * @param path
   * @param needDot true:.jpg; false:jpg.
   * @return 
   * @throws Exception
   */
  public static String getFileExtension(String path, Boolean needDot) {
    return ComRegexUtil.getMatchedString(path, needDot ? "\\.(?!(.*\\..*)).*" : "(?<=\\.)(?!(.*\\..*)).*");
  }
  
  /**
   * get file extension
   * @param path
   * @param needDot true:.jpg; false:jpg.
   * @return 
   * @throws Exception
   */
  public static String getFileExtension(File path, Boolean needDot)  {
    return ComRegexUtil.getMatchedString(path.getPath(), needDot ? "\\.(?!(.*\\..*)).*" : "(?<=\\.)(?!(.*\\..*)).*");
  }
	
	
	/**
	 * 给文件名加上一个重复数. 用于解决目标目录已经有同名文件的问题. 如: 
	 * "E:/eee/aaa.jpg" => "E:/eee/aaa(2).jpg"
	 * "E:/eee/aaa(2).jpg" => "E:/eee/aaa(3).jpg"
	 * "E:/eee/aaa" => "E:/eee/aaa(2)"
	 * "E:/eee/aaa(2)" => "E:/eee/aaa(3)"
	 * @param file 文件path.
	 * @return
	 * @throws Exception
	 */
	public static String addDuplicateFileNameNum(String file) throws Exception {
		String duplicateNumRegex = "(?<=\\()\\d+(?=\\)(\\.|$))";
		String fileName = getFileName(file, false);
		String duplicateNum = ComRegexUtil.getMatchedString(fileName, duplicateNumRegex);
		if(ComStrUtil.isBlank(duplicateNum)) {	//若还没有重复数
			fileName += "(2)";	//直接加上重复数
		} else { //若已经有重复数了
			//直接给重复数+1
			fileName = ComRegexUtil.replaceByRegex(fileName, duplicateNumRegex, Integer.parseInt(duplicateNum) + 1 + "");
		}
		return ComRegexUtil.replaceByRegex(file, getRegexfilenamenoex(), fileName);
	}
	
	
	/**
	 * 得到最后一个斜杠(整的或反的)的index
	 */
	private static Integer getLastSlashIndex(String str) {
		int lastForwardSlashIndex = str.lastIndexOf("/");
		int lastbackSlashIndex = str.lastIndexOf("\\");
		return lastForwardSlashIndex > lastbackSlashIndex ? lastForwardSlashIndex : lastbackSlashIndex;
	}
	
	/**
	 * 给文件后缀名加上点. 如 "avi => ".avi".   ".avi" => ".avi".  "...avi" => ".avi"
	 * @param fileNameExtension
	 * @return 加点后的文件后缀名. 如 ".avi"
	 * @throws Exception
	 */
	public static String addDot2FileNameExtension(String fileNameExtension) throws Exception {
		fileNameExtension = fileNameExtension.trim();
		String returnStr = ComRegexUtil.replaceByRegex(fileNameExtension, "\\.*", ".");
		if(".".equals(returnStr)) throw new Exception("后缀名:" + fileNameExtension + " 格式不对");
		return returnStr;
	}
	
	
	public static String readFile2String(String file) throws Exception {
		return readFile2String(convert2File(file));
	}
	
	public static String readFile2String(String file, String encoding) throws Exception {
		return readFile2String(convert2File(file));
	}
	
    /**
	 * FileInputStream fis = new FileInputStream("C:/Users/qingralf/Desktop/test.txt");
        byte[] buffer = new byte[1024];
        int pos = 0;
        StringBuffer sb = new StringBuffer();
        while((pos = fis.read(buffer)) != -1){
            sb.append(new String(buffer,0,pos));
        }
        String tar = sb.toString();
        fis.close();
//看你的那个象日志,如果是文件，直接写文件地址，如果是字符串tar就是目标串
Matcher matcher = Pattern.compile("(?is)\\]([^\\]]+)count").matcher(tar);
        while(matcher.find()){
            System.out.println(matcher.group(1));
        }
	 * 把文件内容读成字符串
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String readFile2String(File file) throws Exception {
		return readStream2String(new FileInputStream(file));
	}
	
	public static String readFile2String(File file, String encoding) throws Exception {
		return readStream2String(new FileInputStream(file), encoding);
	}
	
	/**
	 * This only apply for text stream.
	 * @param is 
	 * @return
	 * @throws Exception
	 */
	public static String readStream2String(InputStream is) throws Exception {
		return readStream2String(is, UTF8);
	}
	
	/**
	 * This only apply for text stream.
	 * @param is 
	 * @return
	 * @throws Exception
	 */
	public static String readStream2String(InputStream is, String encode) throws Exception {
		StringBuilder resStr = new StringBuilder();
		String readLine = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(is, encode);
			br = new BufferedReader(isr);
			while((readLine = br.readLine()) != null) {
				resStr.append(readLine).append("\n");
			}
		} finally {
			if(br != null) {
				br.close();
				br = null;
			}
			if(isr != null) {
				isr.close();
				isr = null;
			}
		}
		return resStr.toString();
	}
	
	public static void appendString2File(String str, String file, String encode) throws Exception {
		writeString2File(str, file, encode, true);
	}
	
	public static void appendString2File(String str, File file, String encode) throws Exception {
		writeString2File(str, file, encode, true);
	}
	
	public static void writeString2File(String str, String file, Boolean append) throws Exception {
		writeString2File(str, file, UTF8, false);
	}
	
	public static void writeString2File(String str, File file, String encode) throws Exception {
		writeString2File(str, file, encode, false);
	}
	
	public static void writeString2File(String str, String file, String encode) throws Exception {
		writeString2File(str, convert2File(file), encode, false);
	}
	
	public static void writeString2File(String str, String file, String encode, Boolean append) throws Exception {
		writeString2File(str, convert2File(file), encode, append);
	}
	
	public static void writeString2File(String str, File file, String encode, Boolean append) throws IOException {
        PrintWriter out = null;
        try {
        	if(!file.exists()) {
            	createFile(file);
            }
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), encode)));
			out.write(str);
			out.flush();
		} catch(IOException e) {
			ComLogUtil.error("write file:" + file + " failed");
			throw e;
		} finally {
			if(out != null) {
				out.close();
				out = null;
			}
		}
//		
//		FileWriter fw = null; 
//		try {
//			fw = new FileWriter(file, append);
//			fw.write(str);
//			fw.flush();
//			fw.close();
//		} finally{
//			if(fw != null) {
//				fw.close();
//				fw = null;
//			}
//		}
	}
	
	/**
	 * 把流写到文件里去
	 * @param is
	 * @param file
	 * @param encode
	 * @return
	 * @throws Exception
	 */
	public static String writeStream2File(InputStream is, String file, String encode, Boolean append) throws Exception {
		String readStr = readStream2String(is);
		writeString2File(readStr, file, append);
		return readStr; 
	}
	
	public static FileInfo getFileInfo(String file) {
		return new FileInfo(getFileDiretory(file), getFileName(file, false), getFileExtension(file, true));
	}
	
	public static FileInfo getFileInfo(File file) {
		return new FileInfo(getFileDiretory(file), getFileName(file, false), getFileExtension(file, true));
	}
	
	public static String getFileDiretory(String file) {
		return getFileDiretory(convert2File(file));
	}
	
	/**
	 * get the directory of a file
	 * @param file
	 * @return e.g: F:/dir/filename.txt => F:/dir/
	 * 				/dir/filename.txt => /dir/
	 * 				F:aaa.txt => F:/
	 */
	public static String getFileDiretory(File file) {
		String path = file.getPath();
		int lastIndexOf = path.lastIndexOf(SEPARATOR);
		if(lastIndexOf > 0) {//with "/"
			return path.substring(0, lastIndexOf + 1);
		} else {//no "/" then should be like F:aaa.txt
			return ComRegexUtil.getMatchedString(path, "^[a-zA-Z]{1}:") + SEPARATOR;
		}
	}
	
	public static void createFile(String file) throws IOException {
		createFile(convert2File(file));
	}
	
	/**
	 * make sure it's path, and then create the file
	 * @param file the file to be created
	 * @throws IOException 
	 */
	public static void createFile(File file) throws IOException {
		String diretory = getFileDiretory(file);
		File dir = new File(diretory);
//		if(dir.isDirectory()) {
			if(dir.exists() || dir.mkdirs()) {
				file.createNewFile();
			} else {
				throw new IOException("create dir failed file:" + file + " dir:" + dir);
			}
//		} else {
//			throw new IOException("dir invalid file:" + file + " diretory:" + diretory);
//		}
		
	}
	
	public static void main(String[] args) throws IOException {
//		System.out.println(new FileName("E:\\download\\youtube3\\PL5N5Hv3klahFR1o5AKSbh3RU6wMCajj7s", "fileName", "txt").append("_firstAfter").preAppend("firstPre_").preAppend("secondPre_"));
//		System.out.println(ComFileUtil.getFileInfo("E:\\download\\youtube3\\PL5N5Hv3klahFR1o5AKSbh3RU6wMCajj7s\\兄弟本色 頑童MJ116 6 壞鄰居(4K 2160p)@2015高雄啤酒節[無限HD].webm"));
//		String url = "https://www.youtube.com/watch?v=eJKzS55doxM";
//		String outputFileName = "F:\\download\\bbb\\aa.txt";
//		String outputFileName = "E:\\download\\youtube3\\PL5N5Hv3klahFR1o5AKSbh3RU6wMCajj7s\\兄弟本色 頑童MJ116 6 壞鄰居(4K 2160p)@2015高雄啤酒節[無限HD].webm";
//		
//		System.out.println(new FileName(outputFileName).append("_firstAfter").preAppend("firstPre_").preAppend("secondPre_"));
//		System.out.println(getFileDiretory(outputFileName));
//		System.out.println(getFileDiretory("c:/Users/Administrator/aa.txt"));
//		
//		System.out.println(getFileName(outputFileName, false));
//		System.out.println(new StringBuilder(outputFileName.substring(0, outputFileName.lastIndexOf("."))).append("_").append(ComRegexUtil.getMatchedString(url, "(?<=v=)[^&]{4,20}")).append(ComFileUtil.getFileExtension(outputFileName, true)));
		
    	try {
//    		System.out.println(readFile2String("f:\\tmp\\Rugal2nd_-2_AI.cns.bak.txt"));
    		System.out.println(readStream2String(new FileInputStream("f:\\tmp\\Rugal2nd_-2_AI.txt"), UTF8));
    		System.out.println(readStream2String(new FileInputStream("f:\\tmp\\Rugal2nd_-2_AI.cns.bak.txt"), "Shift_JIS"));
//    		FileReader fileReader = new FileReader("f:\\tmp\\Rugal2nd_-2_AI.txt");
    		SortedMap<String,Charset> availableCharsets = java.nio.charset.Charset.availableCharsets();
    		System.out.println(availableCharsets);
//    		File file = new File("F:\\download\\bbb\\aa.txt");
//    		System.out.println(getFileName(file, true));
//    		createFile(file);
//    		System.out.println(addDuplicateFileNameNum("e:/nn/ddd.jpg"));
//    		System.out.println(addDuplicateFileNameNum("e:/nn/ddd"));
//    		System.out.println(addDuplicateFileNameNum("e:/nn/ddd(3).jpg"));
//    		System.out.println(addDuplicateFileNameNum("e:/nn/ddd(4)"));
//    		System.out.println(getFileName("/aaa/bbb/ccc/dddjpg.jgp?bb2", true));
    		//System.out.println(getNoFilePathFrmPath("/var/ddd"));
    		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	System.out.println(new File("D:\\DDD\\OrderManage.java").getPath());
//    	System.out.println(new File("D:\\DDD\\OrderManage.java").getCanonicalPath());
//    	System.out.println(new File("D:\\DDD\\OrderManage.java").getName());
//    	System.out.println(new File("D:\\DDD\\").getParent());
//    	System.out.println(new File("D:\\DDD\\OrderManage.java").getAbsoluteFile());
//    	System.out.println(new File("D:\\DDD\\OrderManage.java").getParentFile());
    	
	}
	
}
