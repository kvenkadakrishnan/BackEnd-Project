package com.venkat.codeexecutor.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.venkat.codeexecutor.webserver.entity.Submissions;

@Repository
public interface SubmissionsRepo extends JpaRepository<Submissions, Long> {

}
