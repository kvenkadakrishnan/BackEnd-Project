package com.venkat.codeexecutor.worker.codeexecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.venkat.codeexecutor.webserver.constants.StatusMessage;
import com.venkat.codeexecutor.webserver.storageservice.IFileStorageService;
import com.venkat.codeexecutor.worker.dto.ExecutionResult;
import com.venkat.codeexecutor.worker.utils.FileUtilities;

@Component
public class CPPExecutor extends CodeExecuter{

	private static List<String> compileCommands = Arrays.asList("g++", "solution.cpp", "-o", "solution");
	
	private IFileStorageService fileStorageService;
	
	@Value("${files.problemsdata}")
	private String problemsData;
	
	@Value("${files.submission}")
	private String submissions;
	
	@Value("${files.result}")
	private String result;
	
	public CPPExecutor(IFileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}
	
	@Override
	public void SetExecutionFiles(String workSpace,String testCase, String testResult, String driverCode, String userCode) throws Exception {
		FileUtils.cleanDirectory(new File(workSpace));
		String testCaseCopy = workSpace+"\\testcase.txt";
		String testResultCopy = workSpace+"\\testresult.txt";
		String codeFilePath = workSpace+"\\solution.cpp";
		String resultFilePath = workSpace+"\\result.txt";
		
		try {
			FileUtilities.Copy(this.fileStorageService.GetFileContent(problemsData, testCase), testCaseCopy);
			FileUtilities.Copy(this.fileStorageService.GetFileContent(problemsData, testResult), testResultCopy);
			File resultFile = new File(resultFilePath);
			resultFile.createNewFile();
			String header = Files.readString(Path.of("D:\\S3 CodeExecutor\\ProblemsData\\header.txt"));
		    String mainFunction = this.fileStorageService.GetTextContent(problemsData, driverCode);
		    String solutionFunction = this.fileStorageService.GetTextContent(submissions, userCode);
			File codeFile = new File(codeFilePath);
	        PrintWriter printWriter = new PrintWriter(codeFile);
	        printWriter.write(header+solutionFunction+mainFunction);
	        printWriter.flush();
	        printWriter.close();
			return;
		}catch(Exception exception) {
			throw new Exception("Initializing code executor exception");
		}
	}

	@Override
	public ExecutionResult CompileAndRun(String workSpace) {
		ExecutionResult executionResult = new ExecutionResult();
		File resultFile = new File(workSpace+"\\result.txt");
		executionResult.ResultFile = resultFile;
		try {
			 // Create process builder 
	        ProcessBuilder processBuilder = new ProcessBuilder(compileCommands).directory(new File(workSpace));
	        Process compileProcess = processBuilder.start();
			String compilerWarning = read(compileProcess.getErrorStream());
			if(compilerWarning.length() > 0) {
				executionResult.Status = StatusMessage.CompileError;
				return executionResult;
			}
			long starttime = System.currentTimeMillis();
			processBuilder = new ProcessBuilder(workSpace+"\\solution.exe").directory(new File(workSpace))
					.redirectErrorStream(true);

	        Process runningProcess = processBuilder.start();
	        
	    	String compilerWarning2 = read(runningProcess.getErrorStream());
	    	String codeOutput = read(runningProcess.getInputStream());
	        if(runningProcess.waitFor() == 0 && compilerWarning2.length() <= 0) {
	        	long endTime = System.currentTimeMillis();
	        	executionResult.Status = StatusMessage.Accepted;
	        	executionResult.Runtime = (int) (endTime - starttime); 
				return executionResult;
	        }else {
	        	executionResult.Status = StatusMessage.RunTimeError;
				return executionResult;
	        }	
		}catch (Exception e) {
			executionResult.Status = StatusMessage.InternalError;
			return executionResult;
		}
	}
	
	private static String read(InputStream inputStream) throws Exception {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while(reader.ready() && (line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			return result;
		}catch(Exception e) {
			throw e;
		}
	}

}
