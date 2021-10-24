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

import java.util.EventObject;

/**
 * disruptor 基本事件，作为所有disruptor事件的父类.
 * <p>任何事件类都必须继承JDK的{@link EventObject}.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @see EventObject
 */
public class DisruptorEvent extends EventObject {

    /** System time when the event happened */
    private long timestamp;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DisruptorEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Return the system time in milliseconds when the event happened.
     * @return system time in milliseconds
     */
    public final long getTimestamp() {
        return this.timestamp;
    }
}
