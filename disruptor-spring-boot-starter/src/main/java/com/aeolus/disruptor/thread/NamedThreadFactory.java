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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程创建工厂类，此工厂可选配置：
 * <pre>
 * 1. 自定义线程命名前缀
 * 2. 自定义是否守护线程
 * </pre>
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public class NamedThreadFactory implements ThreadFactory {

    /** 命名前缀 */
    private final String prefix;
    /** 线程组 */
    private final ThreadGroup threadGroup;
    /** 线程组 */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /** 是否守护线程 */
    private final boolean isDaemon;
    /** 无法捕获的异常统一处理 */
    private final Thread.UncaughtExceptionHandler handler;


    /**
     * 构造方法
     *
     * @param prefix 线程名前缀
     * @param isDaemon 是否守护线程
     */
    public NamedThreadFactory(String prefix, boolean isDaemon) {
        this(prefix, null, isDaemon);
    }

    /**
     * 构造方法
     *
     * @param prefix 线程名前缀
     * @param threadGroup 线程组，可以为null
     * @param isDaemon 是否守护线程
     */
    public NamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon) {
        this(prefix, threadGroup, isDaemon, null);
    }

    /**
     * 构造方法
     *
     * @param prefix 线程名前缀
     * @param threadGroup 线程组，可以为null
     * @param isDaemon 是否守护线程
     * @param handler 未捕获异常处理
     */
    public NamedThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon,
                              Thread.UncaughtExceptionHandler handler) {

        this.prefix = prefix + "-thread-";
        this.isDaemon = isDaemon;
        this.handler = handler;
        if (null == threadGroup) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                threadGroup = securityManager.getThreadGroup();
            } else {
                threadGroup = Thread.currentThread().getThreadGroup();
            }
        }
        this.threadGroup = threadGroup;
    }

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param runnable a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     * create a thread is rejected
     */
    @Override
    public Thread newThread(Runnable runnable) {
        final Thread thread = new Thread(this.threadGroup, runnable,
                prefix + threadNumber.getAndIncrement(), 0);

        thread.setUncaughtExceptionHandler(this.handler);
        if (this.isDaemon && (!thread.isDaemon())) {
            thread.setDaemon(this.isDaemon);
        }
        // 标准优先级
        if (Thread.NORM_PRIORITY != thread.getPriority()) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
