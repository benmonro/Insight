package com.meemaw.rec.beacon.resource.v1;

import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(BeaconResource.PATH)
public interface BeaconResource {

  String PATH = "/v1/beacon";

  @POST
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
  @Path("/beat")
  CompletionStage<Response> textBeacon(
      @NotNull(message = "OrgID required") @Size(min = 6, max = 6)
      @QueryParam("OrgID") String orgID,
      @NotNull(message = "SessionID required")
      @QueryParam("SessionID") UUID sessionID,
      @NotNull(message = "UserID required")
      @QueryParam("UserID") UUID userID,
      @NotNull(message = "pageID required")
      @QueryParam("PageID") UUID pageID,
      String payload);


}
