package com.meemaw.shared.quarkus.rest.mappers;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;


@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDTO implements User {

  UUID id;
  String email;
  UserRole role;
  String org;
}
