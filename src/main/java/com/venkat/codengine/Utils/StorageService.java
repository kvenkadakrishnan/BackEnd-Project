package com.venkat.codengine.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import com.venkat.codengine.constants.FilePath;

public class StorageService {
	
	/**
	 * Helps to create the file in the submissions storage.
	 * @param code Contains user submitted solution.
	 * @return The file path of the submitted solution.
	 * @throws Exception 
	 * @throws IOException Exception in creating file.
	 */
	public static String SaveSubmittedCode(String code) throws Exception  {
		long currentTime = System.currentTimeMillis();
		File codeFile;
		while(true) {
			String fileName = String.valueOf(currentTime);
			String filePath = String.format("%s%s.txt", FilePath.Submissions,fileName);
			codeFile = new File(filePath);
			if(!codeFile.exists()) break;
			currentTime++;
		}
		
		// Writes the user submitted code into the file.
		PrintWriter pw = new PrintWriter(codeFile);
        pw.write(code);
        pw.flush();
        pw.close();
		
		return codeFile.getCanonicalPath();
	}
	
	public static void DeleteFile(String filePath) {
		File file = new File(filePath);
		file.deleteOnExit();
	}
	
	public static String SaveResult(File source) throws Exception{
		String resultFile = FilePath.Results+source.getName();
		Files.copy(source.toPath(),Path.of(resultFile));
		return resultFile;
	}
	
	public static String CreateNewLocalWorkSpace(String directoryName) throws Exception{
		String workSpace = FilePath.WorkSpace + directoryName;
		File directory = new File(workSpace); 
		if(directory.exists() || directory.mkdir() ) {
			return workSpace ;	
		}else {
			throw new Exception("Error in creating workspace");
		}
	}
	
	public static void ClearDirectory12(String directory) throws Exception {
		File fileDirectory = new File(directory); 
		try {
			FileUtils.cleanDirectory(fileDirectory);
		} catch (Exception e) {
			throw e;
		}
	}
}
