package com.meemaw.auth.user.model;

import com.meemaw.auth.model.User;
import com.meemaw.auth.model.UserDTO;
import com.meemaw.auth.model.UserRole;
import java.util.UUID;
import lombok.Value;

@Value
public class UserWithHashedPasswordDTO {

  UUID id;
  String email;
  UserRole role;
  String org;
  String password;

  public User user() {
    return new UserDTO(id, email, role, org);
  }
}
