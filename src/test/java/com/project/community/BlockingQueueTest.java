package com.project.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTest {

    public static void main(String[] args) {

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        Thread p = new Thread(new Producer(queue));
        Thread c1 = new Thread(new Consumer(queue));
        Thread c2 = new Thread(new Consumer(queue));
        Thread c3 = new Thread(new Consumer(queue));
        p.start();
        c1.start();
        c2.start();
        c3.start();
    }
}
class Producer implements Runnable
{
    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue)
    {
        this.queue=queue;
    }

    @Override
    public void run() {
        try
        {
            //生产100个数
            for (int i = 0; i < 100; i++) {
                //睡眠20ms生产数据
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产："+queue.size());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable
{

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue)
    {
        this.queue=queue;
    }

    @Override
    public void run() {
        try
        {
            //一直取出数据
            while (true)
            {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费："+queue.size());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
