package com.meemaw.event.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.events.model.external.BrowserEvent;
import com.meemaw.events.model.internal.AbstractBrowserEvent;
import com.meemaw.events.model.internal.BrowserLoadEvent;
import com.meemaw.test.matchers.SameJSON;
import com.meemaw.test.rest.mappers.JacksonMapper;
import java.io.IOException;
import java.util.UUID;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

public class BrowserEventWrapperSerialization {

  private final ObjectMapper objectMapper = JacksonMapper.get();

  @Test
  public void loadBeaconEventDeserialization() throws IOException, JSONException {
    String payload = "{\"t\": 1234, \"e\": 8, \"a\": [\"http://localhost:8080\"]}";
    AbstractBrowserEvent deserialized = objectMapper.readValue(payload, AbstractBrowserEvent.class);

    UUID pageId = UUID.fromString("f4a824df-e134-46e7-903b-070ed3a69cd9");
    BrowserEvent<BrowserLoadEvent> identified = deserialized.identified(pageId);

    SameJSON.assertEquals(
        "{\"event\":{\"e\":\"8\",\"t\":1234,\"a\":[\"http://localhost:8080\"]},\"pageId\":\"f4a824df-e134-46e7-903b-070ed3a69cd9\"}",
        objectMapper.writeValueAsString(identified));
  }

}
