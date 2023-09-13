/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * feed-consumer-examples
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2022 Autonomic, LLC - All rights reserved
 * ——————————————————————————————————————————————————————————————————————————————
 * Proprietary and confidential.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Autonomic, LLC and its suppliers, if any.  The intellectual and technical
 * concepts contained herein are proprietary to Autonomic, LLC and its suppliers
 * and may be covered by U.S. and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Autonomic, LLC.
 *
 * FOR DEMONSTRATION PURPOSES ONLY. THIS SAMPLE CODE IS UNSUPPORTED, NOT COVERED
 * BY AUTONOMIC SERVICE LEVEL AGREEMENTS AND NOT INTENDED FOR PRODUCTION USE. THIS
 * SAMPLE CODE IS PROVIDED “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE,
 * ARE DISCLAIMED. IN NO EVENT SHALL AUTONOMIC OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES,
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) SUSTAINED BY YOU OR A
 * THIRD PARTY, HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT ARISING IN ANY WAY OUT OF THE USE OF THIS SAMPLE CODE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * ______________________________________________________________________________
 */
package com.autonomic.tmc.example.feed.cluster;


import com.autonomic.ext.feed.FeedStream;
import com.autonomic.ext.feed.FeedStream.FeedStreamBuilder;
import com.autonomic.ext.feed.StartingPoint;
import com.autonomic.ext.feed.cluster.ClusterConfig;
import com.autonomic.ext.feed.cluster.InstanceStore;
import com.autonomic.ext.feed.metrics.FeedMetric;
import com.autonomic.ext.feed.metrics.MetricsConfig;
import com.autonomic.tmc.auth.TokenSupplier;
import java.time.Duration;
import java.util.Collections;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration("Clustering Example Config")
@Profile("clustering")
public class ExampleConfig {

  private static final Logger LOG = Logger.getLogger(ExampleConfig.class.getSimpleName());

  @Value("src/main/resources/cluster")
  private String saveDirectory;

  /**
   * The `configureFeed` method creates a FeedStream which is used to connect to the TMC Feed
   * Service to start consuming from a flow.
   * <p>
   * - Defines a @Bean which returns the FeedStream object. The FeedStream object contains:
   * <p>
   * - TokenSupplier this is authenticated against the TMC. Received from calling the
   * `getTokenSupplier` method
   * <p>
   * - The URL to the Autonomic environment where you would like to access the TMC Feed Service.
   * <p>
   * - The name of the flow you want to read.
   * <p>
   * - How many hours ago do you want to start reading the flow,
   *
   * @param tokenSupplier  The TokenSupplier object from `getTokenSupplier`. {@link
   *                       com.autonomic.tmc.example.TMCConfig#getTokenSupplier getTokenSupplier}
   * @param streamUrl      The streamUrl read from resources/application.yml. URL to the Autonomic
   *                       environment where you want to connect to the TMC Feed Service so that you
   *                       can consume your flow.
   * @param flow           The flow read from resources/application.yml. This is the flow you want
   *                       to consume.
   * @param hoursOfHistory The hoursOfHistory read from resources/application.yml. A
   *                       java.time.Duration parameter that specifies how far back in time to
   *                       start
   * @param instanceStore  The FileInstanceStore object from `getInstanceStore`. {@link
   *                       #getInstanceStore() getInstanceStore}
   */
  @Bean
  public FeedStreamBuilder getStream(TokenSupplier tokenSupplier,
      @Value("${tmc.feed-consumer.streamUrl}") String streamUrl,
      @Value("${tmc.feed-consumer.flow}") String flow,
      @Value("${tmc.feed-consumer.hoursOfHistory}") Integer hoursOfHistory,
      InstanceStore instanceStore) {
    return FeedStream.builder()
        .tokenSupplier(tokenSupplier)
        .streamUrl(streamUrl)
        .flow(flow)
        .feedEventObserver(new Observer())
        .metricsConfig(getMetricsConfig())
        .clusterConfig(ClusterConfig.builder().instanceStore(instanceStore).build())
        .startingPoint(StartingPoint.historical(Duration.ofHours(hoursOfHistory)));
  }

  private MetricsConfig getMetricsConfig() {
    return MetricsConfig.builder()
        .applicationIdentifier("example-clustering")
        .metricsToDisable(Collections.singletonList(FeedMetric.TIMER_TOTAL_ONE_FEED_EVENT))
        .build();
  }

  /**
   * WARNING!!!!! You should not use a FileInstanceStore in your production product. Using flat file
   * storage in production is not a robust persistence solution.
   * <p>
   * We recommend using a document storage strategy such as Redis, Memcached, or Hazelcast to
   * implement the InstanceStore. If you are using Checkpointing, you can either use the same
   * storage strategy for both CheckpointStore and InstanceStore, or you can choose a different
   * storage strategy for each.
   *
   * @return demonstrates an implementation of the InstanceStore using flat file storage.
   */
  @Bean
  public InstanceStore getInstanceStore() {
    return new FileInstanceStore(saveDirectory);
  }

  @Bean
  public Runnable getRunnableExample() {
    return () -> LOG.info("Clustering Example started");
  }

}
