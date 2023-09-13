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

import com.autonomic.ext.feed.FeedStream.FeedStreamBuilder;
import com.autonomic.ext.feed.cluster.InstanceShards;
import com.autonomic.ext.feed.subscription.FeedSubscription;
import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * This service is only for the example controller's use.
 * <p>
 * WARNING!!!!! You should not use this service in your application to use the Clustering feature.
 * This is a stand-in for your cloud services provider.
 */
@Service
@Profile("clustering")
public class ExampleService {

  @Autowired
  private FeedStreamBuilder feedStreamBuilder;

  private Integer instanceCounter = 0;

  private static final Logger LOGGER = Logger.getLogger(ExampleService.class.getSimpleName());

  @VisibleForTesting
  Map<Integer, CompletableFuture<FeedSubscription>> feedSubscriptionCompletableFutureMap = new HashMap<>();

  /**
   * WARNING!!!! This method is mimicking functionality provided by your cloud or infrastructure.
   * <p>
   * This service method is here to demonstrate spinning up an entire new instance of your application,
   * which we are not actually doing in this example.
   * <p>
   * Instead, we have utilized CompletableFutures to start subscriptions inside their own thread.
   * Each thread represents a unique Container or Server in your cloud or infrastructure.
   */
  public void start() {
    CompletableFuture<FeedSubscription> feedSubscriptionCompletableFuture = CompletableFuture
        .supplyAsync(() -> feedStreamBuilder.build().subscribe());

    feedSubscriptionCompletableFutureMap.put(++instanceCounter, feedSubscriptionCompletableFuture);
  }

  /**
   * WARNING!!!! This method is mimicking functionality provided by your cloud or infrastructure.
   * <p>
   * {@link #start() to learn why we did this}
   *
   * @return a mapping of InstanceShards for demonstrating what clustering does.
   */
  public Map<Integer, InstanceShards> viewAll() {
    Map<Integer, InstanceShards> instanceShardsMap = new HashMap<>();
    feedSubscriptionCompletableFutureMap.forEach(
        (key, feedSubscriptionCompletableFuture) -> {
          try {
            final FeedSubscription feedSubscription = feedSubscriptionCompletableFuture.get();
            instanceShardsMap.put(key, feedSubscription.getInstanceShards());
          } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Error reading shardIds from instances", e);
            Thread.currentThread().interrupt();
          } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error reading shardIds from instances", e);
          }
        });
    return instanceShardsMap;
  }

  /**
   * WARNING!!!! This method is mimicking functionality provided by your cloud or infrastructure.
   * <p>
   * {@link #start() to learn why we did this}
   *
   * @param instanceNumber simulated InstanceNumber to stop from the {@link #viewAll()} response
   */
  public void stopOneInstance(Integer instanceNumber) {
    if (feedSubscriptionCompletableFutureMap.get(instanceNumber) == null) {
      return;
    }
    try {
      feedSubscriptionCompletableFutureMap.get(instanceNumber).get().close();
    } catch (InterruptedException e) {
      LOGGER.log(Level.WARNING, "Error stopping instance", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error stopping instance", e);
    }
    feedSubscriptionCompletableFutureMap.remove(instanceNumber);
  }
}
