package com.venkat.codengine.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.venkat.codengine.constants.FilePath;

public class FileStorage {
	
	
	/**
	 * Helps to create the file in the submissions storage.
	 * @param code Contains user submitted solution.
	 * @return The file path of the submitted solution.
	 * @throws Exception 
	 * @throws IOException Exception in creating file.
	 */
	public static String SaveSubmittedCode(String code) throws Exception  {
		
		String fileName = String.valueOf(System.currentTimeMillis());
		String filePath = String.format("%s%s.txt", FilePath.Submissions,fileName);
		File codeFile = new File(filePath);
		
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
}
