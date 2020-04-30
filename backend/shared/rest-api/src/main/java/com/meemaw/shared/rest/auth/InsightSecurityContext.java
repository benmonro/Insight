package com.meemaw.shared.rest.auth;

import com.meemaw.auth.model.User;
import com.meemaw.auth.model.UserRole;
import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import lombok.Value;

@Value
public class InsightSecurityContext implements SecurityContext {

  Principal userPrincipal;
  UserRole userRole;
  boolean secure;

  public InsightSecurityContext(User user, boolean isSecure) {
    this.userRole = user.getRole();
    this.userPrincipal = () -> user.getId().toString();
    this.secure = isSecure;
  }

  @Override
  public boolean isUserInRole(String role) {
    return UserRole.valueOf(role).equals(userRole);
  }

  @Override
  public String getAuthenticationScheme() {
    return "Cookie-Based-Auth-Scheme";
  }
}
