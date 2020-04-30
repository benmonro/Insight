package com.meemaw.quarkus.test.testconainers.service.auth;

import com.meemaw.test.testconainers.service.auth.AuthApiExtension;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;

public class AuthApiTestResource implements QuarkusTestResourceLifecycleManager {


  @Override
  public Map<String, String> start() {
    return AuthApiExtension.start();
  }

  @Override
  public void stop() {
    AuthApiExtension.stop();
  }
}
