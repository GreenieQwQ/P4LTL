var #valid : [int]int;

var #memory_int : [int][int]int;

var #NULL.offset : int;

var #length : [int]int;

var #NULL.base : int;

procedure ULTIMATE.start() returns ()
modifies #valid, #memory_int, #NULL.offset, #length, #NULL.base;
{
    var read~int_#value : int;
    var main_~nondetArea~0.offset : int;
    var main_#t~malloc11.offset : int;
    var cstrncpy_#t~post3.offset : int;
    var main_~nondetString~0.offset : int;
    var main_#t~nondet8 : int;
    var main_~nondetArea~0.base : int;
    var main_#t~malloc11.base : int;
    var cstrncpy_~dst~0.offset : int;
    var write~int_#ptr.base : int;
    var cstrncpy_#in~n : int;
    var #Ultimate.alloc_#res.base : int;
    var #Ultimate.alloc_#res.offset : int;
    var cstrncpy_~n : int;
    var cstrncpy_#t~post7.offset : int;
    var main_~n~0 : int;
    var main_#t~ret12.offset : int;
    var cstrncpy_~src~0.base : int;
    var write~int_old_#memory_int : [int][int]int;
    var cstrncpy_~s1.base : int;
    var main_old_#valid : [int]int;
    var main_#t~ret12.base : int;
    var #Ultimate.alloc_old_#length : [int]int;
    var cstrncpy_#in~s2.offset : int;
    var write~int_#sizeOfWrittenType : int;
    var read~int_#ptr.base : int;
    var cstrncpy_#in~s1.offset : int;
    var main_~nondetString~0.base : int;
    var cstrncpy_#t~post3.base : int;
    var cstrncpy_#t~post7.base : int;
    var write~int_#value : int;
    var cstrncpy_~src~0.offset : int;
    var cstrncpy_~s2.offset : int;
    var main_~length~0 : int;
    var cstrncpy_#in~s2.base : int;
    var cstrncpy_~dst~0.base : int;
    var cstrncpy_#in~s1.base : int;
    var #Ultimate.alloc_old_#valid : [int]int;
    var cstrncpy_~us~0.base : int;
    var main_#t~nondet9 : int;
    var cstrncpy_#t~mem5 : int;
    var cstrncpy_~us~0.offset : int;
    var write~int_#ptr.offset : int;
    var cstrncpy_#res.base : int;
    var cstrncpy_~s1.offset : int;
    var main_#t~malloc10.offset : int;
    var #Ultimate.alloc_~size : int;
    var read~int_#sizeOfReadType : int;
    var cstrncpy_#t~post4.offset : int;
    var cstrncpy_#res.offset : int;
    var cstrncpy_#t~post6 : int;
    var cstrncpy_~n2~0 : int;
    var main_#t~malloc10.base : int;
    var cstrncpy_~s2.base : int;
    var read~int_#ptr.offset : int;
    var main_#res : int;
    var cstrncpy_#t~post4.base : int;
    var cstrncpy_#t~post2 : int;

  loc0:
    #NULL.offset, #NULL.base := 0, 0;
    #valid := #valid[0 := 0];
    main_old_#valid := #valid;
    havoc main_#res;
    havoc main_~nondetArea~0.offset, main_#t~malloc11.offset, main_~nondetString~0.offset, main_#t~nondet8, main_~n~0, main_~nondetArea~0.base, main_#t~nondet9, main_#t~ret12.offset, main_#t~malloc11.base, main_#t~ret12.base, main_#t~malloc10.base, main_~nondetString~0.base, main_~length~0, main_#t~malloc10.offset;
    assume 0 <= main_#t~nondet8 + 2147483648 && main_#t~nondet8 <= 2147483647;
    main_~length~0 := main_#t~nondet8;
    havoc main_#t~nondet8;
    assume 0 <= main_#t~nondet9 + 2147483648 && main_#t~nondet9 <= 2147483647;
    main_~n~0 := main_#t~nondet9;
    havoc main_#t~nondet9;
    assume !(main_~length~0 < 1);
    assume !(main_~n~0 < 1);
    #Ultimate.alloc_old_#valid, #Ultimate.alloc_old_#length := #valid, #length;
    #Ultimate.alloc_~size := main_~n~0;
    havoc #Ultimate.alloc_#res.offset, #Ultimate.alloc_#res.base;
    havoc #valid, #length;
    assume 0 == #Ultimate.alloc_old_#valid[#Ultimate.alloc_#res.base];
    assume #Ultimate.alloc_old_#valid[#Ultimate.alloc_#res.base := 1] == #valid;
    assume #Ultimate.alloc_#res.offset == 0;
    assume !(0 == #Ultimate.alloc_#res.base);
    assume #Ultimate.alloc_old_#length[#Ultimate.alloc_#res.base := #Ultimate.alloc_~size] == #length;
    main_#t~malloc10.base, main_#t~malloc10.offset := #Ultimate.alloc_#res.base, #Ultimate.alloc_#res.offset;
    main_~nondetArea~0.offset, main_~nondetArea~0.base := main_#t~malloc10.offset, main_#t~malloc10.base;
    #Ultimate.alloc_old_#valid, #Ultimate.alloc_old_#length := #valid, #length;
    #Ultimate.alloc_~size := main_~length~0;
    havoc #Ultimate.alloc_#res.offset, #Ultimate.alloc_#res.base;
    havoc #valid, #length;
    assume 0 == #Ultimate.alloc_old_#valid[#Ultimate.alloc_#res.base];
    assume #valid == #Ultimate.alloc_old_#valid[#Ultimate.alloc_#res.base := 1];
    assume #Ultimate.alloc_#res.offset == 0;
    assume !(0 == #Ultimate.alloc_#res.base);
    assume #length == #Ultimate.alloc_old_#length[#Ultimate.alloc_#res.base := #Ultimate.alloc_~size];
    main_#t~malloc11.offset, main_#t~malloc11.base := #Ultimate.alloc_#res.offset, #Ultimate.alloc_#res.base;
    main_~nondetString~0.base, main_~nondetString~0.offset := main_#t~malloc11.base, main_#t~malloc11.offset;
    write~int_old_#memory_int := #memory_int;
    write~int_#sizeOfWrittenType, write~int_#ptr.base, write~int_#value, write~int_#ptr.offset := 1, main_~nondetString~0.base, 0, main_~length~0 + main_~nondetString~0.offset + -1;
    assume #valid[write~int_#ptr.base] == 1;
    assume 0 <= write~int_#ptr.offset && write~int_#sizeOfWrittenType + write~int_#ptr.offset <= #length[write~int_#ptr.base];
    assume 1 == #valid[write~int_#ptr.base];
    assume write~int_#sizeOfWrittenType + write~int_#ptr.offset <= #length[write~int_#ptr.base] && 0 <= write~int_#ptr.offset;
    havoc #memory_int;
    assume #memory_int == write~int_old_#memory_int[write~int_#ptr.base := write~int_old_#memory_int[write~int_#ptr.base][write~int_#ptr.offset := write~int_#value]];
    cstrncpy_#in~s2.base, cstrncpy_#in~s1.base, cstrncpy_#in~s1.offset, cstrncpy_#in~n, cstrncpy_#in~s2.offset := main_~nondetString~0.base, main_~nondetArea~0.base, main_~nondetArea~0.offset, main_~n~0, main_~nondetString~0.offset;
    havoc cstrncpy_#res.base, cstrncpy_#res.offset;
    havoc cstrncpy_~n, cstrncpy_~dst~0.base, cstrncpy_#t~post7.offset, cstrncpy_#t~post3.offset, cstrncpy_~us~0.base, cstrncpy_~src~0.base, cstrncpy_~s1.base, cstrncpy_~dst~0.offset, cstrncpy_#t~post4.offset, cstrncpy_#t~mem5, cstrncpy_~us~0.offset, cstrncpy_#t~post6, cstrncpy_~n2~0, cstrncpy_~s1.offset, cstrncpy_~s2.base, cstrncpy_#t~post3.base, cstrncpy_#t~post4.base, cstrncpy_#t~post7.base, cstrncpy_~src~0.offset, cstrncpy_#t~post2, cstrncpy_~s2.offset;
    cstrncpy_~s1.offset, cstrncpy_~s1.base := cstrncpy_#in~s1.offset, cstrncpy_#in~s1.base;
    cstrncpy_~s2.base, cstrncpy_~s2.offset := cstrncpy_#in~s2.base, cstrncpy_#in~s2.offset;
    cstrncpy_~n := cstrncpy_#in~n;
    cstrncpy_~dst~0.base, cstrncpy_~dst~0.offset := cstrncpy_~s1.base, cstrncpy_~s1.offset;
    cstrncpy_~src~0.base, cstrncpy_~src~0.offset := cstrncpy_~s2.base, cstrncpy_~s2.offset;
    havoc cstrncpy_~us~0.base, cstrncpy_~us~0.offset;
    havoc cstrncpy_~n2~0;
    goto loc1;
  loc1:
    assume 0 < cstrncpy_~n;
    cstrncpy_#t~post2 := cstrncpy_~n;
    cstrncpy_~n := cstrncpy_#t~post2 + -1;
    havoc cstrncpy_#t~post2;
    cstrncpy_#t~post3.offset, cstrncpy_#t~post3.base := cstrncpy_~dst~0.offset, cstrncpy_~dst~0.base;
    cstrncpy_~dst~0.base, cstrncpy_~dst~0.offset := cstrncpy_#t~post3.base, cstrncpy_#t~post3.offset + 1;
    cstrncpy_#t~post4.base, cstrncpy_#t~post4.offset := cstrncpy_~src~0.base, cstrncpy_~src~0.offset;
    cstrncpy_~src~0.base, cstrncpy_~src~0.offset := cstrncpy_#t~post4.base, cstrncpy_#t~post4.offset + 1;
    read~int_#ptr.base, read~int_#ptr.offset, read~int_#sizeOfReadType := cstrncpy_#t~post4.base, cstrncpy_#t~post4.offset, 1;
    assume 1 == #valid[read~int_#ptr.base];
    assume read~int_#sizeOfReadType + read~int_#ptr.offset <= #length[read~int_#ptr.base] && 0 <= read~int_#ptr.offset;
    assume 1 == #valid[read~int_#ptr.base];
    assume read~int_#sizeOfReadType + read~int_#ptr.offset <= #length[read~int_#ptr.base] && 0 <= read~int_#ptr.offset;
    havoc read~int_#value;
    assume read~int_#value == #memory_int[read~int_#ptr.base][read~int_#ptr.offset];
    cstrncpy_#t~mem5 := read~int_#value;
    write~int_old_#memory_int := #memory_int;
    write~int_#sizeOfWrittenType, write~int_#ptr.base, write~int_#value, write~int_#ptr.offset := 1, cstrncpy_#t~post3.base, cstrncpy_#t~mem5, cstrncpy_#t~post3.offset;
    assume 1 == #valid[write~int_#ptr.base];
    goto loc2;
  loc2:
    goto loc2_0, loc2_1;
  loc2_0:
    assume !(0 <= write~int_#ptr.offset) || !(write~int_#ptr.offset + write~int_#sizeOfWrittenType <= #length[write~int_#ptr.base]);
    goto loc3;
  loc2_1:
    assume write~int_#ptr.offset + write~int_#sizeOfWrittenType <= #length[write~int_#ptr.base] && 0 <= write~int_#ptr.offset;
    assume 1 == #valid[write~int_#ptr.base];
    assume write~int_#sizeOfWrittenType + write~int_#ptr.offset <= #length[write~int_#ptr.base] && 0 <= write~int_#ptr.offset;
    havoc #memory_int;
    assume write~int_old_#memory_int[write~int_#ptr.base := write~int_old_#memory_int[write~int_#ptr.base][write~int_#ptr.offset := write~int_#value]] == #memory_int;
    assume !(cstrncpy_#t~mem5 == 0);
    havoc cstrncpy_#t~mem5;
    havoc cstrncpy_#t~post3.offset, cstrncpy_#t~post3.base;
    havoc cstrncpy_#t~post4.base, cstrncpy_#t~post4.offset;
    goto loc1;
  loc3:
    assert false;
}

