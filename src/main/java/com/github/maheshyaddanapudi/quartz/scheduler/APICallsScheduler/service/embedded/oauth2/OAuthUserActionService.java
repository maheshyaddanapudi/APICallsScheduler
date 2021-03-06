package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.service.embedded.oauth2;

import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.constants.Constants;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.entity.embedded.oauth2.OAuthClientDetails;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.entity.embedded.oauth2.OAuthRole;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.entity.embedded.oauth2.User;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.repository.embedded.oauth2.OAuthClientDetailsRepository;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.repository.embedded.oauth2.OAuthRoleRepository;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.repository.embedded.oauth2.UserRepository;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.utils.PasswordUtils;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Profile(Constants.EMBEDDED_OAUTH2)
public class OAuthUserActionService {
	
	private Logger logger = LoggerFactory.getLogger(OAuthUserActionService.class.getSimpleName());
	
	@Value("${security.oauth2.conductor.roles:role_conductor_super_manager,role_conductor_super_viewer,role_conductor_core_manager,role_conductor_core_viewer,role_conductor_execution_manager,role_conductor_execution_viewer,role_conductor_event_manager,role_conductor_event_viewer,role_conductor_metadata_manager,role_conductor_metadata_viewer,role_conductor_workflow_manager,role_conductor_workflow_viewer,role_conductor_task_manager,role_conductor_task_viewer}")
	private String oauth_conductor_roles;
	
	@Value("${security.oauth2.client.roles:role_oauth_client_admin}")
	private String oauth_client_roles;
	
	@Autowired
	OAuthClientDetailsRepository oAuthClientDetailsRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	OAuthRoleRepository oAuthRoleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public String createNewClient(String clientId, String scope, int accessTokenValidityInSeconds,
			int refreshTokenValidityInSeconds)
	{
		OAuthClientDetails newOAuthClientDetails = new OAuthClientDetails();
		
		String clientSecret = PasswordUtils.generatePassword(8);
		
		newOAuthClientDetails.setClientId(clientId);
		newOAuthClientDetails.setClientSecret(passwordEncoder.encode(clientSecret));
		newOAuthClientDetails.setScope(scope);
		newOAuthClientDetails.setAccessTokenValidityInSeconds(accessTokenValidityInSeconds);
		newOAuthClientDetails.setRefreshTokenValidityInSeconds(refreshTokenValidityInSeconds);
		
		newOAuthClientDetails = this.oAuthClientDetailsRepository.saveAndFlush(newOAuthClientDetails);
		
		return null != this.oAuthClientDetailsRepository.getByClientId(newOAuthClientDetails.getClientId()) ? clientSecret : null;
	}
	
	public boolean updateExistingClient(String clientId, String scope, int accessTokenValidityInSeconds,
			int refreshTokenValidityInSeconds)
	{
		OAuthClientDetails oAuthClientDetails = getClientDetailsByClientId(clientId);
		
		oAuthClientDetails.setClientId(clientId);
		oAuthClientDetails.setScope(scope);
		oAuthClientDetails.setAccessTokenValidityInSeconds(accessTokenValidityInSeconds);
		oAuthClientDetails.setRefreshTokenValidityInSeconds(refreshTokenValidityInSeconds);
		
		oAuthClientDetails = this.oAuthClientDetailsRepository.saveAndFlush(oAuthClientDetails);
		
		return this.oAuthClientDetailsRepository.getByClientId(clientId) != null;
	}
	
	public boolean resetExistingClientPassword(String clientId, String newClientSecret)
	{
		OAuthClientDetails oAuthClientDetails = this.oAuthClientDetailsRepository.getByClientId(clientId);
		
		oAuthClientDetails.setClientSecret(passwordEncoder.encode(newClientSecret));
		oAuthClientDetails = this.oAuthClientDetailsRepository.saveAndFlush(oAuthClientDetails);
		
		return passwordEncoder.matches(newClientSecret, oAuthClientDetails.getClientSecret());
	
	}
	
	public boolean deleteExistingClient(String clientId)
	{
		OAuthClientDetails oAuthClientDetails = this.oAuthClientDetailsRepository.getByClientId(clientId);
		
		this.userRepository.deleteUserRoleMappingsByClientId(clientId);
		this.userRepository.deleteUsersByClientId(clientId);
		
		this.oAuthClientDetailsRepository.delete(oAuthClientDetails);
		
		return null == this.oAuthClientDetailsRepository.getByClientId(clientId);
	
	}
	
	public OAuthClientDetails getClientDetailsByClientId(String clientId)
	{
		return this.oAuthClientDetailsRepository.getByClientId(clientId);
	}
	
	public String createNewUser(String username, String email, String client, String[] roles, boolean enabled, boolean account_expired, boolean credentials_expired, boolean account_locked, boolean adminMode)
	{
		User user = this.userRepository.findByUsername(username);
		
		if(null!=user)
		{
			return null;
		}
		else
		{
			String autoGeneratedPassword = PasswordUtils.generatePassword(8);
			
			boolean atleastOneRoleMapped = false;
			
			this.userRepository.insertNewUser(username, passwordEncoder.encode(autoGeneratedPassword), email, client, enabled, account_expired, credentials_expired, account_locked);
			
			User newUser = this.userRepository.findByUsername(username);
			
			for(String aRole : roles)
			{
				if(adminMode || StringHelper.contain(this.oauth_conductor_roles, aRole))
				{
					OAuthRole role = this.oAuthRoleRepository.findByName(aRole);
					
					if(null!=role)
					{
						this.userRepository.mapRoleToUser(role.getId(), newUser.getId());
						atleastOneRoleMapped = true;
					}
				}
				else
				{
					logger.warn("\nInvalid role specified. Role not mapped: "+ aRole + "\nAvailable roles are: "+this.oauth_conductor_roles);
				}
			}
			
			if(!atleastOneRoleMapped)
			{
				logger.warn("User will NOT be onboarded as no role mapping could be performed.");
				
				this.userRepository.delete(newUser);
				
				return null;
			}
			
			return null != newUser ? autoGeneratedPassword : null;
		
		}
	}
	
	public String updateUser(String client, String username, String email, String[] roles, boolean adminMode)
	{
		User user = this.userRepository.findByUsername(username);
		
		if(null!=user)
		{
			String clientFromDB = this.userRepository.getClientIdByUserId(user.getId());
			
			if(clientFromDB.equalsIgnoreCase(client))
			{	
				user.setEmail(email);
				
				user = this.userRepository.saveAndFlush(user);
				
				boolean previousRolesDeleted = false;
				
				for(String aRole : roles)
				{
					if(adminMode || StringHelper.contain(this.oauth_conductor_roles, aRole))
					{
						OAuthRole role = this.oAuthRoleRepository.findByName(aRole);
						
						if(null!=role)
						{
							if(!previousRolesDeleted)
							{
								this.userRepository.deleteUserRoleMappingsByUserId(user.getId());
								previousRolesDeleted = true;
							}
							
							this.userRepository.mapRoleToUser(role.getId(), user.getId());
						}
					}
					else
					{
						logger.warn("\nInvalid role specified. Role not mapped: "+ aRole + "\nAvailable roles are: "+this.oauth_conductor_roles);
					}
				}
				
				return null;
			
			}
			else
				return "Invalid Client Mapping - NOT allowed to map to different client.";
		}
		else
			return Constants.USER_NOT_FOUND;
	}
	
	
	public String resetUserPassword(String client, String username, String newPassword)
	{
		User user = this.userRepository.findByUsername(username);
		
		if(null != user)
		{
			String clientFromDB = this.userRepository.getClientIdByUserId(user.getId());
			
			if(clientFromDB.equalsIgnoreCase(client))
			{
				String encodedPassword = passwordEncoder.encode(newPassword);
				
				user.setPassword(encodedPassword);
				
				user = this.userRepository.saveAndFlush(user);
				
				return null;
			}
			else
				return "Invalid Client Mapping - NOT allowed to map to different client.";
		}
		else
			return Constants.USER_NOT_FOUND;
	}
	
	public String deleteExistingUser(String clientId, String username)
	{
		User user = this.userRepository.findByUsername(username);
		
		if(null!=user)
		{
			String clientIfFromDB = this.userRepository.getClientIdByUserId(user.getId());
			
			if(null != clientIfFromDB && clientId.equalsIgnoreCase(clientIfFromDB))
			{	
				this.userRepository.deleteUserRoleMappingsByUserId(user.getId());
				this.userRepository.delete(user);
				
				this.userRepository.saveAndFlush(user);
			}
			else
				return "Invalid Client Mapping - NOT allowed to map to different client.";
		}
		else
			return Constants.USER_NOT_FOUND;
		
		return null == this.userRepository.findByUsername(username) ? null : "User Not Deleted.";
	
	}
	
}
