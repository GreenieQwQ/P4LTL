//#Safe
type _SIZE_T_TYPE = bv32;

procedure _ATOMIC_OP32(x: [bv32]bv32, y: bv32) returns (z$1: bv32, A$1: [bv32]bv32, z$2: bv32, A$2: [bv32]bv32);

var {:source_name "A"} {:global} $$A: [bv32]bv32;

axiom {:array_info "$$A"} {:global} {:elem_width 32} {:source_name "A"} {:source_elem_width 32} {:source_dimensions "*"} true;

var {:race_checking} {:global} {:elem_width 32} {:source_elem_width 32} {:source_dimensions "*"} _READ_HAS_OCCURRED_$$A: bool;

var {:race_checking} {:global} {:elem_width 32} {:source_elem_width 32} {:source_dimensions "*"} _WRITE_HAS_OCCURRED_$$A: bool;

var {:race_checking} {:global} {:elem_width 32} {:source_elem_width 32} {:source_dimensions "*"} _ATOMIC_HAS_OCCURRED_$$A: bool;

const _WATCHED_OFFSET: bv32;

const {:global_offset_x} global_offset_x: bv32;

const {:global_offset_y} global_offset_y: bv32;

const {:global_offset_z} global_offset_z: bv32;

const {:group_id_x} group_id_x$1: bv32;

const {:group_id_x} group_id_x$2: bv32;

const {:group_size_x} group_size_x: bv32;

const {:group_size_y} group_size_y: bv32;

const {:group_size_z} group_size_z: bv32;

const {:local_id_x} local_id_x$1: bv32;

const {:local_id_x} local_id_x$2: bv32;

const {:num_groups_x} num_groups_x: bv32;

const {:num_groups_y} num_groups_y: bv32;

const {:num_groups_z} num_groups_z: bv32;

function {:builtin "bvadd"} BV32_ADD(bv32, bv32) : bv32;

function {:builtin "bvmul"} BV32_MUL(bv32, bv32) : bv32;

function {:builtin "bvslt"} BV32_SLT(bv32, bv32) : bool;

function {:builtin "bvsub"} BV32_SUB(bv32, bv32) : bv32;

procedure {:source_name "foo"} ULTIMATE.start();
  requires !_READ_HAS_OCCURRED_$$A && !_WRITE_HAS_OCCURRED_$$A && !_ATOMIC_HAS_OCCURRED_$$A;
  requires BV32_SGT(group_size_x, 0bv32);
  requires BV32_SGT(num_groups_x, 0bv32);
  requires BV32_SGE(group_id_x$1, 0bv32);
  requires BV32_SGE(group_id_x$2, 0bv32);
  requires BV32_SLT(group_id_x$1, num_groups_x);
  requires BV32_SLT(group_id_x$2, num_groups_x);
  requires BV32_SGE(local_id_x$1, 0bv32);
  requires BV32_SGE(local_id_x$2, 0bv32);
  requires BV32_SLT(local_id_x$1, group_size_x);
  requires BV32_SLT(local_id_x$2, group_size_x);
  requires BV32_SGT(group_size_y, 0bv32);
  requires BV32_SGT(num_groups_y, 0bv32);
  requires BV32_SGE(group_id_y$1, 0bv32);
  requires BV32_SGE(group_id_y$2, 0bv32);
  requires BV32_SLT(group_id_y$1, num_groups_y);
  requires BV32_SLT(group_id_y$2, num_groups_y);
  requires BV32_SGE(local_id_y$1, 0bv32);
  requires BV32_SGE(local_id_y$2, 0bv32);
  requires BV32_SLT(local_id_y$1, group_size_y);
  requires BV32_SLT(local_id_y$2, group_size_y);
  requires BV32_SGT(group_size_z, 0bv32);
  requires BV32_SGT(num_groups_z, 0bv32);
  requires BV32_SGE(group_id_z$1, 0bv32);
  requires BV32_SGE(group_id_z$2, 0bv32);
  requires BV32_SLT(group_id_z$1, num_groups_z);
  requires BV32_SLT(group_id_z$2, num_groups_z);
  requires BV32_SGE(local_id_z$1, 0bv32);
  requires BV32_SGE(local_id_z$2, 0bv32);
  requires BV32_SLT(local_id_z$1, group_size_z);
  requires BV32_SLT(local_id_z$2, group_size_z);
  requires group_id_x$1 == group_id_x$2 && group_id_y$1 == group_id_y$2 && group_id_z$1 == group_id_z$2 ==> local_id_x$1 != local_id_x$2 || local_id_y$1 != local_id_y$2 || local_id_z$1 != local_id_z$2;
  modifies _WRITE_HAS_OCCURRED_$$A, _WRITE_READ_BENIGN_FLAG_$$A, _WRITE_READ_BENIGN_FLAG_$$A, _READ_HAS_OCCURRED_$$A;

implementation {:source_name "foo"} ULTIMATE.start()
{
  var $i.0: bv32;
  var $i5.0: bv32;
  var v0: bool;
  var v1$1: bv32;
  var v1$2: bv32;
  var v2$1: bv32;
  var v2$2: bv32;
  var v3: bool;
  var v4$1: bv32;
  var v4$2: bv32;
  var v5$1: bv32;
  var v5$2: bv32;
  var v6$1: bv32;
  var v6$2: bv32;
  var v7$1: bv32;
  var v7$2: bv32;
  var v8$1: bv32;
  var v8$2: bv32;
  var v9$1: bv32;
  var v9$2: bv32;
  var v10$1: bv32;
  var v10$2: bv32;
  var v11$1: bv32;
  var v11$2: bv32;

  $entry:
    call _LOG_WRITE_$$A(true, BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1), 1bv32, $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)]);
    call _UPDATE_WRITE_READ_BENIGN_FLAG_$$A(true, BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2));
    assume {:do_not_predicate} {:check_id "check_state_0"} {:captureState "check_state_0"} {:sourceloc} {:sourceloc_num 2} true;
    call _CHECK_WRITE_$$A(true, BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2), 1bv32);
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_WRITE_$$A"} true;
    $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)] := 1bv32;
    $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)] := 1bv32;
    $i.0 := 0bv32;
    assume {:captureState "loop_entry_state_1_0"} true;
    goto $for.cond;

  $for.cond:
    assume {:captureState "loop_head_state_1"} true;
    assert {:candidate} {:procedure_wide_invariant} {:do_not_predicate} {:sourceloc_num 0} {:tag "procedure_wide"} {:thread 1} _b0 ==> (if $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)] == 1bv32 then 1bv1 else 0bv1) != 0bv1;
    assert {:candidate} {:procedure_wide_invariant} {:do_not_predicate} {:sourceloc_num 0} {:tag "procedure_wide"} {:thread 2} _b0 ==> (if $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)] == 1bv32 then 1bv1 else 0bv1) != 0bv1;
    assert {:block_sourceloc} {:sourceloc_num 3} true;
    v0 := BV32_SLT($i.0, 1024bv32);
    goto $truebb, $falsebb;

  $falsebb:
    assume {:partition} !v0;
    v1$1 := BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1);
    v1$2 := BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2);
    call _LOG_READ_$$A(true, v1$1, $$A[v1$1]);
    assume {:do_not_predicate} {:check_id "check_state_1"} {:captureState "check_state_1"} {:sourceloc} {:sourceloc_num 8} true;
    call _CHECK_READ_$$A(true, v1$2, $$A[v1$2]);
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_READ_$$A"} true;
    v2$1 := $$A[v1$1];
    v2$2 := $$A[v1$2];
    call _LOG_WRITE_$$A(true, v1$1, BV32_SUB(v2$1, 1bv32), $$A[v1$1]);
    call _UPDATE_WRITE_READ_BENIGN_FLAG_$$A(true, v1$2);
    assume {:do_not_predicate} {:check_id "check_state_2"} {:captureState "check_state_2"} {:sourceloc} {:sourceloc_num 9} true;
    call _CHECK_WRITE_$$A(true, v1$2, BV32_SUB(v2$2, 1bv32));
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_WRITE_$$A"} true;
    $$A[v1$1] := BV32_SUB(v2$1, 1bv32);
    $$A[v1$2] := BV32_SUB(v2$2, 1bv32);
    $i5.0 := 0bv32;
    assume {:captureState "loop_entry_state_0_0"} true;
    goto $for.cond6;

  $for.cond6:
    assume {:captureState "loop_head_state_0"} true;
    assert {:candidate} {:procedure_wide_invariant} {:do_not_predicate} {:sourceloc_num 0} {:tag "procedure_wide"} {:thread 1} _b1 ==> (if $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)] == 1bv32 then 1bv1 else 0bv1) != 0bv1;
    assert {:candidate} {:procedure_wide_invariant} {:do_not_predicate} {:sourceloc_num 0} {:tag "procedure_wide"} {:thread 2} _b1 ==> (if $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)] == 1bv32 then 1bv1 else 0bv1) != 0bv1;
    assert {:block_sourceloc} {:sourceloc_num 10} true;
    assert {:originated_from_invariant} {:sourceloc_num 11} {:thread 1} (if $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)] == 0bv32 then 1bv1 else 0bv1) != 0bv1;
    assert {:originated_from_invariant} {:sourceloc_num 11} {:thread 2} (if $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)] == 0bv32 then 1bv1 else 0bv1) != 0bv1;
    v3 := BV32_SLT($i5.0, 1024bv32);
    goto $truebb0, $falsebb0;

  $falsebb0:
    assume {:partition} !v3;
    v8$1 := BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1);
    v8$2 := BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2);
    call _LOG_READ_$$A(true, v8$1, $$A[v8$1]);
    assume {:do_not_predicate} {:check_id "check_state_3"} {:captureState "check_state_3"} {:sourceloc} {:sourceloc_num 19} true;
    call _CHECK_READ_$$A(true, v8$2, $$A[v8$2]);
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_READ_$$A"} true;
    v9$1 := $$A[v8$1];
    v9$2 := $$A[v8$2];
    call _LOG_WRITE_$$A(true, v8$1, BV32_ADD(v9$1, 1bv32), $$A[v8$1]);
    call _UPDATE_WRITE_READ_BENIGN_FLAG_$$A(true, v8$2);
    assume {:do_not_predicate} {:check_id "check_state_4"} {:captureState "check_state_4"} {:sourceloc} {:sourceloc_num 20} true;
    call _CHECK_WRITE_$$A(true, v8$2, BV32_ADD(v9$2, 1bv32));
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_WRITE_$$A"} true;
    $$A[v8$1] := BV32_ADD(v9$1, 1bv32);
    $$A[v8$2] := BV32_ADD(v9$2, 1bv32);
    call _LOG_READ_$$A(true, BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1), $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)]);
    assume {:do_not_predicate} {:check_id "check_state_5"} {:captureState "check_state_5"} {:sourceloc} {:sourceloc_num 21} true;
    call _CHECK_READ_$$A(true, BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2), $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)]);
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_READ_$$A"} true;
    v10$1 := $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)];
    v10$2 := $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)];
    assert {:sourceloc_num 22} {:thread 1} (if v10$1 == 1bv32 then 1bv1 else 0bv1) != 0bv1;
    assert {:sourceloc_num 22} {:thread 2} (if v10$2 == 1bv32 then 1bv1 else 0bv1) != 0bv1;
    call _LOG_READ_$$A(true, BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1), $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)]);
    assume {:do_not_predicate} {:check_id "check_state_6"} {:captureState "check_state_6"} {:sourceloc} {:sourceloc_num 23} true;
    call _CHECK_READ_$$A(true, BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2), $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)]);
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_READ_$$A"} true;
    v11$1 := $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)];
    v11$2 := $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)];
    return;

  $truebb0:
    assume {:partition} v3;
    v4$1 := BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1);
    v4$2 := BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2);
    call _LOG_READ_$$A(true, v4$1, $$A[v4$1]);
    assume {:do_not_predicate} {:check_id "check_state_7"} {:captureState "check_state_7"} {:sourceloc} {:sourceloc_num 13} true;
    call _CHECK_READ_$$A(true, v4$2, $$A[v4$2]);
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_READ_$$A"} true;
    v5$1 := $$A[v4$1];
    v5$2 := $$A[v4$2];
    call _LOG_WRITE_$$A(true, v4$1, BV32_ADD(v5$1, 1bv32), $$A[v4$1]);
    call _UPDATE_WRITE_READ_BENIGN_FLAG_$$A(true, v4$2);
    assume {:do_not_predicate} {:check_id "check_state_8"} {:captureState "check_state_8"} {:sourceloc} {:sourceloc_num 14} true;
    call _CHECK_WRITE_$$A(true, v4$2, BV32_ADD(v5$2, 1bv32));
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_WRITE_$$A"} true;
    $$A[v4$1] := BV32_ADD(v5$1, 1bv32);
    $$A[v4$2] := BV32_ADD(v5$2, 1bv32);
    v6$1 := BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1);
    v6$2 := BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2);
    call _LOG_READ_$$A(true, v6$1, $$A[v6$1]);
    assume {:do_not_predicate} {:check_id "check_state_9"} {:captureState "check_state_9"} {:sourceloc} {:sourceloc_num 15} true;
    call _CHECK_READ_$$A(true, v6$2, $$A[v6$2]);
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_READ_$$A"} true;
    v7$1 := $$A[v6$1];
    v7$2 := $$A[v6$2];
    call _LOG_WRITE_$$A(true, v6$1, BV32_SUB(v7$1, 1bv32), $$A[v6$1]);
    call _UPDATE_WRITE_READ_BENIGN_FLAG_$$A(true, v6$2);
    assume {:do_not_predicate} {:check_id "check_state_10"} {:captureState "check_state_10"} {:sourceloc} {:sourceloc_num 16} true;
    call _CHECK_WRITE_$$A(true, v6$2, BV32_SUB(v7$2, 1bv32));
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_WRITE_$$A"} true;
    $$A[v6$1] := BV32_SUB(v7$1, 1bv32);
    $$A[v6$2] := BV32_SUB(v7$2, 1bv32);
    $i5.0 := BV32_ADD($i5.0, 1bv32);
    assume {:captureState "loop_back_edge_state_0_0"} true;
    goto $for.cond6;

  $truebb:
    assume {:partition} v0;
    call _LOG_WRITE_$$A(true, BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1), 1bv32, $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)]);
    call _UPDATE_WRITE_READ_BENIGN_FLAG_$$A(true, BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2));
    assume {:do_not_predicate} {:check_id "check_state_11"} {:captureState "check_state_11"} {:sourceloc} {:sourceloc_num 5} true;
    call _CHECK_WRITE_$$A(true, BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2), 1bv32);
    assume {:captureState "call_return_state_0"} {:procedureName "_CHECK_WRITE_$$A"} true;
    $$A[BV32_ADD(BV32_MUL(group_id_x$1, group_size_x), local_id_x$1)] := 1bv32;
    $$A[BV32_ADD(BV32_MUL(group_id_x$2, group_size_x), local_id_x$2)] := 1bv32;
    $i.0 := BV32_ADD($i.0, 1bv32);
    assume {:captureState "loop_back_edge_state_1_0"} true;
    goto $for.cond;
}

axiom (if group_size_y == 1bv32 then 1bv1 else 0bv1) != 0bv1;

axiom (if group_size_z == 1bv32 then 1bv1 else 0bv1) != 0bv1;

axiom (if num_groups_y == 1bv32 then 1bv1 else 0bv1) != 0bv1;

axiom (if num_groups_z == 1bv32 then 1bv1 else 0bv1) != 0bv1;

axiom (if group_size_x == 1024bv32 then 1bv1 else 0bv1) != 0bv1;

axiom (if num_groups_x == 2bv32 then 1bv1 else 0bv1) != 0bv1;

axiom (if global_offset_x == 0bv32 then 1bv1 else 0bv1) != 0bv1;

axiom (if global_offset_y == 0bv32 then 1bv1 else 0bv1) != 0bv1;

axiom (if global_offset_z == 0bv32 then 1bv1 else 0bv1) != 0bv1;

const {:local_id_y} local_id_y$1: bv32;

const {:local_id_y} local_id_y$2: bv32;

const {:local_id_z} local_id_z$1: bv32;

const {:local_id_z} local_id_z$2: bv32;

const {:group_id_y} group_id_y$1: bv32;

const {:group_id_y} group_id_y$2: bv32;

const {:group_id_z} group_id_z$1: bv32;

const {:group_id_z} group_id_z$2: bv32;

const {:existential true} _b0: bool;

const {:existential true} _b1: bool;

const _WATCHED_VALUE_$$A: bv32;

procedure {:inline 1} _LOG_READ_$$A(_P: bool, _offset: bv32, _value: bv32);
  modifies _READ_HAS_OCCURRED_$$A;

implementation {:inline 1} _LOG_READ_$$A(_P: bool, _offset: bv32, _value: bv32)
{

  log_access_entry:
    _READ_HAS_OCCURRED_$$A := (if _P && _TRACKING && _WATCHED_OFFSET == _offset && _WATCHED_VALUE_$$A == _value then true else _READ_HAS_OCCURRED_$$A);
    return;
}

procedure _CHECK_READ_$$A(_P: bool, _offset: bv32, _value: bv32);
  requires !(_P && _WRITE_HAS_OCCURRED_$$A && _WATCHED_OFFSET == _offset && _WRITE_READ_BENIGN_FLAG_$$A);
  requires !(_P && _ATOMIC_HAS_OCCURRED_$$A && _WATCHED_OFFSET == _offset);

var _WRITE_READ_BENIGN_FLAG_$$A: bool;

procedure {:inline 1} _LOG_WRITE_$$A(_P: bool, _offset: bv32, _value: bv32, _value_old: bv32);
  modifies _WRITE_HAS_OCCURRED_$$A, _WRITE_READ_BENIGN_FLAG_$$A;

implementation {:inline 1} _LOG_WRITE_$$A(_P: bool, _offset: bv32, _value: bv32, _value_old: bv32)
{

  log_access_entry:
    _WRITE_HAS_OCCURRED_$$A := (if _P && _TRACKING && _WATCHED_OFFSET == _offset && _WATCHED_VALUE_$$A == _value then true else _WRITE_HAS_OCCURRED_$$A);
    _WRITE_READ_BENIGN_FLAG_$$A := (if _P && _TRACKING && _WATCHED_OFFSET == _offset && _WATCHED_VALUE_$$A == _value then _value != _value_old else _WRITE_READ_BENIGN_FLAG_$$A);
    return;
}

procedure _CHECK_WRITE_$$A(_P: bool, _offset: bv32, _value: bv32);
  requires !(_P && _WRITE_HAS_OCCURRED_$$A && _WATCHED_OFFSET == _offset && _WATCHED_VALUE_$$A != _value);
  requires !(_P && _READ_HAS_OCCURRED_$$A && _WATCHED_OFFSET == _offset && _WATCHED_VALUE_$$A != _value);
  requires !(_P && _ATOMIC_HAS_OCCURRED_$$A && _WATCHED_OFFSET == _offset);

procedure {:inline 1} _LOG_ATOMIC_$$A(_P: bool, _offset: bv32);
  modifies _ATOMIC_HAS_OCCURRED_$$A;

implementation {:inline 1} _LOG_ATOMIC_$$A(_P: bool, _offset: bv32)
{

  log_access_entry:
    _ATOMIC_HAS_OCCURRED_$$A := (if _P && _TRACKING && _WATCHED_OFFSET == _offset then true else _ATOMIC_HAS_OCCURRED_$$A);
    return;
}

procedure _CHECK_ATOMIC_$$A(_P: bool, _offset: bv32);
  requires !(_P && _WRITE_HAS_OCCURRED_$$A && _WATCHED_OFFSET == _offset);
  requires !(_P && _READ_HAS_OCCURRED_$$A && _WATCHED_OFFSET == _offset);

procedure {:inline 1} _UPDATE_WRITE_READ_BENIGN_FLAG_$$A(_P: bool, _offset: bv32);
  modifies _WRITE_READ_BENIGN_FLAG_$$A;

implementation {:inline 1} _UPDATE_WRITE_READ_BENIGN_FLAG_$$A(_P: bool, _offset: bv32)
{

  _UPDATE_BENIGN_FLAG:
    _WRITE_READ_BENIGN_FLAG_$$A := (if _P && _WRITE_HAS_OCCURRED_$$A && _WATCHED_OFFSET == _offset then false else _WRITE_READ_BENIGN_FLAG_$$A);
    return;
}

var _TRACKING: bool;

function {:builtin "bvsgt"} BV32_SGT(bv32, bv32) : bool;

function {:builtin "bvsge"} BV32_SGE(bv32, bv32) : bool;
