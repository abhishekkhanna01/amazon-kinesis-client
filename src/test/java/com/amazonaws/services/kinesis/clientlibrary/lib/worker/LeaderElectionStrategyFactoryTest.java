/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.services.kinesis.clientlibrary.lib.worker;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import com.amazonaws.services.kinesis.leases.impl.KinesisClientLease;
import com.amazonaws.services.kinesis.leases.interfaces.ILeaseManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LeaderElectionStrategyFactoryTest {

    @Mock
    private ILeaseManager<KinesisClientLease> leaseManager;
    @Mock
    private KinesisClientLibConfiguration config;

    LeaderElectionStrategyFactory factory;

    @Before
    public void setup() {
        factory = new LeaderElectionStrategyFactory(config, leaseManager);
    }

    @Test
    public void testStrategyTypeCreatedByFactory() {
        assertTrue("LeaderElectionStrategyFactory should vend DeterministicShuffleLeaderElection instance",
                factory.getLeaderElectionStrategy() instanceof DeterministicShuffleLeaderElection);
    }

    @Test
    public void testSingletonInstanceVendedByFactory() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                factory.getLeaderElectionStrategy();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                factory.getLeaderElectionStrategy();
            }
        });

        t1.run();
        t2.run();
        t1.join(5);
        t2.join(5);

        assertEquals("LeaderElectionStrategyFactory must vend singleton LeaderElectionStrategy instance",
                factory.vendedInstanceCount, 1);

    }
}