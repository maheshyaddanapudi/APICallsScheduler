package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.entity.embedded.oauth2;

import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.constants.Constants;
import org.springframework.context.annotation.Profile;

import javax.persistence.*;

@Entity
@Table(name="oauth_role")
@Profile(Constants.EMBEDDED_OAUTH2)
public class OAuthRole {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private int id;
	
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	public OAuthRole() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OAuthRole(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "OAuthRole [id=" + id + ", name=" + name + "]";
	}	
}
