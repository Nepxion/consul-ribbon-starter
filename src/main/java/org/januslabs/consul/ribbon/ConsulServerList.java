package org.januslabs.consul.ribbon;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.januslabs.consul.ribbon.model.ServiceNode;

import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

import feign.Param;
import feign.RequestLine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsulServerList implements ServerList<Server> {

  public interface CatalogClient {

    @RequestLine("GET /v1/catalog/service/{serviceId}")
    List<ServiceNode> getServiceNodes(@Param("serviceId") String serviceId);
  }

  private String serviceId;
  private CatalogClient client;
  private List<Server> servers = Collections.emptyList();

  @Override
  public List<Server> getInitialListOfServers() {
    return this.servers;
  }

  @Override
  public List<Server> getUpdatedListOfServers() {
    return getServers();
  }

  private List<Server> getServers() {
    List<ServiceNode> nodes = client.getServiceNodes(serviceId);
    if (nodes == null || nodes.isEmpty()) {
      return Collections.emptyList();
    }
    List<Server> servers =
        nodes.stream().map(node -> new Server(node.getServiceAddress(), node.getServicePort()))
            .collect(Collectors.toList());
    log.info("List of server available for the request {}", servers);
    return servers;
  }

  public ConsulServerList(String serviceId) {
    this.serviceId = serviceId;
    this.client = new ConsulServerListBuilder();
    this.servers = Collections.emptyList();
  }

  public ConsulServerList(String serviceId, List<ServiceNode> nodes) {
    this.serviceId = serviceId;
    this.client = new ConsulServerListBuilder();
    this.servers =
        nodes.stream().map(node -> new Server(node.getServiceAddress(), node.getServicePort()))
            .collect(Collectors.toList());
  }

  public void initWithNiwsConfig(IClientConfig config) {
    this.serviceId = config.getClientName();
    this.client = new ConsulServerListBuilder();

  }

  public static void setServiceListClass(String serviceId) {
    ConfigurationManager.getConfigInstance().setProperty(
        serviceId + ".ribbon.NIWSServerListClassName", ConsulServerList.class.getName());
  }

}
