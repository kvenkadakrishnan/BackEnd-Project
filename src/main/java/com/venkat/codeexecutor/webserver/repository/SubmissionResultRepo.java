package com.venkat.codeexecutor.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.venkat.codeexecutor.webserver.entity.SubmissionResult;

@Repository
public interface SubmissionResultRepo extends JpaRepository<SubmissionResult, Long>{

}
