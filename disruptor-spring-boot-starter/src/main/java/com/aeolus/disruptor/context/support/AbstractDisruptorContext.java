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

import com.aeolus.disruptor.context.DisruptorContext;
import com.aeolus.disruptor.context.DisruptorEventPublisher;
import com.aeolus.disruptor.context.Lifecycle;
import com.aeolus.disruptor.context.event.DisruptorApplicationEvent;
import com.aeolus.disruptor.context.event.DisruptorDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * DisruptorContext 上下文抽象实现.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public abstract class AbstractDisruptorContext implements DisruptorContext, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDisruptorContext.class);

    private final Object startupShutdownMonitor = new Object();


    @Nullable
    public ConfigurableApplicationContext applicationContext;

    @Nullable
    private DisruptorEventPublisher disruptorEventPublisher;

    @Nullable
    private Lifecycle lifecycleProcessor;

    @Nullable
    private Thread shutdownHook;


    /**
     * 发布 spring 事件.
     *
     * @param event 事件对象.
     */
    protected void publishApplicationEvent(DisruptorApplicationEvent event) throws Exception {
        applicationContext.publishEvent(event);
    }


    /**
     * 发布 disruptor 事件.
     *
     * @param event 数据事件.
     */
    @Override
    public void publishEvent(DisruptorDataEvent event) throws Exception {
        this.disruptorEventPublisher.publishEvent(event);
    }


    public void setApplicationContext(ConfigurableApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * 初始化上下文.
     */
    @Override
    public void init() {

    }






    @Override
    public void start() {
        getLifecycle().start();
    }


    @Override
    public void stop() {
        getLifecycle().stop();
    }


    @Override
    public boolean isRunning() {
        return (this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning());
    }


    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *  but not rethrown to allow other beans to release their resources as well.
     */
    @Override
    public void destroy() throws Exception {
        close();
    }


    /**
     * 返回生命周期接口实例.
     *
     * @return 返回生命周期接口实例.
     */
    @Override
    public Lifecycle getLifecycle() {
        if (this.lifecycleProcessor == null) {
            throw new IllegalStateException("LifecycleProcessor not initialized - " +
                    "call 'refresh' before invoking lifecycle methods via the context: " + this);
        }
        return this.lifecycleProcessor;
    }


    /**
     * 返回事件发布实例.
     *
     * @return 返回事件发布实例.
     */
    @Override
    public DisruptorEventPublisher getDisruptorEventPublisher() {
        return this.disruptorEventPublisher;
    }


    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        synchronized (this.startupShutdownMonitor) {
            registerShutdownHook();
            // If we registered a JVM shutdown hook, we don't need it anymore now:
            // We've already explicitly closed the context.
            if (this.shutdownHook != null) {
                try {
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                } catch (IllegalStateException ex) {
                    // ignore - VM is already shutting down
                }
            }
        }
    }


    private void registerShutdownHook() {
        if (this.shutdownHook == null) {
            // No shutdown hook registered yet.
            this.shutdownHook = new Thread(() -> {
                synchronized (startupShutdownMonitor) {
                    stop();
                }
            });
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }


    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}

