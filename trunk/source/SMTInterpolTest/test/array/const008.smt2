(set-option :produce-proofs true)
(set-option :proof-check-mode true)
(set-option :print-terms-cse false)

(set-logic QF_ALIA)
(declare-sort U 0)
(declare-fun v () U)
(declare-fun w () U)
(declare-fun i () Int)
(declare-fun k () Int)
(declare-fun a () (Array Int U))

(assert (= a (store ((as const (Array Int U)) v) k w)))
(assert (not (= i k)))
(assert (not (= v (select a i))))
(check-sat)
(get-proof)
(exit)
