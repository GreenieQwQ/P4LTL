procedure ULTIMATE.start() returns ()
modifies ;
{
    var main_~sn~0 : int;
    var __VERIFIER_assert_~cond : int;
    var main_#res : int;
    var main_~i~0 : int;
    var main_#t~post0 : int;
    var __VERIFIER_assert_#in~cond : int;

  loc0:
    havoc main_#res;
    havoc main_~sn~0, main_~i~0, main_#t~post0;
    havoc main_~i~0;
    main_~sn~0 := 0;
    main_~i~0 := 1;
    goto loc1;
  loc1:
    goto loc2;
  loc2:
    goto loc2_0, loc2_1;
  loc2_0:
    assume !(main_~i~0 <= 8);
    assume ((!(0 == main_~sn~0) && !(16 == main_~sn~0)) || 0 == main_~sn~0) || 16 == main_~sn~0;
    __VERIFIER_assert_#in~cond := (if main_~sn~0 == 16 || main_~sn~0 == 0 then 1 else 0);
    havoc __VERIFIER_assert_~cond;
    __VERIFIER_assert_~cond := __VERIFIER_assert_#in~cond;
    assume 0 == __VERIFIER_assert_~cond;
    goto loc3;
  loc2_1:
    assume main_~i~0 <= 8;
    goto loc4;
  loc3:
    assert false;
  loc4:
    goto loc4_0, loc4_1;
  loc4_0:
    assume main_~i~0 < 4;
    main_~sn~0 := main_~sn~0 + 2;
    goto loc5;
  loc4_1:
    assume !(main_~i~0 < 4);
    goto loc5;
  loc5:
    main_#t~post0 := main_~i~0;
    main_~i~0 := main_#t~post0 + 1;
    havoc main_#t~post0;
    goto loc1;
}

