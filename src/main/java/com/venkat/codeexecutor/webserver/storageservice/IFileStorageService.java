package com.venkat.codeexecutor.webserver.storageservice;

public interface IFileStorageService {
	
	public String GetTextContent(String folder, String fileName) throws Exception;
	
	public byte[] GetFileContent(String folder, String fileName) throws Exception;
	
	/**
	 * Helps to save the text in a text document.
	 * @param text The text content.
	 * @param folder Name of the folder where the text should be saved.
	 * @return Auto generated name of the file.
	 * @throws Exception 
	 */
	public String SaveText(String text, String folder) throws Exception;
	
	/**
	 * Helps to save the file content array as a text document
	 * @param fileContent Content of the file in byte array format.
	 * @param folder Name of the folder where the text should be saved.
	 * @return Auto generated name of the file.
	 * @throws Exception 
	 */
	public String SaveText(byte[] fileContent,String folder) throws Exception;
	
	public void DeleteFile(String folder, String fileName);
}
