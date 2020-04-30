package com.meemaw.auth.model;

import java.util.UUID;

public interface User {

  UUID getId();

  String getOrg();

  UserRole getRole();

  String getEmail();

}
