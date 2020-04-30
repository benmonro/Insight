package com.meemaw.test.testconainers.service.auth;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AuthApiExtension implements BeforeAllCallback {

  private static final AuthApiTestContainer AUTH_API = AuthApiTestContainer.newInstance();

  public static AuthApiTestContainer getInstance() {
    return AUTH_API;
  }

  @Override
  public void beforeAll(ExtensionContext extensionContext) {
    if (!AUTH_API.isRunning()) {
      start(AUTH_API).forEach(System::setProperty);
    }
  }

  public static void stop() {
    AUTH_API.stop();
  }

  public static Map<String, String> start() {
    return start(AUTH_API);
  }

  public static Map<String, String> start(AuthApiTestContainer authApi) {
    authApi.start();
    return Collections.emptyMap();
  }

}
