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

package com.aeolus.test;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Demo4 {

    public static void main(String[] args) {

        ThreadFactory producerFactory = Executors.defaultThreadFactory();
        // 创建缓冲池
        OrderFactory eventFactory = new OrderFactory();

        // 创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
        int ringBufferSize = 1024 * 1024;
        Disruptor<Order> disruptor = new Disruptor<>(eventFactory, ringBufferSize, producerFactory,
                ProducerType.SINGLE, new BlockingWaitStrategy());
        RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();


        Consumer1 consumer1 = new Consumer1();
        Consumer2 consumer2 = new Consumer2();
        Consumer3 consumer3 = new Consumer3();
        Consumer4 consumer4 = new Consumer4();
        Consumer5 consumer5 = new Consumer5();


        // 多个消费者消费同一事件，按顺序执行。
//        disruptor.handleEventsWith(consumer1).handleEventsWith(consumer2).handleEventsWith(consumer3);
//        disruptor.handleEventsWith(consumer1).then(consumer2).then(consumer3);
        // 多个消费者消费同一事件，按顺序执行(另一种实现方式)。
//        disruptor.handleEventsWith(consumer1);
//        disruptor.after(consumer1).handleEventsWith(consumer2).then(consumer3);

        // 多个消费者消费同一事件，交替顺序。
//        disruptor.handleEventsWith(consumer1,consumer2,consumer3);

        /**
         *  多个消费者消费同一事件
         *   /c1\
         *  p    c3  菱形模型
         *   \c2/
         * 即：c1, c2 同时执行，c3 最后执行
         */
//        disruptor.handleEventsWith(consumer1,consumer2).then(consumer3);

        /*
         *  多个消费者消费同一事件
         *  /c1 -> c2\
         * p          c5  六边形模型(多边形模式)
         *  \c3 -> c4/
         * 即：c1，c3同时执行，c1执行后执行c2，c3执行后执行c4，c5 最后执行
         */
//        disruptor.handleEventsWith(consumer1, consumer3);
//        disruptor.after(consumer1).handleEventsWith(consumer2);
//        disruptor.after(consumer3).handleEventsWith(consumer4);
//        disruptor.after(consumer2, consumer4).handleEventsWith(consumer5);

        disruptor.start();

        final ProducerTranslator producer = new ProducerTranslator(ringBuffer);
        for(int j = 0; j < 20; j++) {
            Order order = new Order();
            order.setId(UUID.randomUUID().toString());
            producer.onData(order);
        }
        disruptor.shutdown();
    }
}
