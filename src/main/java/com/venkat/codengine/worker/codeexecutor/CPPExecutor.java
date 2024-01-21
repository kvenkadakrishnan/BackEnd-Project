package com.venkat.codengine.worker.codeexecutor;

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

import com.venkat.codengine.constants.StatusMessage;
import com.venkat.codengine.worker.CodeExecuter;
import com.venkat.codengine.worker.dto.ExecutionResult;

public class CPPExecutor extends CodeExecuter{

	private static List<String> compileCommands = Arrays.asList("g++", "solution.cpp", "-o", "solution");
	@Override
	public void SetExecutionFiles(String testCase, String testResult, String driverCode, String userCode) throws Exception {
		FileUtils.cleanDirectory(new File(this.workSpace));
		String testCaseCopy = this.workSpace+"\\testcase.txt";
		String testResultCopy = this.workSpace+"\\testresult.txt";
		String solutionFilePath = this.workSpace+"\\solution.cpp";
		String resultFilePath = this.workSpace+"\\result.txt";
		
		try {
			Files.copy(Path.of(testCase), Path.of(testCaseCopy));
			Files.copy(Path.of(testResult), Path.of(testResultCopy));
			File resultFile = new File(resultFilePath);
			resultFile.createNewFile();
			String header = Files.readString(Path.of("D:\\LeetCodeFiles\\HeaderFiles\\header.txt"));
		    String mainFunction = Files.readString(Path.of(driverCode));
		    String solutionFunction = Files.readString(Path.of(userCode));
			File solutionFile = new File(solutionFilePath);
	        PrintWriter printWriter = new PrintWriter(solutionFile);
	        printWriter.write(header+solutionFunction+mainFunction);
	        printWriter.flush();
	        printWriter.close();
			return;
		}catch(Exception exception) {
			throw new Exception("Initializing code executor exception");
		}
	}

	@Override
	public ExecutionResult CompileAndRun() {
		ExecutionResult executionResult = new ExecutionResult();
		File resultFile = new File(this.workSpace+"\\result.txt");
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
			processBuilder = new ProcessBuilder(this.workSpace+"\\solution.exe").directory(new File(this.workSpace))
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

}
