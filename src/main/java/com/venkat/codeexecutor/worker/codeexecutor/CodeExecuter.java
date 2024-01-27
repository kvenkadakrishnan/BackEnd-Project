package com.venkat.codeexecutor.worker.codeexecutor;

import java.io.File;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;

import com.venkat.codeexecutor.worker.dto.ExecutionResult;

@Component
public abstract class CodeExecuter {
	
	protected String workSpace;
	
	public void setWorkspace(String workSpace) {
		this.workSpace = workSpace;
	}
		
	public abstract void SetExecutionFiles(String testCase,String testResult,String driverCode, String userCode ) throws Exception;
	
	public abstract ExecutionResult CompileAndRun();
	
	public void ClearWorkSpace() {
		File directory = new File(this.workSpace); 
		try {
			FileUtils.deleteDirectory(directory);
		} catch (Exception e) {
			// No implementation
		}
	}
}
