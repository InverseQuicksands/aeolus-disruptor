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
 * disruptor绑定事件，用于设置disruptor事件的event、tag和key.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 */
public class DisruptorBindEvent extends DisruptorEvent {

    /** Event Name */
    private String event;

    /** Event Tag */
    private String tag;

    /** Event Keys */
    private String key;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DisruptorBindEvent(Object source) {
        super(source);
    }

    /**
     * 返回表达式.
     */
    public String getRouteExpression() {

        return new StringBuilder("/")
                .append(getEvent())
                .append("/")
                .append(getTag())
                .append("/")
                .append(getKey())
                .toString();

    }

    public String getEvent() {
        return event;
    }

    public DisruptorBindEvent setEvent(String event) {
        this.event = event;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public DisruptorBindEvent setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public String getKey() {
        return key;
    }

    public DisruptorBindEvent setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public String toString() {
        return "DisruptorBindEvent{" +
                "event='" + event + '\'' +
                ", tag='" + tag + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
