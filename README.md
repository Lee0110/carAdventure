# **分布式小车合作探索地图**

## 关键字

- 黑板风格体系架构
- RabbitMq
- Redis
- AStar算法
- ...

## 运行代码步骤

1. 引入依赖

   ```xml
   <properties>
       <jedis.version>3.2.0</jedis.version>
       <amqp-client.version>5.12.0</amqp-client.version>
       <slf4j-nop.version>1.7.2</slf4j-nop.version>
   </properties>
   
   <dependencies>
       <dependency>
           <groupId>com.rabbitmq</groupId>
           <artifactId>amqp-client</artifactId>
           <version>${amqp-client.version}</version>
       </dependency>
       <dependency>
           <groupId>org.slf4j</groupId>
           <artifactId>slf4j-nop</artifactId>
           <version>${slf4j-nop.version}</version>
       </dependency>
       <dependency>
           <groupId>redis.clients</groupId>
           <artifactId>jedis</artifactId>
           <version>${jedis.version}</version>
       </dependency>
   </dependencies>
   ```

2. 在ConnectionUtil类修改为你的Rabbitmq、Redis的端口信息等。

   ```java
   public static final String CHANNEL_USERNAME = "?";
       public static final String JEDIS_PASSWORD = "?";
       public static final String CHANNEL_PASSWORD = "?";
       public static final String HOST = "?";
       public static final int JEDIS_PORT = ?;
       public static final int CHANNEL_PORT = ?;
       public static final String VIRTUALHOST = "?";
   ```

3. 提前创建好交换机carId,carDirectExchange和队列carTaskJudge,controller,workQueue,然后建立carId和carTaskJudge,controller绑定关系

   ![image](https://github.com/Lee0110/carAdventure/blob/master/images/exchanges.png)

   ![image](https://github.com/Lee0110/carAdventure/blob/master/images/queues.png)
   ![image](https://github.com/Lee0110/carAdventure/blob/master/images/binding.png)

4. 不同文件夹对应不同的组件,可以在不同的电脑上独立运行(显示组件和汽车组件放在了一起,用按钮直接增加小车,也可以通过在RabbitMq中创建一个队列来互相通信)。运行时，可以写一个Application类让其启动,我在每一个组件都写好了,根据自己需要进行修改,如果你的电脑强悍,可以开很多个导航器。如下给出一个启动一个导航器的例子。

   ```java
   public class Application {
       public static void main(String[] args) {
           Navigator navigator1 = new Navigator(true);
   
           navigator1.start();
           
           Scanner scanner = new Scanner(System.in);
           
           if (scanner.hasNext()){
               navigator1.setWork(false);
           }
       }
   }
   ```

5. 启动各个组件，在显示组件里增加障碍，增加小车，开始探索！

## 运行结果

![image](https://github.com/Lee0110/carAdventure/blob/master/images/carMoving.gif)

## 程序整体架构图与设计思路

![image](https://github.com/Lee0110/carAdventure/blob/master/images/Architecture.png)

