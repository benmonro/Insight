package com.meemaw.quarkus.events.model.serialization;

import com.meemaw.events.model.external.BrowserEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class BrowserEventSerializer extends
    ObjectMapperSerializer<BrowserEvent> {

}