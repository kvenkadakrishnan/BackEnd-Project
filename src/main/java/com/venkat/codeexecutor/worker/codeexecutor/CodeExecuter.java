package com.venkat.codeexecutor.worker.codeexecutor;

import org.springframework.stereotype.Component;

import com.venkat.codeexecutor.worker.dto.ExecutionResult;

@Component
public abstract class CodeExecuter {
		
	/**
	 * Creates the necessary files to compile and execute the code in the workspace.
	 * @param workSpace Path of the workspace.
	 * @param testCase Path of the test case.
	 * @param testResult Path of the test cases results
	 * @param driverCode Path of the driver code.
	 * @param userCode Path of the user code.
	 * @throws Exception
	 */
	public abstract void SetExecutionFiles(String workSpace,String testCase,String testResult,String driverCode, String userCode ) throws Exception;
	
	/**
	 * Compile and execute the user code against the hidden test case.
	 * @param workSpace Path of the work space.
	 * @return The execution result of the user code.
	 */
	public abstract ExecutionResult CompileAndRun(String workSpace);
}
