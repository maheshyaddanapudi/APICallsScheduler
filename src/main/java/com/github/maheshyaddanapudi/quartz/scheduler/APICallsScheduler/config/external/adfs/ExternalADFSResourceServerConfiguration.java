package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.config.external.adfs;

import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.constants.Constants;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.dto.resource.server.ResourceRoleMappingDTO;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.service.external.adfs.AdfsUserInfoTokenServices;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@Profile({Constants.EXTERNAL_ADFS})
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ExternalADFSResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	
	private Logger logger = LoggerFactory.getLogger(ExternalADFSResourceServerConfiguration.class);
	
	@Value("${security.oauth2.resource.mapping}")
	private String resourceMapping;

	@Override
    public void configure(final HttpSecurity http) throws Exception {
		
	if(null!=this.resourceMapping)
	{
		Gson gson = new Gson();
		
		ResourceRoleMappingDTO[] resRoleMappingDTOs = gson.fromJson(this.resourceMapping, ResourceRoleMappingDTO[].class);
		
		String finalLog = Constants.STRING_INITIALIZR;
		
		finalLog = finalLog + "\nResource Mapping to be done for : "+ resRoleMappingDTOs.length+" path mappings\n";
			
		int pathCounter = 1;
		
		for(ResourceRoleMappingDTO aResourceRoleMapping : resRoleMappingDTOs)
		{
			logger.warn("\n\t Starting to Map: \t"+ aResourceRoleMapping.getEndpoint());
			
			finalLog = finalLog + "\n"+pathCounter+")\t Starting to Map: \t"+ aResourceRoleMapping.getEndpoint();
			
			String allowedRoles = "";
			
			for(String aRole: aResourceRoleMapping.getRoles())
			{
				if(Constants.STRING_INITIALIZR.equalsIgnoreCase(allowedRoles))
				{
					allowedRoles = aRole;
				}
				else
				{
					allowedRoles = allowedRoles + ", " + aRole;
				}
			}
			
			for(String aHTTPMethod: aResourceRoleMapping.getHttp_methods()) {
				
				switch(aHTTPMethod) {
					case "GET":
						http.authorizeRequests()
						 .antMatchers(HttpMethod.GET,
								 aResourceRoleMapping.getEndpoint()) .hasAnyAuthority(allowedRoles);
						
						finalLog = finalLog + "\n\n############################## OAUTH2 API Security Mapping - START ####################################"
								+ "\n\t Mapped URL: "+aResourceRoleMapping.getEndpoint()
								+ "\n\t Mapped HTTP Method: GET"
								+ "\n\t Mapped Roles: " + Arrays.asList(aResourceRoleMapping.getRoles()).toString()
								+ "\n############################## OAUTH2 API Security Mapping - END ######################################\n\n";
						break;
					case "POST":
						http.authorizeRequests()
						 .antMatchers(HttpMethod.POST,
								 aResourceRoleMapping.getEndpoint()) .hasAnyAuthority(allowedRoles);
						finalLog = finalLog + "\n\n############################## OAUTH2 API Security Mapping - START ####################################"
								+ "\n\t Mapped URL: "+aResourceRoleMapping.getEndpoint()
								+ "\n\t Mapped HTTP Method: POST"
								+ "\n\t Mapped Roles: " + Arrays.asList(aResourceRoleMapping.getRoles()).toString()
								+ "\n############################## OAUTH2 API Security Mapping - END ######################################\n\n";
						break;
					case "PUT":
						http.authorizeRequests()
						 .antMatchers(HttpMethod.PUT,
								 aResourceRoleMapping.getEndpoint()) .hasAnyAuthority(allowedRoles);
						finalLog = finalLog + "\n\n############################## OAUTH2 API Security Mapping - START ####################################"
								+ "\n\t Mapped URL: "+aResourceRoleMapping.getEndpoint()
								+ "\n\t Mapped HTTP Method: PUT"
								+ "\n\t Mapped Roles: " + Arrays.asList(aResourceRoleMapping.getRoles()).toString()
								+ "\n############################## OAUTH2 API Security Mapping - END ######################################\n\n";
						break;
					case "PATCH":
						http.authorizeRequests()
						 .antMatchers(HttpMethod.PATCH,
								 aResourceRoleMapping.getEndpoint()) .hasAnyAuthority(allowedRoles);
						finalLog = finalLog + "\n\n############################## OAUTH2 API Security Mapping - START ####################################"
								+ "\n\t Mapped URL: "+aResourceRoleMapping.getEndpoint()
								+ "\n\t Mapped HTTP Method: PATCH"
								+ "\n\t Mapped Roles: " + Arrays.asList(aResourceRoleMapping.getRoles()).toString()
								+ "\n############################## OAUTH2 API Security Mapping - END ######################################\n\n";
						break;
					case "DELETE":
						http.authorizeRequests()
						 .antMatchers(HttpMethod.DELETE,
								 aResourceRoleMapping.getEndpoint()) .hasAnyAuthority(allowedRoles);
						finalLog = finalLog + "\n\n############################## OAUTH2 API Security Mapping - START ####################################"
								+ "\n\t Mapped URL: "+aResourceRoleMapping.getEndpoint()
								+ "\n\t Mapped HTTP Method: DELETE"
								+ "\n\t Mapped Roles: " + Arrays.asList(aResourceRoleMapping.getRoles()).toString()
								+ "\n############################## OAUTH2 API Security Mapping - END ######################################\n\n";
						break;
					default:
						logger.error("No matching HTTP Method case found for - "+ aHTTPMethod);
						break;
				}
			}
			
			pathCounter++;
		}
		
		logger.warn(finalLog);
	}
	else
	{
		logger.error("NO RESOURCE SECURITY MAPPING DONE");
	}
		
    http.authorizeRequests()
    	.antMatchers(HttpMethod.GET, Constants.GENERIC_ROOT_URL).permitAll()
		.antMatchers(HttpMethod.GET, Constants.HC_URL).permitAll()
		.antMatchers("/userinfo").authenticated()
		.antMatchers(Constants.GENERIC_API_URL).authenticated()
    	.anyRequest().authenticated()
		.and().cors().disable().httpBasic().disable()
				.exceptionHandling()
				.authenticationEntryPoint(
						(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
				.accessDeniedHandler(
						(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
	}

	@Autowired
	ResourceServerProperties sso;

	@Bean
	public ResourceServerTokenServices resourceServerTokenServices(){
		return new AdfsUserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
	}

}
