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

package com.aeolus.disruptor.configure;

import com.aeolus.disruptor.client.DisruptorTemplate;
import com.aeolus.disruptor.context.DisruptorContext;
import com.aeolus.disruptor.context.Lifecycle;
import com.aeolus.disruptor.context.event.DisruptorDataEvent;
import com.aeolus.disruptor.context.factory.DisruptorDataEventFactory;
import com.aeolus.disruptor.context.handler.AsyncListenerDispatcher;
import com.aeolus.disruptor.context.handler.PathMatchingHandlerChainResolver;
import com.aeolus.disruptor.context.support.DefaultLifecycleProcessor;
import com.aeolus.disruptor.context.support.GenericDisruptorContext;
import com.aeolus.disruptor.context.translator.DisruptorEventOneArgTranslator;
import com.aeolus.disruptor.context.translator.DisruptorEventThreeArgTranslator;
import com.aeolus.disruptor.context.translator.DisruptorEventTwoArgTranslator;
import com.aeolus.disruptor.context.waitstrategy.WaitStrategys;
import com.aeolus.disruptor.thread.NamedThreadFactory;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;

@Configuration
@ConditionalOnClass({ Disruptor.class })
@ConditionalOnProperty(prefix = "spring.disruptor", value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ DisruptorProperties.class })
public class DisruptorAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public EventFactory<DisruptorDataEvent> eventFactory() {
        return new DisruptorDataEventFactory();
    }


    @Bean(name = "disruptorLifecycleProcessor")
    @ConditionalOnMissingBean
    public Lifecycle getLifecycle() {
        return new DefaultLifecycleProcessor();
    }


    @Bean
    @ConditionalOnMissingBean
    public DisruptorContext disruptorContext(ConfigurableApplicationContext applicationContext,
                                             DisruptorProperties disruptorProperties) {
        GenericDisruptorContext disruptorContext = new GenericDisruptorContext(disruptorProperties);
        disruptorContext.setApplicationContext(applicationContext);
        disruptorContext.createDisruptorEventHandler();
        return disruptorContext;
    }


    @Bean
    public DisruptorTemplate disruptorTemplate() {
        return new DisruptorTemplate();
    }


    @Bean(name = "disruptorProducer")
    public Disruptor createDisruptor(DisruptorContext disruptorContext,
            DisruptorProperties properties,
            EventFactory<DisruptorDataEvent> eventFactory) {

        ThreadFactory threadFactory = new NamedThreadFactory("Event", false);
        Disruptor<DisruptorDataEvent> disruptor = new Disruptor<DisruptorDataEvent>(eventFactory,
                properties.getRingBufferSize(), threadFactory, ProducerType.SINGLE,
                WaitStrategys.sleepingWaitStrategy);

        GenericDisruptorContext genericDisruptorContext = (GenericDisruptorContext) disruptorContext;
        PathMatchingHandlerChainResolver pathMatchingHandlerChainResolver = new PathMatchingHandlerChainResolver(
                genericDisruptorContext.getDisruptorHandler(), genericDisruptorContext.getHandlerDefinition());

        final AsyncListenerDispatcher listenerDispatcher = new AsyncListenerDispatcher(pathMatchingHandlerChainResolver);
        disruptor.handleEventsWith(listenerDispatcher);
        disruptor.start();

        return disruptor;
    }


    @Bean
    @ConditionalOnMissingBean
    public EventTranslatorOneArg<DisruptorDataEvent, DisruptorDataEvent> oneArgEventTranslator() {
        return new DisruptorEventOneArgTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventTranslatorTwoArg<DisruptorDataEvent, String, String> twoArgEventTranslator() {
        return new DisruptorEventTwoArgTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventTranslatorThreeArg<DisruptorDataEvent, String, String, String> threeArgEventTranslator() {
        return new DisruptorEventThreeArgTranslator();
    }


}
