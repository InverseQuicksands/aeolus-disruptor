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

package com.aeolus.disruptor.thread;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class DisruptorThreadFactory implements Serializable {

    private static final long serialVersionUID = 8050467030616633777L;


    /** 用于线程创建的线程工厂类 */
    private ThreadFactory backingThreadFactory;

    /** 线程名的前缀 */
    private String namePrefix;

    /** 是否守护线程，默认false */
    private Boolean daemon;

    /** 线程优先级 */
    private Integer priority;

    /** 未捕获异常处理器 */
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * 创建{@link DisruptorThreadFactory}
     *
     * @return {@link DisruptorThreadFactory}
     */
    public static DisruptorThreadFactory create() {
        return new DisruptorThreadFactory();
    }

    /**
     * 设置用于创建基础线程的线程工厂
     *
     * @param backingThreadFactory 用于创建基础线程的线程工厂
     * @return this
     */
    public DisruptorThreadFactory setThreadFactory(ThreadFactory backingThreadFactory) {
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    /**
     * 设置线程名前缀，例如设置前缀为hutool-thread-，则线程名为hutool-thread-1之类。
     *
     * @param namePrefix 线程名前缀
     * @return this
     */
    public DisruptorThreadFactory setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    /**
     * 设置是否守护线程
     *
     * @param daemon 是否守护线程
     * @return this
     */
    public DisruptorThreadFactory setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    /**
     * 设置线程优先级
     *
     * @param priority 优先级
     * @return this
     * @see Thread#MIN_PRIORITY
     * @see Thread#NORM_PRIORITY
     * @see Thread#MAX_PRIORITY
     */
    public DisruptorThreadFactory setPriority(int priority) {
        if (priority < Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException("Thread priority (" + priority + ") must be >= " + Thread.MIN_PRIORITY);
        }
        if (priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException("Thread priority (" + priority + ") must be >= " + Thread.MAX_PRIORITY);
        }
        this.priority = priority;
        return this;
    }

    /**
     * 设置未捕获异常的处理方式
     *
     * @param uncaughtExceptionHandler {@link Thread.UncaughtExceptionHandler}
     * @return this
     */
    public DisruptorThreadFactory setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }

    /**
     * 构建{@link ThreadFactory}
     *
     * @return {@link ThreadFactory}
     */
    public ThreadFactory build() {
        return build(this);
    }

    /**
     * 构建
     *
     * @param builder {@link DisruptorThreadFactory}
     * @return {@link ThreadFactory}
     */
    private static ThreadFactory build(DisruptorThreadFactory builder) {
        final ThreadFactory backingThreadFactory = (null != builder.backingThreadFactory)//
                ? builder.backingThreadFactory //
                : Executors.defaultThreadFactory();
        final String namePrefix = builder.namePrefix;
        final Boolean daemon = builder.daemon;
        final Integer priority = builder.priority;
        final Thread.UncaughtExceptionHandler handler = builder.uncaughtExceptionHandler;
        final AtomicLong count = (null == namePrefix) ? null : new AtomicLong();
        return r -> {
            final Thread thread = backingThreadFactory.newThread(r);
            if (null != namePrefix) {
                thread.setName(namePrefix + count.getAndIncrement());
            }
            if (null != daemon) {
                thread.setDaemon(daemon);
            }
            if (null != priority) {
                thread.setPriority(priority);
            }
            if (null != handler) {
                thread.setUncaughtExceptionHandler(handler);
            }
            return thread;
        };
    }


}
