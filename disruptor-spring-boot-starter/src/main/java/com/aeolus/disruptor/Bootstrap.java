/*
 * Copyright 2021 zhang liang<zhangliang0231@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aeolus.disruptor;

import com.aeolus.disruptor.client.DisruptorTemplate;
import com.aeolus.disruptor.context.event.DisruptorDataEvent;
import com.aeolus.disruptor.test.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@SpringBootApplication
public class Bootstrap implements CommandLineRunner {

    @Autowired
    private DisruptorTemplate disruptorTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        Order order = new Order();
        order.setId("22222");
        order.setName("333333");
        DisruptorDataEvent bindEvent = new DisruptorDataEvent<Order>(UUID.randomUUID());
        bindEvent.setEvent("Event-DC-Output");
        bindEvent.setTag("TagC-Output");
        bindEvent.setData(order);
        disruptorTemplate.publishEvent(bindEvent);

        Order order1 = new Order();
        order1.setId("1111111111");
        order1.setName("aaaaaaaaaaaaaa");
        DisruptorDataEvent bindEvent1 = new DisruptorDataEvent<Order>(UUID.randomUUID());
        bindEvent1.setEvent("app");
        bindEvent1.setTag("a");
        bindEvent1.setData(order1);
        disruptorTemplate.publishEvent(bindEvent1);

        Order order2 = new Order();
        order2.setId("yyyyyyy");
        order2.setName("啊啊啊啊啊啊啊");
        DisruptorDataEvent bindEvent2 = new DisruptorDataEvent<Order>(UUID.randomUUID());
        bindEvent2.setEvent("Event-DC-Output");
        bindEvent2.setTag("TagA-Output");
        bindEvent2.setData(order2);
        disruptorTemplate.publishEvent(bindEvent2);
    }
}
