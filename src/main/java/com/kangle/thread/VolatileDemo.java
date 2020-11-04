package main.java.com.kangle.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MyData{
    volatile int number = 0;
    public void addTo60(){
        this.number = 60;
    }
    public void addPlusPlus() { number++; }
    AtomicInteger atomicInteger = new AtomicInteger();
    public void addMyAtomic() { atomicInteger.getAndIncrement();}
}

public class VolatileDemo {
    public static void main(String[] args) {
//        atomicOfVolatile();
//        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
//        map.put("Lucy", "great");
//        map.put("Tom", "bad");
//        System.out.println(map.entrySet());


        //单线程单例模式调用
//        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
//        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
//        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
//        System.out.println("===========================");

        //并发多线程的情况
//        for (int i = 1; i < 10; i++) {
//            new Thread(()->{
//                SingletonDemo.getInstance();
//            }, String.valueOf(i)).start();
//        }
        AtomicInteger atomicInteger = new AtomicInteger();
        atomicInteger.getAndIncrement();
    }

    private static void atomicOfVolatile() {
        System.out.println(Thread.activeCount());
        MyData myData = new MyData();
        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    myData.addPlusPlus();
                    myData.addMyAtomic();
                }
            }, String.valueOf(i)).start();
        }

        //需要等待20个线程全部计算完成，再用main线程取得最终结果
        //jvm正常有main和gc两个线程，大于2就是有异步线程存在。 yield会让当前线程(现在在main里，那就是让main)主动停止让别的线程尽情地跑CPU
        System.out.println("==================="+Thread.activeCount());
        while(Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName()+"\t finally number value: " + myData.number);
        System.out.println(Thread.currentThread().getName()+"\t AtomicInteger type, finally number value: " + myData.atomicInteger);
//        try{
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    //1. volatile 解决可见性，对于其修饰的变量，jvm会及时通知其他线程，主物理内存的变量值已被修改。若没有volatile，则while会一直loop得不到通知
    //2. volatile 不保证原子性
    //              原子性：不可分割，完整性，也即某个线程正在做某个具体业务时，中间不可以被加塞或者被分割，需要整体完整，要么同时成功，要么同时失败
    private static void visibilityOfVolatile() {
        MyData myData = new MyData();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t come in");
            try {
                TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            myData.addTo60();
            System.out.println(Thread.currentThread().getName() + "\t updated number value: " + myData.number);
        },"AAA").start();

        while(myData.number == 0){}

        System.out.println(Thread.currentThread().getName() + "\t mission is over. updated number is " + myData.number);
    }

}
