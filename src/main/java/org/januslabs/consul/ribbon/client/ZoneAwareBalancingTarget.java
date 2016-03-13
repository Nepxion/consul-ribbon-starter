package org.januslabs.consul.ribbon.client;

import static feign.Util.checkNotNull;
import static java.lang.String.format;

import java.net.URI;

import org.januslabs.consul.ribbon.ConsulRibbonLoadBalancer;
import org.januslabs.consul.ribbon.ConsulServerListBuilder;

import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.Server;

import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ZoneAwareBalancingTarget<T> implements Target<T> {

  private final String name;
  private final String scheme;
  private final Class<T> type;
  private final AbstractLoadBalancer lb;

  protected ZoneAwareBalancingTarget(Class<T> type, String scheme) {
    this.type = checkNotNull(type, "type");
    this.scheme = checkNotNull(scheme, "scheme");
    this.name =  URI.create(scheme).getHost();
    this.lb = AbstractLoadBalancer.class.cast(new ConsulRibbonLoadBalancer(new ConsulServerListBuilder()).build(scheme));
  }

  @Override
  public Class<T> type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String url() {
    return name;
  }

  @Override
  public Request apply(RequestTemplate input) {
    Server currentServer = lb.chooseServer(scheme);
    log.info("RequestTemplate input {}", input.url());
    log.info("Current server {}", currentServer);
    String url = format("%s",  currentServer.getHostPort());
    input.insert(0, url);
    log.info("RequestTemplate input after loadbalancer lookup {}", input.url());
    try {
      return input.request();
    } finally {
      lb.getLoadBalancerStats().incrementNumRequests(currentServer);
    }
  }

  public static <T> ZoneAwareBalancingTarget<T> create(Class<T> type, String schemeName) {
    return new ZoneAwareBalancingTarget<T>(type, schemeName);
  }
}
