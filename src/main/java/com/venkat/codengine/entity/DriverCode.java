package com.venkat.codengine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "drivercode")
@Data
public class DriverCode {
	@Id
	@Column(name = "id")
	private int Id;

	@Column(name = "problemid")
	private int problemId;
	
	@Column(name = "language")
	private String language;
	
	@Column(name = "drivercodefile")
	private String DriverCodeFile;
}
