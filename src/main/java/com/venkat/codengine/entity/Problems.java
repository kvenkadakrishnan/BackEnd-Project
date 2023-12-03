package com.venkat.codengine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "problems")
@Data
public class Problems {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Id
	private int Id;
	
	@Column(name = "drivercodefile")
	private long DriverCodeFile;
	
	@Column(name = "sampletestcasefile")
	private String SampleTestCaseFile;

	@Column(name = "descriptionfile")
	private String DescriptionFile;
	
	@Column(name = "hiddentestcasefile")
	private String HiddenTestCaseFile;
	
	@Column(name = "expoutputfile")
	private String ExpOutputFile;
}
