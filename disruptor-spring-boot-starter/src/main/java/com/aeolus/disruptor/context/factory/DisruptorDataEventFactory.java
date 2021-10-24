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

package com.aeolus.disruptor.context.factory;

import com.aeolus.disruptor.context.event.DisruptorDataEvent;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * disruptor数据事件工厂类.
 * <p>创建disruptor对象时，构造方法传的参数，用于disruptor进行数据传输时，创建的数据对象.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @see Disruptor
 * @see EventFactory
 */
public class DisruptorDataEventFactory implements EventFactory<DisruptorDataEvent> {

    /**
     * 返回 {@code DisruptorDataEvent} 对象实例.
     * @return DisruptorDataEvent
     */
    @Override
    public DisruptorDataEvent newInstance() {
        return new DisruptorDataEvent(this);
    }
}
