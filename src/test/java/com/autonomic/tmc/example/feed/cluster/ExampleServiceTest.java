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

import static com.autonomic.tmc.example.Base64Util.encode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autonomic.ext.feed.FeedStream;
import com.autonomic.ext.feed.FeedStream.FeedStreamBuilder;
import com.autonomic.ext.feed.cluster.InstanceShards;
import com.autonomic.ext.feed.subscription.FeedSubscription;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExampleServiceTest {

  private static final Integer INSTANCE_ID = 1;

  @Mock
  private FeedStream mockFeedStream;
  @Mock
  private FeedSubscription mockFeedSubscription;
  @Mock
  private FeedStreamBuilder mockFeedStreamBuilder;

  @InjectMocks
  private ExampleService exampleService;

  @BeforeEach
  void setup() {
    lenient().when(mockFeedStreamBuilder.build()).thenReturn(mockFeedStream);
    lenient().when(mockFeedStream.subscribe()).thenReturn(mockFeedSubscription);
  }

  @Test
  void start_instance_successfully() throws ExecutionException, InterruptedException {
    exampleService.start();
    assertThat(exampleService.feedSubscriptionCompletableFutureMap.get(INSTANCE_ID).get())
      .isEqualTo(mockFeedSubscription);
  }

  @Test
  void hasOneInstance_stop_oneInstance_only() {
    exampleService.start();
    exampleService.stopOneInstance(INSTANCE_ID);
    assertThat(exampleService.feedSubscriptionCompletableFutureMap).isEmpty();
  }


  @Test
  void hasTwoInstances_stop_oneInstance_only() {
    exampleService.start();
    exampleService.start();
    exampleService.stopOneInstance(INSTANCE_ID);
    assertThat(exampleService.feedSubscriptionCompletableFutureMap).hasSize(1);
    verify(mockFeedSubscription).close();
  }

  @Test
  void viewAll_whenInstanceRunning_thenReturnAll() {

    List<String> expectedReadingShardIds = Arrays.asList(
      encode("shard1"),
      encode("shard2"),
      encode("shard3"),
      encode("shard4"));

    List<String> expectedStartingShardIds = Arrays.asList(
      encode("shard5"),
      encode("shard6"),
      encode("shard7"),
      encode("shard8"));

    List<String> expectedStoppingShardIds = Arrays.asList(
      encode("shard9"),
      encode("shard10"),
      encode("shard11"),
      encode("shard12"));

    InstanceShards instanceShards = new InstanceShards(expectedReadingShardIds,
      expectedStartingShardIds, expectedStoppingShardIds);

    when(mockFeedSubscription.getInstanceShards()).thenReturn(instanceShards);

    exampleService.start();

    assertThat(exampleService.viewAll().get(1).getReading()).isEqualTo(expectedReadingShardIds);
    assertThat(exampleService.viewAll().get(1).getStarting()).isEqualTo(expectedStartingShardIds);
    assertThat(exampleService.viewAll().get(1).getStopping()).isEqualTo(expectedStoppingShardIds);
  }

  @Test
  void viewAll_whenNoInstanceRunning_thenReturnEmpty() {
    assertThat(exampleService.viewAll()).isEmpty();
  }

  @Test
  void stop_whenNoInstanceRunning_thenNothingHappens() {
    assertDoesNotThrow(() -> exampleService.stopOneInstance(INSTANCE_ID));
    verify(mockFeedSubscription, never()).close();
  }

  @Test
  void stop_whenInstanceNotFound_thenDoesNothing() {
    exampleService.start();

    assertDoesNotThrow(() -> exampleService.stopOneInstance(2));
    verify(mockFeedSubscription, never()).close();

  }
}
