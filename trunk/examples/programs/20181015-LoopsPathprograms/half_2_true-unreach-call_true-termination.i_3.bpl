procedure ULTIMATE.start() returns ()
modifies ;
{
    var main_~k~0 : int;
    var main_#t~nondet1 : int;
    var main_~j~0 : int;
    var __VERIFIER_assert_~cond : int;
    var main_#res : int;
    var main_#t~post3 : int;
    var main_~n~0 : int;
    var main_#t~post2 : int;
    var main_~i~0 : int;
    var __VERIFIER_assert_#in~cond : int;
    var main_#t~post4 : int;

  loc0:
    havoc main_#res;
    havoc main_~k~0, main_#t~nondet1, main_~j~0, main_#t~post3, main_~n~0, main_#t~post2, main_~i~0, main_#t~post4;
    havoc main_~n~0;
    havoc main_~i~0;
    havoc main_~k~0;
    assume 0 <= main_#t~nondet1 + 2147483648 && main_#t~nondet1 <= 2147483647;
    main_~n~0 := main_#t~nondet1;
    havoc main_#t~nondet1;
    assume main_~n~0 <= 1000000;
    main_~k~0 := main_~n~0;
    main_~i~0 := 0;
    goto loc1;
  loc1:
    goto loc2;
  loc2:
    goto loc2_0, loc2_1;
  loc2_0:
    assume !(main_~i~0 < main_~n~0);
    main_~j~0 := 0;
    assume main_~j~0 < (if !(0 == main_~n~0 % 2) && main_~n~0 < 0 then main_~n~0 / 2 + 1 else main_~n~0 / 2);
    __VERIFIER_assert_#in~cond := (if 0 < main_~k~0 then 1 else 0);
    havoc __VERIFIER_assert_~cond;
    __VERIFIER_assert_~cond := __VERIFIER_assert_#in~cond;
    assume 0 == __VERIFIER_assert_~cond;
    goto loc3;
  loc2_1:
    assume main_~i~0 < main_~n~0;
    main_#t~post2 := main_~k~0;
    main_~k~0 := main_#t~post2 + -1;
    havoc main_#t~post2;
    main_~i~0 := main_~i~0 + 2;
    goto loc1;
  loc3:
    assert false;
}

