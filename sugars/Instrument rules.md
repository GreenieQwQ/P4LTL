- > $match(\bar{e})$
  >
  > - $\bar{e}$为等式表达式组成的向量，$v$为$bv$类型的值
  >   - $\bar{e} \rightarrow e | e, \bar{e} $ 
  >   - $e \rightarrow header=v$ | $header$ != $v$
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
  >       - $\Phi = Boogie(\bigwedge_{h:header \in \bar{e}} valid\_after(h) \,\&\, \bigwedge_{e \in \bar{e}} e)$
  >   - conclusion
  >     - s被插桩为了新的程序：$s \rightarrow while(true)\{body;b=\Phi;\}$

- > $valid\_after(\bar{h})$
  >
  > - $\bar{h}$为header组成的向量
  >   - $\bar{h} \rightarrow h | h, \bar{h} $ 
  > - valid
  >   - Premise
  >     - s语句为`s = while(true){body;}`
  >     - 为这个match谓词分配一个布尔变量b：$\phi = valid\_after(\bar{h})$、$BoolVar(\phi, b)$
  >     - 将表达式$\bar{e}$的结果转化为boogie的结果$\Phi$：是否存在变量能够表征header的有效？
  >       - $\Phi = Boogie(?)$
  >   - conclusion
  >     - s被插桩为了新的程序：$s \rightarrow while(true)\{body;b=\Phi;\}$

- > $valid\_before(\bar{h})$
  >
  > - $\bar{h}$为header组成的向量
  >   - $\bar{h} \rightarrow h | h, \bar{h} $ 
  > - valid
  >   - Premise
  >     - s语句为`s = while(true){body1; havocProcedure(); body2;}`
  >     - 为这个match谓词分配一个布尔变量b：$\phi = valid\_before(\bar{h})$、$BoolVar(\phi, b)$
  >     - 将表达式$\bar{e}$的结果转化为boogie的结果$\Phi$：是否存在变量能够表征header的有效？
  >       - $\Phi = Boogie(?)$
  >   - conclusion
  >     - s被插桩为了新的程序：$s \rightarrow while(true)\{body1; havocProcedure(); b=\Phi; body2; \}$