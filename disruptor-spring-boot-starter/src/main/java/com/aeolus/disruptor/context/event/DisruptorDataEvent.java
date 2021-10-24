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

package com.aeolus.disruptor.context.event;

/**
 * disruptor 基础数据事件.
 * <p>在disruptor中，数据都必须是事件类型，才能进行传输.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 */
public class DisruptorDataEvent<T> extends DisruptorBindEvent {

    private T data;


    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DisruptorDataEvent(Object source) {
        super(source);
    }

    /**
     * 返回数据对象.
     * @return 数据对象.
     */
    public T getData() {
        return data;
    }

    public DisruptorDataEvent<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "DisruptorDataEvent{" +
                "data=" + data +
                "} " + super.toString();
    }
}
