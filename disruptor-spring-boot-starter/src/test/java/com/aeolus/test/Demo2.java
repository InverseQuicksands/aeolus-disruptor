package com.aeolus.test;

import com.aeolus.disruptor.thread.NamedThreadFactory;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.ThreadFactory;

/**
 * 单生产者-多消费者
 * 生产者要实现 EventTranslatorOneArg、EventTranslator 接口.
 * 消费者要实现 WorkHandler 接口.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public class Demo2 {

    public static void main(String[] args) {
        ThreadFactory producerFactory = new NamedThreadFactory("Event", false);
//        ThreadFactory producerFactory = Executors.defaultThreadFactory();
        // 创建缓冲池
        OrderFactory eventFactory = new OrderFactory();

        // 创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
        int ringBufferSize = 1024 * 1024;
        Disruptor<Order> disruptor = new Disruptor<>(eventFactory, ringBufferSize, producerFactory,
                ProducerType.SINGLE, new BlockingWaitStrategy());
        RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();

        // 使用 handleEventsWithWorkerPool 就可以完成不重复消费，使用 handleEventsWith 就是重复消费。
        // 这里定义了10个消费者，那么就会启动10个线程来不重复消费生产者发出的消息。
        Consumer[] consumers = new Consumer[10];
        for(int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer("C" + i);
        }
        // handleEventsWith同一消息被不同handler独立消费(类似于广播订阅), handleEventsWithWorkerPool不重复消费(类似于并行处理)。
        disruptor.handleEventsWithWorkerPool(consumers);

        disruptor.start();

        final ProducerTranslator producer = new ProducerTranslator(ringBuffer);
        long start = System.currentTimeMillis();
        for(int j = 0; j < 10000000; j++) {
            Order order = new Order();
            order.setId(UUID.randomUUID().toString());
            producer.onData(order);
//            producer.onData();
        }
        disruptor.shutdown();
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("耗时：" + time);
    }
}
