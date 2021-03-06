package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.dto.embedded.oauth2;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"remoteAddress",
"tokenValue",
"tokenType",
"display"
})
public class Details_ {

@JsonProperty("remoteAddress")
private String remoteAddress;
@JsonProperty("tokenValue")
private String tokenValue;
@JsonProperty("tokenType")
private String tokenType;
@JsonProperty("display")
private String display;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("remoteAddress")
public String getRemoteAddress() {
return remoteAddress;
}

@JsonProperty("remoteAddress")
public void setRemoteAddress(String remoteAddress) {
this.remoteAddress = remoteAddress;
}

@JsonProperty("tokenValue")
public String getTokenValue() {
return tokenValue;
}

@JsonProperty("tokenValue")
public void setTokenValue(String tokenValue) {
this.tokenValue = tokenValue;
}

@JsonProperty("tokenType")
public String getTokenType() {
return tokenType;
}

@JsonProperty("tokenType")
public void setTokenType(String tokenType) {
this.tokenType = tokenType;
}

@JsonProperty("display")
public String getDisplay() {
return display;
}

@JsonProperty("display")
public void setDisplay(String display) {
this.display = display;
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