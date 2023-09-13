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

import static java.lang.String.format;

import com.autonomic.ext.feed.cluster.Instance;
import com.autonomic.ext.feed.cluster.InstanceStore;
import com.autonomic.ext.feed.exception.StoreException;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * InstanceStore is an interface for reading and writing instance statuses when Clustering is
 * enabled.
 * <p>
 * This FileInstanceStore demonstrates an implementation of the InstanceStore using flat file
 * storage.
 * <p>
 * WARNING!!!!! You should not use a FileInstanceStore in your production product. Using flat file
 * storage in production is not a robust persistence solution.
 * <p>
 * We recommend using a document storage strategy such as Redis, Memcached, or Hazelcast to
 * implement the InstanceStore. If you are using Checkpointing, you can either use the same storage
 * strategy for both CheckpointStore and InstanceStore, or you can choose a different storage
 * strategy for each.
 */
public class FileInstanceStore implements InstanceStore {

  private static final Logger LOG = Logger.getLogger(FileInstanceStore.class.getSimpleName());

  private static final String INSTANCE_PREFIX = "instance_";
  private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error has occurred.";

  private static final Gson gson = new GsonBuilder()
    .setPrettyPrinting()
    .create();

  private final File storagePath;

  public FileInstanceStore(String pathToDirectory) {
    this.storagePath = Paths.get(pathToDirectory).toFile();
  }

  /**
   * Implement `save` for writing an Instance status to the storage backend.
   * <p>
   * When implementing this method, make sure a StoreException is thrown when an Instance status is
   * unable to be written. A StoreException informs the Instance that it should stop reading all its
   * shards because it cannot communicate with the other Instances in the cluster. Then the cluster
   * will attempt balance the shards between the healthy Instances.
   * <p>
   * WARNING!!!!! Using flat file storage in production is not a robust persistence solution.
   * <p>
   * We recommend using a document storage strategy such as Redis, Memcached, or Hazelcast to
   * implement the InstanceStore. If you are using Checkpointing, you can either use the same
   * storage strategy for both CheckpointStore and InstanceStore, or you can choose a different
   * storage strategy for each.
   *
   * @param instance
   * @throws StoreException
   */
  @Override
  public void save(Instance instance) throws StoreException {
    if (instance == null) {
      return;
    }
    try (FileWriter writer = new FileWriter(format("%s/%s.json", storagePath,
      INSTANCE_PREFIX + instance.getInstanceId().toString()))) {
      gson.toJson(instance, writer);
      writer.flush();
    } catch (IOException e) {
      throw new StoreException(UNEXPECTED_ERROR_MESSAGE, e);
    }
  }

  /**
   * Implement `remove` so that your cluster can clean up Instances after they are shut down.
   * <p>
   * When implementing this method, make sure a StoreException is thrown when an Instance status is
   * unable to be removed. A StoreException informs the Instance that it should stop reading all its
   * shards because it cannot communicate with the other Instances in the cluster. Then the cluster
   * will attempt balance the shards between the healthy Instances.
   * <p>
   * WARNING!!!!! Using flat file storage in production is not a robust persistence solution.
   * <p>
   * We recommend using a document storage strategy such as Redis, Memcached, or Hazelcast to
   * implement the InstanceStore. If you are using Checkpointing, you can either use the same
   * storage strategy for both CheckpointStore and InstanceStore, or you can choose a different
   * storage strategy for each.
   *
   * @param instanceId
   * @throws StoreException
   */
  @Override
  public void remove(UUID instanceId) {
    allStatusFiles().filter(file -> file.getName().contains(instanceId.toString()))
      .findFirst()
      .ifPresent(File::delete);
  }

  /**
   * Implement readAll so that the cluster is aware of all healthy Instances.
   * <p>
   * When implementing this method, make sure a StoreException is thrown when an Instance status is
   * unable to be read. A StoreException informs the Instance that it should stop reading all its
   * shards because it cannot communicate with the other Instances in the cluster. Then the cluster
   * will attempt balance the shards between the healthy Instances.
   * <p>
   * WARNING!!!!! Using flat file storage in production is not a robust persistence solution.
   * <p>
   * We recommend using a document storage strategy such as Redis, Memcached, or Hazelcast to
   * implement the InstanceStore. If you are using Checkpointing, you can either use the same
   * storage strategy for both CheckpointStore and InstanceStore, or you can choose a different
   * storage strategy for each.
   *
   * @return
   * @throws StoreException
   */
  @Override
  public List<Instance> readAll() {
    return allStatusFiles()
      .map(file -> {
        try {
          return tryToRead(file, Instance.class);
        } catch (StoreException e) {
          LOG.log(Level.SEVERE, UNEXPECTED_ERROR_MESSAGE, e);
          return null;
        }
      })
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  private Stream<File> allStatusFiles() {
    final File[] files = storagePath.listFiles(
      (d, name) -> name.startsWith(INSTANCE_PREFIX));
    return files == null ? Stream.empty() : Arrays.stream(files);
  }

  @VisibleForTesting
  <T> T tryToRead(File file, Class<T> classToRead) throws StoreException {
    try (Reader reader = new FileReader(file)) {
      return gson.fromJson(reader, classToRead);
    } catch (IOException e) {
      throw new StoreException(String.format("Unable to read %s", file), e);
    }
  }
}
