package org.januslabs.consul.ribbon.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceNode {

  @JsonProperty("Node")
  private String node;

  @JsonProperty("Address")
  private String address;

  @JsonProperty("ServiceID")
  private String serviceID;

  @JsonProperty("ServiceName")
  private String serviceName;

  @JsonProperty("ServiceTags")
  private List<String> serviceTags;

  @JsonProperty("ServicePort")
  private int servicePort;

  @JsonProperty("ServiceAddress")
  private String serviceAddress;
}
