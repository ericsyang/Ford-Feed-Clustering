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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autonomic.ext.feed.cluster.InstanceShards;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExampleControllerTest {

  @InjectMocks
  private ExampleController exampleController;

  @Mock
  private ExampleService exampleService;

  @Test
  void start_subscribes_to_feed() throws ExecutionException, InterruptedException {
    exampleController.start();

    verify(exampleService).start();
  }

  @Test
  void whenViewAll_hasRunningInstances_thenReturnInstances() {
    //Arrange
    InstanceShards instanceShards = new InstanceShards();
    instanceShards.setReading(Arrays.asList(
      encode("shard1"),
      encode("shard2"),
      encode("shard3"),
      encode("shard4")));
    instanceShards.setStarting(Arrays.asList(
      encode("shard5"),
      encode("shard6"),
      encode("shard7"),
      encode("shard8")));
    instanceShards.setStopping(Arrays.asList(
      encode("shard9"),
      encode("shard10"),
      encode("shard11"),
      encode("shard12")));
    Map<Integer, InstanceShards> instanceShardsMap = new HashMap<>();
    instanceShardsMap.put(1, instanceShards);
    when(exampleService.viewAll()).thenReturn(instanceShardsMap);

    //Act
    final Map<Integer, InstanceShards> actual = exampleController.viewAll();

    //Assert
    assertThat(actual).isEqualTo(instanceShardsMap);
  }

  @Test
  void stop_closes_subscription() throws ExecutionException, InterruptedException {
    exampleController.stop(1);

    verify(exampleService).stopOneInstance(1);
  }
}
