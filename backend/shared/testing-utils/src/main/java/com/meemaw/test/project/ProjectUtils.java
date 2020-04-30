package com.meemaw.test.project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProjectUtils {

  private final String BACKEND = "backend";

  private String getUserDirectory() {
    return System.getProperty("user.dir");
  }

  private File root() {
    return new File(getUserDirectory().split(BACKEND)[0]);
  }

  public Path backendPath() {
    return Paths.get(root().toString(), BACKEND).toAbsolutePath();
  }

  public Path getFromBackend(String... args) {
    return Paths.get(ProjectUtils.backendPath().toString(), args);
  }

  public Path getFromModule(String... args) {
    return Paths.get(getUserDirectory(), args);
  }

}
