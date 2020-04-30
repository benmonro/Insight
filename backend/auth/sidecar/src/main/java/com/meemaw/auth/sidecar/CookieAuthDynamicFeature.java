package com.meemaw.auth.sidecar;

import com.meemaw.auth.model.User;
import com.meemaw.shared.rest.auth.AbstractCookieAuthDynamicFeature;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class CookieAuthDynamicFeature extends AbstractCookieAuthDynamicFeature {

  @Inject
  SsoSessionClient ssoSessionClient;

  @Override
  protected ContainerRequestFilter cookieAuthFilter() {
    return new CookieAuthFilter();
  }

  private class CookieAuthFilter extends AbstractCookieAuthFilter<User> {

    @Override
    protected CompletionStage<Optional<User>> findSession(String sessionId) {
      return ssoSessionClient.session(sessionId).subscribeAsCompletionStage();
    }
  }

}
