package com.aeolus.test;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.util.UUID;

public class ProducerTranslator {

    private final RingBuffer<Order> ringBuffer;

    public ProducerTranslator(RingBuffer<Order> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<Order, Order> TRANSLATOR =
            new EventTranslatorOneArg<Order, Order>() {
                @Override
                public void translateTo(Order event, long sequence, Order order) {
                    event.setId(order.getId());
                }
            };

    private static final EventTranslator<Order> eventTranslator =
            new EventTranslator<Order>() {
                @Override
                public void translateTo(Order event, long sequence) {
                    event.setId(UUID.randomUUID().toString());
                }
            };


    public void onData(Order order) {
        ringBuffer.publishEvent(TRANSLATOR, order);
    }

    public void onData() {
        ringBuffer.publishEvent(eventTranslator);
    }

}
