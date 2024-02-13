package com.venkat.codeexecutor.worker.utils;

import java.io.File;
import java.io.FileOutputStream;

import com.venkat.codeexecutor.webserver.constants.FilePath;

public class FileUtilities {
	public static void Copy(byte[] fileContent, String targetPath) throws Exception {
		File targetFile = new File(targetPath);
		targetFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(targetFile);
		fos.write(fileContent);
		fos.close();
	}
	public static String CreateNewLocalWorkSpace(String directoryName) throws Exception{
		String workSpace = FilePath.WorkSpace + directoryName;
		File directory = new File(workSpace); 
		if(directory.exists() || directory.mkdir() ) {
			return workSpace ;	
		}else {
			System.out.println("Error in file utils");
			throw new Exception("Error in creating workspace");
		}
	}
}
