procedure ULTIMATE.start() returns ()
modifies ;
{
    var main_#t~nondet0 : int;
    var main_~sn~0 : int;
    var __VERIFIER_assert_~cond : int;
    var main_#res : int;
    var main_~n~0 : int;
    var main_#t~post1 : int;
    var main_~i~0 : int;
    var __VERIFIER_assert_#in~cond : int;

  loc0:
    havoc main_#res;
    havoc main_#t~nondet0, main_~sn~0, main_~n~0, main_#t~post1, main_~i~0;
    havoc main_~i~0;
    assume 0 <= main_#t~nondet0 + 2147483648 && main_#t~nondet0 <= 2147483647;
    main_~n~0 := main_#t~nondet0;
    havoc main_#t~nondet0;
    main_~sn~0 := 0;
    assume 0 <= main_~n~0 + 1000 && main_~n~0 < 1000;
    main_~i~0 := 1;
    goto loc1;
  loc1:
    goto loc2;
  loc2:
    goto loc2_0, loc2_1;
  loc2_0:
    assume !(main_~i~0 <= main_~n~0);
    assume (2 * main_~n~0 == main_~sn~0 || 0 == main_~sn~0) || (!(0 == main_~sn~0) && !(2 * main_~n~0 == main_~sn~0));
    __VERIFIER_assert_#in~cond := (if main_~sn~0 == 2 * main_~n~0 || main_~sn~0 == 0 then 1 else 0);
    havoc __VERIFIER_assert_~cond;
    __VERIFIER_assert_~cond := __VERIFIER_assert_#in~cond;
    assume 0 == __VERIFIER_assert_~cond;
    goto loc3;
  loc2_1:
    assume main_~i~0 <= main_~n~0;
    main_~sn~0 := main_~sn~0 + 2;
    main_#t~post1 := main_~i~0;
    main_~i~0 := main_#t~post1 + 1;
    havoc main_#t~post1;
    goto loc1;
  loc3:
    assert false;
}

