procedure ULTIMATE.start() returns ()
modifies ;
{
    var main_~k~0 : int;
    var main_#t~nondet1 : int;
    var #in~cond : int;
    var main_#t~nondet5 : int;
    var main_~j~0 : int;
    var main_#res : int;
    var main_#t~nondet2 : int;
    var main_#t~post3 : int;
    var main_~n~0 : int;
    var main_~i~0 : int;
    var main_#t~post6 : int;
    var main_#t~post4 : int;

  loc0:
    havoc main_#res;
    havoc main_~k~0, main_#t~nondet1, main_#t~nondet5, main_~j~0, main_#t~nondet2, main_#t~post3, main_~n~0, main_~i~0, main_#t~post6, main_#t~post4;
    havoc main_~i~0;
    havoc main_~j~0;
    havoc main_~k~0;
    havoc main_~n~0;
    assume 0 <= main_#t~nondet1 + 2147483648 && main_#t~nondet1 <= 2147483647;
    main_~k~0 := main_#t~nondet1;
    havoc main_#t~nondet1;
    assume 0 <= main_#t~nondet2 + 2147483648 && main_#t~nondet2 <= 2147483647;
    main_~n~0 := main_#t~nondet2;
    havoc main_#t~nondet2;
    assume main_~n~0 < 1000000;
    assume main_~k~0 == main_~n~0;
    main_~i~0 := 0;
    assume main_~i~0 < main_~n~0;
    main_~j~0 := 2 * main_~i~0;
    goto loc1;
  loc1:
    assume main_~j~0 < main_~n~0;
    assume main_#t~nondet5 <= 2147483647 && 0 <= main_#t~nondet5 + 2147483648;
    goto loc2;
  loc2:
    goto loc2_0, loc2_1;
  loc2_0:
    assume 0 == main_#t~nondet5;
    havoc main_#t~nondet5;
    #in~cond := (if main_~n~0 <= main_~k~0 then 1 else 0);
    havoc main_~k~0, main_~n~0;
    goto loc3;
  loc2_1:
    assume !(0 == main_#t~nondet5);
    havoc main_#t~nondet5;
    main_~k~0 := main_~j~0;
    goto loc4;
  loc4:
    goto loc5;
  loc5:
    goto loc5_0, loc5_1;
  loc5_0:
    assume !(main_~k~0 < main_~n~0);
    main_#t~post4 := main_~j~0;
    main_~j~0 := main_#t~post4 + 1;
    havoc main_#t~post4;
    goto loc1;
  loc5_1:
    assume main_~k~0 < main_~n~0;
    #in~cond := (if 2 * main_~i~0 <= main_~k~0 then 1 else 0);
    havoc main_~k~0, main_~i~0;
    goto loc3;
}

procedure __VERIFIER_assert() returns ()
modifies ;
{
    var #in~cond : int;
    var ~cond : int;

  loc3:
    ~cond := #in~cond;
    goto loc6;
  loc6:
    goto loc6_0, loc6_1;
  loc6_0:
    assume ~cond == 0;
    goto loc7;
  loc6_1:
    assume !(~cond == 0);
    return;
  loc7:
    assert false;
}

