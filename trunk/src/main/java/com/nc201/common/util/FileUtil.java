package com.nc201.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 * IO操作工具类
 * 
 * @author zhuzf
 * @date 2005-11-23
 */
public class FileUtil {

	public static void input2output(final InputStream in, final OutputStream out) throws Exception {
		in.available();
		byte[] buffer = new byte[1024];
		while (true) {
			int r = in.read(buffer);
			if (r == -1) {
				break;
			}
			out.write(buffer, 0, r);
		} // End while
		buffer = null;
		in.close();
		out.close();
	}

	public static void input2file(final InputStream in, final File file) throws Exception {
		input2output(in, new FileOutputStream(file));
	}

	public static void input2file(final InputStream in, final String fileName) throws Exception {
		input2file(in, new File(fileName));
	}

	public static void deleteFile(String filePath) throws Exception {
		File file = new File(filePath);
		if (file.exists() && !file.isDirectory()) {
			file.delete();
		}
	}

	/**
	 * 创建文件的路径，包括父路径
	 * 
	 * @param filePath
	 *            String
	 */
	public static void creatFilePath(String filePath) {
		java.io.File path = new java.io.File(filePath);
		path.mkdirs();
	}

	/**
	 * 读取文件内容
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String readFileContent(String filePath) throws Exception {
		File file = new File(filePath);
		return readFileContent(file);
	}

	/**
	 * 输出文件内容到指定文件中
	 * 
	 * @param file
	 * @param content
	 * @throws Exception
	 */
	public static void outputFile(File file, String content) throws Exception {
		outputFile(file,content,"GBK");
		// return txtContent.toString();
	}
	/**
	 * 输出文件内容到指定文件中
	 * 
	 * @param file
	 * @param content
	 * @throws Exception
	 */
	public static void outputFile(File file, String content,String encoding) throws Exception {
		OutputStream fileOut = new FileOutputStream(file);
		Writer fileWriter = new OutputStreamWriter(fileOut, encoding);
		fileWriter.write(content);
		fileWriter.close();
		fileOut.close();
		// return txtContent.toString();
	}
	/**
	 * 读取文件内容
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String readFileContent(File file) throws Exception {
		return readFileContent(file,"GBK");
	}
	/**
	 * 读取文件内容
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String readFileContent(File file,String encoding) throws Exception {
		return readFileContent(new FileInputStream(file), encoding);
	}	
	
	/**
	 * 读取文件内容
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String readFileContent(InputStream fileIn,String encoding) throws Exception {
		//InputStream fileIn = new FileInputStream(file);
		Reader fileReader = new InputStreamReader(fileIn, encoding);
		int j = -1;
		StringBuffer txtContent = new StringBuffer();
		while ((j = fileReader.read()) != -1) {
			txtContent.append((char) j);
		}
		fileIn.close();
		return txtContent.toString();
	}		
	public static void main(String[] args) throws Exception {
		//System.out.println(readFileContent(FileUtil.class.getResourceAsStream("./user.json"),"GBK"));
		Properties prop=new Properties();
		prop.load(FileUtil.class.getResourceAsStream("./test.txt"));
		System.out.println(prop.get("sql"));
	}
}
