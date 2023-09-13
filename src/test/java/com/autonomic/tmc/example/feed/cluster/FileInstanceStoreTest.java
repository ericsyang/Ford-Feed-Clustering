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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.autonomic.ext.feed.cluster.Instance;
import com.autonomic.ext.feed.cluster.InstanceRole;
import com.autonomic.ext.feed.exception.StoreException;
import com.autonomic.ext.feed.subscription.SubscriptionHealth.State;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileInstanceStoreTest {

  private static final String tempDirectoryPath = "src/test/resources/cluster";

  private FileInstanceStore fileInstanceStore;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void cleanup() {
    for (Instance instance : fileInstanceStore.readAll()) {
      fileInstanceStore.remove(instance.getInstanceId());
    }
  }

  @Test
  void givenStatus_whenSaved_thenStatusIsStored() throws StoreException {
    // Arrange
    final Instance instance = buildInstance();
    fileInstanceStore = new FileInstanceStore(tempDirectoryPath);

    // Act
    fileInstanceStore.save(instance);

    // Assert
    assertThat(fileInstanceStore.readAll()).contains(instance);
  }

  @Test
  void givenInvalidPath_whenSaving_thenStoreExceptionThrown() {
    fileInstanceStore = new FileInstanceStore("XYZ");

    assertThatThrownBy((() -> fileInstanceStore.save(buildInstance())))
      .isInstanceOf(StoreException.class)
      .hasMessage("An unexpected error has occurred.");
  }

  @Test
  void givenNullInstance_whenSaving_thenTrapped() {
    fileInstanceStore = new FileInstanceStore("XYZ");

    assertThatCode((() -> fileInstanceStore.save((Instance) null)))
      .doesNotThrowAnyException();
  }

  @Test
  void givenInvalidPath_whenTryToRead_thenTrapped() {
    fileInstanceStore = new FileInstanceStore("XYZ");

    assertThatThrownBy(
      (() -> fileInstanceStore.tryToRead(Paths.get("XYZ").toFile(), Object.class)))
      .isInstanceOf(StoreException.class)
      .hasMessage("Unable to read XYZ");
  }

  @Test
  void remove_WhenFilePresent_thenDeletesFile() throws StoreException, InterruptedException {
    // Arrange
    final Instance instance = buildInstance();
    fileInstanceStore = new FileInstanceStore(tempDirectoryPath);
    fileInstanceStore.save(instance);
    assertThat(fileInstanceStore.readAll()).contains(instance);

    // Act
    fileInstanceStore.remove(instance.getInstanceId());

    // Assert
    assertThat(fileInstanceStore.readAll()).doesNotContain(instance);
  }

  private Instance buildInstance() {
    final Instance instance = new Instance();
    instance.setInstanceId(UUID.randomUUID());
    instance.setInstanceRole(InstanceRole.READER);
    instance.setState(State.HEALTHY);
    instance.setHealthyUntil(Clock.systemUTC().instant());
    return instance;
  }
}
