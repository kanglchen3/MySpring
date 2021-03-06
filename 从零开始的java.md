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

​	2.1. 可见性 - 多个线程共享一个变量的时候，某个线程在本地内存空间中更改了变量，为了通知其他线程变量已被改动，会将变量写入主内存并通知其他线程

​	2.2. 原子性  - 原子性指什么： 不可分割，完整性，也即某个线程正在做某个具体业务时，中间不可以被加塞或者被分割。需要整体完整，要么同时成功，要么同时失败

​	2.3. 有序性 - 禁止指令重排 

volatile: 只保证可见性，禁止指令重排（保证有序性），但是不保证原子性： -> 如何解决：可以引用atomicInteger来解决原子性 -> CAS
    根因：
    指令“lock; addl $0,0(%%esp)”表示加锁，把0加到栈顶的内存单元，该指令操作本身无意义，但这些指令起到内存屏障的作用，让前面的指令执行完成。具有XMM2特征的CPU已有内存屏障指令，就直接使用该指令volatile方式的i++，总共是四个步骤：
    i++实际为load、Increment、store、Memory Barriers 四个操作。(先做了加法，再做内存屏障)
    内存屏障是线程安全的,但是内存屏障之前的指令并不是.在某一时刻线程1将i的值load取出来，放置到cpu缓存中，然后再将此值放置到寄存器A中，然后A中的值自增1（寄存器A中保存的是中间值，没有直接修改i，因此其他线程并不会获取到这个自增1的值）。
    如果在此时线程2也执行同样的操作，获取值i==10,自增1变为11，然后马上刷入主内存。此时由于线程2修改了i的值，实时的线程1中的i==10的值缓存失效，重新从主内存中读取，变为11。
    接下来线程1恢复。将自增过后的A寄存器值11赋值给cpu缓存i。这样就出现了线程安全问题。
    
   哪里会用到volatile：
   1. 单例模式
    DCL单例模式会用到volatile，此处是为了它的禁止指令重排。
    
    instance = new Singleton(); 可以分为一下三步完成:
    
    memory = allocate();    //1. 分配对象内存空间
    instance(memory);       //2. 初始化对象
    instance = memory;      //3. 设置instance指向刚分配的内存地址，此时instance != null
    
    由于2和3不存在数据依赖关系，而且无论重排前后程序的执行结果在单线程中并没有改变，因此这种重排优化是允许的
    所以可能出现：
    
    memory = allocate();    //1. 分配对象内存空间
    instance = memory;      //3. 设置instance指向刚分配的内存地址，此时instance != null 但是对象还没有初始化，如果此时别的线程判断不为空直接调用，程序就可能崩溃
    instance(memory);       //2. 初始化对象
   
   2. 手写读写锁
   3. CAS  ouc包     
cas: 比较并交换 compare and swap
    自旋锁  
    unsafe类: cas的核心类，由于java方法无法直接访问底层系统，需要通过本地 native 方法来访问，unsafe相当于以一个后门，基于该类可以直接操作特定内存的数据。
              Unsafe诶存在于sun.misc包中，其内部方法操作可以像C的指针一样直接操作内存，因为java中cas操作的执行依赖于unsafe类的方法
                    unsafe.getAndAddInt(this, valueoffset, 1); this:对象本身， valueoffset，内存偏移地址
              unsafe类的方法是一种系统原语，在执行过程中不允许被中断，也就是说cas是一条cpu的原子指令。不会造成所谓的数据不一致问题  
    缺点：循环时间长可能造成cpu开销大， 只能保证一个变量的原子性， 引出ABA问题    
 
synchronized: 
       
指令重排：计算机在执行程序时，为了提高性能，编译器和处理器常常会对指令做重排，一般分为以下三种：

源代码 -> 1 编译器优化的重排 -> 2 指令并行的重排 -> 3 内存系统的重排 -> 最终执行的指令
单线程环境里面确保程序最终执行结果和代码顺序执行的结果一致。
处理器在进行重排序时必须要考虑指令之间的数据依赖性。
多线程环境中线程交替执行，由于编译器优化重排的存在，两个线程中使用的变量能否保证一致性是无法确定的，结果无法预测的。