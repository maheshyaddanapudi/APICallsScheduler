package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.repository.embedded.oauth2;

import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.constants.Constants;
import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.db.entity.embedded.oauth2.OAuthClientDetails;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Profile(Constants.EMBEDDED_OAUTH2)
public interface OAuthClientDetailsRepository extends JpaRepository<OAuthClientDetails, Integer> {

	@Query(value="SELECT * FROM oauth_client_details WHERE client_id = ?1", nativeQuery = true)
	OAuthClientDetails getByClientId(String clientId);
}
