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

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

public interface Builder {

    /**
     * 创建 Disruptor.
     *
     * @param ringBufferSize ringbuffer 大小
     * @param threadFactory 线程工厂
     * @param waitStrategy 等待策略
     * @param producerType 是否单生产者
     * @param eventFactory 事件工厂
     * @param workHandlers 处理类实例
     * @return Disruptor
     */
    <T> Disruptor<T> build(int ringBufferSize,
                       ThreadFactory threadFactory,
                       EventFactory eventFactory,
                       ProducerType producerType,
                       WaitStrategy waitStrategy,
                       WorkHandler<T>[] workHandlers);

}
