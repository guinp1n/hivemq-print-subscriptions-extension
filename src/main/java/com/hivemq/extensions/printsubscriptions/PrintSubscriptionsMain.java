/*
 * Copyright 2018-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.extensions.printsubscriptions;
import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.parameter.*;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.general.IterationCallback;
import com.hivemq.extension.sdk.api.services.general.IterationContext;
import com.hivemq.extension.sdk.api.services.subscription.SubscriptionStore;
import com.hivemq.extension.sdk.api.services.subscription.SubscriptionsForClientResult;
import com.hivemq.extension.sdk.api.services.subscription.TopicSubscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintSubscriptionsMain implements ExtensionMain {

    private static final @NotNull Logger log = LoggerFactory.getLogger(PrintSubscriptionsMain.class);

    @Override
    public void extensionStart(
            final @NotNull ExtensionStartInput extensionStartInput,
            final @NotNull ExtensionStartOutput extensionStartOutput) {

        try {
            final ExtensionInformation extensionInformation = extensionStartInput.getExtensionInformation();
            log.info("Started " + extensionInformation.getName() + ":" + extensionInformation.getVersion());
            final SubscriptionStore subscriptionStore = Services.subscriptionStore();
            final AtomicInteger counter = new AtomicInteger();

            CompletableFuture<Void> iterationFuture = subscriptionStore.iterateAllSubscriptions(
                    new IterationCallback<SubscriptionsForClientResult>() {
                        @Override
                        public void iterate(IterationContext context, SubscriptionsForClientResult subscriptionsForClient) {
                            final String clientId = subscriptionsForClient.getClientId();
                            if (clientId.startsWith("client")) {
                                final Set<TopicSubscription> subscriptions = subscriptionsForClient.getSubscriptions();
                                //log.info("client id=" + clientId + " and subscriptions count " + subscriptions.size());
                                if (subscriptions.size() == 0) {
                                    log.info("client id=" + clientId + " has no subscriptions");
                                }
                            }
                            //log.info("client id=" + clientId + " and subscriptions: " + subscriptions);
                        }
                    });




        } catch (final Exception e) {
            log.error("Exception thrown at extension start: ", e);
        }
    }

    @Override
    public void extensionStop(
            final @NotNull ExtensionStopInput extensionStopInput,
            final @NotNull ExtensionStopOutput extensionStopOutput) {

        final ExtensionInformation extensionInformation = extensionStopInput.getExtensionInformation();
        log.info("Stopped " + extensionInformation.getName() + ":" + extensionInformation.getVersion());
    }

}