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

package com.aeolus.disruptor.context.support;

import com.aeolus.disruptor.context.Lifecycle;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * disruptor生命周期默认处理器.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public class DefaultLifecycleProcessor implements Lifecycle {

    private volatile AtomicBoolean running = new AtomicBoolean(false);

    private Disruptor disruptor;

    public DefaultLifecycleProcessor setDisruptor(Disruptor disruptor) {
        this.disruptor = disruptor;
        return this;
    }

    /**
     * disruptor 启动
     */
    @Override
    public void start() {
        if (!running.get()) {
            this.disruptor.start();
        }
        this.running.compareAndSet(false, true);
    }

    /**
     * disruptor 停止
     */
    @Override
    public void stop() {
        if (running.get()) {
            this.disruptor.shutdown();
        }
        this.running.compareAndSet(true, false);
    }

    /**
     * 是否已经启动.
     *
     * @return 是否已经启动.
     */
    @Override
    public boolean isRunning() {
        return this.running.get();
    }
}
