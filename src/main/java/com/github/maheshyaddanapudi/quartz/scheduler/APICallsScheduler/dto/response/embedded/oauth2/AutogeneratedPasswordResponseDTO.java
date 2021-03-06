package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.dto.response.embedded.oauth2;

public class AutogeneratedPasswordResponseDTO extends BaseResponseDTO {

	private String autoGeneratedPassword;

	public AutogeneratedPasswordResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AutogeneratedPasswordResponseDTO(boolean status, String message) {
		super(status, message);
		// TODO Auto-generated constructor stub
	}

	public AutogeneratedPasswordResponseDTO(String autoGeneratedPassword) {
		super();
		this.autoGeneratedPassword = autoGeneratedPassword;
	}

	public String getAutoGeneratedPassword() {
		return autoGeneratedPassword;
	}

	public void setAutoGeneratedPassword(String autoGeneratedPassword) {
		this.autoGeneratedPassword = autoGeneratedPassword;
	}

	@Override
	public String toString() {
		return "NewUserResponseDTO [autoGeneratedPassword=" + autoGeneratedPassword + "]";
	}
}
