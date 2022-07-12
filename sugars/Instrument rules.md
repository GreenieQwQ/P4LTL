- > $match(\bar{e})$
  >
  > - $\bar{e}$为等式表达式组成的向量，$v$为$bv$类型的值
  >   - $\bar{e} \rightarrow e | e, \bar{e} $ 
  >   - $e \rightarrow header=v$ | $header$ != $v$
  >     - TODO：应该还包含有bv相关的加减操作
  >   - $v \rightarrow bvtype | ipmask$
  > - match
  >   - Premise
  >     - s语句为`s = while(true){body1; havocProcedure(); body2;}`
  >     - 为这个match谓词分配一个布尔变量b：$\phi = match(\bar{e})$、$BoolVar(\phi, b)$
  >     - 将表达式$\bar{e}$的结果转化为boogie的结果$\Phi$：将所有等式的结果相与即可计算match
  >       - $\Phi = Boogie(\bigwedge_{e \in \bar{e}} e)$
  >   - conclusion
  >     - s被插桩为了新的程序：$s \rightarrow while(true)\{body1; havocProcedure(); b=\Phi; body2; \}$
  
- > $modify(\bar{e})$
  >
  > - $\bar{e}$为等式表达式组成的向量，$v$为$bv$类型的值
  >   - $\bar{e} \rightarrow e | e, \bar{e} $ 
  >   - $e \rightarrow header=v$ | $header$ != $v$
  >   - $v \rightarrow bvtype | ipmask$
  > - modify
  >   - Premise
  >     - s语句为`s = while(true){body;}`
  >     - 为这个match谓词分配一个布尔变量b：$\phi = modify(\bar{e})$、$BoolVar(\phi, b)$
  >     - 将表达式$\bar{e}$的结果转化为boogie的结果$\Phi$：首先需要对应的header在包被处理后有效、并且将所有等式的结果相与进行计算
  >       - $\Phi = Boogie(\bigwedge_{e \in \bar{e}} e)$
  >       - 弃用——**目前不检查header是否为valid**：$\Phi = Boogie(\bigwedge_{h:header \in \bar{e}} valid\_after(h) \,\&\, \bigwedge_{e \in \bar{e}} e)$
  >   - conclusion
  >     - s被插桩为了新的程序：$s \rightarrow while(true)\{body;b=\Phi;\}$
  
- > $valid\_after(\bar{h})$
  >
  > - 尚未完成
  >
  > - $\bar{h}$为header组成的向量
  >   - $\bar{h} \rightarrow h | h, \bar{h} $ 
  > - valid\_after
  >   - Premise
  >     - s语句为`s = while(true){body;}`
  >     - 为这个match谓词分配一个布尔变量b：$\phi = valid\_after(\bar{h})$、$BoolVar(\phi, b)$
  >     - 将表达式$\bar{e}$的结果转化为boogie的结果$\Phi$：是否存在变量能够表征header的有效？
  >       - $\Phi = Boogie(?)$
  >   - conclusion
  >     - s被插桩为了新的程序：$s \rightarrow while(true)\{body;b=\Phi;\}$
  
- > $valid\_before(\bar{h})$
  >
  > - 尚未完成
  > - $\bar{h}$为header组成的向量
  >   - $\bar{h} \rightarrow h | h, \bar{h} $ 
  > - valid\_before
  >   - Premise
  >     - s语句为`s = while(true){body1; havocProcedure(); body2;}`
  >     - 为这个match谓词分配一个布尔变量b：$\phi = valid\_before(\bar{h})$、$BoolVar(\phi, b)$
  >     - 将表达式$\bar{e}$的结果转化为boogie的结果$\Phi$：是否存在变量能够表征header的有效？
  >       - $\Phi = Boogie(?)$
  >   - conclusion
  >     - s被插桩为了新的程序：$s \rightarrow while(true)\{body1; havocProcedure(); b=\Phi; body2; \}$
  
- > $fwd(P)$
  >
  > - 尚未完成
  > - $P$表示包被转发的port
  > - fwd
  >   - Premise
  >     - s语句为`s = while(true){body;}`
  >     - 为这个match谓词分配一个布尔变量b：$\phi = fwd(P)$、$BoolVar(\phi, b)$
  >     - 将表达式$\bar{e}$的结果转化为boogie的结果$\Phi$：是否存在变量能够表征包被转发到哪个端口？
  >       - $\Phi = Boogie(?)$
  >   - conclusion
  >     - s被插桩为了新的程序：$s \rightarrow while(true)\{body;b=\Phi;\}$
  
- > $old(header)$
  >
  > - 表达式
  > - Boogie相关对应的old expression的使用方案
  >   - 插桩到mainProcedure中即可：![image-20220710170228805](../../../../AppData/Roaming/Typora/typora-user-images/image-20220710170228805.png)

- > $ipmask$
  >
  > - 表达式
  >
  > - $[0-255].[0-255].[0-255].[0-255]/[0-32]$
  >
  >   - 基于正则匹配实现了上面的区间阈值
  >
  >   - ```
  >     IPInt = 0|1{Digit}?{Digit}?|2[0-4]?{Digit}?|25[0-5]?|[3-9]{Digit}?
  >     IP = ({IPInt}\.){3}({IPInt})
  >     MaskInt = 0|[1-2]{Digit}?|3[0-2]|[4-9]
  >     IPMask = {IP} "/" {MaskInt}
  >     ```
  >
  > - $ipmask$仅在$header = v$中作为$v$出现，因此插桩为
  >
  >   - `header[32:32-mask] == v[32:32-mask]`