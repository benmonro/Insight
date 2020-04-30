package com.meemaw.auth.org.invite.model;

import com.meemaw.auth.model.UserRole;
import java.util.UUID;

public interface CanInviteSend {

  String getEmail();

  String getOrg();

  UserRole getRole();

  UUID getCreator();

}
