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

import com.aeolus.disruptor.annotation.EventRule;
import com.aeolus.disruptor.configure.DisruptorProperties;
import com.aeolus.disruptor.context.config.EventHandlerDefinition;
import com.aeolus.disruptor.context.config.HandlerDefinitionMap;
import com.aeolus.disruptor.context.event.DisruptorDataEvent;
import com.aeolus.disruptor.context.handler.DisruptorHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericDisruptorContext extends AbstractDisruptorContext {

    private static final Logger LOG = LoggerFactory.getLogger(GenericDisruptorContext.class);

    private DisruptorProperties disruptorProperties;

    /**
     * DisruptorHandler的实例
     */
    private Map<String, DisruptorHandler<DisruptorDataEvent>> disruptorHandler = new HashMap<>();

    /**
     * 处理器链定义: key-express; value-beanName
     */
    private Map<String, String> handlerDefinition = new HashMap<>();


    public Map<String, DisruptorHandler<DisruptorDataEvent>> getDisruptorHandler() {
        return this.disruptorHandler;
    }

    public Map<String, String> getHandlerDefinition() {
        return this.handlerDefinition;
    }

    public GenericDisruptorContext(DisruptorProperties disruptorProperties) {
        this.disruptorProperties = disruptorProperties;
    }


    /**
     * 获取 application 配置文件的 handler-definitions 属性和应用中声明{@code EventRule}注解类的集合。
     *
     * 配置文件中使用示例：
     * <pre>
     * spring:
     *   disruptor:
     *     enabled: true
     *     ring-buffer: false
     *     ring-buffer-size: 1024
     *     ring-thread-numbers: 4
     *     multi-producer: true
     *     handler-definitions:
     *     - order: 1
     *       definitions: /Event-DC-Output/TagA-Output/** = inDbPreHandler
     *     - order: 2
     *       definitions: /Event-DC-Output/TagB-Output/** = smsPostHandler
     * </pre>
     *
     */
    public void createDisruptorEventHandler() {
        // 获取所有类中声明 @EventRule 注解的 bean.
//        Map<String, Object> beansWithEventRule = super.applicationContext.getBeansWithAnnotation(EventRule.class);
//        LOG.warn("{} not implement DisruptorHandler.class will not register Disruptor Component.", "");

        Map<String, DisruptorHandler> beansOfType = super.applicationContext.getBeansOfType(DisruptorHandler.class);
        if (MapUtils.isNotEmpty(beansOfType)) {
            beansOfType.forEach((beanName, handler) -> {
                EventRule annotationType = super.applicationContext.findAnnotationOnBean(beanName, EventRule.class);
                if (annotationType == null) {
                    LOG.warn("Not found AnnotationType '@EventRule' on {} with Bean name '{}'", handler.getClass(), beanName);
                } else {
                    handlerDefinition.put(annotationType.rule(), beanName);
                }
                disruptorHandler.put(beanName, handler);
            });
        }

        List<EventHandlerDefinition> handlerDefinitionList = disruptorProperties.getHandlerDefinitions();
        if (CollectionUtils.isNotEmpty(handlerDefinitionList)) {
            for (EventHandlerDefinition definition: handlerDefinitionList) {
                Map<String, String> chainDefinitions = this.parseHandlerChainDefinitions(definition.getDefinitions());
                handlerDefinition.putAll(chainDefinitions);
                chainDefinitions.forEach((express, beanName) -> {
                    DisruptorHandler handler = super.applicationContext.getBean(beanName, DisruptorHandler.class);
                    disruptorHandler.put(beanName, handler);
                });
            }
        }
    }


    private Map<String, String> parseHandlerChainDefinitions(String definitions) {
        HandlerDefinitionMap handlerDefinitionMap = new HandlerDefinitionMap();
        handlerDefinitionMap.load(definitions);
        HandlerDefinitionMap.Section section = handlerDefinitionMap.getSection(HandlerDefinitionMap.DEFAULT_SECTION_NAME);
        return section;
    }



}
