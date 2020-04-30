package com.meemaw.auth.org.invite.resource.v1;

import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.auth.model.UserRole;
import com.meemaw.auth.org.invite.model.dto.InviteAcceptDTO;
import com.meemaw.auth.org.invite.model.dto.InviteCreateDTO;
import com.meemaw.auth.org.invite.model.dto.InviteSendDTO;
import com.meemaw.auth.sso.resource.v1.SsoResourceImplTest;
import com.meemaw.shared.rest.auth.SsoSession;
import com.meemaw.test.testconainers.pg.Postgres;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Postgres
@QuarkusTest
@Tag("integration")
public class InviteResourceImplTest {

  @Inject
  MockMailbox mailbox;

  @Inject
  ObjectMapper objectMapper;

  @BeforeEach
  void init() {
    mailbox.clear();
  }

  private static String sessionId;

  public String getSessionId() {
    if (sessionId == null) {
      String email = "org_invite_test@gmail.com";
      String password = "org_invite_test_password";
      sessionId = SsoResourceImplTest.signupAndLogin(mailbox, objectMapper, email, password);
    }

    return sessionId;
  }


  @Test
  public void invite_should_fail_when_invalid_contentType() {
    given()
        .when()
        .contentType(MediaType.TEXT_PLAIN)
        .post(InviteResource.PATH)
        .then()
        .statusCode(415)
        .body(sameJson(
            "{\"error\":{\"statusCode\":415,\"reason\":\"Unsupported Media Type\",\"message\":\"Media type not supported.\"}}"));
  }

  @Test
  public void invite_should_fail_when_not_authenticated() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .post(InviteResource.PATH)
        .then()
        .statusCode(401)
        .body(sameJson(
            "{\"error\":{\"statusCode\":401,\"reason\":\"Unauthorized\",\"message\":\"Unauthorized\"}}"));
  }

  @Test
  public void invite_should_fail_when_no_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .post(InviteResource.PATH)
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg0\":\"Payload is required\"}}}"));
  }

  @Test
  public void invite_should_fail_when_empty_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body("{}")
        .post(InviteResource.PATH)
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"role\":\"Required\",\"email\":\"Required\"}}}"));
  }

  @Test
  public void invite_should_fail_when_invalid_role() throws URISyntaxException, IOException {
    String payload = Files
        .readString(Path.of(getClass().getResource("/org/invite/invalidRole.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(InviteResource.PATH)
        .then()
        .statusCode(422)
        .body(sameJson(
            "{\"error\":{\"statusCode\":422,\"reason\":\"Unprocessable Entity\",\"message\":\"Cannot deserialize value of type `com.meemaw.auth.model.UserRole` from String \\\"HA\\\": not one of the values accepted for Enum class: [STANDARD, ADMIN]\"}}"));
  }

  @Test
  public void invite_should_fail_when_invalid_email() throws IOException {
    String payload = objectMapper
        .writeValueAsString(new InviteCreateDTO("notEmail", UserRole.ADMIN));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(InviteResource.PATH)
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"email\":\"must be a well-formed email address\"}}}"));
  }

  @Test
  public void invite_flow_should_succeed_on_valid_payload() throws IOException {
    String payload = objectMapper.writeValueAsString(
        new InviteCreateDTO("test-team-invitation@gmail.com", UserRole.ADMIN));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(InviteResource.PATH)
        .then()
        .statusCode(201);

    // creating same invite twice should fail with 409 Conflict
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(InviteResource.PATH)
        .then()
        .statusCode(409)
        .body(sameJson(
            "{\"error\":{\"statusCode\":409,\"reason\":\"Conflict\",\"message\":\"User has already been invited\"}}"));

    // accept the invite
    List<Mail> sent = mailbox.getMessagesSentTo("test-team-invitation@gmail.com");
    assertEquals(1, sent.size());
    Mail actual = sent.get(0);
    assertEquals("Insight Support <support@insight.com>", actual.getFrom());

    Document doc = Jsoup.parse(actual.getHtml());
    Elements link = doc.select("a");
    String acceptInviteUrl = link.attr("href");

    Matcher emailMatcher = Pattern.compile("^.*email=(.*\\.com).*$").matcher(acceptInviteUrl);
    emailMatcher.matches();
    String email = emailMatcher.group(1);

    Matcher orgMatcher = Pattern.compile("^.*orgId=(.*)&.*$").matcher(acceptInviteUrl);
    orgMatcher.matches();
    String orgId = orgMatcher.group(1);

    Matcher tokenMatcher = Pattern.compile("^.*token=(.*)$").matcher(acceptInviteUrl);
    tokenMatcher.matches();
    String token = tokenMatcher.group(1);

    String inviteAcceptPayload = objectMapper.writeValueAsString(new InviteAcceptDTO(email, orgId,
        UUID.fromString(token),
        "superDuperPassword123"));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(inviteAcceptPayload)
        .post(InviteResource.PATH + "/accept")
        .then()
        .statusCode(201)
        .body(sameJson("{\"data\":true}"));
  }

  @Test
  public void list_invites_should_fail_when_not_authenticated() {
    given()
        .when()
        .get(InviteResource.PATH)
        .then()
        .statusCode(401)
        .body(sameJson(
            "{\"error\":{\"statusCode\":401,\"reason\":\"Unauthorized\",\"message\":\"Unauthorized\"}}"));
  }

  @Test
  public void list_invites_should_return_collection() throws IOException {
    String sessionId = SsoResourceImplTest
        .signupAndLogin(mailbox, objectMapper, "list-invites-fetcher@gmail.com",
            "list-invites-fetcher");

    given()
        .when()
        .cookie(SsoSession.COOKIE_NAME, sessionId)
        .get(InviteResource.PATH)
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":[]}"))
        .body("data.size()", is(0));

    String payload = objectMapper.writeValueAsString(
        new InviteCreateDTO("list-invites-test@gmail.com", UserRole.STANDARD));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, sessionId)
        .body(payload)
        .post(InviteResource.PATH)
        .then()
        .statusCode(201);

    Response response = given()
        .when()
        .cookie(SsoSession.COOKIE_NAME, sessionId)
        .get(InviteResource.PATH);
    response.then().statusCode(200).body("data.size()", is(1));

    UUID token = UUID.fromString(response.body().path("data[0].token"));

    // delete the created invite
    given()
        .when()
        .cookie(SsoSession.COOKIE_NAME, sessionId)
        .pathParam("token", token)
        .delete(InviteResource.PATH + "/{token}")
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":true}"));

    // should return 0 invites now
    given()
        .when()
        .cookie(SsoSession.COOKIE_NAME, sessionId)
        .get(InviteResource.PATH)
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":[]}"))
        .body("data.size()", is(0));
  }


  @Test
  public void delete_invite_should_fail_when_no_token_param() {
    given()
        .when()
        .delete(InviteResource.PATH)
        .then()
        .statusCode(405)
        .body(sameJson(
            "{\"error\":{\"statusCode\":405,\"reason\":\"Method Not Allowed\",\"message\":\"Method Not Allowed\"}}"));
  }

  @Test
  public void delete_invite_should_fail_when_not_authenticated() {
    given()
        .when()
        .pathParam("token", UUID.randomUUID())
        .delete(InviteResource.PATH + "/{token}")
        .then()
        .statusCode(401)
        .body(sameJson(
            "{\"error\":{\"statusCode\":401,\"reason\":\"Unauthorized\",\"message\":\"Unauthorized\"}}"));
  }

  @Test
  public void delete_invite_should_fail_when_invalid_token_param() {
    given()
        .when()
        .cookie(SsoSession.COOKIE_NAME, sessionId)
        .pathParam("token", "randomToken")
        .delete(InviteResource.PATH + "/{token}")
        .then()
        .statusCode(404)
        .body(sameJson(
            "{\"error\":{\"statusCode\":404,\"reason\":\"Not Found\",\"message\":\"Resource Not Found\"}}"));
  }

  @Test
  public void send_invite_should_fail_when_invalid_contentType() {
    given()
        .when()
        .contentType(MediaType.TEXT_PLAIN)
        .post(InviteResource.PATH + "/send")
        .then()
        .statusCode(415)
        .body(sameJson(
            "{\"error\":{\"statusCode\":415,\"reason\":\"Unsupported Media Type\",\"message\":\"Media type not supported.\"}}"));
  }

  @Test
  public void send_invite_should_fail_when_not_authenticated() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .post(InviteResource.PATH + "/send")
        .then()
        .statusCode(401)
        .body(sameJson(
            "{\"error\":{\"statusCode\":401,\"reason\":\"Unauthorized\",\"message\":\"Unauthorized\"}}"));
  }

  @Test
  public void send_invite_should_fail_when_no_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .post(InviteResource.PATH + "/send")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg0\":\"Payload is required\"}}}"));
  }

  @Test
  public void send_invite_should_fail_when_empty_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body("{}")
        .post(InviteResource.PATH + "/send")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"email\":\"Required\",\"token\":\"Required\"}}}"));
  }

  @Test
  public void send_invite_should_fail_when_invalid_payload() throws JsonProcessingException {
    InviteSendDTO inviteSendDTO = new InviteSendDTO("random", UUID.randomUUID());
    String payload = objectMapper.writeValueAsString(inviteSendDTO);

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(InviteResource.PATH + "/send")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"email\":\"must be a well-formed email address\"}}}"));
  }

  @Test
  public void send_invite_flow_should_succeed_on_existing_invite() throws JsonProcessingException {
    String email = "send-invite-flow@gmail.com";
    String invitePayload = objectMapper
        .writeValueAsString(new InviteCreateDTO(email, UserRole.ADMIN));

    // Invite the user
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(invitePayload)
        .post(InviteResource.PATH)
        .then()
        .statusCode(201);

    List<Mail> sent = mailbox.getMessagesSentTo(email);
    assertEquals(1, sent.size());
    Mail actual = sent.get(0);
    assertEquals("Insight Support <support@insight.com>", actual.getFrom());

    Document doc = Jsoup.parse(actual.getHtml());
    Elements link = doc.select("a");
    String acceptInviteUrl = link.attr("href");

    // extract the token
    Matcher tokenMatcher = Pattern.compile("^.*token=(.*)$").matcher(acceptInviteUrl);
    tokenMatcher.matches();
    String token = tokenMatcher.group(1);

    // resend the invite email
    String sendInvitePayload = objectMapper
        .writeValueAsString(new InviteSendDTO(email, UUID.fromString(token)));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(sendInvitePayload)
        .post(InviteResource.PATH + "/send")
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":true}"));

    assertEquals(2, mailbox.getMessagesSentTo(email).size());
  }

}
