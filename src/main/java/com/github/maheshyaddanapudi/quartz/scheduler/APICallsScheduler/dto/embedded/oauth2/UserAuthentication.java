package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.dto.embedded.oauth2;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"principal",
"authorities",
"details",
"authenticated"
})
public class UserAuthentication {

@JsonProperty("principal")
private Principal principal;
@JsonProperty("authorities")
private List<Authority> authorities = null;
@JsonProperty("details")
private Details details;
@JsonProperty("authenticated")
private Boolean authenticated;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("principal")
public Principal getPrincipal() {
return principal;
}

@JsonProperty("principal")
public void setPrincipal(Principal principal) {
this.principal = principal;
}

@JsonProperty("authorities")
public List<Authority> getAuthorities() {
return authorities;
}

@JsonProperty("authorities")
public void setAuthorities(List<Authority> authorities) {
this.authorities = authorities;
}

@JsonProperty("details")
public Details getDetails() {
return details;
}

@JsonProperty("details")
public void setDetails(Details details) {
this.details = details;
}

@JsonProperty("authenticated")
public Boolean getAuthenticated() {
return authenticated;
}

@JsonProperty("authenticated")
public void setAuthenticated(Boolean authenticated) {
this.authenticated = authenticated;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}