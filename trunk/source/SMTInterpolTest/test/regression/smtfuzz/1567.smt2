(set-info :expect-errors 196)
(set-option :produce-unsat-cores true)
(set-logic UF)
(declare-fun v0 () Bool)
(declare-fun v1 () Bool)
(declare-fun v2 () Bool)
(declare-fun v3 () Bool)
(declare-fun v4 () Bool)
(declare-fun v5 () Bool)
(declare-fun v6 () Bool)
(declare-fun v7 () Bool)
(declare-fun v8 () Bool)
(declare-fun v9 () Bool)
(push 1)
(declare-sort S0 0)
(declare-fun v10 () Bool)
(assert (forall ((q0 S0) (q1 S0)) (not (distinct q0 q1))))
(assert (! v4 :named IP_1))
(assert (not (exists ((q2 S0) (q3 Bool) (q4 S0)) (=> (= q4 q4) (xor v0 (xor v5 v3 v7 v6 v1 v6 v7) v4 q3 v0)))))
(assert (! (not v0) :named IP_2))
(assert (not (exists ((q5 Bool) (q6 S0) (q7 S0)) (=> (or (=> v0 v8) q5 q5 q5 q5) (distinct q6 q6)))))
(assert (not (exists ((q8 S0)) v9)))
(assert (not (forall ((q9 S0) (q10 Bool)) v2)))
(assert (! (distinct (distinct (xor v5 v3 v7 v6 v1 v6 v7) v5 v10) (=> v0 v8)) :named IP_3))
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(declare-sort S1 0)
(assert (not (exists ((q11 S1) (q12 S1)) (=> v0 v8))))
(assert (! (and v3 v1 v3 v5) :named IP_4))
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(declare-fun v11 () Bool)
(assert (forall ((q13 S1) (q14 S0)) (not (distinct q14 q14))))
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(push 1)
(declare-fun v12 () Bool)
(assert (not (exists ((q15 Bool) (q16 Bool) (q17 S1) (q18 S0)) (not (=> q16 q16)))))
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(pop 1)
(assert (forall ((q19 S1)) v5))
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(assert (exists ((q20 Bool)) (not (and (distinct (xor v5 v3 v7 v6 v1 v6 v7) v5 v10) q20 q20 q20 q20 (not v0)))))
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(declare-fun v13 () Bool)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(pop 1)
(assert (not (exists ((q21 Bool)) v2)))
(assert (! v8 :named IP_5))
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_4))
(get-unsat-core)
(assert (not (forall ((q22 Bool) (q23 Bool)) (not (xor q22 q22 q22 v0 v1 v7 v4 q22)))))
(check-sat-assuming (IP_2 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(declare-fun v14 () Bool)
(assert (exists ((q24 Bool) (q25 Bool)) (not (or q24 q25 v14 q24 q25 v9))))
(assert (! (and v7 v3 v8) :named IP_6))
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_4))
(get-unsat-core)
(declare-fun v15 () Bool)
(assert (exists ((q26 Bool)) v15))
(assert (! v8 :named IP_7))
(check-sat-assuming (IP_5 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(declare-sort S0 0)
(declare-fun v16 () Bool)
(assert (forall ((q27 Bool)) (not (= v4 q27 (and v7 v3 v8) q27))))
(check-sat-assuming (IP_1 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_5))
(get-unsat-core)
(assert (not (exists ((q28 S0) (q29 S0)) v1)))
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_4))
(get-unsat-core)
(assert (forall ((q30 Bool) (q31 S0) (q32 S0) (q33 Bool)) (=> (distinct q31 q32 q32 q31 q31) (xor q33 q30 v2 q33 v1 q33 q30 v5))))
(assert (! (=> v7 v14) :named IP_8))
(check-sat-assuming (IP_3 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(declare-fun v17 () Bool)
(assert (! v5 :named IP_9))
(check-sat-assuming (IP_5 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_7 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(assert (exists ((q34 Bool)) v14))
(assert (or (forall ((q34 Bool)) v14) (exists ((q34 Bool)) (not (= v4 q34 q34)))))
(check-sat-assuming (IP_5 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(assert (not (forall ((q35 Bool) (q36 Bool)) (not (not v0)))))
(check-sat-assuming (IP_5 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(assert (exists ((q37 S0) (q38 Bool) (q39 Bool) (q40 Bool)) (not (=> q38 v9))))
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(assert (exists ((q41 S0)) v1))
(check-sat-assuming (IP_3 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_7 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_5))
(get-unsat-core)
(declare-fun v18 () Bool)
(assert (not (exists ((q42 Bool)) (not v1))))
(check-sat-assuming (IP_2 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_8))
(get-unsat-core)
(declare-sort S1 0)
(declare-fun v19 () Bool)
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_5))
(get-unsat-core)
(assert (not (exists ((q43 S1) (q44 S1)) (not (distinct q44 q43)))))
(check-sat-assuming (IP_1 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_6 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_4))
(get-unsat-core)
(push 1)
(assert (not (exists ((q45 S0) (q46 Bool) (q47 S1) (q48 S1)) (=> (distinct q45 q45 q45) (not q46)))))
(check-sat-assuming (IP_5 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_7))
(get-unsat-core)
(assert (exists ((q49 S1) (q50 Bool) (q51 Bool)) v7))
(check-sat)
(check-sat-assuming (IP_5 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_6))
(get-unsat-core)
(assert (! (or (=> v7 v14) (=> v7 v16) (xor v17 v3 v0 v18 v7 v19 v14 v6 (=> v7 v14)) (xor v17 v3 v0 v18 v7 v19 v14 v6 (=> v7 v14))) :named IP_10))
(check-sat-assuming (IP_3 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_8 IP_9))
(get-unsat-core)
(assert (exists ((q52 S1)) v7))
(check-sat-assuming (IP_7 IP_9))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(assert (exists ((q53 S0) (q54 Bool)) (not (not (=> v7 v16)))))
(assert (or (forall ((q53 S0) (q54 Bool)) (not (= v3 (not v1) (or (=> v7 v14) (=> v7 v16) (xor v17 v3 v0 v18 v7 v19 v14 v6 (=> v7 v14)) (xor v17 v3 v0 v18 v7 v19 v14 v6 (=> v7 v14))) v1 q54))) (exists ((q53 S0) (q54 Bool)) v7)))
(check-sat-assuming (IP_3 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(assert (exists ((q55 S1) (q56 S0) (q57 Bool) (q58 S0)) (=> (or (distinct (=> (not v1) v17) v19 v0 v18 v1 v1 (xor v17 v3 v0 v18 v7 v19 v14 v6 (=> v7 v14)) v14 (and v7 v3 v8) (=> (not v1) v17) v17) (or v14 v17 v14 v18 (and v7 v3 v8) v14 v15 v18 v16) (or (=> v7 v16) v6 v1 (not v1) v0 v14 (=> (not v1) v17) (and v3 v1 (and v7 v3 v8) (or v14 v17 v14 v18 (and v7 v3 v8) v14 v15 v18 v16) v4 v1 (not v1) (=> v7 v16)) v5) q57 q57 v9 q57 q57 q57) (distinct q55 q55 q55))))
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_6 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_9))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_9))
(get-unsat-core)
(assert (not (exists ((q59 S0) (q60 Bool) (q61 S0) (q62 Bool)) v19)))
(assert (! (xor v1 v5 v8) :named IP_11))
(check-sat-assuming (IP_2 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_10))
(get-unsat-core)
(check-sat-assuming (IP_6 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_6))
(get-unsat-core)
(assert (not (exists ((q63 S0) (q64 S1) (q65 Bool)) (and (or q65 q65 v1) (= q64 q64) (distinct q63 q63 q63 q63 q63)))))
(assert (or (exists ((q63 S0) (q64 S1) (q65 Bool)) (=> (distinct q64 q64 q64 q64) (= q63 q63 q63 q63 q63))) (exists ((q63 S0) (q64 S1) (q65 Bool)) (not (= q63 q63 q63)))))
(check-sat-assuming (IP_3 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat-assuming (IP_8 IP_9))
(get-unsat-core)
(assert (not (exists ((q66 S1) (q67 S1)) (not (= q66 q66 q66)))))
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_9))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_2))
(get-unsat-core)
(assert (! (or (=> v7 v16) v6 v1 (not v1) v0 v14 (=> (not v1) v17) (and v3 v1 (and v7 v3 v8) (or v14 v17 v14 v18 (and v7 v3 v8) v14 v15 v18 v16) v4 v1 (not v1) (=> v7 v16)) v5) :named IP_12))
(check-sat-assuming (IP_2 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_9))
(get-unsat-core)
(check-sat-assuming (IP_9 IP_11))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_8))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_5))
(get-unsat-core)
(assert (exists ((q68 S0)) (not (= q68 q68 q68))))
(check-sat-assuming (IP_3 IP_9))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_9))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_8))
(get-unsat-core)
(assert (not (exists ((q69 S1) (q70 Bool)) v18)))
(check-sat-assuming (IP_3 IP_7))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_1 IP_4))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_9))
(get-unsat-core)
(check-sat-assuming (IP_8 IP_10))
(get-unsat-core)
(assert (not (exists ((q71 Bool) (q72 S0) (q73 Bool)) (=> (xor q73 v14 q73) (distinct q72 q72 q72)))))
(assert (! (distinct v0 v0 v18 v4 v14) :named IP_13))
(check-sat-assuming (IP_1 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_3 IP_5))
(get-unsat-core)
(check-sat-assuming (IP_9 IP_12))
(get-unsat-core)
(check-sat-assuming (IP_7 IP_10))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_3))
(get-unsat-core)
(check-sat)
(get-unsat-core)
(check-sat-assuming (IP_3 IP_11))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_12))
(get-unsat-core)
(check-sat-assuming (IP_4 IP_9))
(get-unsat-core)
(check-sat-assuming (IP_7 IP_12))
(get-unsat-core)
(check-sat-assuming (IP_6 IP_10))
(get-unsat-core)
(check-sat-assuming (IP_5 IP_6))
(get-unsat-core)
(check-sat-assuming (IP_10 IP_11))
(get-unsat-core)
(check-sat-assuming (IP_9 IP_11))
(get-unsat-core)
(check-sat-assuming (IP_6 IP_11))
(get-unsat-core)
(check-sat-assuming (IP_2 IP_5))
(get-unsat-core)
(exit)
