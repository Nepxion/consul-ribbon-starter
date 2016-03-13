package org.januslabs.test.services;

import java.util.Map;

import org.januslabs.test.services.model.Forecast;

import feign.QueryMap;
import feign.RequestLine;

public interface MyForecastClient {

  @RequestLine("GET /data/2.5/weather")
  Forecast currentForecast(@QueryMap Map<String, Object> params);
}
