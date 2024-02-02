package com.venkat.codeexecutor.webserver.storageservice;

import java.io.ByteArrayInputStream;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3Service implements IFileStorageService{

	private AmazonS3 s3;
	
	@Value("${s3.name}")
	private String bucketName;
	
	public S3Service(AmazonS3 amazonS3) {
		this.s3 = amazonS3;
	}
	@Override
	public String GetTextContent(String folder, String fileName) throws Exception{
		try {
			String fileText  =  s3.getObjectAsString(bucketName, folder+"/"+fileName);
	        return fileText;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public byte[] GetFileContent(String folder, String fileName) throws Exception {
	
		GetObjectRequest request = new GetObjectRequest(bucketName, folder+"/"+fileName);
        S3Object s3Object  =  s3.getObject(request);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        ObjectMetadata objectMetadata =  s3Object.getObjectMetadata();
        byte[] fileContent = new byte[(int) objectMetadata.getContentLength()];
        inputStream.read(fileContent);
        inputStream.close();
        return fileContent;
		
	}

	@Override
	public String SaveText(String text, String folder) throws Exception{
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
		    String fqfn = folder+"/"+fileName;
		    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(fileContent);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("plain/text");
			metadata.setContentLength(fileContent.length);
			PutObjectRequest request = new PutObjectRequest(bucketName, fqfn, arrayInputStream, metadata);
            s3.putObject(request);
			return fileName;
		}catch(Exception exception) {
			throw exception;
		}
	}

	@Override
	public void DeleteFile(String folder, String fileName) {
		// TODO Auto-generated method stub
		try {
			DeleteObjectRequest request = new DeleteObjectRequest(bucketName, folder+"/"+fileName);
			s3.deleteObject(request);
		}catch(Exception e) {
			// No implementation required
		}
	}

}
