type ~msg_t = int;
const #funAddr~node1.base : int;
const #funAddr~node1.offset : int;
const #funAddr~node2.base : int;
const #funAddr~node2.offset : int;
const #funAddr~node3.base : int;
const #funAddr~node3.offset : int;
const #funAddr~node4.base : int;
const #funAddr~node4.offset : int;
const #funAddr~node5.base : int;
const #funAddr~node5.offset : int;
axiom #funAddr~node1.base == -1 && #funAddr~node1.offset == 0;
axiom #funAddr~node2.base == -1 && #funAddr~node2.offset == 1;
axiom #funAddr~node3.base == -1 && #funAddr~node3.offset == 2;
axiom #funAddr~node4.base == -1 && #funAddr~node4.offset == 3;
axiom #funAddr~node5.base == -1 && #funAddr~node5.offset == 4;
var ~r1 : int;

var ~p1_old : int;

var ~p1_new : int;

var ~id1 : int;

var ~st1 : int;

var ~mode1 : int;

var ~alive1 : int;

var ~p2_old : int;

var ~p2_new : int;

var ~id2 : int;

var ~st2 : int;

var ~mode2 : int;

var ~alive2 : int;

var ~p3_old : int;

var ~p3_new : int;

var ~id3 : int;

var ~st3 : int;

var ~mode3 : int;

var ~alive3 : int;

var ~p4_old : int;

var ~p4_new : int;

var ~id4 : int;

var ~st4 : int;

var ~mode4 : int;

var ~alive4 : int;

var ~p5_old : int;

var ~p5_new : int;

var ~id5 : int;

var ~st5 : int;

var ~mode5 : int;

var ~alive5 : int;

var ~nomsg : ~msg_t;

var ~send1 : ~msg_t;

var ~send2 : ~msg_t;

var ~send3 : ~msg_t;

var ~send4 : ~msg_t;

var ~send5 : ~msg_t;

implementation ULTIMATE.start() returns (){
    var main_#res : int;
    var main_#t~nondet10 : int;
    var main_#t~nondet11 : int;
    var main_#t~nondet12 : int;
    var main_#t~nondet13 : int;
    var main_#t~nondet14 : int;
    var main_#t~nondet15 : int;
    var main_#t~nondet16 : int;
    var main_#t~nondet17 : int;
    var main_#t~nondet18 : int;
    var main_#t~nondet19 : int;
    var main_#t~nondet20 : int;
    var main_#t~nondet21 : int;
    var main_#t~nondet22 : int;
    var main_#t~nondet23 : int;
    var main_#t~nondet24 : int;
    var main_#t~nondet25 : int;
    var main_#t~nondet26 : int;
    var main_#t~nondet27 : int;
    var main_#t~nondet28 : int;
    var main_#t~nondet29 : int;
    var main_#t~nondet30 : int;
    var main_#t~nondet31 : int;
    var main_#t~nondet32 : int;
    var main_#t~nondet33 : int;
    var main_#t~nondet34 : int;
    var main_#t~nondet35 : int;
    var main_#t~ret36 : int;
    var main_#t~ret37 : int;
    var main_~c1~137 : int;
    var main_~i2~137 : int;
    var init_#res : int;
    var init_~tmp~64 : int;
    var node1_#t~ite0 : int;
    var node1_#t~ite1 : int;
    var node1_~m1~7 : ~msg_t;
    var node2_#t~ite2 : int;
    var node2_#t~ite3 : int;
    var node2_~m2~20 : ~msg_t;
    var node3_#t~ite4 : int;
    var node3_#t~ite5 : int;
    var node3_~m3~31 : ~msg_t;
    var node4_#t~ite6 : int;
    var node4_#t~ite7 : int;
    var node4_~m4~42 : ~msg_t;
    var node5_#t~ite8 : int;
    var node5_#t~ite9 : int;
    var node5_~m5~53 : ~msg_t;
    var check_#res : int;
    var check_~tmp~130 : int;
    var assert_#in~arg : int;
    var assert_~arg : int;

  loc0:
    ~r1 := 0;
    ~p1_old := 0;
    ~p1_new := 0;
    ~id1 := 0;
    ~st1 := 0;
    ~mode1 := 0;
    ~alive1 := 0;
    ~p2_old := 0;
    ~p2_new := 0;
    ~id2 := 0;
    ~st2 := 0;
    ~mode2 := 0;
    ~alive2 := 0;
    ~p3_old := 0;
    ~p3_new := 0;
    ~id3 := 0;
    ~st3 := 0;
    ~mode3 := 0;
    ~alive3 := 0;
    ~p4_old := 0;
    ~p4_new := 0;
    ~id4 := 0;
    ~st4 := 0;
    ~mode4 := 0;
    ~alive4 := 0;
    ~p5_old := 0;
    ~p5_new := 0;
    ~id5 := 0;
    ~st5 := 0;
    ~mode5 := 0;
    ~alive5 := 0;
    ~nomsg := -1;
    ~send1 := 0;
    ~send2 := 0;
    ~send3 := 0;
    ~send4 := 0;
    ~send5 := 0;
    havoc main_#res;
    havoc main_#t~nondet10, main_#t~nondet11, main_#t~nondet12, main_#t~nondet13, main_#t~nondet14, main_#t~nondet15, main_#t~nondet16, main_#t~nondet17, main_#t~nondet18, main_#t~nondet19, main_#t~nondet20, main_#t~nondet21, main_#t~nondet22, main_#t~nondet23, main_#t~nondet24, main_#t~nondet25, main_#t~nondet26, main_#t~nondet27, main_#t~nondet28, main_#t~nondet29, main_#t~nondet30, main_#t~nondet31, main_#t~nondet32, main_#t~nondet33, main_#t~nondet34, main_#t~nondet35, main_#t~ret36, main_#t~ret37, main_~c1~137, main_~i2~137;
    havoc main_~c1~137;
    havoc main_~i2~137;
    main_~c1~137 := 0;
    ~r1 := main_#t~nondet10;
    havoc main_#t~nondet10;
    assume -128 <= main_#t~nondet11 && main_#t~nondet11 <= 127;
    ~id1 := main_#t~nondet11;
    havoc main_#t~nondet11;
    assume -128 <= main_#t~nondet12 && main_#t~nondet12 <= 127;
    ~st1 := main_#t~nondet12;
    havoc main_#t~nondet12;
    assume -128 <= main_#t~nondet13 && main_#t~nondet13 <= 127;
    ~send1 := main_#t~nondet13;
    havoc main_#t~nondet13;
    ~mode1 := main_#t~nondet14;
    havoc main_#t~nondet14;
    ~alive1 := main_#t~nondet15;
    havoc main_#t~nondet15;
    assume -128 <= main_#t~nondet16 && main_#t~nondet16 <= 127;
    ~id2 := main_#t~nondet16;
    havoc main_#t~nondet16;
    assume -128 <= main_#t~nondet17 && main_#t~nondet17 <= 127;
    ~st2 := main_#t~nondet17;
    havoc main_#t~nondet17;
    assume -128 <= main_#t~nondet18 && main_#t~nondet18 <= 127;
    ~send2 := main_#t~nondet18;
    havoc main_#t~nondet18;
    ~mode2 := main_#t~nondet19;
    havoc main_#t~nondet19;
    ~alive2 := main_#t~nondet20;
    havoc main_#t~nondet20;
    assume -128 <= main_#t~nondet21 && main_#t~nondet21 <= 127;
    ~id3 := main_#t~nondet21;
    havoc main_#t~nondet21;
    assume -128 <= main_#t~nondet22 && main_#t~nondet22 <= 127;
    ~st3 := main_#t~nondet22;
    havoc main_#t~nondet22;
    assume -128 <= main_#t~nondet23 && main_#t~nondet23 <= 127;
    ~send3 := main_#t~nondet23;
    havoc main_#t~nondet23;
    ~mode3 := main_#t~nondet24;
    havoc main_#t~nondet24;
    ~alive3 := main_#t~nondet25;
    havoc main_#t~nondet25;
    assume -128 <= main_#t~nondet26 && main_#t~nondet26 <= 127;
    ~id4 := main_#t~nondet26;
    havoc main_#t~nondet26;
    assume -128 <= main_#t~nondet27 && main_#t~nondet27 <= 127;
    ~st4 := main_#t~nondet27;
    havoc main_#t~nondet27;
    assume -128 <= main_#t~nondet28 && main_#t~nondet28 <= 127;
    ~send4 := main_#t~nondet28;
    havoc main_#t~nondet28;
    ~mode4 := main_#t~nondet29;
    havoc main_#t~nondet29;
    ~alive4 := main_#t~nondet30;
    havoc main_#t~nondet30;
    assume -128 <= main_#t~nondet31 && main_#t~nondet31 <= 127;
    ~id5 := main_#t~nondet31;
    havoc main_#t~nondet31;
    assume -128 <= main_#t~nondet32 && main_#t~nondet32 <= 127;
    ~st5 := main_#t~nondet32;
    havoc main_#t~nondet32;
    assume -128 <= main_#t~nondet33 && main_#t~nondet33 <= 127;
    ~send5 := main_#t~nondet33;
    havoc main_#t~nondet33;
    ~mode5 := main_#t~nondet34;
    havoc main_#t~nondet34;
    ~alive5 := main_#t~nondet35;
    havoc main_#t~nondet35;
    havoc init_#res;
    havoc init_~tmp~64;
    havoc init_~tmp~64;
    assume ~r1 % 256 == 0;
    assume ~alive1 % 256 + ~alive2 % 256 + ~alive3 % 256 + ~alive4 % 256 + ~alive5 % 256 >= 1;
    assume ~id1 >= 0;
    assume ~st1 == 0;
    assume ~send1 == ~id1;
    assume ~mode1 % 256 == 0;
    assume ~id2 >= 0;
    assume ~st2 == 0;
    assume ~send2 == ~id2;
    assume ~mode2 % 256 == 0;
    assume ~id3 >= 0;
    assume ~st3 == 0;
    assume ~send3 == ~id3;
    assume ~mode3 % 256 == 0;
    assume ~id4 >= 0;
    assume ~st4 == 0;
    assume ~send4 == ~id4;
    assume ~mode4 % 256 == 0;
    assume ~id5 >= 0;
    assume ~st5 == 0;
    assume ~send5 == ~id5;
    assume ~mode5 % 256 == 0;
    assume ~id1 != ~id2;
    assume ~id1 != ~id3;
    assume ~id1 != ~id4;
    assume ~id1 != ~id5;
    assume ~id2 != ~id3;
    assume ~id2 != ~id4;
    assume ~id2 != ~id5;
    assume ~id3 != ~id4;
    assume ~id3 != ~id5;
    assume ~id4 != ~id5;
    init_~tmp~64 := 1;
    init_#res := init_~tmp~64;
    main_#t~ret36 := init_#res;
    assume -2147483648 <= main_#t~ret36 && main_#t~ret36 <= 2147483647;
    main_~i2~137 := main_#t~ret36;
    havoc main_#t~ret36;
    assume main_~i2~137 != 0;
    ~p1_old := ~nomsg;
    ~p1_new := ~nomsg;
    ~p2_old := ~nomsg;
    ~p2_new := ~nomsg;
    ~p3_old := ~nomsg;
    ~p3_new := ~nomsg;
    ~p4_old := ~nomsg;
    ~p4_new := ~nomsg;
    ~p5_old := ~nomsg;
    ~p5_new := ~nomsg;
    main_~i2~137 := 0;
    goto loc1;
  loc1:
    assume true;
    assume !false;
    havoc node1_#t~ite0, node1_#t~ite1, node1_~m1~7;
    havoc node1_~m1~7;
    node1_~m1~7 := ~nomsg;
    goto loc2;
  loc2:
    goto loc2_0, loc2_1;
  loc2_0:
    assume ~mode1 % 256 != 0;
    assume !(~r1 % 256 == 255);
    ~r1 := ~r1 % 256 + 1;
    node1_~m1~7 := ~p5_old;
    ~p5_old := ~nomsg;
    assume !(node1_~m1~7 != ~nomsg);
    ~mode1 := 0;
    goto loc3;
  loc2_1:
    assume !(~mode1 % 256 != 0);
    assume ~alive1 % 256 != 0;
    assume ~send1 != ~nomsg && ~p1_new == ~nomsg;
    node1_#t~ite0 := ~send1;
    ~p1_new := (if node1_#t~ite0 % 256 <= 127 then node1_#t~ite0 % 256 else node1_#t~ite0 % 256 - 256);
    havoc node1_#t~ite0;
    ~mode1 := 1;
    goto loc3;
  loc3:
    havoc node2_#t~ite2, node2_#t~ite3, node2_~m2~20;
    havoc node2_~m2~20;
    node2_~m2~20 := ~nomsg;
    goto loc4;
  loc4:
    goto loc4_0, loc4_1;
  loc4_0:
    assume ~mode2 % 256 != 0;
    node2_~m2~20 := ~p1_old;
    ~p1_old := ~nomsg;
    assume !(node2_~m2~20 != ~nomsg);
    ~mode2 := 0;
    goto loc5;
  loc4_1:
    assume !(~mode2 % 256 != 0);
    assume !(~alive2 % 256 != 0);
    assume !(~send2 != ~id2);
    ~mode2 := 1;
    goto loc5;
  loc5:
    havoc node3_#t~ite4, node3_#t~ite5, node3_~m3~31;
    havoc node3_~m3~31;
    node3_~m3~31 := ~nomsg;
    goto loc6;
  loc6:
    goto loc6_0, loc6_1;
  loc6_0:
    assume ~mode3 % 256 != 0;
    node3_~m3~31 := ~p2_old;
    ~p2_old := ~nomsg;
    assume !(node3_~m3~31 != ~nomsg);
    ~mode3 := 0;
    goto loc7;
  loc6_1:
    assume !(~mode3 % 256 != 0);
    assume !(~alive3 % 256 != 0);
    assume !(~send3 != ~id3);
    ~mode3 := 1;
    goto loc7;
  loc7:
    havoc node4_#t~ite6, node4_#t~ite7, node4_~m4~42;
    havoc node4_~m4~42;
    node4_~m4~42 := ~nomsg;
    goto loc8;
  loc8:
    goto loc8_0, loc8_1;
  loc8_0:
    assume ~mode4 % 256 != 0;
    node4_~m4~42 := ~p3_old;
    ~p3_old := ~nomsg;
    assume !(node4_~m4~42 != ~nomsg);
    ~mode4 := 0;
    goto loc9;
  loc8_1:
    assume !(~mode4 % 256 != 0);
    assume !(~alive4 % 256 != 0);
    assume !(~send4 != ~id4);
    ~mode4 := 1;
    goto loc9;
  loc9:
    havoc node5_#t~ite8, node5_#t~ite9, node5_~m5~53;
    havoc node5_~m5~53;
    node5_~m5~53 := ~nomsg;
    goto loc10;
  loc10:
    goto loc10_0, loc10_1;
  loc10_0:
    assume ~mode5 % 256 != 0;
    node5_~m5~53 := ~p4_old;
    ~p4_old := ~nomsg;
    assume !(node5_~m5~53 != ~nomsg);
    ~mode5 := 0;
    goto loc11;
  loc10_1:
    assume !(~mode5 % 256 != 0);
    assume !(~alive5 % 256 != 0);
    assume !(~send5 != ~id5);
    ~mode5 := 1;
    goto loc11;
  loc11:
    ~p1_old := ~p1_new;
    ~p1_new := ~nomsg;
    ~p2_old := ~p2_new;
    ~p2_new := ~nomsg;
    ~p3_old := ~p3_new;
    ~p3_new := ~nomsg;
    ~p4_old := ~p4_new;
    ~p4_new := ~nomsg;
    ~p5_old := ~p5_new;
    ~p5_new := ~nomsg;
    havoc check_#res;
    havoc check_~tmp~130;
    havoc check_~tmp~130;
    assume ~st1 + ~st2 + ~st3 + ~st4 + ~st5 <= 1;
    goto loc12;
  loc12:
    goto loc12_0, loc12_1;
  loc12_0:
    assume !(~r1 % 256 < 5);
    assume !(~st1 + ~st2 + ~st3 + ~st4 + ~st5 == 1);
    check_~tmp~130 := 0;
    goto loc13;
  loc12_1:
    assume ~r1 % 256 < 5;
    check_~tmp~130 := 1;
    goto loc13;
  loc13:
    check_#res := check_~tmp~130;
    main_#t~ret37 := check_#res;
    assume -2147483648 <= main_#t~ret37 && main_#t~ret37 <= 2147483647;
    main_~c1~137 := main_#t~ret37;
    havoc main_#t~ret37;
    assert_#in~arg := (if main_~c1~137 == 0 then 0 else 1);
    havoc assert_~arg;
    assert_~arg := assert_#in~arg;
    goto loc14;
  loc14:
    goto loc14_0, loc14_1;
  loc14_0:
    assume assert_~arg % 256 == 0;
    assume !false;
    goto loc15;
  loc14_1:
    assume !(assert_~arg % 256 == 0);
    goto loc1;
  loc15:
    assert false;
}

procedure ULTIMATE.start() returns ();
modifies ~r1, ~p1_old, ~p1_new, ~id1, ~st1, ~mode1, ~alive1, ~p2_old, ~p2_new, ~id2, ~st2, ~mode2, ~alive2, ~p3_old, ~p3_new, ~id3, ~st3, ~mode3, ~alive3, ~p4_old, ~p4_new, ~id4, ~st4, ~mode4, ~alive4, ~p5_old, ~p5_new, ~id5, ~st5, ~mode5, ~alive5, ~nomsg, ~send1, ~send2, ~send3, ~send4, ~send5, ~r1, ~id1, ~st1, ~send1, ~mode1, ~alive1, ~id2, ~st2, ~send2, ~mode2, ~alive2, ~id3, ~st3, ~send3, ~mode3, ~alive3, ~id4, ~st4, ~send4, ~mode4, ~alive4, ~id5, ~st5, ~send5, ~mode5, ~alive5, ~p1_old, ~p1_new, ~p2_old, ~p2_new, ~p3_old, ~p3_new, ~p4_old, ~p4_new, ~p5_old, ~p5_new;
modifies ~r1, ~p5_old, ~send1, ~st1, ~mode1, ~p1_new, ~p1_old, ~send2, ~st2, ~mode2, ~p2_new, ~p2_old, ~send3, ~st3, ~mode3, ~p3_new, ~p3_old, ~send4, ~st4, ~mode4, ~p4_new, ~p4_old, ~send5, ~st5, ~mode5, ~p5_new, ~id1, ~alive1, ~id2, ~alive2, ~id3, ~alive3, ~id4, ~alive4, ~id5, ~alive5;

