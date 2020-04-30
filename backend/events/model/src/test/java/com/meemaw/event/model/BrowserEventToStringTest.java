package com.meemaw.event.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.events.model.internal.AbstractBrowserEvent;
import com.meemaw.test.rest.mappers.JacksonMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BrowserEventToStringTest {

  private final ObjectMapper objectMapper = JacksonMapper.get();

  @Test
  public void unloadBrowserEventToString() throws IOException {
    String payload = "{\"t\": 1234, \"e\": 1, \"a\": [\"http://localhost:8080\"]}";
    AbstractBrowserEvent deserialized = objectMapper.readValue(payload, AbstractBrowserEvent.class);
    assertEquals("AbstractBrowserEvent(timestamp=1234, args=[http://localhost:8080])",
        deserialized.toString());
  }
}
