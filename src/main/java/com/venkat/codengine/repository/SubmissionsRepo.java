package com.venkat.codengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.venkat.codengine.entity.Submissions;

@Repository
public interface SubmissionsRepo extends JpaRepository<Submissions, Long> {

}
