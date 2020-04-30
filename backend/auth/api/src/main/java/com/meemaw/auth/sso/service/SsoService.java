package com.meemaw.auth.sso.service;

import com.meemaw.auth.model.User;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SsoService {

  CompletionStage<String> createSession(User user);

  CompletionStage<Optional<User>> findSession(String sessionId);

  CompletionStage<Boolean> logout(String sessionId);

  CompletionStage<String> login(String email, String password);

  CompletionStage<String> socialLogin(String email);
}
