package com.meemaw.events.model.internal;

import java.util.HashMap;
import java.util.Map;

public class BrowserMouseDownEvent extends BrowserMouseMoveEvent {

  @Override
  public Map<String, Object> index() {
    Map<String, Object> index = new HashMap<>(4);
    index.put("type", BrowserEventTypeConstants.MOUSEDOWN);
    index.put("timestamp", timestamp);
    index.put("clientX", getClientX());
    index.put("clientY", getClientY());
    return index;
  }
}