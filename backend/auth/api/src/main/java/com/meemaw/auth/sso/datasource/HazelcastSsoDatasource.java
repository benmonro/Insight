package com.meemaw.auth.sso.datasource;

import com.hazelcast.map.IMap;
import com.meemaw.auth.model.User;
import com.meemaw.auth.sso.model.SsoUser;
import com.meemaw.shared.rest.auth.SsoSession;
import io.quarkus.arc.AlternativePriority;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
@AlternativePriority(Priorities.AUTHENTICATION)
public class HazelcastSsoDatasource implements SsoDatasource {

  private static final Logger log = LoggerFactory.getLogger(HazelcastSsoDatasource.class);
  private static final String SESSION_MAP_NAME = "auth.auth.session";

  @Inject
  HazelcastProvider hazelcastProvider;

  private IMap<String, SsoUser> sessions;

  @PostConstruct
  public void init() {
    log.info("Initializing HazelcastSsoDatasource IMap={}", SESSION_MAP_NAME);
    sessions = hazelcastProvider.getInstance().getMap(SESSION_MAP_NAME);
  }

  public CompletionStage<String> createSession(User user) {
    String sessionId = SsoSession.newIdentifier();
    return sessions
        .setAsync(sessionId, new SsoUser(user), SsoSession.TTL, TimeUnit.SECONDS,
            SsoSession.MAX_IDLE, TimeUnit.SECONDS)
        .thenApply(x -> {
          log.info("Session id={} created for userId={}", sessionId, user.getId());
          return sessionId;
        });
  }

  public CompletionStage<Optional<SsoUser>> findSession(String sessionId) {
    return sessions
        .getAsync(sessionId)
        .thenApply(Optional::ofNullable);
  }

  @Override
  public CompletionStage<Boolean> deleteSession(String sessionId) {
    return sessions
        .removeAsync(sessionId)
        .thenApply(ssoUser -> {
          if (ssoUser == null) {
            return false;
          }
          log.info("Session id={} deleted for userId={}", sessionId, ssoUser.getId());
          return true;
        });
  }
}
