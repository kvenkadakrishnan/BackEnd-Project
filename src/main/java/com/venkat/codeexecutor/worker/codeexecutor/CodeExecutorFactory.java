package com.venkat.codeexecutor.worker.codeexecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeExecutorFactory {
	@Autowired
	private CPPExecutor cppExecutor;
	public CodeExecuter getCodeExecutor(String type) throws Exception {
		if(type.equalsIgnoreCase("CPP")) {
			return cppExecutor;
		}
		throw new Exception("No code executor found for the tye "+type);
	}
}
