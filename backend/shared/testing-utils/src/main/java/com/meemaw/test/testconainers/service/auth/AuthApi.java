package com.meemaw.test.testconainers.service.auth;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target({TYPE})
@Retention(RUNTIME)
@ExtendWith(AuthApiExtension.class)
public @interface AuthApi {

}
