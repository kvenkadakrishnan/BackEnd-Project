package com.venkat.codeexecutor.worker.codeexecutor;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
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
public class JavaExecutor extends CodeExecuter{

	private static List<String> compileCommands = Arrays.asList("javac", "Drivercode.java");
	
	private static List<String> runCommands = Arrays.asList("java", "Drivercode");


	private IFileStorageService fileStorageService;
	
	@Value("${files.problemsdata}")
	private String problemsData;
	
	@Value("${files.submission}")
	private String submissions;
	
	@Value("${files.result}")
	private String result;
	
	public JavaExecutor(IFileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}
	
	@Override
	public void SetExecutionFiles(String workSpace, String testCase, String testResult, String driverCode,
			String userCode) throws Exception {
		System.out.println("Creating Execution Files..");
		FileUtils.cleanDirectory(new File(workSpace));
		String testCaseCopy = workSpace+"\\testcase.txt";
		String testResultCopy = workSpace+"\\testresult.txt";
		String codeFilePath = workSpace+"\\Drivercode.java";
		String resultFilePath = workSpace+"\\result.txt";
		String stderr = workSpace+"\\stderr.txt";
		String stdout = workSpace+"\\stdout.txt";
		try {
			FileUtilities.Copy(this.fileStorageService.GetFileContent(problemsData, testCase), testCaseCopy);
			FileUtilities.Copy(this.fileStorageService.GetFileContent(problemsData, testResult), testResultCopy);
			File resultFile = new File(resultFilePath);
			resultFile.createNewFile();
			new File(stderr).createNewFile();
			new File(stdout).createNewFile();
			String header = this.fileStorageService.GetTextContent(problemsData, "javaheader.txt");
		    String driverClass = this.fileStorageService.GetTextContent(problemsData, driverCode);
		    String solutionClass = this.fileStorageService.GetTextContent(submissions, userCode);
			File codeFile = new File(codeFilePath);
	        PrintWriter printWriter = new PrintWriter(codeFile);
	        printWriter.write(header+driverClass+solutionClass);
	        printWriter.flush();
	        printWriter.close();
			return;
		}catch(Exception exception) {
			System.out.println("Error in creating execution files..");
			throw new Exception("Initializing code executor exception");
		}
		
	}

	@Override
	public ExecutionResult CompileAndRun(String workSpace) {
		System.out.println("Started compile and run process");
		ExecutionResult executionResult = new ExecutionResult();
		File resultFile = new File(workSpace+"\\result.txt");
		File stderr = new File(workSpace+"\\stderr.txt");
		File stdout = new File(workSpace+"\\stdout.txt");
		executionResult.ResultFile = resultFile;
		try {
			 // Create process builder 
	        ProcessBuilder processBuilder = new ProcessBuilder(compileCommands).directory(new File(workSpace))
	        		.redirectError(stderr).redirectOutput(stdout);;
	        Process compileProcess = processBuilder.start();
	        int compilationResult = compileProcess.waitFor();
	        String compileError = Files.readString(stderr.toPath());
	        if(compilationResult != 0 || compileError.length() > 0) {
				executionResult.Status = StatusMessage.CompileError;
				PrintWriter printWriter = new PrintWriter(resultFile);
		        printWriter.write(StatusMessage.CompileErrorResult+StatusMessage.ErrorMessage+compileError);
		        printWriter.flush();
		        printWriter.close();
				return executionResult;
			}
			long starttime = System.currentTimeMillis();
			processBuilder = new ProcessBuilder(runCommands).directory(new File(workSpace))
					.redirectError(stderr).redirectOutput(stdout);

	        Process runningProcess = processBuilder.start();
	        int execResult = runningProcess.waitFor();
	    	String runtimeError = Files.readString(stderr.toPath());
	        if(execResult == 0 && runtimeError.length() == 0) {
	        	long endTime = System.currentTimeMillis();
	        	executionResult.Status = StatusMessage.Success;
	        	executionResult.Runtime = (int) (endTime - starttime); 
				return executionResult;
	        }else {
	        	executionResult.Status = StatusMessage.RunTimeError;
	        	PrintWriter printWriter = new PrintWriter(resultFile);
		        printWriter.write(StatusMessage.RuntimeErrorResult+StatusMessage.ErrorMessage+runtimeError);
		        printWriter.flush();
		        printWriter.close();
				return executionResult;
	        }	
		}catch (Exception e) {
			System.out.println("Error in Java executor"+e.getMessage());

			executionResult.Status = StatusMessage.InternalError ;
			PrintWriter printWriter;
			try {
				printWriter = new PrintWriter(resultFile);
				printWriter.write(StatusMessage.InternalErrorResult);
		        printWriter.flush();
		        printWriter.close();
			} catch (Exception e1) {
				// Not Required
			}
	        
			return executionResult;
		}
	}

}
