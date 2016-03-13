package org.januslabs.consul.ribbon.tests;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.januslabs.consul.ribbon.ConsulRibbonLoadBalancer;
import org.januslabs.consul.ribbon.ConsulServerList.CatalogClient;
import org.januslabs.consul.ribbon.ConsulServerListBuilder;
import org.januslabs.consul.ribbon.client.FeignRibbonClientFactory;
import org.januslabs.test.services.MyForecastClient;
import org.januslabs.test.services.model.Forecast;
import org.junit.Assert;
import org.junit.Test;

import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.Server;

import feign.Feign;
import feign.Logger.Level;
import feign.jackson.JacksonDecoder;
import feign.slf4j.Slf4jLogger;

public class ConsulRibbonTests {

  private static final String SERVICE_ID = "http://consul-starter-tester";
  private static final String OPEN_WEATHER_SERVICE_ID = "http://OpenWeatherAPI";

  @Test
  public void catalogClient() {

    CatalogClient mytest = Feign.builder().decoder(new JacksonDecoder()).logger(new Slf4jLogger())
        .logLevel(Level.FULL).target(CatalogClient.class, "http://localhost:8500");
    mytest.getServiceNodes("consul-starter-tester");
  }


  @Test
  public void forecast() {

    AbstractLoadBalancer loadbalancer =
        new ConsulRibbonLoadBalancer(new ConsulServerListBuilder()).build(OPEN_WEATHER_SERVICE_ID);
    MyForecastClient mytest =
        Feign.builder().decoder(new JacksonDecoder()).logger(new Slf4jLogger()).logLevel(Level.FULL)
            .target(MyForecastClient.class, loadbalancer.chooseServer(OPEN_WEATHER_SERVICE_ID).getHostPort());
    Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    params.put("zip", "66213,us");
    params.put("appid", "dd52e802c71658150b59247afaa77958");

    Forecast forecast=mytest.currentForecast(params);
    Assert.assertNotNull(forecast.getName());
  }
  @Test
  public void forecastWithFactory() {

   
    FeignRibbonClientFactory clientFactory=new FeignRibbonClientFactory(OPEN_WEATHER_SERVICE_ID);
    MyForecastClient mytest=clientFactory.wrap(MyForecastClient.class, OPEN_WEATHER_SERVICE_ID);
    Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    params.put("zip", "66213,us");
    params.put("appid", "dd52e802c71658150b59247afaa77958");

    Forecast forecast=mytest.currentForecast(params);
    Assert.assertNotNull(forecast.getName());
  }

  @Test
  public void loadbalanceServers() {
    AbstractLoadBalancer loadbalancer =
        new ConsulRibbonLoadBalancer(new ConsulServerListBuilder()).build(SERVICE_ID);
    List<Server> servers = loadbalancer.getServerList(true);
    servers.stream().forEach(server -> System.out.println("****Server**** " + server));
    Server server = loadbalancer.chooseServer();
    Server server2 = loadbalancer.chooseServer(SERVICE_ID);
    Assert.assertEquals("192.168.99.1:8080", server.getHostPort());
    Assert.assertEquals("192.168.99.1:8080", server2.getHostPort());

  }


}
