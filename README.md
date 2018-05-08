原文放在--> http://childe.net.cn/2018/04/28/%E7%8A%B6%E6%80%81%E6%9C%BA%E9%80%89%E5%9E%8B%E7%AE%80%E8%AE%B0

## 背景

业务中涉及到一些关于单据的操作，每种单据单据都会有自己的状态，单据的一些行为受限于当前订单的状态，单据的状态直接用常量表示，业务进行前的检查部分通过if判断来检测当前单据是否可以流转到目标状态。

## 痛点

业务发展的比较快，某些单据状态不停的增加，每一次增加都需要改动业务中使用到状态的相关代码，更糟的的是这些代码可能遍布于多个类的多个方法中（散弹枪一样），不仅增加发布的风险也同时增加了测试的回归任务。

## 目的

单据的状态及状态转换与业务解耦，避免散弹一样的效果。

## 涉及到的知识点

了解以下几个知识点有助于我们更好的理解状态机，也相当于是调研状态机是否满足我们最核心的需求。

### FSM<sup>1</sup>

有限状态机（英语：finite-state machine，缩写：FSM）又称有限状态自动机，简称状态机，是表示有限个状态以及在这些状态之间的转移和动作等行为的数学模型。

[FSM图解](fsm.png "图1")

状态存储关于过去的信息，就是说：它反映从系统开始到现在时刻的输入变化。转移指示状态变更，并且用必须满足确使转移发生的条件来描述它。动作是在给定时刻要进行的活动的描述。有多种类型的动作：

- 进入动作（entry action）：在进入状态时进行
- 退出动作：在退出状态时进行
- 输入动作：依赖于当前状态和输入条件进行
- 转移动作：在进行特定转移时进行

FSM（有限状态机）可以使用上面图1那样的状态图（或状态转移图）来表示。此外可以使用多种类型的状态转移表。下面展示最常见的表示：当前状态（B）和条件（Y）的组合指示出下一个状态（C）。完整的动作信息可以只使用脚注来增加。包括完整动作信息的FSM定义可以使用状态表。

| _当前状态→_ <br> _条件↓_ | _状态A_ | _状态B_ |_状态C_|
| :--------------------:| :----: | :----: |:-----:|
| 条件X 			       | ...    | ...    |...    |
| 条件Y 			       | ...    |状态C   |...   |
| 条件Z 			       | ...    | ...    |...    |

### 状态图的基本概念<sup>2</sup>

状态图（Statechart Diagram）主要用于描述一个对象在其生存期间的动态行为，表现为一个对象所经历的状态序列，引起状态转移的事件（Event），以及因状态转移而伴随的动作（Action）。一般可以用状态机对一个对象的生命周期建模，状态图用于显示状态机（State Machine Diagram），重点在与描述状态图的控制流。

状态图有以下几类元素构成：

1. 状态（States）
2. 转移（Transitions）
3. 动作（State Actions）
4. 自身转移（Self-Transitions）
5. 组合状态（Compound States）
6. 进入节点（Entry Point）
7. 退出节点（Exit Point）
8. 历史状态（History States）
9. 并发区域（Concurrent Regions）
10. 警备条件（Guard condition）

网上介绍状态图的文章很多，给出两篇我认为不错的，里面对状态图介绍的比较详尽，内容不多。

- [UML建模之状态图-中文](http://www.cnblogs.com/ywqu/archive/2009/12/17/1626043.html)
- [状态机图-英文](https://www.uml-diagrams.org/state-machine-diagrams.html)

### 状态模式

在之前[设计模式-行为模式之State](http://childe.net.cn/2018/01/16/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F-%E7%BB%93%E6%9E%84%E6%A8%A1%E5%BC%8F%E4%B9%8BState/)的博文中有详细的介绍，不再赘述。

## 开源的状态机实现

状态机是上述对状态图定义的实现，下面几种实现遵循基本的定义，但实现的完善度不尽相同。学习时Demo放在[GitHub](https://github.com/Childe-Chen/statemachine)上，需要可取。

### [spring-statemachine](https://github.com/spring-projects/spring-statemachine)

spring-statemachine的优点官网中介绍的很清楚，不赘述，但在简单看了介绍和实现后基本就放弃了，原因如下：

- 天生依赖spring，目前最新版本的依赖到Spring框架5.0.X，而我需要引入状态机的工程目前还停留在4.2.X。
- 状态机实例较重，在官方文档给出的Demo中，推荐注解的形式注入状态机，这样难以随用随new。
- 关于上一点，其给出了采用工厂的方式解决，但根据单据的业务场景来看，缓存这些实例意义并不大。

### [squirrel-foundation](https://github.com/hekailiang/squirrel)

#### 特点

1. 代码量适中，扩展和维护相对而言比较容易

2. StateMachine轻量

StateMachine实例创建开销小，本身不支持单例复用，状态机的生命周期清晰

3. 切入点丰富

支持exit、transition、entry基本动作，转换过程留有足够的切入点。

下面是一个状态转换的过程，可以看到我们有很多可以切入的点来记录或者改变状态机的行为。比较蛋疼的是，在状态机初始化时，squirrel把初始化状态当作一个事件发布，导致会多出来一个相应的事件记录。

``` java
//第一行为初始化状态机为OffHook时，注册事件处理器所打印
15:59:09.570 [main] WARN com.cxd.squirrel.StateMachineSquirrel - Entry State OffHook
//以下是一个完整的状态转换过程
15:59:09.574 [main] WARN com.cxd.squirrel.StateMachineSquirrel - beforeTransitionBegin
15:59:09.581 [main] WARN com.cxd.squirrel.MyCondition - 自定义转换条件 isSatisfied MyContext{no='yes'}
15:59:09.581 [main] WARN com.cxd.squirrel.StateMachineSquirrel - beforeActionInvoked
15:59:09.581 [main] WARN com.cxd.squirrel.StateMachineSquirrel - exit State OffHook
15:59:09.581 [main] WARN com.cxd.squirrel.StateMachineSquirrel - afterActionInvoked
15:59:09.582 [main] WARN com.cxd.squirrel.StateMachineSquirrel - beforeActionInvoked
15:59:09.582 [main] WARN com.cxd.squirrel.StateMachineSquirrel - callMethod Transition...
15:59:11.586 [main] WARN com.cxd.squirrel.StateMachineSquirrel - afterActionInvoked
15:59:11.587 [main] WARN com.cxd.squirrel.StateMachineSquirrel - Entry State Ringing
15:59:11.588 [main] WARN com.cxd.squirrel.StateMachineSquirrel - afterTransitionCompleted
15:59:11.589 [main] WARN com.cxd.squirrel.StateMachineSquirrel - afterTransitionEnd
```

4. 支持异步

事件处理机制上squirrel和Spring-statemachine比较相似，将事件处理与产生分离，使用deque交互，通过这种方式可以支持异步，采用生产-消费的方式，让线程责任更加明确。

在我们的业务场景中所有关于单据状态流转的操作都是用户通过移动端发起，需要同步响应操作结果，所以异步在我的场景中不适用。

#### 鸡肋

1. 过于便利的设计

squirrel在设计上为了足够的便利，在注册事件处理方法时通过传入方法名来实现，框架在处理时从状态机实现类中去找这些方法，通过动态的方式调用。

``` java
builder.onEntry(States.OffHook).callMethod("entry");
```

个人认为这种方式不是很好，不具有强制约束性，编译期间难以发现错误，也不知道自定义方法的签名到底是怎样。

2. 框架的约定性太强

```
方法名为transitFrom[SourceStateName]To[TargetStateName]On[EventName]，
参数名为[MyState, MyState, MyEvent, MyContext]的方法会被添加到transition “A-(GoToB)->B”的action列表中。
当状态机从’A’到’B’且触发的event为GoToB的时候，该方法会被调用。
```
这是框架的一个约定，个人认为作为一个框架不应该有这种画蛇添足的约定，对于框架而言，这太过于约定了。

3. 难以理解的异步处理方式

看了下异步处理的过程，Spring-statemachine在把事件塞进队列后仅是提交了异步任务到Executor，业务线程便立刻返回，这和我理解的异步是一致的。

``` java
AbstractStateMachine#sendEventInternal

private boolean sendEventInternal(Message<E> event) {
	//...省略...
	//此处是一个扩展，用户可自行加业务逻辑决定是否接受当前event
	boolean accepted = acceptEvent(event);
	//在此方法中创建了一个Runnable并提交到Executor
	stateMachineExecutor.execute();
	if (!accepted) {
		notifyEventNotAccepted(buildStateContext(Stage.EVENT_NOT_ACCEPTED, event, null, getRelayStateMachine(), getState(), null));
	}
	return accepted;
}
```

而squirrel在把事件塞进队列后还需要等待消费线程消费完毕，不知道此时异步的意义体现在哪里。

另外所有的事件处理默认均有同一个线程来处理，如果用得到异步这种方式，请务必通过扩展方式定义自己的线程池。

``` java
AbstractExecutionService#doExecute

private void doExecute(String bucketName, List<ActionContext<T, S, E, C>> bucketActions) {
    final Map<ActionContext<T, S, E, C>, Future<?>> futures = Maps.newHashMap();
    for (int i=0, actionSize = bucketActions.size(); i<actionSize; ++i) {
        final ActionContext<T, S, E, C> actionContext = bucketActions.get(i);
        //...省略...
        Future<?> future = SquirrelConfiguration.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                StateMachineContext.set(instance, isTestEvent);
                try {
                    actionContext.run();
                } finally {
                    StateMachineContext.set(null);
                }
            }
        });
        futures.put(actionContext, future);
        //...省略...
    }

    for(Entry<ActionContext<T, S, E, C>, Future<?>> entry : futures.entrySet()) {
        final Future<?> future = entry.getValue();
        final ActionContext<T, S, E, C> actionContext = entry.getKey();
        try {
            logger.debug("Waiting action \'"+actionContext.action.toString()+"\' to finish.");
            if(actionContext.action.timeout()>=0) {
                future.get(actionContext.action.timeout(), TimeUnit.MILLISECONDS);
            } else {
                future.get();
            }
            logger.debug("Action \'"+actionContext.action.toString()+"\' finished.");
        } catch (Exception e) {
            //...省略...
        }
    }
}
```

### [sateless4j](https://github.com/oxo42/stateless4j)

#### 特点

1. sateless4j是C#版本FSM的Java实现，代码量很少，不到30个类。

2. StateMachine轻量，比squirrel创建实例代价还要小。

3. 支持基本的事件迁移，exit/entry action、guard、dynamic permit(根据自定义的condition来控制状态的迁移)。

#### 鸡肋

因为状态迁移模型设计过于简单，导致本身的扩展点太少。

## 小结

作出决定时我的考量：

- 上手速度

越简单越快速

- 和现有框架的兼容

只需要做代码的变动，不涉及到框架层面。

- 改动代码少

- 社区的活跃度

- 是否有公司在使用

- 使用状态机到那种地步：仅做状态判断？状态机是否和业务关联？持久化？


至此，我需要再次回顾我最初的诉求：管理状态的转换，避免散弹式的效果。结合考量点，其实sateless4j和squirrel都满足我现在的要求，但我只是想方便管理单据状态的转换，并不想基于这些状态的特性更多的改动工程中的代码，所以决定使用sateless4j。PS：一般PS都是重点啦。朋友的项目中已经在使用sateless4j。

## 参考及引用

1. [有限状态机FSM](https://zh.wikipedia.org/wiki/%E6%9C%89%E9%99%90%E7%8A%B6%E6%80%81%E6%9C%BA)
2. [UML建模之状态图](http://www.cnblogs.com/ywqu/archive/2009/12/17/1626043.html)
3. [中文版squirrel文档-部分](https://www.yangguo.info/2015/02/01/squirrel/)
4. [英文版squirrel文档-全](http://hekailiang.github.io/squirrel)
5. [别人的选型记录](http://www.timguan.net/2017/06/19/%E7%8A%B6%E6%80%81%E6%9C%BA%E5%BC%95%E6%93%8E%E9%80%89%E5%9E%8B/)