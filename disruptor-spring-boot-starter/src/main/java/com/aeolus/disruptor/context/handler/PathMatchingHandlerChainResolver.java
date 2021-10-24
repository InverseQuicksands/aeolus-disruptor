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

package com.aeolus.disruptor.context.handler;

import com.aeolus.disruptor.context.event.DisruptorDataEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Map;

public class PathMatchingHandlerChainResolver {

    private static final Logger LOG = LoggerFactory.getLogger(PathMatchingHandlerChainResolver.class);

    private final Map<String, DisruptorHandler<DisruptorDataEvent>> disruptorHandler;

    private final Map<String, String> handlerDefinition;

    public PathMatchingHandlerChainResolver(Map<String, DisruptorHandler<DisruptorDataEvent>> disruptorHandler,
                                            Map<String, String> handlerDefinition) {
        this.disruptorHandler = disruptorHandler;
        this.handlerDefinition = handlerDefinition;
    }

    /**
     * 路径匹配器
     */
    private PathMatcher pathMatcher = new AntPathMatcher();


    /**
     * 获取事件中表达式是否在当前应用中存在.
     *
     * @param event 数据事件
     * @return DisruptorHandler instance
     * @throws Exception
     */
    public DisruptorHandler getExecutionChain(DisruptorDataEvent event) throws Exception {
        DisruptorHandler disruptorHandler = null;

        String expression = event.getRouteExpression();
        if (StringUtils.isBlank(expression)) {
            throw new NullPointerException("Event Expression must not be null!");
        }

        for (String pattern: this.handlerDefinition.keySet()) {
            if (pathMatcher.match(pattern, expression)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Matched path pattern [{}] for expression [{}].  " +
                            "Utilizing corresponding handler chain...", pattern, expression);
                }
                String beanName = this.handlerDefinition.get(pattern);
                disruptorHandler = this.disruptorHandler.get(beanName);
            }
        }

        if (disruptorHandler == null) {
            LOG.info("No matched path pattern for the current event.  Will not be processed.");
        }

        return disruptorHandler;
    }

}
