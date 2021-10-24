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

package com.aeolus.disruptor.context.translator;

import com.aeolus.disruptor.context.event.DisruptorDataEvent;
import com.lmax.disruptor.EventTranslatorOneArg;

public class DisruptorEventOneArgTranslator implements EventTranslatorOneArg<DisruptorDataEvent, DisruptorDataEvent> {

    @Override
    public void translateTo(DisruptorDataEvent bindEvent, long sequence, DisruptorDataEvent dataEvent) {
        bindEvent.setEvent(dataEvent.getEvent());
        bindEvent.setTag(dataEvent.getTag());
        bindEvent.setData(dataEvent.getData());
    }

}

