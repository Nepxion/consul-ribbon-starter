package org.januslabs.consul.ribbon;

import java.net.URI;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.WeightedResponseTimeRule;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsulRibbonLoadBalancer extends RibbonFeignClientConfig {

  private final ConsulServerListBuilder factory;

  public ConsulRibbonLoadBalancer(@Nonnull final ConsulServerListBuilder factory) {
    this.factory = Objects.requireNonNull(factory);
  }

  public ZoneAwareLoadBalancer<Server> build(String schemeName) {
    String servicename = URI.create(schemeName).getHost();
    log.info("creating a loadbalancer {} ", servicename);
    final DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl();
    clientConfig.setClientName(servicename);
    clientConfig.set(CommonClientConfigKey.ServerListRefreshInterval, 300 * 1000);

    return LoadBalancerBuilder.newBuilder().withClientConfig(clientConfig)
        .withRule(new WeightedResponseTimeRule()).withDynamicServerList(factory.build(servicename))
        .buildDynamicServerListLoadBalancer();
  }
}
