package com.venkat.codeexecutor.worker.codeexecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CodeExecutorFactory {
	@Autowired
	private AbstractApplicationContext context;
	public CodeExecuter getCodeExecutor(String type) throws Exception {
		if(type.equalsIgnoreCase("CPP")) {
			return context.getBean(CPPExecutor.class);
		}
		if(type.equalsIgnoreCase("JAVA")) {
			return context.getBean(JavaExecutor.class);
		}
		throw new Exception("No code executor found for the type "+type);
	}
}
