package com.meemaw.auth.sso.datasource;

import com.meemaw.auth.model.User;
import com.meemaw.auth.sso.model.SsoUser;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SsoDatasource {

  CompletionStage<String> createSession(User user);

  CompletionStage<Optional<SsoUser>> findSession(String sessionId);

  CompletionStage<Boolean> deleteSession(String sessionId);


}
