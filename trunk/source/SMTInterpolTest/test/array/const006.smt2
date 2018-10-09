(set-option :produce-proofs true)
(set-option :proof-check-mode true)
(set-option :print-terms-cse false)

(set-logic QF_ALIA)
(declare-sort U 0)
(declare-fun v () U)
(declare-fun i1 () Int)
(declare-fun i2 () Int)
(declare-fun v1 () U)
(declare-fun v2 () U)
(declare-fun a () (Array Int U))

(assert (= a (store (store ((as const (Array Int U)) v) i1 v1) i2 v2)))
(assert (= (select a i1) v))
(assert (= (select a i2) v))
(assert (not (= a ((as const (Array Int U)) v))))
(check-sat)
(get-proof)
(exit)