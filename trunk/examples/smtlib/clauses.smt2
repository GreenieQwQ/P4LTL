(set-option :produce-proofs true)
(set-logic QF_UFLIA)
(declare-fun arg0 () Int)
(declare-fun arg1 () Int)
(declare-fun fmt0 () Int)
(declare-fun fmt1 () Int)
(declare-fun select_format ( Int) Int)
(declare-fun x_count ( Int) Int)
(declare-fun s_count ( Int) Int)
(declare-fun percent () Int)
(declare-fun x () Int)
(declare-fun s () Int)
(declare-fun adr_hi () Int)
(declare-fun adr_medhi () Int)
(declare-fun adr_medlo () Int)
(declare-fun adr_lo () Int)
(declare-fun distance () Int)
(declare-fun fmt_length () Int)
(assert (<= (+ (- arg1) fmt0) 0))
(assert (or (or (not (= (select_format 3) percent)) (not (= (select_format 4) s))) (= (select_format 3) percent)))
(assert (or (or (not (= (select_format 3) percent)) (not (= (select_format 4) s))) (= (select_format 4) s)))
(assert (or (not (= (s_count 3) (+ (s_count 2) 1))) (not (or (not (= (select_format 3) percent)) (not (= (select_format 4) s))))))
(assert (= arg0 (+ (- distance) fmt0)))
(assert (or (or (not (= (select_format 3) x)) (not (= (select_format 2) percent))) (= (select_format 3) x)))
(assert (or (or (not (= (select_format 3) x)) (not (= (select_format 2) percent))) (= (select_format 2) percent)))
(assert (or (not (or (not (= (select_format 3) x)) (not (= (select_format 2) percent)))) (not (= (x_count 2) (+ (x_count 1) 1)))))
(assert (or (not (= (select_format 3) percent)) (not (= (s_count 3) (s_count 2))) (not (= (select_format 4) s))))
(assert (or (or (not (= (select_format 3) percent)) (not (= (select_format 4) x))) (= (select_format 3) percent)))
(assert (or (or (not (= (select_format 3) percent)) (not (= (select_format 4) x))) (= (select_format 4) x)))
(assert (or (not (= (x_count 3) (+ (x_count 2) 1))) (not (or (not (= (select_format 3) percent)) (not (= (select_format 4) x))))))
(assert (<= (+ (- fmt1) (+ fmt0 2)) 0))
(assert (or (not (= (select_format 3) percent)) (not (= (select_format 4) x)) (not (= (x_count 3) (x_count 2)))))
(assert (<= (+ fmt1 (+ (- fmt0) (+ (- fmt_length) 2))) 0))
(assert (or (= arg1 (+ fmt0 1)) (= arg1 fmt0)))
(assert (<= (+ arg1 (+ (- arg0) (+ (- fmt_length) (+ (- distance) 5)))) 0))
(assert (<= (+ (- arg1) (+ arg0 distance)) 0))
(assert (or (not (= (s_count 2) (s_count 1))) (not (= (select_format 3) s)) (not (= (select_format 2) percent))))
(assert (or (or (not (= (select_format 1) percent)) (not (= (select_format 2) x))) (= (select_format 1) percent)))
(assert (or (or (not (= (select_format 1) percent)) (not (= (select_format 2) x))) (= (select_format 2) x)))
(assert (or (not (or (not (= (select_format 1) percent)) (not (= (select_format 2) x)))) (not (= (x_count 1) (+ (x_count 0) 1)))))
(assert (or (not (= (select_format 1) percent)) (not (= (x_count 1) (x_count 0))) (not (= (select_format 2) x))))
(assert (or (not (= (select_format 3) x)) (not (= (select_format 2) percent)) (not (= (x_count 2) (x_count 1)))))
(assert (or (or (not (= (select_format 3) s)) (not (= (select_format 2) percent))) (= (select_format 3) s)))
(assert (or (or (not (= (select_format 3) s)) (not (= (select_format 2) percent))) (= (select_format 2) percent)))
(assert (or (not (or (not (= (select_format 3) s)) (not (= (select_format 2) percent)))) (not (= (s_count 2) (+ (s_count 1) 1)))))
(assert (= distance 8))
(assert (= arg1 (+ (* 4 (s_count (+ fmt1 (- fmt0) (- 2)))) arg0 (* 4 (x_count (+ fmt1 (- fmt0) (- 2)))))))
(assert (or (= fmt1 fmt0) (= fmt1 (+ fmt0 1)) (= fmt1 (+ fmt0 4)) (= fmt1 (+ fmt0 3)) (= fmt1 (+ fmt0 2))))
(assert (= fmt_length 5))
(assert (or (or (not (= (select_format 0) percent)) (not (= (select_format 1) s))) (= (select_format 0) percent)))
(assert (or (or (not (= (select_format 0) percent)) (not (= (select_format 1) s))) (= (select_format 1) s)))
(assert (or (not (= (s_count 0) 1)) (not (or (not (= (select_format 0) percent)) (not (= (select_format 1) s))))))
(assert (= adr_lo 4))
(assert (or (not (= (select_format 0) percent)) (not (= (select_format 1) x)) (not (= (x_count 0) 0))))
(assert (or (not (= (select_format 0) percent)) (not (= (s_count 0) 0)) (not (= (select_format 1) s))))
(assert (or (or (not (= (select_format 0) percent)) (not (= (select_format 1) x))) (= (select_format 0) percent)))
(assert (or (or (not (= (select_format 0) percent)) (not (= (select_format 1) x))) (= (select_format 1) x)))
(assert (or (not (or (not (= (select_format 0) percent)) (not (= (select_format 1) x)))) (not (= (x_count 0) 1))))
(assert (= adr_medlo 2))
(assert (= adr_medhi 5))
(assert (= adr_hi 4))
(assert (or (= (select_format 1) adr_medhi) (= (select_format 0) percent) (= (select_format 4) adr_lo) (= (select_format 1) percent) (= (select_format 1) adr_hi) (= (select_format 1) s) (= (select_format 1) x) (= (select_format 3) 255) (= (select_format 1) adr_lo) (= (select_format 2) percent) (= (select_format 2) s) (= (select_format 1) adr_medlo) (= (select_format 3) adr_hi) (= (select_format 2) x) (= (select_format 3) percent) (= (select_format 4) 255) (= (select_format 2) adr_lo) (= (select_format 3) s) (= (select_format 3) x) (= (select_format 4) adr_hi) (= (select_format 2) adr_medlo) (= (select_format 4) percent) (= (select_format 1) 255) (= (select_format 4) s) (= (select_format 4) adr_medhi) (= (select_format 0) s) (= (select_format 4) x) (= (select_format 4) adr_medlo) (= (select_format 0) adr_lo) (= (select_format 2) 255) (= (select_format 0) x) (= (select_format 0) adr_medhi) (= (select_format 2) adr_medhi) (= (select_format 0) adr_medlo) (= (select_format 2) adr_hi) (= (select_format 0) 255) (= (select_format 3) adr_medlo) (= (select_format 0) adr_hi) (= (select_format 3) adr_medhi) (= (select_format 3) adr_lo)))
(assert (= percent 37))
(assert (= s 115))
(assert (or (not (= (s_count 4) (s_count 3))) (not (= (select_format 4) percent)) (not (= (select_format 5) s))))
(assert (or (or (not (= (select_format 1) percent)) (not (= (select_format 2) s))) (= (select_format 1) percent)))
(assert (or (or (not (= (select_format 1) percent)) (not (= (select_format 2) s))) (= (select_format 2) s)))
(assert (or (not (= (s_count 1) (+ (s_count 0) 1))) (not (or (not (= (select_format 1) percent)) (not (= (select_format 2) s))))))
(assert (or (or (not (= (select_format 4) percent)) (not (= (select_format 5) s))) (= (select_format 4) percent)))
(assert (or (or (not (= (select_format 4) percent)) (not (= (select_format 5) s))) (= (select_format 5) s)))
(assert (or (not (= (s_count 4) (+ (s_count 3) 1))) (not (or (not (= (select_format 4) percent)) (not (= (select_format 5) s))))))
(assert (or (not (= (select_format 1) percent)) (not (= (s_count 1) (s_count 0))) (not (= (select_format 2) s))))
(assert (= x 120))
(assert (or (not (= (select_format 4) percent)) (not (= (select_format 5) x)) (not (= (x_count 4) (x_count 3)))))
(assert (or (or (not (= (select_format 4) percent)) (not (= (select_format 5) x))) (= (select_format 4) percent)))
(assert (or (or (not (= (select_format 4) percent)) (not (= (select_format 5) x))) (= (select_format 5) x)))
(assert (or (not (or (not (= (select_format 4) percent)) (not (= (select_format 5) x)))) (not (= (x_count 4) (+ (x_count 3) 1)))))
(assert (or (not (= (select_format (+ arg1 1)) adr_medlo)) (not (= (select_format (+ arg1 2)) adr_medhi)) (not (= (select_format (+ fmt1 1)) s)) (not (= (select_format (+ arg1 3)) adr_hi)) (not (= (select_format fmt1) percent)) (not (= (select_format arg1) adr_lo))))
(assert (= fmt0 0))
(assert (or false (and (= (select_format 0) (- 19)) (= (select_format 1) (- 263)) (and (= (select_format 2) (- 255)) (and (= (select_format 3) (- 86)) (and (= (select_format 4) 255) (= (select_format 5) (- 129))))))))
(assert (or false (and (= (s_count 0) 75) (and (= (s_count 1) 1) (and (= (s_count 2) (- 255)) (and (= (s_count 3) (- 475)) (= (s_count 4) (- 737))))))))
(assert (or false (and (= (x_count 0) 16) (and (= (x_count 1) 1) (and (= (x_count 2) (- 19)) (and (= (x_count 3) (- 282)) (= (x_count 4) (- 546))))))))
(check-sat)
(exit)
