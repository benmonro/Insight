package com.meemaw.session.service;

import com.meemaw.events.stream.EventsStream;
import com.meemaw.events.model.internal.BrowserUnloadEvent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Slf4j
public class SessionSocketService {

  private final Map<String, Session> sessions = new ConcurrentHashMap<>();

  public void onOpen(Session session) {
    sessions.put(session.getId(), session);
    log.info("onOpen {}", session.getId());
  }

  public void onClose(Session session) {
    sessions.remove(session.getId());
    log.info("onClose {}", session.getId());
  }

  public void onError(Session session, Throwable throwable) {
    sessions.remove(session.getId());
    log.error("onError {}", session.getId(), throwable);
  }

  public void pageStart(UUID pageID) {
    log.info("Notifying sockets about page start pageID={}", pageID);
    sessions.values().forEach(session -> sendText(session, "PAGE START"));
  }

  private void sendText(Session session, String text) {
    String sessionId = session.getId();
    session.getAsyncRemote().sendText(text, sendResult -> {
      if (sendResult.getException() != null) {
        log.error("Failed to send text {} to client {}", text, sessionId,
            sendResult.getException());
      } else {
        log.trace("Text {} sent to client {}", text, sessionId);
      }
    });
  }

  @Incoming(EventsStream.UNLOAD)
  public void process(BrowserUnloadEvent event) {
    log.info("Notifying sockets about page end {}", event);
    sessions.values().forEach(session -> sendText(session, "PAGE END"));
  }

}
