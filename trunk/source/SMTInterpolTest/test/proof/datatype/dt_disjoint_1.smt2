(set-option :produce-proofs true)
(set-option :proof-check-mode true)
(set-logic QF_UFDT)
(declare-sort U 0)
(declare-datatypes ( (List 0) (Tree 0) ) (
 ( (nil) (cons (car U) (cdr List) ))
 ( (leaf (data U)) (node (left Tree) (right Tree)) )
))
(declare-const x1 List)
(declare-const x2 List)
(declare-const w1 List)
(declare-const y U)

(assert (= x1 (cons y w1)))
(assert (= x2 nil))
(assert (= x1 x2))
(check-sat)
(get-proof)
