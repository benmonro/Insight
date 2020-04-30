package com.meemaw.shared.rest.auth;

import com.meemaw.auth.model.User;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;
import lombok.Getter;

@RequestScoped
@Getter
public class InsightPrincipal {

  private UUID userId;
  private String org;

  public InsightPrincipal as(User user) {
    userId = user.getId();
    org = user.getOrg();
    return this;
  }

}
