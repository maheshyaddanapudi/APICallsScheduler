package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.entity.embedded.oauth2;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseIdEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;

	public int getId() {
		return id;
	}

}
