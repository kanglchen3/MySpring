package main.java.com.kangle.thread;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;

public class SingletonDemo {
    private static volatile SingletonDemo instance = null;  // DCL里的volatile很重要，目的是为了禁止指令重排，避免出现 instance!-null但对象还没有被初始化，调用可能会崩溃
    private SingletonDemo(){
        System.out.println(Thread.currentThread().getName() + " is contructing...");
    }

    //DCL (double check lock 双端检锁机制)
    public static SingletonDemo getInstance(){
        if(instance == null){
            synchronized (SingletonDemo.class){
                if(instance == null){
                    instance = new SingletonDemo();
                }
            }
        }
        return instance;
    }
}

//饿汉模式
class Singleton1{
    private static Singleton1 instance = new Singleton1();
    private Singleton1(){}
    public static Singleton1 getInstance(){
        return instance;
    }
}

//登记式/静态内部类
class Singleton2{
    private static class SingletonHolder{
        private static final Singleton2 instance = new Singleton2();
    }
    private Singleton2(){}
    public static Singleton2 getInstance(){
        return SingletonHolder.instance;        //利用类加载器的机制，内部类直到被引用的时候才会被加载
    }
}

//枚举 since jdk1.5
enum Singleton3{
    INSTANCE;
    public void foo(){}
}