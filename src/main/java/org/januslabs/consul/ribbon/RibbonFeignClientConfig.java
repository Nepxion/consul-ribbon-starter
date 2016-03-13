package org.januslabs.consul.ribbon;

import feign.Logger;
import feign.Retryer;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxb.JAXBContextFactory;
import feign.jaxb.JAXBDecoder;
import feign.jaxb.JAXBEncoder;
import feign.slf4j.Slf4jLogger;

public class RibbonFeignClientConfig {

  protected HystrixFeign.Builder client() {
    return HystrixFeign.builder().logger(new Slf4jLogger()).retryer(new Retryer.Default())
        .logLevel(Logger.Level.FULL);
  }

  protected HystrixFeign.Builder jaxbclient() {
    return HystrixFeign.builder().encoder(new JAXBEncoder(new JAXBContextFactory.Builder().build()))
        .decoder(new JAXBDecoder(new JAXBContextFactory.Builder().build()))
        .logger(new Slf4jLogger()).retryer(new Retryer.Default()).logLevel(Logger.Level.FULL);
  }

  protected HystrixFeign.Builder jsonclient() {
    return HystrixFeign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
        .logger(new Slf4jLogger()).retryer(new Retryer.Default()).logLevel(Logger.Level.FULL);
  }

}
