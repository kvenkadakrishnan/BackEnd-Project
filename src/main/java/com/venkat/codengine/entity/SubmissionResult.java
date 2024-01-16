package com.venkat.codengine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "submissionresult")
@Data
public class SubmissionResult {
	@Column(name = "submissionid")
	@Id
	private long SubmissionId;
	
	@Column(name = "runtime")
	private int Runtime;
	
	@Column(name = "memory")
	private int Memory;

	@Column(name = "status")
	private String Status;
	
	@Column(name = "testcasepassed")
	private int TestCasePassed;
	
	@Column(name = "resultfile")
	private String ResultFile;
}
