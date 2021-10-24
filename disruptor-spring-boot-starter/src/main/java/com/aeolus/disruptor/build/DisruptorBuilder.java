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

package com.aeolus.disruptor.build;

import com.aeolus.disruptor.context.waitstrategy.WaitStrategys;
import com.aeolus.disruptor.thread.NamedThreadFactory;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

public class DisruptorBuilder<T> implements Builder {


    private int ringBufferSize = 1024;
    private ProducerType producerType = ProducerType.SINGLE;
    private ThreadFactory threadFactory = new NamedThreadFactory("Event", false);
    private WaitStrategy waitStrategy = WaitStrategys.blockingWaitStrategy;


    public DisruptorBuilder() {

    }

    public DisruptorBuilder(int ringBufferSize, ProducerType producerType,
                            ThreadFactory threadFactory, WaitStrategy waitStrategy) {

        this.ringBufferSize = ringBufferSize;
        this.producerType = producerType;
        this.threadFactory = threadFactory;
        this.waitStrategy = waitStrategy;
    }

    public Disruptor<T> build(EventFactory eventFactory, WorkHandler<T>[] workHandlers) {

        return build(this.ringBufferSize, this.threadFactory, eventFactory,
                this.producerType, waitStrategy, workHandlers);
    }


    /**
     * 创建单生产者的 Disruptor.
     *
     * @param ringBufferSize ringbuffer 大小
     * @param threadFactory  线程工厂
     * @param waitStrategy   等待策略
     * @param eventFactory   事件工厂
     * @param workHandlers   处理类实例
     * @return Disruptor
     */
    @Override
    public <T> Disruptor<T> build(int ringBufferSize,
                              ThreadFactory threadFactory,
                              EventFactory eventFactory,
                              ProducerType producerType,
                              WaitStrategy waitStrategy,
                              WorkHandler<T>[] workHandlers) {

        Disruptor<T> disruptor = new Disruptor<T>(
                eventFactory,
                ringBufferSize, threadFactory,
                producerType, waitStrategy);

        disruptor.handleEventsWithWorkerPool(workHandlers);

        return disruptor;
    }

}
