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
package com.autonomic.tmc.example;

import com.autonomic.tmc.auth.ClientCredentialsTokenSupplier;
import com.autonomic.tmc.auth.TokenSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * In this class a bean for TokenSupplier is setup.
 * <p>
 * - The TokenSupplier is required to authenticate with the TMC.</p>
 * <p>
 * - The FeedStringConfig is required to connect to the TMC Feed Service to start consuming a
 * flow.</p>
 * <p>Registers this class as a `@Configuration` class with `Spring Boot`. Reads properties
 * in from resources/application.yml using the `@Value` annotation.</p>
 */
@Configuration
public class TMCConfig {

  /**
   * The `getTokenSupplier` method builds a TokenSupplier for authenticating with the TMC.
   * <p>
   * - Defines a `@Bean` which returns the `TokenSupplier`
   * <p>
   * - Configured with values from our properties file.
   *
   * @param clientId     The clientId read from resources/application.yml. TMC Client identifier
   *                     provided to you by Autonomic.
   * @param clientSecret The clientSecret read from resources/application.yml. TMC Client secret
   *                     provided to you by Autonomic.
   * @param tokenUrl     The tokenUrl read from resources/application.yml. The URL to Autonomic's
   *                     account authentication service.
   */
  @Bean
  public TokenSupplier getTokenSupplier(@Value("${tmc.auth.clientId}") String clientId,
      @Value("${tmc.auth.clientSecret}") String clientSecret,
      @Value("${tmc.auth.tokenUrl}") String tokenUrl) {
    return ClientCredentialsTokenSupplier.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .tokenUrl(tokenUrl)
        .build();
  }
}
