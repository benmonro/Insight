package com.meemaw.auth.password.service;

import com.meemaw.auth.password.model.dto.PasswordResetRequestDTO;
import com.meemaw.shared.auth.UserDTO;
import io.vertx.axle.sqlclient.Transaction;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface PasswordService {

  CompletionStage<UserDTO> verifyPassword(String email, String password);

  CompletionStage<Boolean> forgot(String email);

  CompletionStage<Boolean> reset(PasswordResetRequestDTO passwordResetRequestDTO);

  CompletionStage<Boolean> create(Transaction transaction, UUID userId, String email, String org,
      String password);

  CompletionStage<Boolean> resetRequestExists(String email, String org, UUID token);

}
