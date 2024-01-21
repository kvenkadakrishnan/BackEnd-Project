package com.venkat.codengine.worker.codeexecutor;

import com.venkat.codengine.worker.CodeExecuter;

public class CodeExecutorFactory {
	public static CodeExecuter getCodeExecutor(String type) throws Exception {
		if(type.equalsIgnoreCase("CPP")) {
			return new CPPExecutor();
		}
		throw new Exception("No code executor found for the tye "+type);
	}
}
