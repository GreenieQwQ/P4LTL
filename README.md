## Build

- 参考[Installation · ultimate-pa/ultimate Wiki](https://github.com/ultimate-pa/ultimate/wiki/Installation)的build方案
- 注意在打开target编译前记得配置preference-java-compile选项，将编译器设置为jdk：![image-20220625170111681](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625170111681.png)

## 每个插件共有的接口

- Activator
  - 定义了插件名供识别
  - ![image-20220625161559473](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625161559473.png)
- 插件名.java
  - 重要的为这个函数：![image-20220625162501498](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625162501498.png)
  - 若toolchain中，前面的工具匹配case，则工具尝试基于这个case作为输入进行调用
- **插件名Oberserver.java**
  - 插件的主体实现部分，调用的流程为init->process->finish
  - init基本不需要改动，作用为初始化一些toochain的配置和变量![image-20220625163349667](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625163349667.png)
  - process基本也不需要改动，作用为遍历输入toolchain的model来初始化对应的输入参数：![image-20220625163429849](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625163429849.png)
  - finish为插件的主要实现部分，在里面基于init、process配置后的参数进行运算处理，并将输出存储在类内变量中即可![image-20220625163626395](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625163626395.png)
- PreferenceInitializer.java
  - 这里面定义了Obeserver中用户可配置的参数，在Debug.product中可以方便地配置和存储setting
  - ![image-20220625162025215](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625162025215.png)
    - label是这个setting显示的名字
    - def为这个setting的默认参数

## ThufvLTL2Aut

- 作用：从SpecLang插件中获取解析后的ltl2ba格式的ltl（fairness + property），然后结合为单个ltl后调用原有的ltl2ba
- 基本沿用了`UtopiaLTL2Aut`插件的代码
- 已基本完成了因UA代码更新造成的改动，应该基本不需要再改动这部分的代码了

## ThufvSpeclang

- 作用：

  - 从输入的Boogie文件中读取用户指明的spec（使用特定marker、基于注释标注的faireness、property、自由变量等性质）

    - 此部分沿用`UtopiaSpeclang`的代码已完成

  - **将P4LTL解析**，并且将其翻译为ltlba形式的regular ltl的String，存储在PropertyContainer类中作为输出

    - 待完成，接口如下
      - 输入为p4ltl字符串
      - 对p4ltl字符串解析
        - 参考smartltl的.cup和.lex文件解析
      - 基于解析后的ast进行boogie插桩
        - **需要结合P4Boogie的具体构造来设计插桩规则**
          - ![image-20220606114730425](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220606114730425.png)
          - 先实现match+drop？
        - 通配符的实现：如`x == 192.168.1.0/24`
          - 可以考虑基于bv进行mask操作来做？
      - 基于p4ltlast的tostring方案将p4ltl翻译为regularltl
        - 参考smartltl的实现
    - ![image-20220625184651194](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625184651194.png)
  
  - **插桩输入的Boogie文件**，因为将P4LTL翻译为regular ltl需要对Boogie文件**定义并跟踪**相关变量的值
  
    - 待完成，接口如下
  
      ![image-20220625184709983](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625184709983.png)

## BuchiAutomizer

- 作用：
  - ![image-20220625164018457](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625164018457.png)
  - 基于CEGAR尝试证明Buchi自动机是否有feasible、fair的trace
  - 我们需要改动的：
    - 参考Utopia的代码中对于事务时间步的实现，对Final state进行包时间步的定义

## Debug

- 参考ultimate的wiki运行`Debug-E4.product`即可
- ![image-20220625164849091](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625164849091.png)
- Settings可以方便地配置各个插件的参数：
  - ![image-20220625164325486](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625164325486.png)
  - ![image-20220625165336676](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625165336676.png)
- ![image-20220625165818195](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625165818195.png)
  - Save Settings可以将选项存储为epf文件（应该是eclipse preference的缩写）
  - Load settings选项可以读取这样的epf文件
- ![image-20220625165905691](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625165905691.png)
  - 点击Open Source打开一个bpl文件，然后会弹出配置toolchain的选项：![image-20220625170202673](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625170202673.png)
  - 选择`P4LTL.xml`即可：![image-20220625170229790](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625170229790.png)
  - 然后就可以看到运行后的结果：![image-20220625170338882](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220625170338882.png)

## TODO

- LTL：![image-20220518115110797](C:\Users\13566\AppData\Roaming\Typora\typora-user-images\image-20220518115110797.png)

  - 为了方便描述，加入了`if elif else then`，`if a then b`表示`a => b`、`if a then c elif b then d else e`表示

    `a => c & (!a & b => d) & (!a & !b) => e`

  - 为方便描述，加入R算子：

    > ψ **R** φ ≡ ¬(¬ψ **U** ¬φ) ( φ remains true until and including once ψ becomes true. If ψ never becomes true, φ must remain true forever.)

- Atom：

  - `match(->e)`表示在**包被处理前**，包头被匹配为相应值
    - `-> e := e | ->e, e`
    - `e := header = val`
  - `drop`表示**包被处理后**丢弃

- Exp:

  - example: `match(header=IPV4) => drop`

## Sugars

- [插桩Boogie代码思路](./Boogie instrument.md)

