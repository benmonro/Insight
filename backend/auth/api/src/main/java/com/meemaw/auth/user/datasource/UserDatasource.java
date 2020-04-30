package com.meemaw.auth.user.datasource;


import com.meemaw.auth.model.User;
import com.meemaw.auth.model.UserRole;
import com.meemaw.auth.signup.model.SignupRequest;
import io.vertx.axle.sqlclient.Transaction;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface UserDatasource {

  CompletionStage<UUID> createUser(Transaction transaction, String email, String org,
      UserRole role);

  CompletionStage<Optional<User>> findUser(String email);

  CompletionStage<SignupRequest> createUser(Transaction transaction, SignupRequest signupRequest);

  CompletionStage<SignupRequest> createOrganization(Transaction transaction,
      SignupRequest signupRequest);

}
