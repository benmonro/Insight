package com.meemaw.auth.signup.datasource;

import com.meemaw.auth.model.User;
import com.meemaw.auth.signup.model.dto.SignupRequestDTO;
import io.vertx.axle.sqlclient.Transaction;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface SignupDatasource {

  CompletionStage<SignupRequestDTO> create(Transaction transaction, User user);

  CompletionStage<Boolean> exists(String email, String org, UUID token);

  CompletionStage<Optional<SignupRequestDTO>> find(String email, String org, UUID token);

  CompletionStage<Boolean> delete(Transaction transaction, String email, String orgId, UUID userId);

}
