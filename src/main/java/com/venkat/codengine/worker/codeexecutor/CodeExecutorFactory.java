package com.venkat.codengine.worker.codeexecutor;

import com.venkat.codengine.worker.CodeExecuter;

public class CodeExecutorFactory {
	public static CodeExecuter getCodeExecutor(String type) {
		if(type.equalsIgnoreCase("CPP")) {
			return new CPPExecutor();
		}
		return null;
	}
}
