package com.meemaw.auth.signup.service;

import com.meemaw.auth.model.User;
import com.meemaw.auth.signup.model.dto.SignupRequestCompleteDTO;
import com.meemaw.auth.signup.model.dto.SignupRequestDTO;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface SignupService {

  CompletionStage<Boolean> exists(String email, String org, UUID token);

  CompletionStage<SignupRequestDTO> create(String email);

  CompletionStage<Boolean> complete(SignupRequestCompleteDTO completeSignup);

  CompletionStage<User> createOrganization(String email);

}
