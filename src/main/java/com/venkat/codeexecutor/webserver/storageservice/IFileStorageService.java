package com.venkat.codeexecutor.webserver.storageservice;

public interface IFileStorageService {
	
	/**
	 * Gets the file content as string.
	 * @param folder The folder that contains file.
	 * @param fileName Name of the file
	 * @return The file content in string.
	 * @throws Exception
	 */
	public String GetTextContent(String folder, String fileName) throws Exception;
	
	/**
	 * Gets the file content as byte array.
	 * @param folder The folder that contains file.
	 * @param fileName Name of the file
	 * @return The file content in byte array format.
	 * @throws Exception
	 */
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
	
	/**
	 * Deletes file present in the specified folder.
	 * @param folder The folder that contains the file that needs to be deleted.
	 * @param fileName The name of the file that needs to be deleted.
	 */
	public void DeleteFile(String folder, String fileName);
}
