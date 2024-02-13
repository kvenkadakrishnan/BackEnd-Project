package com.venkat.codeexecutor.worker.codeexecutor;

import org.springframework.stereotype.Component;

import com.venkat.codeexecutor.worker.dto.ExecutionResult;

@Component
public abstract class CodeExecuter {
		
	public abstract void SetExecutionFiles(String workSpace,String testCase,String testResult,String driverCode, String userCode ) throws Exception;
	
	public abstract ExecutionResult CompileAndRun(String workSpace);
}
