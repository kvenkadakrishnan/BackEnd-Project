package com.venkat.codengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.venkat.codengine.entity.SubmissionResult;

@Repository
public interface SubmissionResultRepo extends JpaRepository<SubmissionResult, Long>{

}
