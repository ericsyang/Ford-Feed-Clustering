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


import com.autonomic.ext.feed.cluster.InstanceShards;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller is for adding API endpoints for the example. It is solely for demonstration
 * purposes only.
 * <p>
 * WARNING!!!!! You should not use this controller in your application to use the Clustering
 * feature. This controller mimics functions that are typically provided by your cloud
 * infrastructure.
 */
@Profile("clustering")
@RestController
public class ExampleController {

  @Autowired
  private ExampleService exampleService;

  /**
   * WARNING!!!! This method is mimicking functionality provided by your cloud or infrastructure.
   * <p>
   * This controller method is here to demonstrate spinning up an entire new instance of your
   * application, which is simulated by starting multiple subscriptions in this example.
   */
  @PutMapping("/start")
  @ResponseStatus(HttpStatus.CREATED)
  public void start() {
    exampleService.start();
  }

  /**
   * WARNING!!!! This method is mimicking functionality provided by your cloud or infrastructure.
   * <p>
   * This controller method is here to demonstrate monitoring a cluster of instances of your
   * application, which is simulated by querying a property of each subscription started by the
   * {@link #start()} method of this controller.
   *
   * @return a mapping of InstanceShards for demonstrating what clustering does.
   */
  @GetMapping("/view-all")
  @ResponseStatus(HttpStatus.OK)
  public Map<Integer, InstanceShards> viewAll() {
    return exampleService.viewAll();
  }

  /**
   * WARNING!!!! This method is mimicking functionality provided by your cloud or infrastructure.
   * <p>
   * This controller method is here to demonstrate shutting-down entire instances of your
   * application, which is simulated by shutting down an instance. We do this by closing
   * subscriptions we started with the {@link #start()} method of this controller.
   *
   * @param instanceNumber simulated InstanceNumber to stop from the {@link #viewAll()} response
   */
  @DeleteMapping("/stop/{instanceNumber}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void stop(@PathVariable Integer instanceNumber) {
    exampleService.stopOneInstance(instanceNumber);
  }

}
