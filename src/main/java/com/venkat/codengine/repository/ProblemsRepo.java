package com.venkat.codengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.venkat.codengine.entity.Problems;

public interface ProblemsRepo extends JpaRepository<Problems, Integer>{

}
