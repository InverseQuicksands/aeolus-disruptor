package com.aeolus.test;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Demo3 {

    public static void main(String[] args) throws InterruptedException {

        /**
         * ProducerType.MULTI,多个生产者
         * EventFactory,它的职责是产生数据填充RingBuffer的区块
         * RingBuffer的大小，它必须是2的倍数，目的是为了提高运算效率（涉及到求模和&运算）
         * RingBuffer的生产者在没有可用区块时（也可能是消费者（或者访问是事件处理器）太慢了）的等待策略
         */
        RingBuffer<Order> ringBuffer =//线路单一时用ringbuffer
                RingBuffer.create(ProducerType.MULTI,
                        new EventFactory<Order>() {
                            @Override
                            public Order newInstance() {
                                return new Order();
                            }
                        },1024*1024,new YieldingWaitStrategy());

        //创建SequenceBarrier,平衡生产和消费数据的速度
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        Consumer[] consumers = new Consumer[10];
        for (int i=0;i<10;i++){
            consumers[i] = new Consumer("c"+i);
        }

        //1.第一个参数：数据来源
        //2.第二个参数：做生产者和消费者的平衡
        //3.第三个参数：抛异常时的处理机制
        //4.第四个参数：消费者
        WorkerPool<Order> workerPool =
                new WorkerPool<Order>(ringBuffer,sequenceBarrier,new IntEventExceptionHandler(),consumers);

        //这一步的目的是把消费者的位置信息引用注入到生产者，如果只有一个消息费者的情况可以省略
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());//三个消费者，每个消费者的消费进度，消费下标扔给生产者（三个消费者的进度直接设置到ringbuffer中，生产就可以获取了）
        workerPool.start(Executors.newFixedThreadPool(5));//根据机器性能，获得当前机器的线程数，创建固定数量的线程池

        final CountDownLatch latch = new CountDownLatch(1);

        //生产2个数据
        for (int i=0;i<10;i++){
            final Producer p = new Producer(ringBuffer);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    for(int j=0;j<2;j++){
                        p.sendData(UUID.randomUUID().toString());
                    }
                }
            }).start();
        }

        Thread.sleep(2000);
        System.out.println("----------开始生产--------------");
        latch.countDown();//同时进行生产
        Thread.sleep(5000);

    }



    static class IntEventExceptionHandler implements ExceptionHandler {
        @Override
        public void handleEventException(Throwable throwable, long l, Object o) {

        }

        @Override
        public void handleOnStartException(Throwable throwable) {

        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {

        }
    }

}
