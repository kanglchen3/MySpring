一、 JUC

1. 你对volatile的理解

volatile是jvm提供的<u>轻量级</u>的同步机制: 

​	1.1 保证可见性

​	1.2 <u>不保证原子性</u>

​	1.3 禁止指令重排



2. JMM   java内存模型(java memory model)本身是一种抽象的概念，并不真实存在，它描述的是一组规则或者规范，通过这组规范定义了程序中各个变量(实例字段，静态字段和构成数组对象的元素)的访问方式

JMM关于同步的规定:

1. 线程解锁前，必须把共享变量的值刷新回主内存

2. 线程加锁前，必须读取主内存的最新值到自己的工作内存

   <img src="C:\Users\hasee\AppData\Roaming\Typora\typora-user-images\image-20201022230014969.png" alt="image-20201022230014969"  />



	3. 加锁解锁是同一把锁 

JMM三大特性: 

​	2.1. 可见性 - volatile

​	2.2. 原子性  - synchronized

​	2.3. 有序性 - volatile