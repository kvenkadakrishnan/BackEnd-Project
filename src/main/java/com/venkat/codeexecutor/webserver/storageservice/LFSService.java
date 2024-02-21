package com.venkat.codeexecutor.webserver.storageservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Contains local file system service that helps to get and save files.
 */
public class LFSService implements IFileStorageService {

	@Value("${disk.name}")
	private String disk;
	
	@Override
	public String GetTextContent(String folder, String fileName) throws Exception {
		return Files.readString(Path.of(disk+"\\"+ folder+"\\"+fileName));
	}

	@Override
	public byte[] GetFileContent(String folder, String fileName) throws Exception {
		File file = new File(disk+"\\"+ folder+"\\"+fileName);
		byte[] fileContent = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(fileContent);
		fis.close();
		return fileContent;
	}

	@Override
	public String SaveText(String text, String folder) throws Exception {
		return SaveText(text.getBytes(),folder);
	}

	@Override
	public String SaveText(byte[] fileContent, String folder) throws Exception {
		try {
		    String randomText = new Random().ints(97, 123)
		    	      .limit(3)
		    	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		    	      .toString();

		    String fileName = randomText+String.valueOf(System.currentTimeMillis())+".txt";
		    String fqfn = disk+"\\"+ folder+"\\"+fileName;
		    File destFile = new File(fqfn);
		    destFile.createNewFile();
		    FileOutputStream fos = new FileOutputStream(destFile);
		    fos.write(fileContent);
		    fos.close();
			return fileName;
		}catch(Exception exception) {
			throw exception;
		}
	}

	@Override
	public void DeleteFile(String folder, String fileName) {
		try {
			File deleteFile = new File(disk+"\\"+ folder+"\\"+fileName);
			deleteFile.deleteOnExit();
		}
		catch(Exception ex){
			// No implementation required
		}
	}

}
