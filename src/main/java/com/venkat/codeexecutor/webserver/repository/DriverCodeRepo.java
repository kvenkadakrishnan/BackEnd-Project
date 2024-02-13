package com.venkat.codeexecutor.webserver.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.venkat.codeexecutor.webserver.entity.DriverCode;

@Repository
public interface DriverCodeRepo extends JpaRepository<DriverCode, Integer> {
	List<DriverCode> findByProblemIdAndLanguage(int problemId, String language);	
}
