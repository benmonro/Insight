package com.meemaw.shared.quarkus.rest.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.shared.rest.mappers.JacksonObjectMapperCustomizer;
import io.quarkus.jackson.ObjectMapperCustomizer;
import javax.inject.Singleton;

@Singleton
public class SuperDuperClass implements ObjectMapperCustomizer {

  @Override
  public void customize(ObjectMapper mapper) {
    JacksonObjectMapperCustomizer.configure(mapper);
  }

}
