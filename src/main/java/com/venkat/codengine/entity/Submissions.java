package com.venkat.codengine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "submissions")
@Data
public class Submissions {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long Id;

	@Column(name = "problemid")
	private int ProblemId;
	
	@Column(name = "codefile")
	private String CodeFile;

	@Column(name = "testcasefile")
	private String TestCaseFile;
	
	@Column(name = "lang")
	private String Lang;
	
	@Column(name = "status")
	private String Status;
}
