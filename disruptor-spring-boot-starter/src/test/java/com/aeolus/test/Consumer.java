package com.aeolus.test;

import com.lmax.disruptor.WorkHandler;


public class Consumer implements WorkHandler<Order> {

    private String consumerId;

    public Consumer(String consumerId) {
        this.consumerId = consumerId;
    }

    public void onEvent(Order event) throws Exception {
//        System.out.println(Thread.currentThread().getName() + " - 当前消费者: " + this.consumerId + ", " +
//                "消费信息ID: " + event.getId());
    }


}
