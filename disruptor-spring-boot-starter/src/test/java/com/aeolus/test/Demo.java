package com.aeolus.test;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单生产者-多消费者.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public class Demo {

    public static void main(String[] args) {

        /**
         * ProducerType.MULTI,多个生产者
         * EventFactory,它的职责是产生数据填充RingBuffer的区块
         * RingBuffer的大小，它必须是2的倍数，目的是为了提高运算效率（涉及到求模和&运算）
         * RingBuffer的生产者在没有可用区块时（也可能是消费者（或者访问是事件处理器）太慢了）的等待策略
         */
        RingBuffer<Order> ringBuffer = RingBuffer.create(
                ProducerType.SINGLE,
                new OrderFactory(),
                1024 * 1024,
                new BlockingWaitStrategy());

        //创建SequenceBarrier,平衡生产和消费数据的速度
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        Consumer[] consumers = new Consumer[10];
        for(int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer("C" + i);
        }

        // 这里可以使用 Disruptor#handleEventsWithWorkerPool()方法，实现原理与下方是一样的。
        WorkerPool<Order> workerPool = new WorkerPool<Order>(
                ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                consumers);

        // 设置多个消费者的sequence序号 用于单独统计消费进度, 并且设置到ringbuffer中
        // 这一步的目的是把消费者的位置信息引用注入到生产者，如果只有一个消息费者的情况可以省略
        // 三个消费者，每个消费者的消费进度，消费下标扔给生产者（三个消费者的进度直接设置到ringbuffer中，生产就可以获取了）
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());

        //根据机器性能，获得当前机器的线程数，创建固定数量的线程池
        //最佳线程数目 = （（线程等待时间+线程CPU时间）/线程CPU时间 ）* CPU数目
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        workerPool.start(Executors.newFixedThreadPool(10));

        final Producer producer = new Producer(ringBuffer);
        for(int j = 0; j < 20; j++) {
            producer.sendData(UUID.randomUUID().toString());
        }
        workerPool.halt();
    }


    static class EventExceptionHandler implements ExceptionHandler<Order> {

        public void handleEventException(Throwable ex, long sequence, Order event) {
        }

        public void handleOnStartException(Throwable ex) {
        }

        public void handleOnShutdownException(Throwable ex) {
        }
    }










}
