type ref;
type realVar;
type classConst;
// type Field x;
// var $HeapVar : <x>[ref, Field x]x;

const unique $null : ref ;
const unique $intArrNull : [int]int ;
const unique $realArrNull : [int]realVar ;
const unique $refArrNull : [int]ref ;

const unique $arrSizeIdx : int;
var $intArrSize : [int]int;
var $realArrSize : [realVar]int;
var $refArrSize : [ref]int;

var $stringSize : [ref]int;

//built-in axioms 
axiom ($arrSizeIdx == -1);

//note: new version doesn't put helpers in the perlude anymore//Prelude finished 



var Tree$TreeList$value255 : Field ref;
var Tree$Tree$left258 : Field ref;
var int$Tree$value0 : Field int;
var java.lang.String$lp$$rp$$Random$args257 : [int]ref;
var int$Random$index0 : int;
var int$IntList$value0 : Field int;
var IntList$IntList$next254 : Field ref;
var Tree$Tree$right259 : Field ref;
var TreeList$TreeList$next256 : Field ref;


// procedure is generated by joogie.
function {:inline true} $neref(x : ref, y : ref) returns (__ret : int) {
if (x != y) then 1 else 0
}


// <Random: void <init>()>
procedure void$Random$$la$init$ra$$2236(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r041 : ref;
Block49:
	r041 := __this;
	 assert ($neref((r041), ($null))==1);
	 //  @line: 1
	 call void$java.lang.Object$$la$init$ra$$28((r041));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $realarrtoref($param00 : [int]realVar) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $modreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $leref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $modint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $gtref($param00 : ref, $param11 : ref) returns (__ret : int);



// <FlattenRTA: void <init>()>
procedure void$FlattenRTA$$la$init$ra$$2228(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r01 : ref;
Block16:
	r01 := __this;
	 assert ($neref((r01), ($null))==1);
	 //  @line: 1
	 call void$java.lang.Object$$la$init$ra$$28((r01));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqrealarray($param00 : [int]realVar, $param11 : [int]realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $addint(x : int, y : int) returns (__ret : int) {
(x + y)
}


// procedure is generated by joogie.
function {:inline true} $subref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $inttoreal($param00 : int) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shrint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $negReal($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $ushrint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $refarrtoref($param00 : [int]ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $divref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $mulref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $neint(x : int, y : int) returns (__ret : int) {
if (x != y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $ltreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 23
// <FlattenRTA: void main(java.lang.String[])>
procedure void$FlattenRTA$main$2230($param_0 : [int]ref)
  modifies $stringSize, java.lang.String$lp$$rp$$Random$args257;
 {
var r015 : [int]ref;
var r424 : ref;
var $r323 : ref;
var r122 : ref;
var i125 : int;
var i017 : int;

 //temp local variables 
var $freshlocal0 : ref;

Block32:
	r015 := $param_0;
	 //  @line: 24
	java.lang.String$lp$$rp$$Random$args257 := r015;
	 //  @line: 25
	 call i017 := int$Random$random$2237();
	 //  @line: 26
	r424 := $null;
	 //  @line: 27
	i125 := i017;
	 goto Block33;
	 //  @line: 27
Block33:
	 goto Block34, Block36;
	 //  @line: 27
Block34:
	 assume ($leint((i125), (0))==1);
	 goto Block35;
	 //  @line: 27
Block36:
	 //  @line: 27
	 assume ($negInt(($leint((i125), (0))))==1);
	 //  @line: 28
	 call r122 := Tree$Tree$createTree$2242();
	 //  @line: 29
	$r323 := $newvariable((37));
	 assume ($neref(($newvariable((37))), ($null))==1);
	 assert ($neref(($r323), ($null))==1);
	 //  @line: 29
	 call void$TreeList$$la$init$ra$$2234(($r323), (r122), (r424));
	 //  @line: 29
	r424 := $r323;
	 //  @line: 27
	i125 := $addint((i125), (-1));
	 goto Block33;
	 //  @line: 32
Block35:
	 //  @line: 32
	 call $freshlocal0 := IntList$FlattenRTA$flatten$2229((r424));
	 goto Block38;
	 //  @line: 33
Block38:
	 return;
}


// procedure is generated by joogie.
function {:inline true} $reftorefarr($param00 : ref) returns (__ret : [int]ref);



// procedure is generated by joogie.
function {:inline true} $gtint(x : int, y : int) returns (__ret : int) {
if (x > y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $reftoint($param00 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $addref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $xorreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $andref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 2
// <Random: void <clinit>()>
procedure void$Random$$la$clinit$ra$$2238()
  modifies int$Random$index0;
 {
	 //  @line: 3
Block51:
	 //  @line: 3
	int$Random$index0 := 0;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $cmpreal(x : realVar, y : realVar) returns (__ret : int) {
if ($ltreal((x), (y)) == 1) then 1 else if ($eqreal((x), (y)) == 1) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $addreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $gtreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 13
// <IntList: IntList createList()>
procedure IntList$IntList$createList$2233() returns (__ret : ref) {
var r135 : ref;
var i136 : int;
var $r033 : ref;
var $i034 : int;
	 //  @line: 14
Block41:
	 //  @line: 14
	r135 := $null;
	 //  @line: 15
	 call i136 := int$Random$random$2237();
	 goto Block42;
	 //  @line: 16
Block42:
	 goto Block45, Block43;
	 //  @line: 16
Block45:
	 //  @line: 16
	 assume ($negInt(($leint((i136), (0))))==1);
	 //  @line: 17
	$r033 := $newvariable((46));
	 assume ($neref(($newvariable((46))), ($null))==1);
	 //  @line: 17
	 call $i034 := int$Random$random$2237();
	 assert ($neref(($r033), ($null))==1);
	 //  @line: 17
	 call void$IntList$$la$init$ra$$2231(($r033), ($i034), (r135));
	 //  @line: 17
	r135 := $r033;
	 //  @line: 18
	i136 := $addint((i136), (-1));
	 goto Block42;
	 //  @line: 16
Block43:
	 assume ($leint((i136), (0))==1);
	 goto Block44;
	 //  @line: 20
Block44:
	 //  @line: 20
	__ret := r135;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqreal(x : realVar, y : realVar) returns (__ret : int) {
if (x == y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $ltint(x : int, y : int) returns (__ret : int) {
if (x < y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $newvariable($param00 : int) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $divint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $geint(x : int, y : int) returns (__ret : int) {
if (x >= y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $mulint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $leint(x : int, y : int) returns (__ret : int) {
if (x <= y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $shlref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 4
// <TreeList: void <init>(Tree,TreeList)>
procedure void$TreeList$$la$init$ra$$2234(__this : ref, $param_0 : ref, $param_1 : ref)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var r138 : ref;
var r037 : ref;
var r239 : ref;
Block47:
	r037 := __this;
	r138 := $param_0;
	r239 := $param_1;
	 assert ($neref((r037), ($null))==1);
	 //  @line: 5
	 call void$java.lang.Object$$la$init$ra$$28((r037));
	 assert ($neref((r037), ($null))==1);
	 //  @line: 6
	$HeapVar[r037, Tree$TreeList$value255] := r138;
	 assert ($neref((r037), ($null))==1);
	 //  @line: 7
	$HeapVar[r037, TreeList$TreeList$next256] := r239;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqrefarray($param00 : [int]ref, $param11 : [int]ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $reftointarr($param00 : ref) returns (__ret : [int]int);



	 //  @line: 10
// <Tree: void <init>()>
procedure void$Tree$$la$init$ra$$2240(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r051 : ref;
Block53:
	r051 := __this;
	 assert ($neref((r051), ($null))==1);
	 //  @line: 11
	 call void$java.lang.Object$$la$init$ra$$28((r051));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $ltref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $mulreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



	 //  @line: 50
// <Tree: void main(java.lang.String[])>
procedure void$Tree$main$2243($param_0 : [int]ref)
  modifies $stringSize, java.lang.String$lp$$rp$$Random$args257;
 {
var r068 : [int]ref;

 //temp local variables 
var $freshlocal0 : ref;

Block81:
	r068 := $param_0;
	 //  @line: 51
	java.lang.String$lp$$rp$$Random$args257 := r068;
	 //  @line: 52
	 call $freshlocal0 := Tree$Tree$createTree$2242();
	 return;
}


// procedure is generated by joogie.
function {:inline true} $shrref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $ushrreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $shrreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $divreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $orint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $reftorealarr($param00 : ref) returns (__ret : [int]realVar);



// procedure is generated by joogie.
function {:inline true} $cmpref(x : ref, y : ref) returns (__ret : int) {
if ($ltref((x), (y)) == 1) then 1 else if ($eqref((x), (y)) == 1) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $realtoint($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $geref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 9
// <IntList: void <init>()>
procedure void$IntList$$la$init$ra$$2232(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r029 : ref;
Block40:
	r029 := __this;
	 assert ($neref((r029), ($null))==1);
	 //  @line: 10
	 call void$java.lang.Object$$la$init$ra$$28((r029));
	 return;
}


	 //  @line: 2
// <FlattenRTA: IntList flatten(TreeList)>
procedure IntList$FlattenRTA$flatten$2229($param_0 : ref) returns (__ret : ref)
  modifies $HeapVar;
 {
var $r510 : ref;
var $r611 : ref;
var $r712 : ref;
var r02 : ref;
var $r47 : ref;
var $i08 : int;
var r15 : ref;
var r26 : ref;
var r39 : ref;
var r914 : ref;
var r813 : ref;
Block17:
	r02 := $param_0;
	 //  @line: 3
	r813 := r02;
	 //  @line: 4
	r914 := $null;
	 goto Block18;
	 //  @line: 5
Block18:
	 goto Block19, Block21;
	 //  @line: 5
Block19:
	 assume ($eqref((r813), ($null))==1);
	 goto Block20;
	 //  @line: 5
Block21:
	 //  @line: 5
	 assume ($negInt(($eqref((r813), ($null))))==1);
	 assert ($neref((r813), ($null))==1);
	 //  @line: 6
	r15 := $HeapVar[r813, Tree$TreeList$value255];
	 goto Block22;
	 //  @line: 19
Block20:
	 goto Block31, Block29;
	 //  @line: 7
Block22:
	 goto Block23, Block25;
	 //  @line: 19
Block31:
	 //  @line: 19
	 assume ($negInt(($eqref((r813), (r02))))==1);
	 goto Block30;
	 //  @line: 19
Block29:
	 assume ($eqref((r813), (r02))==1);
	 goto Block30;
	 //  @line: 7
Block23:
	 assume ($eqref((r15), ($null))==1);
	 goto Block24;
	 //  @line: 7
Block25:
	 //  @line: 7
	 assume ($negInt(($eqref((r15), ($null))))==1);
	 //  @line: 8
	r26 := r914;
	 //  @line: 9
	$r47 := $newvariable((26));
	 assume ($neref(($newvariable((26))), ($null))==1);
	 assert ($neref(($r47), ($null))==1);
	 //  @line: 9
	 call void$IntList$$la$init$ra$$2232(($r47));
	 //  @line: 9
	r914 := $r47;
	 assert ($neref((r15), ($null))==1);
	 //  @line: 10
	$i08 := $HeapVar[r15, int$Tree$value0];
	 assert ($neref((r914), ($null))==1);
	 //  @line: 10
	$HeapVar[r914, int$IntList$value0] := $i08;
	 assert ($neref((r914), ($null))==1);
	 //  @line: 11
	$HeapVar[r914, IntList$IntList$next254] := r26;
	 //  @line: 12
	r39 := r813;
	 //  @line: 13
	$r510 := $newvariable((27));
	 assume ($neref(($newvariable((27))), ($null))==1);
	 assert ($neref(($r510), ($null))==1);
	 //  @line: 13
	 call void$TreeList$$la$init$ra$$2235(($r510));
	 //  @line: 13
	r813 := $r510;
	 assert ($neref((r15), ($null))==1);
	 //  @line: 14
	$r611 := $HeapVar[r15, Tree$Tree$left258];
	 assert ($neref((r813), ($null))==1);
	 //  @line: 14
	$HeapVar[r813, Tree$TreeList$value255] := $r611;
	 assert ($neref((r813), ($null))==1);
	 //  @line: 15
	$HeapVar[r813, TreeList$TreeList$next256] := r39;
	 assert ($neref((r15), ($null))==1);
	 //  @line: 16
	$r712 := $HeapVar[r15, Tree$Tree$right259];
	 assert ($neref((r39), ($null))==1);
	 //  @line: 16
	$HeapVar[r39, Tree$TreeList$value255] := $r712;
	 goto Block28;
	 //  @line: 20
Block30:
	 //  @line: 20
	__ret := r914;
	 return;
	 //  @line: 7
Block24:
	 assert ($neref((r813), ($null))==1);
	 //  @line: 7
	r813 := $HeapVar[r813, TreeList$TreeList$next256];
	 goto Block28;
	 //  @line: 18
Block28:
	 goto Block18;
}


	 //  @line: 4
// <IntList: void <init>(int,IntList)>
procedure void$IntList$$la$init$ra$$2231(__this : ref, $param_0 : int, $param_1 : ref)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var r128 : ref;
var i027 : int;
var r026 : ref;
Block39:
	r026 := __this;
	i027 := $param_0;
	r128 := $param_1;
	 assert ($neref((r026), ($null))==1);
	 //  @line: 5
	 call void$java.lang.Object$$la$init$ra$$28((r026));
	 assert ($neref((r026), ($null))==1);
	 //  @line: 6
	$HeapVar[r026, int$IntList$value0] := i027;
	 assert ($neref((r026), ($null))==1);
	 //  @line: 7
	$HeapVar[r026, IntList$IntList$next254] := r128;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $orreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $eqint(x : int, y : int) returns (__ret : int) {
if (x == y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $ushrref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 5
// <Tree: void <init>(Tree,Tree)>
procedure void$Tree$$la$init$ra$$2239(__this : ref, $param_0 : ref, $param_1 : ref)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var r149 : ref;
var r048 : ref;
var r250 : ref;
Block52:
	r048 := __this;
	r149 := $param_0;
	r250 := $param_1;
	 assert ($neref((r048), ($null))==1);
	 //  @line: 6
	 call void$java.lang.Object$$la$init$ra$$28((r048));
	 assert ($neref((r048), ($null))==1);
	 //  @line: 7
	$HeapVar[r048, Tree$Tree$left258] := r149;
	 assert ($neref((r048), ($null))==1);
	 //  @line: 8
	$HeapVar[r048, Tree$Tree$right259] := r250;
	 return;
}


	 //  @line: 5
// <Random: int random()>
procedure int$Random$random$2237() returns (__ret : int)
  modifies $stringSize, int$Random$index0;
 {
var $i145 : int;
var $i347 : int;
var r044 : ref;
var $r143 : [int]ref;
var $i246 : int;
var $i042 : int;
	 //  @line: 6
Block50:
	 //  @line: 6
	$r143 := java.lang.String$lp$$rp$$Random$args257;
	 //  @line: 6
	$i042 := int$Random$index0;
	 assert ($geint(($i042), (0))==1);
	 assert ($ltint(($i042), ($refArrSize[$r143[$arrSizeIdx]]))==1);
	 //  @line: 6
	r044 := $r143[$i042];
	 //  @line: 7
	$i145 := int$Random$index0;
	 //  @line: 7
	$i246 := $addint(($i145), (1));
	 //  @line: 7
	int$Random$index0 := $i246;
	$i347 := $stringSize[r044];
	 //  @line: 8
	__ret := $i347;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $modref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $eqintarray($param00 : [int]int, $param11 : [int]int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $negRef($param00 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $lereal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $nereal(x : realVar, y : realVar) returns (__ret : int) {
if (x != y) then 1 else 0
}


	 //  @line: 14
// <Tree: Tree createNode()>
procedure Tree$Tree$createNode$2241() returns (__ret : ref)
  modifies $HeapVar;
 {
var r053 : ref;
var $i054 : int;
var $r152 : ref;
	 //  @line: 15
Block54:
	 //  @line: 15
	$r152 := $newvariable((55));
	 assume ($neref(($newvariable((55))), ($null))==1);
	 assert ($neref(($r152), ($null))==1);
	 //  @line: 15
	 call void$Tree$$la$init$ra$$2240(($r152));
	 //  @line: 15
	r053 := $r152;
	 //  @line: 16
	 call $i054 := int$Random$random$2237();
	 assert ($neref((r053), ($null))==1);
	 //  @line: 16
	$HeapVar[r053, int$Tree$value0] := $i054;
	 //  @line: 17
	__ret := r053;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $instanceof($param00 : ref, $param11 : classConst) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $xorref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 20
// <Tree: Tree createTree()>
procedure Tree$Tree$createTree$2242() returns (__ret : ref)
  modifies $HeapVar;
 {
var r058 : ref;
var i061 : int;
var $r162 : ref;
var $r465 : ref;
var $r364 : ref;
var r567 : ref;
var $r263 : ref;
var i166 : int;
	 //  @line: 21
Block56:
	 //  @line: 21
	 call i166 := int$Random$random$2237();
	 goto Block57;
	 //  @line: 22
Block57:
	 goto Block60, Block58;
	 //  @line: 22
Block60:
	 //  @line: 22
	 assume ($negInt(($neint((i166), (0))))==1);
	 //  @line: 23
	__ret := $null;
	 return;
	 //  @line: 22
Block58:
	 assume ($neint((i166), (0))==1);
	 goto Block59;
	 //  @line: 25
Block59:
	 //  @line: 25
	 call r058 := Tree$Tree$createNode$2241();
	 goto Block61;
	 //  @line: 26
Block61:
	 //  @line: 26
	r567 := r058;
	 goto Block62;
	 //  @line: 28
Block62:
	 goto Block65, Block63;
	 //  @line: 28
Block65:
	 //  @line: 28
	 assume ($negInt(($leint((i166), (0))))==1);
	 //  @line: 29
	 call i061 := int$Random$random$2237();
	 goto Block66;
	 //  @line: 28
Block63:
	 assume ($leint((i166), (0))==1);
	 goto Block64;
	 //  @line: 30
Block66:
	 goto Block69, Block67;
	 //  @line: 48
Block64:
	 //  @line: 48
	__ret := r058;
	 return;
	 //  @line: 30
Block69:
	 //  @line: 30
	 assume ($negInt(($leint((i061), (0))))==1);
	 assert ($neref((r567), ($null))==1);
	 //  @line: 31
	$r364 := $HeapVar[r567, Tree$Tree$left258];
	 goto Block70;
	 //  @line: 30
Block67:
	 assume ($leint((i061), (0))==1);
	 goto Block68;
	 //  @line: 31
Block70:
	 goto Block73, Block71;
	 //  @line: 38
Block68:
	 assert ($neref((r567), ($null))==1);
	 //  @line: 38
	$r162 := $HeapVar[r567, Tree$Tree$right259];
	 goto Block76;
	 //  @line: 31
Block73:
	 //  @line: 31
	 assume ($negInt(($neref(($r364), ($null))))==1);
	 //  @line: 32
	 call $r465 := Tree$Tree$createNode$2241();
	 assert ($neref((r567), ($null))==1);
	 //  @line: 32
	$HeapVar[r567, Tree$Tree$left258] := $r465;
	 //  @line: 33
	r567 := r058;
	 goto Block74;
	 //  @line: 31
Block71:
	 assume ($neref(($r364), ($null))==1);
	 goto Block72;
	 //  @line: 38
Block76:
	 goto Block77, Block79;
	 //  @line: 45
Block74:
	 //  @line: 45
	i166 := $addint((i166), (-1));
	 goto Block80;
	 //  @line: 35
Block72:
	 assert ($neref((r567), ($null))==1);
	 //  @line: 35
	r567 := $HeapVar[r567, Tree$Tree$left258];
	 goto Block75;
	 //  @line: 38
Block77:
	 assume ($neref(($r162), ($null))==1);
	 goto Block78;
	 //  @line: 38
Block79:
	 //  @line: 38
	 assume ($negInt(($neref(($r162), ($null))))==1);
	 //  @line: 39
	 call $r263 := Tree$Tree$createNode$2241();
	 assert ($neref((r567), ($null))==1);
	 //  @line: 39
	$HeapVar[r567, Tree$Tree$right259] := $r263;
	 //  @line: 40
	r567 := r058;
	 goto Block74;
	 //  @line: 46
Block80:
	 goto Block62;
	 //  @line: 35
Block75:
	 goto Block74;
	 //  @line: 42
Block78:
	 assert ($neref((r567), ($null))==1);
	 //  @line: 42
	r567 := $HeapVar[r567, Tree$Tree$right259];
	 goto Block74;
}


// procedure is generated by joogie.
function {:inline true} $orref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $intarrtoref($param00 : [int]int) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $subreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shlreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $negInt(x : int) returns (__ret : int) {
if (x == 0) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $gereal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 9
// <TreeList: void <init>()>
procedure void$TreeList$$la$init$ra$$2235(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r040 : ref;
Block48:
	r040 := __this;
	 assert ($neref((r040), ($null))==1);
	 //  @line: 10
	 call void$java.lang.Object$$la$init$ra$$28((r040));
	 return;
}


// <java.lang.Object: void <init>()>
procedure void$java.lang.Object$$la$init$ra$$28(__this : ref);



// procedure is generated by joogie.
function {:inline true} $eqref(x : ref, y : ref) returns (__ret : int) {
if (x == y) then 1 else 0
}


// <java.lang.String: int length()>
procedure int$java.lang.String$length$59(__this : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $cmpint(x : int, y : int) returns (__ret : int) {
if (x < y) then 1 else if (x == y) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $andint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $andreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $shlint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $xorint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $subint(x : int, y : int) returns (__ret : int) {
(x - y)
}


