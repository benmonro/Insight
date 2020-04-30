package com.meemaw.quarkus.test.testconainers.elasticsearch;

import com.meemaw.test.testconainers.elasticsearch.ElasticsearchExtension;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;

/**
 * Quarkus compatible test resource.
 *
 * <p>
 * USAGE: @QuarkusTestResource(ElasticsearchTestResource.class)
 */
public class ElasticsearchTestResource implements QuarkusTestResourceLifecycleManager {

  @Override
  public Map<String, String> start() {
    return ElasticsearchExtension.start();
  }

  @Override
  public void stop() {
    ElasticsearchExtension.stop();
  }
}
