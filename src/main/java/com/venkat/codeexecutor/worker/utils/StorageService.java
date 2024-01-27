package com.venkat.codeexecutor.worker.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import com.venkat.codeexecutor.webserver.constants.FilePath;

public class StorageService {
	
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
}
