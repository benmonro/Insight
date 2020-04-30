package com.meemaw.shared.quarkus.rest.mappers;

import java.util.UUID;

public interface User {

  UUID getId();

  String getOrg();

  UserRole getRole();

  String getEmail();

}
