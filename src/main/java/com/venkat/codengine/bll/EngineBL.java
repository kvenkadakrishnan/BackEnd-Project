package com.venkat.codengine.bll;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import com.venkat.codengine.constants.StatusMessage;
import com.venkat.codengine.models.Submission;
import com.venkat.codengine.models.SubmissionResult;

@Service
public class EngineBL {
	public SubmissionResult RunCode(Submission submission) throws Exception {
		String fileName = String.valueOf(System.currentTimeMillis());
		
		String codeFilePath = String.format("D:\\PlayGround\\%s.%s",fileName,submission.languageExtension);
		
		String driverCode = String.format("D:\\LeetCodeFiles\\%s\\tester.cpp",submission.problemId);
		String testCaseFilePath = String.format("D:\\LeetCodeFiles\\%s\\testcase.txt",submission.problemId);
		String testResultFilePath = String.format("D:\\LeetCodeFiles\\%s\\testresult.txt",submission.problemId);
		String headerFile = "D:\\LeetCodeFiles\\HeaderFiles\\header.txt";
		
		String playGround = "D:\\PlayGround";
		
		
		File codeFile = new File(codeFilePath);
		String testCase = playGround+"\\testcase.txt";
		String testResult = playGround+"\\testresult.txt";
		try {
			codeFile.createNewFile();
			Files.copy(Path.of(testCaseFilePath), Path.of(testCase));
			Files.copy(Path.of(testResultFilePath), Path.of(testResult));

	        PrintWriter pw = new PrintWriter(codeFile);
	        
	        String header = Files.readString(Path.of(headerFile));
	        String mainFun = Files.readString(Path.of(driverCode));
	        pw.write(header+submission.code+mainFun);
	        pw.flush();
	        pw.close();
	        
	        // Create commands to compile the cpp file
	        List<String> cmds = new ArrayList<String>();
	        cmds.add("g++");
	        cmds.add(fileName+"."+submission.languageExtension);
	        cmds.add("-o");
	        cmds.add(fileName);

	        // Create process builder 
	        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
	        processBuilder.directory(new File(playGround));
	        Process process1 = processBuilder.start();
    		String compilerWarning = read(process1.getErrorStream());
    		if(compilerWarning.length() > 0) {
    			clearDirectory();
	        	SubmissionResult submissionResult = new SubmissionResult();
		        submissionResult.StatusMessage = StatusMessage.CompileError;
		        submissionResult.StatusCode = StatusMessage.CompErrorCode;
		        submissionResult.DisplayMessage = compilerWarning;
				return submissionResult;
    		}
	        
    		long starttime = System.currentTimeMillis();
    		processBuilder = new ProcessBuilder("D:\\PlayGround\\"+fileName+".exe");
	        processBuilder.directory(new File(playGround));
	        processBuilder.redirectErrorStream(true);

	        Process process2 = processBuilder.start();
	        
        	String compilerWarning2 = read(process2.getErrorStream());
        	String codeOutput = read(process2.getInputStream());
	        if(process2.waitFor() == 0 && compilerWarning2.length() <= 0) {
	        	long endTime = System.currentTimeMillis();
	        	clearDirectory();
		        SubmissionResult submissionResult = new SubmissionResult();
		        submissionResult.StatusMessage = StatusMessage.Accepted;
		        submissionResult.StatusCode = StatusMessage.AccCode;
		        submissionResult.CodeOutput = codeOutput;
		        submissionResult.DisplayMessage = "Total Exec time = "+(endTime - starttime)+"ms";
				return submissionResult;
	        }else {
	        	clearDirectory();
	        	SubmissionResult submissionResult = new SubmissionResult();
		        submissionResult.StatusMessage = StatusMessage.RunTimeError;
		        submissionResult.StatusCode = StatusMessage.RtError;
		        submissionResult.DisplayMessage = compilerWarning2;
				return submissionResult;
	        }
	        	
        	
        	 	
        	
        	
	        
	        
	       

		} catch (Exception e) {
			clearDirectory();
			throw e;
		}
	}
		
	private static String read(InputStream inputStream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			return result;
		}catch(Exception e) {
			return e.getMessage();
		}
	}
	private void clearDirectory() throws Exception {
		File directory = new File("D:\\PlayGround\\"); 
		try {
			FileUtils.cleanDirectory(directory);
		} catch (Exception e) {
			throw e;
		}
	}
}
