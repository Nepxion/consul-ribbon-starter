package org.januslabs.consul.ribbon;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.januslabs.consul.ribbon.ConsulServerList.CatalogClient;
import org.januslabs.consul.ribbon.model.ServiceNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsulServerListBuilder extends RibbonFeignClientConfig implements CatalogClient {

  private static final String CONSUL_URL = "http://localhost:8500";

  @Override
  public List<ServiceNode> getServiceNodes(String serviceId) {
    CatalogClient fallback = new CatalogClient() {
      @Override
      public List<ServiceNode> getServiceNodes(String serviceId) {

        return Collections.emptyList();
      }
    };

    log.info("Consul request to get all service nodes with service id {}", serviceId);
    CatalogClient catalogClient =
        jsonclient().target(CatalogClient.class, ConsulServerListBuilder.CONSUL_URL, fallback);
    List<ServiceNode> serviceNodes = catalogClient.getServiceNodes(serviceId);
    log.info("Consul request processed for service nodes with service id {} , {}", serviceId,
        serviceNodes);
    return serviceNodes;
  }

  public ConsulServerList build(@Nonnull final String service) {
    Objects.requireNonNull(service);
    final List<ServiceNode> services = getServiceNodes(service);
    return new ConsulServerList(service, services);
  }
}
