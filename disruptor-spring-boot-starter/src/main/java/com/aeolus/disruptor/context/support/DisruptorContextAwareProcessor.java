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
import com.aeolus.disruptor.context.DisruptorContextAware;
import com.aeolus.disruptor.context.DisruptorEventPublisherAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.context.*;
import org.springframework.stereotype.Component;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * DisruptorEvent扩展接口.
 * <p>用于扩展{@link DisruptorEventPublisherAware}、{@link DisruptorContextAware} 自定义实现.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 * @see DisruptorEventPublisherAware
 * @see DisruptorContextAware
 */
@Component
public class DisruptorContextAwareProcessor implements BeanPostProcessor {

    private DisruptorContext disruptorContext;

    private ConfigurableApplicationContext applicationContext;

    /** Security context used when running with a SecurityManager */
    private SecurityContextProvider securityContextProvider;


    public DisruptorContextAwareProcessor(DisruptorContext disruptorContext, ConfigurableApplicationContext applicationContext) {
        this.disruptorContext = disruptorContext;
        this.applicationContext = applicationContext;
    }

    /**
     * Set the security context provider for this bean factory. If a security manager
     * is set, interaction with the user code will be executed using the privileged
     * of the provided security context.
     * @param securityProvider {@link SecurityContextProvider} instance
     */
    public void setSecurityContextProvider(SecurityContextProvider securityProvider) {
        this.securityContextProvider = securityProvider;
    }

    /**
     * Delegate the creation of the access control context to the
     * {@link #setSecurityContextProvider SecurityContextProvider}.
     * @return {@link AccessControlContext} instance
     */
    public AccessControlContext getAccessControlContext() {
        if(this.securityContextProvider != null){
            return this.securityContextProvider.getAccessControlContext();
        }

        if(applicationContext.getAutowireCapableBeanFactory() instanceof ConfigurableBeanFactory){
            return this.applicationContext.getBeanFactory().getAccessControlContext();
        }

        return AccessController.getContext();
    }


    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        if (!(bean instanceof DisruptorEventPublisherAware || bean instanceof DisruptorContextAware)){
            return bean;
        }

        AccessControlContext acc = null;

        if (System.getSecurityManager() != null) {
            acc = getAccessControlContext();
        }

        if (acc != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                invokeAwareInterfaces(bean);
                return null;
            }, acc);
        } else {
            invokeAwareInterfaces(bean);
        }

        return bean;
    }


    /**
     * 扩展 DisruptorEventPublisherAware 接口.
     *
     * @param bean
     */
    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof DisruptorEventPublisherAware) {
                ((DisruptorEventPublisherAware) bean).setDisruptorEventPublisher(this.disruptorContext);
            }
            if (bean instanceof DisruptorContextAware) {
                ((DisruptorContextAware) bean).setDisruptorContext(this.disruptorContext);
            }
        }
    }

}
