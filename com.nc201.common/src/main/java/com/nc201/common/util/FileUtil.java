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

/**
 * IO操作工具类
 * 
 * @author zhuzf
 */
public class FileUtil {

	public static void input2output(final InputStream in, final OutputStream out) throws Exception {
		try {
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
		} catch (Exception e) {
			throw e;
		}finally{
			if(in!=null){
				in.close();
			}
			if(out!=null){
				out.close();
			}			
		}
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
	}
	/**
	 * 输出文件内容到指定文件中
	 * 
	 * @param file
	 * @param content
	 * @throws Exception
	 */
	public static void outputFile(File file, String content,String encoding) throws Exception {
		OutputStream fileOut=null;
		Writer fileWriter=null;
		try {
			fileOut = new FileOutputStream(file);
			fileWriter = new OutputStreamWriter(fileOut, encoding);
			fileWriter.write(content);
		} catch (Exception e) {
			throw e;
		}finally{
			if(fileWriter!=null){
				fileWriter.close();
			}
			if(fileOut!=null){
				fileOut.close();
			}					
		}
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
		Reader fileReader = null;
		StringBuffer txtContent = new StringBuffer();
		try {
			fileReader = new InputStreamReader(fileIn, encoding);
			int j = -1;
			while ((j = fileReader.read()) != -1) {
				txtContent.append((char) j);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if(fileIn!=null){
				fileIn.close();
			}
			if(fileReader!=null){
				fileReader.close();
			}	
		}

		return txtContent.toString();
	}		

}
