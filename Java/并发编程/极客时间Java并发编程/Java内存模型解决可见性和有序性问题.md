可见性、原子性、顺序性导致的问题常常会违背我们的直觉，从而成为并发编程的Bug之源，针对上述可能出现的问题，Java有其解决方案---**Java内存模型**  
# 什么是Java内存模型 
我们已经知道，导致可见性的原因是缓存，导致有序性的原因是编译优化，那解决可见性、有序性最直接的办法就是**禁用缓存和禁用编译优化**，但是这样问题
虽然解决了，我们程序的性能可能就堪忧了。  
合理的方案是**按需禁用缓存以及编译优化**，对于并发程序来说，只有程序猿在自己编程的时候能够知道何时去进行合理的缓存和编译优化禁止，所以，为了解决
可见性和有序性的问题，只需要提供给程序员按需禁用缓存以及编译优化的方法即可  

Java内存模型站在程序员的角度来看可以理解为规范了JVM如何提供按需禁用缓存和编译优化的方法。具体来说，这些方法包括 volatile、synchronized和final
三个关键字，以及六项 Happens-Before原则  

# volatile的不足  
volatile并不是Java特产的关键字，古老的C语言中也有，他的最原始语意就是禁用cpu缓存   
例如，声明一个变量 volatile int x = 0; 他表达的是：告诉编译器，对这个变量的读写不使用 cpu缓存，必须从内存中读取或者写入。这个语意看起来正确但在
实际使用的时候却会带来困惑  
```java
class VolatileExample {
    int x = 0;
    volatile boolean v = false;

    public void writer() {
        x = 42;
        v = true;
    }

    public void reader() {
        if (v == true) {
            //这里x会是多少？
        }
    }
}
```
如上代码，此时这里x的值还是有可能是0，原因就是变量x可能被cpu缓存而导致了可见性问题。这个问题在1.5版本已经被圆满解决，Java内存模型在1.5版本对volatile
使用Happens-Before规则进行了语意增强。  
# Happens-Before规则  
Happens-Before不能只通过单词的表面意义翻译   
Happens-Before表达的含义：**前面一个操作的结果对后续操作是可见的！** Happens-Before约束了编译器的优化行为，虽允许编译器优化，但是要求优化器优化后
一定遵守Happens-Before规则  
和程序员相关的规则一共有如下六条：
## 1.程序的顺序性规则  
这条规则是指在一个线程中，按照程序顺序，前面的操作Happens-Before于后续的任意操作。  
套用前面的例子，就是x=42这个操作一定Happens-Before后续的操作，即 v=true。这也比较符合单线程里面的思想：程序前面对某个变量的修改一定是对后续操作可见的  
## 2.volatile变量规则  
这条规则是指对一个volatile变量的写操作，Happens-Before于后续对这个 volatile变量的读操作  
这个规则需要和规则三一起来看  
## 3.传递性 
这条规则是指A Happens-Before B，B Happens-Before C，那么A Happens—Before C   
按照上述代码逻辑解释：  
x = 42 Happens-Before 写变量v=true ，这是规则1中的内容  
写变量v=true Happens-Before 读变量 "v==true"  
所以，x = 42 Happens-Before 读变量 "v==true"  
这意味着，如果线程B读到了“v==true”，那么线程A设置的“x=42”是对线程B可见的。也就是说，线程B能看到 “x=42”。  
这就是1.5版本对volatile语意的增强，这个增强意义重大，1.5版本并发包就是靠volatile语义来搞定可见性的  
## 4.管程中锁的规则  
这个规则是指对一个锁的解锁 Happens-Before 于后续对这个锁的枷锁  
管程：一种通用的同步原语，在Java中指的就是 synchronized，synchronized是Java里对管程的实现。管程中的锁在Java里是隐式实现的，即加解锁都是编译器帮我们实现的  
```text
synchronized(this){ //此处自动加锁
    //x是共享变量，初始值=10
    if(this.x<12){
        this.x = 12;
    }
}//此处自动解锁
```
如上述代码，可以这样理解：假设x的初始值是10，线程A执行完代码块后x的值变成了12（执行完自动释放锁），线程B进入代码块时，能够看到线程A对x的写操作，也就是线程B能够
看到x==12。   
## 5.线程start()规则  
这条是关于线程启动的。它是指线程A启动自线程B后，自线程B能够看到主线程在启动子线程B前的操作  
换句话说如果A调用线程B的start()方法（即在线程A中启动线程B），那么该start()操作 Happens-Before 于线程B中的任意操作。  
## 6.线程join()规则  
这条是关于线程等待的。  他是指主线程A等待线程B完成（主线层A通过调用子线程B的join()实现），当子线程B完成后(主线程A中join()方法返回)，主线程能够看到子线程的操作，
当然所谓看到，指的是主线程能够看到子线程对**共享变量**的操作  
# final关键字
告诉优化器这个变量可以优化得更好  
使用final关键字：**final修饰变量时，初衷是告诉编译器：这个变量生而不变，可以使劲优化！**
# 总结  
Happens-Before的语义可以理解是一种因果关系。在现实世界里，如果A事件是导致B时间的起因，那么A事件一定是先于(Happens-Before)B事件发生的，这个就是Happens-Before语义的现实理解  
在Java语言里，Happens-Before的语义本质是一种可见性，A Happens-Before B，意味着A事件对B事件是可见的，无论A事件和B事件是否发生在同一个线程里。例如A事件发生在线程1上，B事件发
生在线程2上也能看到A事件的发生。  

