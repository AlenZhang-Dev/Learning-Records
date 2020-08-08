#   Zigbee 不完全整理

---------------------------------------------

对AF_dataRequest()函数进行内部代码的分析。（感觉消息队列的组织等都在这个内部完成。

几个系统事件的处理。

系统api函数

zigbee的基本概念

Halluartpoll相关函数

---

## 基本概念：

计算机网络定义：

​		1.用通信链路将分散的多台计算机、终端、外设等互联起来，使之能彼此通信，同时共享各种**硬件、软件和数据资源**，整个系统可称为计算机网络

​		2.计算机网络将地理位置不同的具独立功能的多台主机、外设或其它设备，通过通信线路进行连接，在**网络操作系统、管理软件及通信协议**的管理协调下，实现资源共享和信息传递的完整系统。

计算机网络的作用：资源共享、信息交换、协同处理。

计算机网络在逻辑上可以分为**通讯子网**和**资源子网**。按网络传输分配可以分为点对点分类和广播分类。按网络规模和覆盖范围可以分为局域网、城域网以及广域网。计算机网络的作用资源共享、信息交换、协同处理。

计算机网络的四个产生阶段：第一阶段：分散的多个终端连接到一台中心机上，典型：**美国飞机订票系统**；第二阶段：多主机通过通信链路互联，典型：**ARPANET**；第三阶段：互联互通阶段：  网络互连遵循统一的技术标准(OSI模型和TCP/IP模型)，成为第三代网络。第四阶段：高速网络阶段：光纤高速网络、多媒体网络、智能网络、无线网络，成为第四代网络。

网络协议的三要素：**语义、语法、定时**。

三网融合：计算机网络、电信网络、广播电视网络。

ZigBee网络协议架构通过分层实现，每一层相当于一个模块，且为上层提供服务，层与层之间通过**服务接入点**接口进行信息交换。

在网状网络中，每个设备都可以与在无线通信范围内的其他设备进行通信。

CC2530是8位单片机，具有**两个**串口、**21个通用I/O端口**引脚（19个4mA、2个20mA）。芯片具有8KB、16KB、32KB在线可编程的FLASH、同时具有**8KB的数据存储能力**（RAM）。具有低功耗模式、2.4GHz IEEE802.15.4兼容的RF收发器、支持精确链路指示、**不具备多路DAC转换器**。**8路输入且可配置的12位ADC**。从芯片CC2530的组成框图看，三个模块中**组成CPU及相关存储模块、外设、时钟和电源管理模块和无线模块**。

单片机最小系统包括**单片机、电源引脚、复位电路和晶振电路**组成。

无线网络发展几个阶段：

​		第一代（1G）无线系统是面向**语音的模拟**无线系统，使用FDMA技术实现。	第二代（2G）无线系统是面向**语音的数字**无线系统，使用TDMA或窄带CDMA技术实现。第三代（3G）无线系统把**蜂窝电话、PCS语音业务以及移动数据业务**用各种分组交换数据业务综合在一个高语音质量、高容量、高速率的网络系统中。

Zigbee无线通信技术的特点：Zigbee无线技术是一种新兴的**短距离、低复杂度、低功耗、低数据速率、低成本**的技术。

Zigbee适合传输**低反应时间、周期性、间歇性**的数据，**不适合传输高延迟**的数据。

依据**IEEE802.15.4**标准建立。可使用的频段有三个：2.4GHZ、欧洲868MHZ、美国915MHZ，不同频段可用的信道分别是16、1、10个。短地址14位，长地址64位。Zigbee最多可支持240个设备。非开源意思是看不见源码，只提供函数借口（API）

分层（key）：从下到上：物理层  媒体访问控制层（MAC）  「**IEEE802.15.4定义**」 网络层 应用层「**Zigbee联盟定义**」

> 对各层的基本功能了解：
>
> 物理层：定义了无线信道和MAC子层之间的**接口**，提供了物理层数据服务和管理服务。「**激活和关闭射频收发器/对当前信道进行能量检测/对收发的包进行链路质量指示/收发数据和空闲信道评估/信道选择/数据单元收发/向MAC层提供管理服务接口**」
>
> MAC层：**MAC层提供网络层和物理层之间的接口**。「实现对从基本物理层数据单元中提取MAC层数据包的进一步处理，并**发送信标**，利用信标与父节点同步，**能量检测**，主动、被动、孤立扫描机制，关联和退出关联，CSMA/CA冲突避免信道访问控制机制，时隙划分， MAC层数据传输及安全机制等功能。」
>
> 网络层(核心层):**利用IEEE 802.15.4标准使MAC子层正确工作，并为应用层提供服务接口**。「主要负责网络层协议数据单元收发、网络管理和路由管理。**网络管理**主要包括网络启动、设备请求加入/离开网络、网络发现、网络地址分配等。 **路由管理**包括邻居节点发现、路由发现、路由维护、消息单播、多播、广播实现等」
>
> 应用层：包括应用支持子层（APS）、应用框架（AF）、 ZigBee设备对象（ZDO）及ZDO管理平台。应用支持子层（APS），作为应用层的一个组成部分，它提供了网络层（NWK）和应用层（APL）之间的接口。  「**应用支持子层**负责应用层协议数据单元数据的传输、设备绑定表创建和维护、组表的管理和维护、数据可靠传输等。**应用框架**「AF层」主要为方便程序员进行开发而在ZigBee设备中为所实现的应用对象提供的模板。**应用对象**就是使用ZigBee联盟制定的Profile进行开发的，并在协议栈上运行的应用程序。**安全服务层**安全服务向NWK层和APS提供了安全服务，主要完成一些加密工作，包括密钥建立、密钥传输、帧保护和设备管理。』

设备分类：

​		**全功能设备**支持 IEEE802.15.4标准定义的所有功能和特性，并拥有较多的存储资源、计算能力。**半功能设备**只支持标准定义的一部分，功能简单。        	

​		ZigBee 网络中的设备按照各自**作用不同**可以分为**协调器节点、路由器节点和终端节点**。全功能设备既可以作为协调器、路由器，也可以作为终端；半功能设备只能作为终端。

协调器（key）：

​		**负责网络的启动，配置网络使用的信道和网络标识符（PAN ID），完成网络成员的地址分配、节点绑定、建立安全层等任务。**协调器是网络的第一个设备，是整个网络的中心。一个ZigBee网络只有一个网络协调器。当网络建立成功后，协调器便充当路由器的角色。　

 路由器（key）：

​		**主要实现允许设备加入网络、扩展网络覆盖的物理范围和数据包路由的功能。一般处于活跃状态，由主电源供电** 「扩展网络是指该设备可以作为网络中的潜在父节点，允许更多的路由和终设备接入网络。路由器最为重要的功能是“允许多跳路由”。路由节点存储路由表、负责寻找、建立及修复数据包路由路径。」

终端设备：

​		通过ZigBee协调器或者ZigBee路由器接入到网络中，主要负责数据采集或控制功能，不允许其他节点通过它加入到网络中。多数时间处于休眠状态，**电池供电**。

组网过程：

​		**组建网络**：节点上电，判断是否是全功能设备->判断是否已经加入到其他网络->信道扫描，选择合适的信道->设置PAN-ID和协调器短地址，网络初始化成功，等待其他节点加入网络。         

​         **节点加入网络**：通过全功能节点加入网络。分为首次加入网络和再次入网，再次入网采用孤儿扫描。

网状/树形/星形网络三者比较：自行扩展。

Zigbee相关技术：

**节点**：一个节点包含一组设备，并对应一个无线信号收发器，只能使用一个无线通信信道。「**一个节点对应一个设备**」   　　

端点：应用对象驻留的地方，是协议栈应用层的入口，为实现一个设备描述而定义的一组群集。一个设备最多支持**240个**用户自定义端点。「0分配给ZDO，用与设备管理，255用于广播，241-254保留扩展」

群集（Cluster）：一个端点可以具有多个群集，使用群集号（Cluster ID）分配。分为输入群集和输出群集，多个节点通过群集号位不同端点建立一个逻辑连接，即绑定（Bonding）「**群集是属性的集合，包含一个或多个属性**」簇的含义指设备或应用对象之间的消息。

规约（Profile）：相同应用对象采用的所有群集的集合称为规约。目的是为了制定标准，兼容不同制造商之间的产品，说明了设备类型、接口等。还有一种协议栈规约（Zigbee和Zigbee Pro），定义**网络类型、网络深度**等。

节点地址：

​		扩展地址：IEEE地址，64位，设备商固化在设备中。短地址：称为**网络地址**，由协调器进行分配，**通过端点号+短地址确定一个终端**。

PANID：**网络标号**。不同PANID代表不同网络。PANID16位，PANID为

时，协调器随机选择一个PANID作为网络标号建立网络，终端随机选择一个信号最强的网络加入网络。

事件：系统定义事件和用户定义事件，16位独热码。

间接通信/直接通信：间接通信指各个节点通关端点的**绑定**建立通信关系。直接通信需要获取对方的地址。

绑定：一种多个应用设备之间信息流的控制机制。

数据传送方式：单点传送（Unicast）：将素举报发送给一个已知网络地址的设备。广播传送（Indirect）：应用程序需要将数据包发送给网络的每一个设备广播传送的目标地址：**0xFFFF**--传送到网络上的所有设备。**0xFFFC**--只发送给协调器和路由器， **0xFFFD**--数据包传送到网络上所有非睡眠设备。

LQI；链路信号质量。最差0x00-最强0xff，越大越好

RSSI信号强度，越小越好。

Zigbee协议栈任何数据利用帧格式来组织，每层都有特定的帧结构。（分为键值对和报文，报文适合传递数据量大的消息，支持任何数据传输）

Zigbee具有两种休眠模式：轻度休眠「定时器休眠」和深度休眠。

```
应用框架（AF）主要为方便程序员进行开发而在ZigBee设备中为所实现的应用对象提供的模板，由设备对应的（ 端口号和短地址）两部分组成。
ZigBee协议栈的系统轻度休眠模式时一个预定延时后不能唤醒执行任务。（F）
ZigBee协议栈的系统进入深度休眠模式，需要一个外部触发来唤醒设备。
ZigBee协议栈的端点，包括任务号、端点号、简单描述符、潜藏周期四项。
ZigBee协议栈中定义的设备状态类型devStates_t中，不包含终端节点状态。（F）
ZigBee协议栈的两种休眠模式：真休眠（定时器休眠）和假休眠。（F）
在ZigBee协议中如果一个消息来到了节点上，是通过（ 终端「不是端点）来投递消息到某一个具体的应用程序对象
不同的ZigBee协议栈版本使用不同的地址分配方案，ZigBee 2007使用（随机分配地址的方案）
在ZigBee协议中，（全功能节点）能够承担协调器和路由器的功能。
在ZigBee协议中，（应用程序对象）是指针对目标物理量或事件都会提供控制或测量功能，实际的通信实体
？ 在ZigBee协议中，事件分为系统级事件和任务级事件，系统级事件可以（在任务间传递的事件），任务级事件在任务内使用的事件。
ZigBee协议建立路由的原则是（按照需要建立路由），既有优点也有缺点，优点是节省资源，缺点是实时性不好。
在Zigbee中，OSAL指的是操作系统抽象层。
使用ZigBee协议栈编程，每个任务有一个任务号，有一个初始化函数和一个事件处理函数，（端口）提供的其他函数都会被这两个函数直接或间接地调用到。
使用ZigBee协议栈编程，（簇）仅提供一个消息队列及消息处理的功能。
函数osalInitTasks( void )初始化了事件队列。
ZigBee协议中，端点描述符结构体endPointDesc_t的成员变量有端点号endPoint、简单描述符SimpleDescriptionFormat_t、网络发送速度afNetworkLatencyReq_t和（指向任务ID即Task_ID的指针）
当PAN_ID设置为0x1234时，协调器（使用0x1234作为网络标号，建立网络）路由器和终端（路由器和终端节点只能加入网络标号为0x1234的网络）
链接器控制文件所在的文件夹（Tools）
！从代码角度看，一个任务是由一个（初始化函数）和一个事件处理函数组成的。
任务ID，又叫任务号，每个任务都有自己的消息队列，任务号的作用是（事件发生时，使用该任务号获取消息）
```

ISO/OSI参考模型：物理层、链路层、网络层、传输层、会话层、表示层和应用层

TCP/IP参考模型：网络接入层，网际互联层，传输层，应用层



----



## C++跨平台调用

> 一般用于将C++代码以标准C形式输出（即以C的形式被调用），这是因为C++虽然常被认为是C的超集，但是C++的编译器还是与C的编译器不同的。C中调用C++中的代码这样定义会是安全的。 

```c
#ifdefined(__cplusplus)||defined(c_plusplus) //跨平台定义方法

extern "C"{

\#endif

简单的用在windows下可以如下定义:

\#ifdef   __cplusplus

extern "C"{

//... 正常的声明段

}

#endif
```



## 协议栈

> APP：应用层目录，这是用户创建各种不同工程的区域，在这个目录中包含了应用层的内容和这个项目的主要内容，在协议栈里面一般是以操作系统的任务实现的。
>
> HAL：硬件层目录，包含有与硬件相关的配置和驱动及操作函数。
>
> MAC：MAC 层目录，包含了MAC 层的参数配置文件及其MAC 的LIB 库的函数接口文件。
>
> MT： 监控调试层，主要用于调试目的，即实现通过串口调试各层，与各层进行直接交互。
>
> NWK：网络层目录，含网络层配置参数文件及网络层库的函数接口文件，APS 层库的函数接口。
>
> OSAL：协议栈的操作系统。
>
> Profile：AF 层目录，包含AF 层处理函数文件。
>
> Security：安全层目录，安全层处理函数接口文件，比如加密函数等。
>
> Services：地址处理函数目录，包括着地址模式的定义及地址处理函数。
>
> Tools：工程配置目录，包括空间划分及ZStack 相关配置信息。
>
> ZDO：ZDO 目录。
>
> ZMac： MAC 层目录，包括MAC 层参数配置及MAC 层LIB 库函数回调处理函数。
>
> ZMain：主函数目录，包括入口函数main（）及硬件配置文件。
>
> Output：输出文件目录，这个自动生成的。
>
> ZMain函数中，一方面进行系统的初始化，一方面对事件进行轮循。

## Zstack的工作原理图

![Zstack工作原理](C:\Users\83802\iCloudDrive\Zstack工作原理.png)

Zstack运行流程图如上。综上，Zstack最重要的两个步骤即事件的初始化以及事件的处理，其余各层的初始化Ti公司基本已经为你做完了。

用户自定义事件初始化：

Main -> oasl_init_system()

## OSAL工作机制

### OSAL任务初始化

oaslInitTask()函数中对任务进行初始化，在此创建**任务表**。

```c
void osalInitTasks( void )
{
 	uint8 taskID = 0;
	tasksEvents = (uint16 *)osal_mem_alloc( sizeof( uint16 ) * tasksCnt);
  osal_memset( tasksEvents, 0, (sizeof( uint16 ) * tasksCnt));
  macTaskInit( taskID++ );
  nwk_init( taskID++ );
  Hal_Init( taskID++ );
#if defined( MT_TASK )
  MT_TaskInit( taskID++ );
#endif
  APS_Init( taskID++ );
#if defined ( ZIGBEE_FRAGMENTATION )
  APSF_Init( taskID++ );
#endif
  ZDApp_Init( taskID++ );
#if defined ( ZIGBEE_FREQ_AGILITY ) || defined ( ZIGBEE_PANID_CONFLICT )//ip冲突和跳频相关
  ZDNwkMgr_Init( taskID++ );
#endif
  //task_init function
  DNUI_SampleApp_Init(taskID);
}
//任务初始化函数如上，更具taskCnt的数量申请对应的事件表，两者通过taskID一一对应，然后对各层定义进行初始化，taskID越小，其优先级越高。这个函数可用于自定义相关的任务。例：DNUI_SampleApp_Init（）,对应用户自定义的初始化函数，用户需要用到的相关初始化都在其内定义。
```

> OSAL通过轮询查看任务表判断各层有无事件发生。

### OSAL如何处理事件

```c
//操作系统启动函数
void osal_start_system( void )
{
#if !defined ( ZBIT ) && !defined ( UBIT )
  for(;;)  // Forever Loop
#endif
  {
    osal_run_system();
  }
}
//可见操作系统通过for无限循环osal_run_system()

```

进入oasl_run_system( )一探究竟

```c
//操作系统运行函数
void osal_run_system( void )
{
  uint8 idx = 0;
//扫描哪个时间被触发，标志位置一
  //更新定时器
  osalTimeUpdate();
  //查看硬件方法是否有事件发生
  Hal_ProcessPoll();//硬件初始化轮询调用

 //从最高优先级任务一次判断每个任务是否有事件的触发，有则跳出循环
  do {
    if (tasksEvents[idx])  // Task is highest priority that is ready.
    {
      break;
    }
  } while (++idx < tasksCnt);

  //idx小于任务数量，即有时间发生
  
  //so hard to understand
  if (idx < tasksCnt)
  {
    uint16 events;
    halIntState_t intState;

    HAL_ENTER_CRITICAL_SECTION(intState);//关中断，防止干扰
    events = tasksEvents[idx];//调用事件去处理函数，coz taskArr是一个函数指针，
    tasksEvents[idx] = 0;  // Clear the Events for this task。表示此类任务事件
    //执行finsih，在事件处理函数中又调用消息处理函数
    HAL_EXIT_CRITICAL_SECTION(intState);

    activeTaskID = idx;//设置activeTaskID的作用？
    events = (tasksArr[idx])( idx, events );//核心中的核心，taskArr和tasksEvent中的idx相同，返回处理过事件的events
    activeTaskID = TASK_NO_TASK;

    HAL_ENTER_CRITICAL_SECTION(intState);
    tasksEvents[idx] |= events;  // Add back unprocessed events to the current task./将处理过的events对应的tasksEvents位清0
    HAL_EXIT_CRITICAL_SECTION(intState);
  }
  
 //定义低功耗，则进入低功耗模式
#if defined( POWER_SAVING )
  else  // Complete pass through all task events with no activity?
  {
    osal_pwrmgr_powerconserve();  // Put the processor/system into sleep
  }
#endif
//定义协同操作，进入协同操作
  /* Yield in case cooperative scheduling is being used. */
#if defined (configUSE_PREEMPTION) && (configUSE_PREEMPTION == 0)
  {
    osal_task_yield();
  }
#endif
}
//函数设计非常巧妙，首先调用定时器和硬件初始化轮询函数。
//通过do...while...循环，对事件进行查看，一旦有事件发生便跳出循环，进行事件处理，把事件存入event中并调用taskArr[idx]（idx,events）进行对应事件的函数处理
```

> Hal_ProcessPoll( )；poll在linux中相当于把文件挂起，此函数可能有这个意思；

进入事件处理函数TaskArr[].

> taskArr是一个存放各事件处理函数的指针数组。存放对应taskid上产生事件的事件处理函数。

```c
const pTaskEventHandlerFn tasksArr[] = {
  macEventLoop,
  nwk_event_loop,
  Hal_ProcessEvent,
#if defined( MT_TASK )
  MT_ProcessEvent,
#endif
  APS_event_loop,
#if defined ( ZIGBEE_FRAGMENTATION )
  APSF_ProcessEvent,
#endif
  ZDApp_event_loop,
#if defined ( ZIGBEE_FREQ_AGILITY ) || defined ( ZIGBEE_PANID_CONFLICT )
  ZDNwkMgr_event_loop,
#endif
  DNUI_SampleApp_ProcessEvent
};
//以上定义了各层task的事件处理函数。其中最后的DNUI_SampleApp_ProcessEvent;调用的是用户自定义的事件处理函数。用户对于系统或各自定义事件产生的动作都在此定义
```

```c
uint16 DNUI_SampleApp_ProcessEvent( uint8 task_id, uint16 events )
{
  afIncomingMSGPacket_t *MSGpkt;  //定义一个接受消息的指针
  if ( events & SYS_EVENT_MSG )
  {
   MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( task_id );//将对应taskid上的消息队列中的消息取出进行处理
    while ( MSGpkt )//有事件接受
    {
      switch ( MSGpkt->hdr.event )//根据消息头判断，如果接收到了无线数据
      {
         //无线接受消息，A设备用AF_DataRequest()发送消息，b设备街道消息触发此事件
        case AF_INCOMING_MSG_CMD: //incoming msg type megs
        if(DNUI_SampleApp_NwkState == DEV_ZB_COORD)//若为协调器事件
        {//--start--协调器接收数据的处理逻辑  
         HalUARTWrite(0,MSGpkt->cmd.Data,MSGpkt->cmd.DataLength-1);       
          //--end--协调器接收数据的处理逻辑           
        }else if(DNUI_SampleApp_NwkState == DEV_END_DEVICE)
        {//--start--终端设备接收数据的处理逻辑                  
          //--end--终端设备接收数据的处理逻辑          
        }else if(DNUI_SampleApp_NwkState == DEV_ROUTER)
        {
          //--start--路由器接收数据的处理逻辑           
          //--end--路由器接收数据的处理逻辑          
        }
        break;   
      //网络状态改变
      case ZDO_STATE_CHANGE:  //ZDO has changed the device's network state  
        DNUI_SampleApp_NwkState = (devStates_t)(MSGpkt->hdr.status);//status
        
        if(DNUI_SampleApp_NwkState == DEV_END_DEVICE)
        { //--start--终端设备接收数据的处理逻辑  
            osal_start_timerEx(task_id,DNUI_SAMPLEAPP_ENDDEVICE_PERIODIC_MSG_EVT,1000);          
          //--end--终端设备接收数据的处理逻辑            
        } else if(DNUI_SampleApp_NwkState == DEV_ZB_COORD)
        {//--start--协调器接收数据的处理逻辑           
          //--end--协调器接收数据的处理逻辑            
        }else if(DNUI_SampleApp_NwkState == DEV_ROUTER)
        {
          //--start--路由器接收数据的处理逻辑              
          //--end--路由器接收数据的处理逻辑          
        }        
        break;
      default:
        break;
      }      
      //消息处理完后，释放消息所占据的内存空间，在zigbee中接收到的消息放在堆上，
      //需要用deallocate函数将其内存释放，否则容易导致内存泄露
      osal_msg_deallocate( (uint8 *)MSGpkt );      
      //WHY DNUI_sampleAppTaskID
      MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( DNUI_SampleAppTaskID);
    }   
    return (events ^ SYS_EVENT_MSG);//SYS_EVENT_MSG =0X8000，系统事件
    //按理解当事件处理完毕后将事件对应位清0,解封装的过程，有事件的时候就将对应出置1
  }   
    //若产生用户自定义事件，在此处理
   if ( events & DNUI_SAMPLEAPP_ENDDEVICE_PERIODIC_MSG_EVT )  //0x01
    {  
     //发送设置，数据发送可以使用AF_DataRequest函数实现，该函数调用协议栈里与硬件相关的函数
        AF_DataRequest( &Coor_Addr, 
                            &DNUI_SampleApp_epDesc,
                            DNUI_SAMPLEAPP_DATATEST_CLUSTER_ID,
                            3, //Lab6
                            buf,
                            &DNUI_SampleApp_TransID,//任务id号指针
                            AF_DISCV_ROUTE,//有效位掩码发送选项
                            AF_DEFAULT_RADIUS );
        osal_start_timerEx( task_id, DNUI_SAMPLEAPP_ENDDEVICE_PERIODIC_MSG_EVT,1000);  
    return (events ^ DNUI_SAMPLEAPP_ENDDEVICE_PERIODIC_MSG_EVT);
   } 
  return 0;
}
```

> 通过Osal_set_event(taskid , flag)函数将相应的标志位置1，在之后的协议栈运行过程中就会根据需要调用这个函数。

此函数为Osal的核心函数，包含很多重要的结构和函数。

afIncomingMSGPacket_t *MSGpkt

> 一个消息包的结构内容

```c
typedef struct
{
  osal_event_hdr_t hdr;     /* OSAL Message header */
  uint16 groupId;           /* Message's group ID - 0 if not set */
  uint16 clusterId;         /* Message's cluster ID */
  afAddrType_t srcAddr;     /* Source Address, if endpoint is STUBAPS_INTER_PAN_EP,
                               it's an InterPAN message */
  uint16 macDestAddr;       /* MAC header destination short address */
  uint8 endPoint;           /* destination endpoint */
  uint8 wasBroadcast;       /* TRUE if network destination was a broadcast address */
  uint8 LinkQuality;        /* The link quality of the received data frame */
  uint8 correlation;        /* The raw correlation value of the received data frame */
  int8  rssi;               /* The received RF power in units dBm */
  uint8 SecurityUse;        /* deprecated */
  uint32 timestamp;         /* receipt timestamp from MAC */
  uint8 nwkSeqNum;          /* network header frame sequence number */
  afMSGCommandFormat_t cmd; /* Application Data */
} afIncomingMSGPacket_t;
----------------------------------------------------------------------
typedef struct
{
  uint8  event;
  uint8  status;
} osal_event_hdr_t;


typedef struct
{
  union
  {
    uint16      shortAddr;
    ZLongAddr_t extAddr;
  } addr;
  afAddrMode_t addrMode;
  uint8 endPoint;
  uint16 panId;  // used for the INTER_PAN feature
} afAddrType_t;

// Generalized MSG Command Format
typedef struct
{
  uint8   TransSeqNumber;
  uint16  DataLength;              // Number of bytes in TransData
  uint8  *Data;
} afMSGCommandFormat_t;
```

### AF_DataRequest（）

```c
afStatus_t AF_DataRequest( afAddrType_t *dstAddr, endPointDesc_t *srcEP,
                           uint16 cID, uint16 len, uint8 *buf, uint8 *transID,
                           uint8 options, uint8 radius )
{
}

//很重要的一个APi函数，处理过程过于复杂，只展示参数。

返回值adStatus_t = uint8
```

> *dstAddr - Full ZB destination address: Nwk Addr + End Point.
>
> 全终端节点，网络号和终端号
>
>  *srcEP - Origination (i.e. respond to or ack to) End Point Descr.
>
> //终端节点的描述符
>
>  cID - A valid cluster ID as specified by the Profile.
>
> //有效簇ID
>
>   len - Number of bytes of data pointed to by next param.
>
> //发送数据的长度
>
>  *buf - A pointer to the data bytes to send.
>
> //指向发送数据的指针
>
>   *transID - A pointer to a byte which can be modified and which will be used as the transaction sequence number of the msg.
>
>  options - Valid bit mask of Tx options.
>
>  radius - Normally set to AF_DEFAULT_RADIUS.
>
> //跳数，一般为10跳
>
>  *transID - Incremented by one if the return value is success.
>
> afStatus_t - See previous definition of afStatus_... types.

关于osal_msg_receive( )函数用于从消息队列中取出函数

```c
uint8 *osal_msg_receive( uint8 task_id )
{
  osal_msg_hdr_t *listHdr;
  osal_msg_hdr_t *prevHdr = NULL;
  osal_msg_hdr_t *foundHdr = NULL;
  halIntState_t   intState; //unsigned char

  // Hold off interrupts
  HAL_ENTER_CRITICAL_SECTION(intState);

  // Point to the top of the queue
  listHdr = osal_qHead;

  // Look through the queue for a message that belongs to the asking task
  while ( listHdr != NULL )
  {
    if ( (listHdr - 1)->dest_id == task_id )
    {
      if ( foundHdr == NULL )
      {
        // Save the first one
        foundHdr = listHdr;
      }
      else
      {
        // Second msg found, stop looking
        break;
      }
    }
    if ( foundHdr == NULL )
    {
      prevHdr = listHdr;
    }
    listHdr = OSAL_MSG_NEXT( listHdr );
  }

  // Is there more than one?
  if ( listHdr != NULL )
  {
    // Yes, Signal the task that a message is waiting
    osal_set_event( task_id, SYS_EVENT_MSG );
  }
  else
  {
    // No more
    osal_clear_event( task_id, SYS_EVENT_MSG );
  }

  // Did we find a message?
  if ( foundHdr != NULL )
  {
    // Take out of the link list
    osal_msg_extract( &osal_qHead, foundHdr, prevHdr );
  }

  // Release interrupts
  HAL_EXIT_CRITICAL_SECTION(intState);

  return ( (uint8*) foundHdr );
}

typedef struct
{
  void   *next;
  uint16 len;
  uint8  dest_id;
} osal_msg_hdr_t;


typedef struct
{
  uint8  event;
  uint8  status;
} osal_event_hdr_t;
```

由于系统检测到相关事件触发此函数，如果触发了系统事件，需要通过oasl_msg_receive()确认是否真有事件发生，如果不止一个系统事件，通过setevent函数进行再次设置。

关于 osal_start_timerEx()函数，设置该taskid对应时间的定时，没到一个定时时长触发一次任务事件。（但是这个触发设置在哪并没有找到对应的函数。

```c
uint8 osal_start_timerEx( uint8 taskID, uint16 event_id, uint16 timeout_value )
{
  halIntState_t intState; //unsigned char type
  osalTimerRec_t *newTimer;//

  HAL_ENTER_CRITICAL_SECTION( intState );  // Hold off interrupts.

  // Add timer
  newTimer = osalAddTimer( taskID, event_id, timeout_value );

  HAL_EXIT_CRITICAL_SECTION( intState );   // Re-enable interrupts.

  return ( (newTimer != NULL) ? SUCCESS : NO_TIMER_AVAIL );
}
```



## 事件的管理

### 事件管理

事件管理详见【OSAL如何处理事件】

### 几个系统事件

SYS_EVENT_MSG(0x8000),为系统强制事件，该事件主要用来发送全局的系统信息，主要有以下信息：

> 事件处理首先根据事件类号判断是何种事件，根据任务号得到消息指针pMSG,最后更具消息指针内的结构pMSG->event 来处理具体的事件。在ZcomDef.h中定义。
>
> AF_DATA_CONFIRM_CMD:数据收到确认。A设备发送数据，B设备收到数据后返回应答Ack触发此次事件。
>
> **AF_INCOMING_MSG_CMD**：Incoming MSG type message收到报文类型的消息；A设备用AF_DataRequest函数发出报文消息，B设备收到报文消息将触发AF_INCOMING_MSG_CMD事件。
>
> AF_INCOMING_KWP_CMD：收到键值对类型的消息。
>
> AF_INCOMING_GRP_KVP_CMD：收到群键值对类型的消息
>
> **KEY_CHANGE**：按键触发事件「Key Events」
>
> ZDO_NEW_DSTADDR:ZDO设备获取新地址
>
> **ZDO_STATE_CHANGE**：ZDO网络状态改变，A设备的网络状态发生改变，就会触发这个事件
>
> ZDO_MATCH_DESC_RSP_SENT：
>
> ZDO_CB_MSG：收到ZDO反馈消息。

## 关于无线节点和端点的解释

> 一个节点可以有多个端点，每个端点对应一个taskid ，每增加一个端点，需要为其配置一个新的任务id。
>
> 在zstack协议栈中，我们采用AF_DataRequest这个函数进行无线数据包发送，在AF_DataRequest函数的参数中，SampleApp_Periodic_DstAddr目的地址，目的地址中便包含了16位的短地址（协调器默认为0x0000）以及端点号endpoint。**终端将数据包发出，短地址匹配的协调器会收到这个消息（短地址匹配不对则丢弃该包），然后协议栈底层进行解析，将数据包发给协调器的对应endpoint**（不匹配则丢弃该包）。
>
> 简单来说，**endpoint是用来管理同一个节点上不同任务的工具，相当于一个分类箱，将不同功能分别存放在不同任务上，这样做的好处是规范数据包，你不用去规定第几个字节是属于哪个模块信息。**
>



端点的管理：

> 每个节点都有物理地址（MAC）和网络地址（短地址），每个节点都有241个端点，如果设备需要绑定，必须在网络层注册一个或多个端点，进行数据的发送以及绑定表的建立。

> 1、Zigbee节点(Radio unit)
>     Zigbee本身其实只是一种协议规范，落实到具体目标上无非就是一个具有2.4G射频发射接收功能和单片机功能的一块电路板(TI的2530、2430，意法半导体的STM32W108处理器都在一块芯片上集成了这两个部分，Ember和飞思卡尔也有集成的，也有一些非集成的方案)，在这块电路板上去运行Zigbee的协议，并且按照协议规范的射频频段和无线数据封包格式来在多个这样的电路板之间实现无线通信。这就引出了节点的概念(英文大致叫做Device，我这里称之为Radio unit吧)，理解起来就是这块能够实现无线通信的电路了。如果不是zigbee，而是之前的那些诸如nrf905这样的射频芯片，如果其没有协议栈支持，这个节点就是通信的全部了。
> 2、zigbee的EndPoint
>      Endpoint这个概念在Zigbee中绝对是非常重要的。**因为所有的Zigbee无线数据包都必须有一个Endpoint为目标。**那么什么是Endpoint呢？从理解的层面上来说，Endpoint是一个radio unit上的真正的数据目标。按照协议规范，0号endpoint是Zigbee device object(ZDO)用的一个端点，255号是用作广播用途，我们可以自己设定的是1～240号，其余的保留。**在一个Radio unit上可以实现多个EndPoint。**当进行无线数据收发的时候，数据包里面就必须包含**radio unit信息(设备的短地址），端点信息(destination endpoint number)**。也就是说一个radio unit在接收到一个数据包后，会在协议栈的底层进行解析，比对应该把这个数据包发给哪个endpoint，如果找不到，这个包将被丢弃。
>
> 短地址+端点号=确定一个终端节点
>
> 3、例子
>    下面我举两个例子来解释一下endpoint的问题
>    例子一：一个无线节点(radio unit)A上有一个温湿度传感器，有一个空调控制系统；另外一个无线节点B则负责接收A发回的温度数据，并通过一定的算法来控制空调系统。我们不管B如何实现，只研究A如何实现。这种情况的一个很规范的实现方式是：温湿度传感器设置一个endpoint，比如为10号；空调控制系统设置一个endpoint，比如为20号。还要说明的是：还应该为每一个endpoint建立一个任务，**这样在注册端点描述符的时候(调用afRegister函数)**，就会向协议栈底层说明处理这个端点数据的任务是谁。这样：当B想要获取温湿度的时候，**他将会发出一个包含A的短地址和10号端点的信息，这个信息到了A，协议栈会将这个消息转给10号端点所对应的task去处理，**管理空调的20号端点根本就看不到这个消息；类似地，如果B想要控制空调，他发出的数据包将包含A的短地址和20号端点信息，A收到消息后会发给20号端点的task去处理。（需要注意的是：在网络层面经常会有发给ZDO的消息，这时候信息包的端点号就将是0号)。这种将不同功能分配到不同endpoint上的方法非常有利于任务的划分，是一种很正规的方法。
>    例子二、一个无线节点(radio uint)A上有4个LED需要被控制，另外一个无线节点B则有4个开关用来控制这4个LED。这种情形的规范实现方式还是要为每一个LED设置一个endpoint(允许的范围内你任意指定，只要不重复),并为每个endpoint建立一个task。这样处理之后，B可以用同样的命令来控制4个LED，而不是每一个led 用不同的命令，这种情况在public profile实际上是必须这么做的。
>
> 上面两个例子可能很多同学认为太麻烦，完全可以变通。变通的想法就是我所有的被控对象都落在一个endpoint上，但是我发的数据包内容不同，接收端这个endpoint通过解析数据包的内容来判断具体该做什么，这种方式实际上完全可以实现，不过需要你自己规定一下数据包的格式，即第几个字节表示什么。。。。。
> 虽然这可以实现要求，但是我很不赞成这样，一方面实际上是增加了你程序设计的复杂度，另一方面完全没有了互联的可能，尤其是当你用ZCL的时候，这种方式就行不通了。
>
> [source](https://blog.csdn.net/linuxheik/article/details/8563015)

## Osal定时器组织模式

Osal_start_timer()是如何调用set_event()函数，整个协作的过程详见课程参考资料。

> 通过定时器数据链表将其组织起来，这个链表由osal_timer_update()函数来管理，由osalTimerUpdate以ms为单位对这些“软定时器”减计数，当定时器溢出，即调用osal_set_event函数。
>
> osal_start_timerEx通过osalAddTimer向链表中添加定时器，由osalTimerUpdate来减计数，当这个定时器溢出后，则会对taskID对应的task设置一个event_id，从而让这个任务在后面的主循环中运行到。能够在主循环中运行到的原因是会调用osal_set_event函数来实现主循环里对此项任务的调用
>
> ————————————————
> https://blog.csdn.net/Rhiney_97/article/details/89739491

![Timerarr](/Users/superming/Library/Mobile Documents/com~apple~CloudDocs/Pics/Timerarr.png)

```c
byte osal_start_timerEx( byte taskID, UINT16 event_id, UINT16 timeout_value )
{
halIntState_t intState;
  osalTimerRec_t *newTimer;
 
  HAL_ENTER_CRITICAL_SECTION( intState );  // Hold off interrupts.
 
  // Add timer
  newTimer = osalAddTimer( taskID, event_id, timeout_value );
  if ( newTimer )
  {
#ifdef POWER_SAVING
    // Update timer registers
    osal_retune_timers();
    (void)timerActive;
#endif
    // Does the timer need to be started?
    if ( timerActive == FALSE )
    {
      osal_timer_activate( TRUE );
    }
  }

```



osal_start_timerEx() ->osal_add_timer()

```c
typedef struct
 
{
 
  void   *next;
 
  uint16 timeout;  
 
  uint16 event_flag;
 
  uint8  task_id;
 
  uint16 reloadTimeout;
 
} osalTimerRec_t  //定时器数据链表结构体
```

## 数据发送/消息

### 串口发送函数

```c
typedef struct
{
　　bool configured; // 配置与否
　　uint8 baudRate;     // 波特率
　　bool flowControl;    // 流控制
　　uint16 flowControlThreshold;  //在RX缓存达到maxRxBufSize之前还有多少字节空余。当到达maxRxBufSize –flowControlThreshold时并且流控制打开时，会触发相应的应用事件：HAL_UART_RX_ABOUT_FULL
　　uint8 idleTimeout; // 在idleTimout 时间内RX还没有得到新的数据，将会触发相应的事件   HAL_UART_RX_TIMEOUT 
　　halUARTBufControl_t rx;// 接收
　　halUARTBufControl_t tx;// 发送
　　bool intEnable; // 中断使能
　　uint32 rxChRvdTime; // 接收数据时间
　　halUARTCBack_t callBackFunc; // 回调函数
}halUARTCfg_t;

typedef struct
{
  uint16 bufferHead;  //Rx/Tx 缓冲区中的起始字节位置的索引
  uint16 bufferTail;    // Rx/Tx 缓冲区中的末尾字节位置的索引 
  uint16 maxBufSize; // Rx/Tx 缓冲区一次最多接收或发送的字节数，当接收或者发送字节数到达该值时，产生HAL_UART_RX_FULL or HAL_UART_TX_FULL事件。
  uint8 *pBuffer;  //指向接收字节的缓冲区
}halUARTBufControl_t; 
```

### 消息接受

> 一般来说，一个消息对应一个事件。

```c
if ( events & SYS_EVENT_MSG )
  {
    MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( GenericApp_TaskID );
    while ( MSGpkt )
    {
      switch ( MSGpkt->hdr.event )
     ...
    }
    
    由以上可见，消息作为系统事件接受的。
```



> 消息的使用与事件类似，使用byte osal_msg_send(byte destination task ,byte *ptr) 触发，这个函数有两个参数分别为接收事件任务的ID，另一个为指向消息的指针，**用于向一个任务发送命令或消息数据。**

#### osal_msg_allocate()

```c
uint8 * osal_msg_allocate( uint16 len) 
{
  osal_msg_hdr_t *hdr;
  if ( len == 0 )
    return ( NULL );
 hdr = (osal_msg_hdr_t *) osal_mem_alloc( (short)(len + sizeof( osal_msg_hdr_t )) );
  if ( hdr )
  {
    hdr->next = NULL;
    hdr->len = len;
    hdr->dest_id = TASK_NO_TASK;
    return ( (uint8 *) (hdr + 1) );
  }
  else
    return ( NULL );
}
\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
typedef struct
{
  void   *next;
  uint16 len;
  uint8  dest_id;
} osal_msg_hdr_t;

由以上可知，消息由两个部分组成：消息头和消息的实际内容。消息头为协议栈定义的，消息内容我们自己添加的。
```

#### osal_mag_send()

```C
uint8 osal_msg_send( uint8 destination_task, uint8 *msg_ptr ) 
{
  if ( msg_ptr == NULL )
    return ( INVALID_MSG_POINTER );

  if ( destination_task >= tasksCnt )
  {
    osal_msg_deallocate( msg_ptr );
    return ( INVALID_TASK );
  }

  // Check the message header
  if ( OSAL_MSG_NEXT( msg_ptr ) != NULL ||
       OSAL_MSG_ID( msg_ptr ) != TASK_NO_TASK )
  {
    osal_msg_deallocate( msg_ptr );
    return ( INVALID_MSG_POINTER );
  }

  OSAL_MSG_ID( msg_ptr ) = destination_task;

  // queue message
  osal_msg_enqueue( &osal_qHead, msg_ptr );

  // Signal the task that a message is waiting
  osal_set_event( destination_task, SYS_EVENT_MSG );

  return ( SUCCESS );
}

//以上，osal_msg_engueue()为入栈操作，消息是按照队列的方式被操作的。
//osal_set_event( destination_task, SYS_EVENT_MSG );这句的定义很重要，在发送消息后，触发系统事件，暗示我们消息处理函数去哪找。
```

#### *osal_msg_receive*

```c
uint16 SampleApp_ProcessEvent( uint8 task_id, uint16 events )
{
  afIncomingMSGPacket_t *MSGpkt;

  if ( events & SYS_EVENT_MSG )
  {
    MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( SampleApp_TaskID );
    while ( MSGpkt )
    {
      switch ( MSGpkt->hdr.event )
      {
        // Received when a key is pressed
        case KEY_CHANGE:
          SampleApp_HandleKeys( ((keyChange_t *)MSGpkt)->state, ((keyChange_t *)MSGpkt)->keys );
          break;

        // Received when a messages is received (OTA) for this endpoint
        case AF_INCOMING_MSG_CMD:
          SampleApp_MessageMSGCB( MSGpkt );
          break;
          ...
      }
    }
  }
```



```C
typedef struct
{
  uint8  event;
  uint8  status;
} osal_event_hdr_t;
```

osal_event_hdr_t，包含事件发生的标志和状态，用来找到事件对应消息的。然后通过消息中的Event来判断需要进行的操作。

相关消息事件在ZComDef.h中定义





### ##消息队列的维护##

系统事件会在对应接收到消息后在对应的Event中触发。

消息的处理与使用与事件类似，使用byte_osal_msg_send(byte destination , byte_*msg_ptr)函数触发，指向一个任务发送命令或数据消息，同时出发系统消息任务。

消息是按照队列的方式进行管理的， 



## 重要函数Collection

> ASDU:Zigbee协议中APS应用服务数单元

### AF_DataRequest()

> **向指定的节点的端口发送数据。**
>
> 端点借助单项链表进行管理，用户需要使用某个端点继续通讯时，先要调用afResiger注册相应的端点向端点管理链表添加一条记录。
>
> 端点的接受过程：afIncomingData函数提取来自APS层数据包中的目标端点号，匹配对应的端点。
>
> //若配置应用ID、应用配置ID，则数据会被打包，	借助OSAL的消息机制，最终发送到相应对点应用对象的消息处理函数。

#### Question :

> 并未找到此函数在何处调用。
>
> 在老师AF_DataRequest()函数说明文档中标注：
>
> 发送数据通过在应用层调用函数void SampleApp_SendFlashMessage(uint 16 flashTime)，其参数为发送的数据。

```C
afStatus_t AF_DataRequest(   //afStatus_t == uint 8 
afAddrType_t *dstAddr,  //*dstAddr 发送网络号+端点号，即确定一个事件
endPointDesc_t *srcEP,//终端节点的描述
uint16 cID, //簇id
uint16 len, //发送数据长度
uint8 *buf, //发送数据缓冲区
uint8 *transID,//任务ID号指针
uint8 options, //有效位掩码发送选项
uint8 radius//传送跳数，常为AF_DEFAULT_RADIUS，10跳
)
```

> Uint 16 cID: 簇ID号，具体应用串ID
>
> uint8 options: 
>
> ​    #defineAF_FRAGMENTED                                0x01 
>
> ​    #defineAF_ACK_REQUEST                              0x10  //需要接收方的确认
>
> ​    #defineAF_DISCV_ROUTE                               0x20 
>
> ​    #defineAF_EN_SECURITY                               0x40 
>
>     # defineAF_SKIP_ROUTING                              0x80 



##### afAddrType_t 

```C
typedef struct 
{ 
  union 
  { 
     uint16 shortAddr; //短地址
     ZlongAddr_t extAddr;//长地址 byte ZLongAddr_t[8],8个8字节，即64位
  }addr; 
  afAddrMode_t   addrMode;//传送模式 ，广播/单点/间接...
  byte     endPoint; //端点号 1-240，区分不同端点
  uint panID //网络号  同一节点的网络号相同
}afAddrType_t; 
```

#### endPointDesc_t  端点描述集群

```c
typedef struct 
{ 
  byte endPoint;   //端点号 
  byte* task_id;   //指向任务的TASK_ID
  SimpleDescriptionFormat_t  *simpleDesc;  //简单的端点描述 
  afNetworkLatencyReq_t latencyReq; //网路延迟请求：有noLaencyReqs/fastBeacons/slowBeacons.三种模式
}endPointDesc_t; 
```

#### afAddrMode_t  地址发送模式

```c
typedef  enum//afAddrMode_t数据传送类型
{
afAddrNotPresent  =  AddrNotPresent, //间接传送，直接按照绑定表传输。
afAddr16Bit  =  Addr16Bit,//短地址传输，即点对点传输，在传输是需要设置addr.shortAddr为目的节点的16为地址
adAddr64bit = Addr64bit,//点对点，通过IEEE地址传输
afAddrGroup  =  AddrGroup,//传输在一个组内传输。地址模式设置为afAddGroup，且addr.shortAddr设置为组id。
afAddrBroadcast  =  AddrBroadcast //广播传送。
} afAddrMode_t;//数据传送类型
```

#### endPointDesc_t. 设备端点描述符

```c
typedef struct
{

  byte  endPoint;

  byte *task_id;   // Pointer to location of the Application task ID.

  SimpleDescriptionFormat_t *simpleDesc; //设备简单描述符

  afNetworkLatencyReq_t  latencyReq;

} endPointDesc_t;//设备端点描述符
```

#### SimpleDescriptionFormat_t  简单描述符群集

```c
typedef struct{ 
  byte            EndPoint;  //EP 
  uint16          AppProfId;  //应用规范ID 
  uint16          AppDeviceId; //特定规范ID的设备类型 
  byte            AppDevVer:4; //特定规范ID的设备的版本 
  byte            Reserved:4;               
  byte            AppNumInClusters;//输入簇ID的个数 
  cId_t          *pAppInClusterList;//输入簇ID的列表的指针
  byte           AppNumOutClusters; //输出簇ID的个数 
  cId_t          *pAppOutClusterList;//输出簇ID的列表 的指针
}SimpleDescriptionFormat_t; 

```

#### afStatus_t

```c
typedef enum
{
  afStatus_SUCCESS,//0x00
  afStatus_FAILED ，//0x01
  afStatus_MEM_Error,//0x10
  afStatus_INVALID_PARAMETER，//0x02
  afStatus_NO_ROUTE//0xCD
} afStatus_t;
```

#### halUARTCfg_t类型 串口设置

```c
typedef struct
{
  bool                configured;//
  uint8               baudRate;//
  bool                flowControl;//
  uint16              flowControlThreshold;//
  uint8               idleTimeout;//如果在idleTimout 时间内RX还没//有得到新的数据，将会触发相应的事件HAL_UART_RX_TIMEOUT ，这时应用可以选择读出所有RX的值或者一部分。
  halUARTBufControl_t rx;//用于控制RX缓存
  halUARTBufControl_t tx;//用于控制TX缓存
  bool                intEnable;//
  uint32              rxChRvdTime;//
  halUARTCBack_t      callBackFunc;//回调函数
}halUARTCfg_t;
```

### osal_start_timeEx()

> （一旦网络状态变化）进行任务初始化，设置任务，发送编号，设定时间
>
> 函数帮助我们登记任务，设置编号，设定定时器时间。任务即确定在哪个task，编号则确定触发什么事件，定时器则是间隔多久触发这个事件。

#### Dev_States_t

> 网络状态结构体、定义了节点各个网络状态
>

```c
typedef enum
{
  DEV_HOLD,                                // Initialized - not started automatically
  DEV_INIT,                                // Initialized - not connected to anything
  DEV_NWK_DISC,                            // Discovering PAN's to join
  DEV_NWK_JOINING,                         // Joining a PAN
  DEV_NWK_SEC_REJOIN_CURR_CHANNEL,         // ReJoining a PAN in secure mode scanning in current channel, only for end devices
  DEV_END_DEVICE_UNAUTH,                   // Joined but not yet authenticated by trust center
  DEV_END_DEVICE,                          // Started as device after authentication
  DEV_ROUTER,                              // Device joined, authenticated and is a router
  DEV_COORD_STARTING,                      // Started as Zigbee Coordinator
  DEV_ZB_COORD,                            // Started as Zigbee Coordinator
  DEV_NWK_ORPHAN,                          // Device has lost information about its parent..
  DEV_NWK_KA,                              // Device is sending KeepAlive message to its parent
  DEV_NWK_BACKOFF,                         // Device is waiting before trying to rejoin
  DEV_NWK_SEC_REJOIN_ALL_CHANNEL,          // ReJoining a PAN in secure mode scanning in all channels, only for end devices
  DEV_NWK_TC_REJOIN_CURR_CHANNEL,          // ReJoining a PAN in Trust center mode scanning in current channel, only for end devices
  DEV_NWK_TC_REJOIN_ALL_CHANNEL            // ReJoining a PAN in Trust center mode scanning in all channels, only for end devices
} devStates_t;
```

#### osalTimerRec_t结构体

```C
typedef struct
{
  void   *next;
  uint16 timeout;
  uint16 event_flag;
  uint8  task_id;
  uint16 reloadTimeout;
} osalTimerRec_t;
```

### 消息的传输

afIncomingData()函数用来从APS层传递一个ASDU到AF层，中间调用afBuildMSGIncoming()函数，为APS层建立一个特定格式的消息包，最后调用osal_msg_send()把消息传送到AF层。

处理AF层数据包的大致流程：
afIncomingData() -> adBuildMSGIncoming()->osal_msg_send()->oasl_set_event()

### setevent()

> 在tasksEvent数组中对应的taskid位置设置events

```c
uint8 osal_set_event( uint8 task_id, uint16 event_flag )
{
  if ( task_id < tasksCnt )
  {
    halIntState_t   intState;
    HAL_ENTER_CRITICAL_SECTION(intState);    // Hold off interrupts
    tasksEvents[task_id] |= event_flag;  // Stuff the event bit(s)
    HAL_EXIT_CRITICAL_SECTION(intState);     // Release interrupts
    return ( SUCCESS );
  }
   else
  {
    return ( INVALID_TASK );
  }
}
```





### AfIncomingData（）

当节点收到数据时，会调用AfIncomingData（）

### afIncomingMSGPacket_t 

> 接受发送的数据，afIncomingMSGPacket_t结构体为消息的封装内容。
>

```c
typedef struct  
{

  osal_event_hdr_t  hdr;     /* OSAL Message header */

  uint16  groupId;           /* Message's group ID - 0 if not set */

  uint16  clusterId;         /* Message's cluster ID */

  afAddrType_t  srcAddr;     /* Source Address, if endpoint is STUBAPS_INTER_PAN_EP,

                               it's an InterPAN message */

  uint16  macDestAddr;       /* MAC header destination short address */

  uint8  endPoint;           /* destination endpoint */

  uint8  wasBroadcast;       /* TRUE if network destination was a broadcast address */

  uint8  LinkQuality;        /* The link quality of the received data frame */

  uint8  correlation;        /* The raw correlation value of the received data frame */

  int8   rssi;               /* The received RF power in units dBm */

  uint8  SecurityUse;        /* deprecated */

  uint32 timestamp;         /* receipt timestamp from MAC */

  afMSGCommandFormat_t  cmd; /* Application Data */

} afIncomingMSGPacket_t;

//afIncomingMSGPacket_t  gtwRxFromNode;

 

// Generalized MSG Command Format

typedef struct  // afMSGCommandFormat_t;
{

  byte   TransSeqNumber;

  uint16 DataLength;               // Number of bytes in TransData

  byte  *Data;

} afMSGCommandFormat_t;
```

---

## 数据的发送和接受

>  对接收方需要做的两件事
>
> 1）注册一个端点，通过在XXXXApp_Init()函数里调用afRegister()来实现。**如果需要处理ZDO消息和案件消息还得调用相应的注册函数ZDO_RegisterForZDOMsg，RegisterForKeys。**
>
> 2）注册了端点和消息之后，需要写一个消息处理函数，来响应各种消息，这个函数通常叫作XXXXApp_ProcessEvent()。
>
> 对于发送来说，事情就简单了，只要调AF_DataRequest()函数，向指定的节点的端口发送数据即可。
>
> 从AF.C和AF.H可以看到端点是借助**单向链表**来管理的。用户需要使用某个端点进行通讯时，先要调用afRegister注册相应的端点向端点管理链表添加一条记录。
>
>  端点一🥚注册之后，在接收和发送两个过程中都会使用到。
>
> 在接收过程，**afIncomingData函数提取来自APS层数据包中的目标端点号，搜索节点已注册的端点号，进行匹配。**如果端点号匹配则需要进一步匹配应用配置ID，应用配置ID也匹配的语，数据包就会被打包，然后借助OSAL的消息机制，最终发送到相应端点应用对象的消息处理函数。
>
>  在发送过程，**端点信息被AF_DataRequest读取，填写到数据包相关的区域**，如果端点注册了回调函数，回调函数将在数据发送前被调用。

数据收发过程中双方必需指定端点号和网络地址，这样才能在一个节点中确定一个任务。一个端点对应一个任务，一个任务可以挂载多个端点。

**端点里可以定义多个簇，发送的时候定义哪个端点中的哪个簇。**



[？Zstack Osal定时器事件触发流程分析](https://e2echina.ti.com/question_answer/wireless_connectivity/zigbee/f/104/t/114725)

系统事件的几种类型。

系统事件触发的条件。

Zstack对于端点的管理。（单项链表）

## 



协调器和终端节点的设备号以及端点号可以都一样吗？是不是协调器有协调器的设备号和端点号，终端节点有终端节点的。二者不相干：

ProfileID必须一样

DeviceID可以不一样

Endpoint也可以不一样，但是通信的时候必须要知道目的设备的Endpoint，负责无法通信上。

>Profile ID只是一类应用的ID号，比方说智能家居，无线开关，温度传感器，doorlock，窗帘控制器等等，属于智能家居的产品，那么他们的profile ID都是一样的。智能家居是0x0104
>
>device ID是值一个profile ID下面，不同设备的id号，设置这个ID号的目的在于知道ID号，就知道这个设备具备哪些功能。
>
>比方说一个on/off Light 和一个Dimmer Light的device id是不一样的，功能不同。

## 总结

## Q：

Zstack控制在代码一样的情况下将设备分别烧成协调器、终端，路由器。

### 流程整理

如果一个节点（Radio Unit）有多个终端设置，每增加一个终端节点，设置对应的taskid，并对其进行注册，接入链表，终端节点是通过**链表**进行维护的，通过端点号对其进行查找；注册完毕，对其进行初始化，然后为每一个taskid建立一个任务处理函数，对其进行操作。







----

1、为什么网络协议栈都以分层形式实现？
2、比较常用无线网络技术Wi-Fi、ZigBee和蓝牙。

Zigbee：
1、简述协调器组建网络的过程。

> 节点上电，判断是否为全功能设备，**判断是否已经加入网络，信道扫描**，选择合适的信道，设置PANID和协调器短地址，初始化完毕，等待其他节点的加入

2、简述ZigBee协议栈中如何定义端点以及端点的作用。	

> 如何定义端点：zigbee设备最多可支持240个端点，也就说最多可以有240个应用对象。其中端点0为zigbee设备对象，端点255用于广播，240-254保留用户扩展使用。
>
> 端点作用：端点为应用层的入口，应用对象驻留的地方。~~用于描述zigbee设备属性群集的集合。~~    **为实现设备描述而定义的一组群集**

3、简述Profiles的定义及作用

>Profile是相同应用对象所采用的群集的集合。
>
>Profile即对逻辑设备及其接口描述进行规定的协议集合。是面向某个具体应用对象所采用的规定、准则
>
>Profile用于制定标准，以兼容不同厂商之间的产品

4、简述ZigBee协议栈中节点的地址类型及如何获得地址。

> 16位短地址，网络地址，入网时由协调器进行分配
>
> 64位长地址，IEEE地址，唯一的，厂家固化到芯片中的

5、ZigBee协议栈中数据传输方式有哪几种？当应用程序需要将数据包发送给网络的每一个设备时，使用广播传送(broadcast)数据传送方式，解释目标地址(0xFFFF)、(0xFFFD) 或(0xFFFC)时数据广播范围。

> 单点、广播、间接传送
>
> 广播传送，目标地址不同，传送范围不同。
>
> 0xFFFF：向网络中所有设备发送数据，对于睡眠中的节点，查找到后数据存储在其父节点，直到其唤醒或超时。
>
> ：发送到网络中活跃的设备，除了睡眠中的节点。
>
> C：发送至网络中的路由器和协调器

6.解释ZigBee协议栈中事件AF_INCOMING_MSG_CMD、事件KEY_CHANGE和事件ZDO_STATE_CHANGE的触发条件。

> 1.收到报文类型的消息，A设备通过AF_Datarequest函数给B设备发送报文消息，B设备接受到报文消息时触发该事件
>
> 按键触发
>
> 设备网络状态改变事件，

7、ZigBee协议栈中PAN ID，也称网络标号，PAN ID的作用是什么？解释将PAN ID设置位0xFFFF时，组网过程中将出现何种情况。

> 用于标识不同的网络。同一信道、不同信道...
>
> 0xffff协调器随机选择Panid组建网络
>
> 终端选择信号最强的网络加入

8、ZigBee协议栈中，解释事件的分类，如何定义事件。

> 事件分为系统事件和用户自定义事件
>
> 系统事件为协议栈定义的，用户自定义为用户定义的事件
>
> 事件采用16位独热码编制，独热码是只有1个位为1，其余位全为0的码制，有点是操作简单，只需要简单的位移就可以实现。
>
> 比如系统事件的处理
>
> 事件类号是16位，0x8000为系统定义事件，15位用户自定义事件，0x40-0x01

9、解释事件处理函数app_ProcessEvent( byte task_id, UINT16 events )如何找到待处理的事件。

> 通过event来判断是哪个事件，然后使用taskid从消息队列中取出传送的消息，将接收到的消息存储在MSGkpt的数据格式中，对该格式进行解封装，提取系统的消息MSGpkt->hdr.event来判断是何种事件。

10、在ZigBee协议栈中，任何通信数据都是利用帧的格式来组织的，协议栈的每一层都有特定的帧结构，两种帧类型各是什么？比较两种帧类型。

> kvp简直对，键值对是包含数据量较小的一种数据格式，在zigbee复杂的使用环境中，并不能很好的满足需求。（用于发送数据量较小的谁格式）
>
> msg报文：报文对数据格式不作要求，适合任何数据传输，适用于传输数据量大的数据格式。

11、解释CC2530的五种功耗模式及其功耗。 

> 接受24
>
> 发送29
>
> 电源1 4us唤醒 0.2
>
> 电源2 定器 1ma
>
> 电源3 外部终端 0.4

12、解释ZigBee两种休眠模式及其如何唤醒。

> 轻度休眠；定时器延时唤醒
>
> 重度：外部中断触发

BLE：
1、描述低功耗蓝牙的链路层五种状态的切换。

> standby：该状态不连接到任何设备，是最主要的状态，没有进行任何数据传送
>
> adversting：进入广播态的链路层可以发送报文，并且可以回应由发送报文导致的响应报文。想要进入连接态或发起态的设备需要进入广播态，同时想要向周围范围内的设备进行广播的设备也可以进入广播态
>
> scanning：扫描监听态，能够扫描进行广播的设备发送的报文，有两种自状态，主动扫描接收到广播报文后会发送扫描请求给广播设备，同时获得少量的的扫描信息，被动扫面只能接受广播报文
>
> initiating：发起态，能够主动向设备发起连接，如果接收到了广播报文，该设备会发送连接请求给广播设备同时进入连接态。如果没有接收到报文，进入待机态
>
> connected：连接态，处于广播态和发起态的设备都可以进入连接态，广播从。。

2、解释链路层状态（主机和从机）与设备的ATT角色ATT服务器或ATT客户端的关系。

> 链路层状态与蛇诶att角色ATT服务器没有直接联系，两者相互独立
>
> 也就是说主机可以为...

3、解释BLE协议栈的通用广播。

> ble使用最广泛的广播
>
> 通用广播发送的广播报文能够被处于扫描态的设备扫描到。「且在接收到连接请求的同时能够作为从设备进入一个连接」
>
> 可以在不进行连接的同时发送广播「没有主从设备之分」

4、简述作为一个BLE设备，有六种可能的状态及相应的状态描述。 

> 待机：没有连接到任何设备，没有进行任何数据交换
>
> 广播：周期性发送广播报文
>
> 扫描：扫描正在广播的设备
>
> 发起连接：主动向其他设备发起连接
>
> 主设备：以主设备进入连接 「作为主设备进入到其他连接」
>
> 从设备：以从设备进入连接



5、简述BLE协议栈下，主从机连接过程。

> 两者处于待机状态，一个设备发起广播报文，另一个设备进行扫描监听，广播设备对扫描监听进行回应，扫描监听设备收到回应发起连接，广播设备以从设备进入连接，扫描监听设备以主设备进入连接。

6、解释BLE协议栈下的连接事件及作用。

> 所有的通信，都发生在两个设备的连接事件之间。按照连接参数设定的发送间隔，进行事件的发送。
>
> 每个事件发生在不同通道内（0-36），下一次事件发生的通道由跳频增量决定。在一个连接事件中master先发送数据 slave150us后回应
>
> 即使两个进入连接的设备在事件发生时没有数据发送，双方也承认对方的存在并且处于活跃的连接状态。

7、解释BLE协议栈下Profile层次结构。

> 一个profile由一个或多个service组成。
>
> 一个service可能包含一个特征值，每个特征值必须占有一个特征声明结构，包含他的特征，他是客户端和数据端共享的数据空间。每个特征值还可能包括一个特征描述符定义，用户对特征进行描述

8、解释BLE协议栈Attribute Table的构成。

> handle 属性句柄，在属性表中的地址
>
> type：说明代表什么数据，可以是BLE委员会定义的uuid，也可以是用户自定义的uuid。
>
> permission：属性权限，定义了客户端能够访问特定的属性结构，以及已何种方式访问。
>
> value：值

9、BLE协议栈中服务可分为哪几种类型，作用分别是什么？
10、解释服务声明的含义？
11、BLE协议栈中服务的含义是什么？服务如何实现的？
12、解释规范、服务与特征的关系
13、解释特征定义的含义
14、解释特征描述符声明的含义
15、通用访问配置规范的功能


1、简述基于AP组建的基础无线网络的基本特点。
2、简述自组网的基本特点。
3、如何理解Wi-Fi的无中心网络
4、如何理解Wi-Fi的有中心网络？
5、TCP的可靠传输控制方法由哪些？ 
6、UDP协议特点有哪些？

1、物联网组网规划的原则。
2、物联网组网规划设计的步骤。

