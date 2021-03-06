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



var java.lang.String$lp$$rp$$Random$args257 : [int]ref;
var int$Random$index0 : int;
var Tree$Tree$right255 : Field ref;
var Tree$Tree$left254 : Field ref;
var java.lang.Object$Tree$value256 : Field ref;


// procedure is generated by joogie.
function {:inline true} $neref(x : ref, y : ref) returns (__ret : int) {
if (x != y) then 1 else 0
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



	 //  @line: 14
// <Tree: Tree createNode()>
procedure Tree$Tree$createNode$2233() returns (__ret : ref)
  modifies $HeapVar;
 {
var $r128 : ref;
var r029 : ref;
var $r230 : ref;
	 //  @line: 15
Block37:
	 //  @line: 15
	$r128 := $newvariable((38));
	 assume ($neref(($newvariable((38))), ($null))==1);
	 assert ($neref(($r128), ($null))==1);
	 //  @line: 15
	 call void$Tree$$la$init$ra$$2232(($r128));
	 //  @line: 15
	r029 := $r128;
	 //  @line: 16
	$r230 := $newvariable((39));
	 assume ($neref(($newvariable((39))), ($null))==1);
	 assert ($neref(($r230), ($null))==1);
	 //  @line: 16
	 call void$java.lang.Object$$la$init$ra$$28(($r230));
	 assert ($neref((r029), ($null))==1);
	 //  @line: 16
	$HeapVar[r029, java.lang.Object$Tree$value256] := $r230;
	 //  @line: 17
	__ret := r029;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $negReal($param00 : realVar) returns (__ret : int);



	 //  @line: 50
// <Tree: void main(java.lang.String[])>
procedure void$Tree$main$2235($param_0 : [int]ref)
  modifies java.lang.String$lp$$rp$$Random$args257, $stringSize;
 {
var r044 : [int]ref;

 //temp local variables 
var $freshlocal0 : ref;

Block65:
	r044 := $param_0;
	 //  @line: 51
	java.lang.String$lp$$rp$$Random$args257 := r044;
	 //  @line: 52
	 call $freshlocal0 := Tree$Tree$createTree$2234();
	 return;
}


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


// <java.lang.String: int length()>
procedure int$java.lang.String$length$59(__this : ref) returns (__ret : int);



// <Random: void <init>()>
procedure void$Random$$la$init$ra$$2236(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r046 : ref;
Block66:
	r046 := __this;
	 assert ($neref((r046), ($null))==1);
	 //  @line: 1
	 call void$java.lang.Object$$la$init$ra$$28((r046));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $ltreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



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



// procedure is generated by joogie.
function {:inline true} $cmpreal(x : realVar, y : realVar) returns (__ret : int) {
if ($ltreal((x), (y)) == 1) then 1 else if ($eqreal((x), (y)) == 1) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $addreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $gtreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 2
// <DuplicateTreePath: void main(java.lang.String[])>
procedure void$DuplicateTreePath$main$2229($param_0 : [int]ref)
  modifies java.lang.String$lp$$rp$$Random$args257, $stringSize;
 {
var r14 : ref;
var r02 : [int]ref;
Block17:
	r02 := $param_0;
	 //  @line: 3
	java.lang.String$lp$$rp$$Random$args257 := r02;
	 //  @line: 4
	 call r14 := Tree$Tree$createTree$2234();
	 //  @line: 5
	 call void$DuplicateTreePath$duplicateRandomPath$2230((r14));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $eqreal(x : realVar, y : realVar) returns (__ret : int) {
if (x == y) then 1 else 0
}


	 //  @line: 10
// <Tree: void <init>()>
procedure void$Tree$$la$init$ra$$2232(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r027 : ref;
Block36:
	r027 := __this;
	 assert ($neref((r027), ($null))==1);
	 //  @line: 11
	 call void$java.lang.Object$$la$init$ra$$28((r027));
	 return;
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



// procedure is generated by joogie.
function {:inline true} $eqrefarray($param00 : [int]ref, $param11 : [int]ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $reftointarr($param00 : ref) returns (__ret : [int]int);



// procedure is generated by joogie.
function {:inline true} $ltref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $mulreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shrref($param00 : ref, $param11 : ref) returns (__ret : int);



// <java.lang.Object: void <init>()>
procedure void$java.lang.Object$$la$init$ra$$28(__this : ref);



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


	 //  @line: 2
// <Random: void <clinit>()>
procedure void$Random$$la$clinit$ra$$2238()
  modifies int$Random$index0;
 {
	 //  @line: 3
Block68:
	 //  @line: 3
	int$Random$index0 := 0;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $realtoint($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $geref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 8
// <DuplicateTreePath: void duplicateRandomPath(Tree)>
procedure void$DuplicateTreePath$duplicateRandomPath$2230($param_0 : ref)
  modifies $HeapVar;
 {
var r1321 : ref;
var r05 : ref;
var $i07 : int;
var $r18 : ref;
var $r513 : ref;
var $r29 : ref;
var $r1119 : ref;
var $r614 : ref;
var r1422 : ref;
var $r917 : ref;
var $r411 : ref;
var $r310 : ref;
var $r1018 : ref;
var $r715 : ref;
var $r816 : ref;
var $r1220 : ref;
var r1523 : ref;
Block18:
	r05 := $param_0;
	 //  @line: 9
	r1321 := r05;
	 goto Block19;
	 //  @line: 10
Block19:
	 goto Block20, Block22;
	 //  @line: 10
Block20:
	 assume ($eqref((r1321), ($null))==1);
	 goto Block21;
	 //  @line: 10
Block22:
	 //  @line: 10
	 assume ($negInt(($eqref((r1321), ($null))))==1);
	 //  @line: 11
	 call $i07 := int$Random$random$2237();
	 goto Block23;
	 //  @line: 27
Block21:
	 return;
	 //  @line: 11
Block23:
	 goto Block24, Block26;
	 //  @line: 11
Block24:
	 assume ($geint(($i07), (42))==1);
	 goto Block25;
	 //  @line: 11
Block26:
	 //  @line: 11
	 assume ($negInt(($geint(($i07), (42))))==1);
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 10
	$r715 := $HeapVar[r1321, Tree$Tree$left254];
	 goto Block27;
	 //  @line: 11
Block25:
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 11
	$r18 := $HeapVar[r1321, Tree$Tree$right255];
	 goto Block31;
	 //  @line: 10
Block27:
	 goto Block29, Block28;
	 //  @line: 11
Block31:
	 goto Block32, Block33;
	 //  @line: 10
Block29:
	 //  @line: 10
	 assume ($negInt(($eqref(($r715), ($null))))==1);
	 //  @line: 12
	$r816 := $newvariable((30));
	 assume ($neref(($newvariable((30))), ($null))==1);
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 12
	$r1018 := $HeapVar[r1321, Tree$Tree$left254];
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 12
	$r917 := $HeapVar[r1321, Tree$Tree$right255];
	 assert ($neref(($r816), ($null))==1);
	 //  @line: 12
	 call void$Tree$$la$init$ra$$2231(($r816), ($r1018), ($r917));
	 //  @line: 12
	r1422 := $r816;
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 13
	$r1119 := $HeapVar[r1321, java.lang.Object$Tree$value256];
	 assert ($neref((r1422), ($null))==1);
	 //  @line: 13
	$HeapVar[r1422, java.lang.Object$Tree$value256] := $r1119;
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 14
	$HeapVar[r1321, Tree$Tree$right255] := $null;
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 15
	$HeapVar[r1321, Tree$Tree$left254] := r1422;
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 16
	$r1220 := $HeapVar[r1321, Tree$Tree$left254];
	 assert ($neref(($r1220), ($null))==1);
	 //  @line: 16
	r1321 := $HeapVar[$r1220, Tree$Tree$left254];
	 goto Block19;
	 //  @line: 10
Block28:
	 assume ($eqref(($r715), ($null))==1);
	 goto Block25;
	 //  @line: 11
Block32:
	 assume ($eqref(($r18), ($null))==1);
	 goto Block21;
	 //  @line: 11
Block33:
	 //  @line: 11
	 assume ($negInt(($eqref(($r18), ($null))))==1);
	 //  @line: 18
	$r29 := $newvariable((34));
	 assume ($neref(($newvariable((34))), ($null))==1);
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 18
	$r411 := $HeapVar[r1321, Tree$Tree$left254];
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 18
	$r310 := $HeapVar[r1321, Tree$Tree$right255];
	 assert ($neref(($r29), ($null))==1);
	 //  @line: 18
	 call void$Tree$$la$init$ra$$2231(($r29), ($r411), ($r310));
	 //  @line: 18
	r1523 := $r29;
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 19
	$r513 := $HeapVar[r1321, java.lang.Object$Tree$value256];
	 assert ($neref((r1523), ($null))==1);
	 //  @line: 19
	$HeapVar[r1523, java.lang.Object$Tree$value256] := $r513;
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 20
	$HeapVar[r1321, Tree$Tree$left254] := $null;
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 21
	$HeapVar[r1321, Tree$Tree$right255] := r1523;
	 assert ($neref((r1321), ($null))==1);
	 //  @line: 22
	$r614 := $HeapVar[r1321, Tree$Tree$right255];
	 assert ($neref(($r614), ($null))==1);
	 //  @line: 22
	r1321 := $HeapVar[$r614, Tree$Tree$right255];
	 goto Block19;
}


// procedure is generated by joogie.
function {:inline true} $orreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $eqint(x : int, y : int) returns (__ret : int) {
if (x == y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $ushrref($param00 : ref, $param11 : ref) returns (__ret : int);



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


// procedure is generated by joogie.
function {:inline true} $instanceof($param00 : ref, $param11 : classConst) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $xorref($param00 : ref, $param11 : ref) returns (__ret : int);



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



// procedure is generated by joogie.
function {:inline true} $eqref(x : ref, y : ref) returns (__ret : int) {
if (x == y) then 1 else 0
}


	 //  @line: 5
// <Random: int random()>
procedure int$Random$random$2237() returns (__ret : int)
  modifies int$Random$index0, $stringSize;
 {
var r049 : ref;
var $i047 : int;
var $i150 : int;
var $r148 : [int]ref;
var $i352 : int;
var $i251 : int;
	 //  @line: 6
Block67:
	 //  @line: 6
	$r148 := java.lang.String$lp$$rp$$Random$args257;
	 //  @line: 6
	$i047 := int$Random$index0;
	 assert ($geint(($i047), (0))==1);
	 assert ($ltint(($i047), ($refArrSize[$r148[$arrSizeIdx]]))==1);
	 //  @line: 6
	r049 := $r148[$i047];
	 //  @line: 7
	$i150 := int$Random$index0;
	 //  @line: 7
	$i251 := $addint(($i150), (1));
	 //  @line: 7
	int$Random$index0 := $i251;
	$i352 := $stringSize[r049];
	 //  @line: 8
	__ret := $i352;
	 return;
}


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



// <DuplicateTreePath: void <init>()>
procedure void$DuplicateTreePath$$la$init$ra$$2228(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r01 : ref;
Block16:
	r01 := __this;
	 assert ($neref((r01), ($null))==1);
	 //  @line: 1
	 call void$java.lang.Object$$la$init$ra$$28((r01));
	 return;
}


	 //  @line: 20
// <Tree: Tree createTree()>
procedure Tree$Tree$createTree$2234() returns (__ret : ref)
  modifies $HeapVar;
 {
var $r138 : ref;
var i037 : int;
var $r239 : ref;
var $r441 : ref;
var i142 : int;
var r543 : ref;
var $r340 : ref;
var r034 : ref;
	 //  @line: 21
Block40:
	 //  @line: 21
	 call i142 := int$Random$random$2237();
	 goto Block41;
	 //  @line: 22
Block41:
	 goto Block44, Block42;
	 //  @line: 22
Block44:
	 //  @line: 22
	 assume ($negInt(($neint((i142), (0))))==1);
	 //  @line: 23
	__ret := $null;
	 return;
	 //  @line: 22
Block42:
	 assume ($neint((i142), (0))==1);
	 goto Block43;
	 //  @line: 25
Block43:
	 //  @line: 25
	 call r034 := Tree$Tree$createNode$2233();
	 goto Block45;
	 //  @line: 26
Block45:
	 //  @line: 26
	r543 := r034;
	 goto Block46;
	 //  @line: 28
Block46:
	 goto Block49, Block47;
	 //  @line: 28
Block49:
	 //  @line: 28
	 assume ($negInt(($leint((i142), (0))))==1);
	 //  @line: 29
	 call i037 := int$Random$random$2237();
	 goto Block50;
	 //  @line: 28
Block47:
	 assume ($leint((i142), (0))==1);
	 goto Block48;
	 //  @line: 30
Block50:
	 goto Block53, Block51;
	 //  @line: 48
Block48:
	 //  @line: 48
	__ret := r034;
	 return;
	 //  @line: 30
Block53:
	 //  @line: 30
	 assume ($negInt(($leint((i037), (0))))==1);
	 assert ($neref((r543), ($null))==1);
	 //  @line: 31
	$r340 := $HeapVar[r543, Tree$Tree$left254];
	 goto Block54;
	 //  @line: 30
Block51:
	 assume ($leint((i037), (0))==1);
	 goto Block52;
	 //  @line: 31
Block54:
	 goto Block55, Block57;
	 //  @line: 38
Block52:
	 assert ($neref((r543), ($null))==1);
	 //  @line: 38
	$r138 := $HeapVar[r543, Tree$Tree$right255];
	 goto Block60;
	 //  @line: 31
Block55:
	 assume ($neref(($r340), ($null))==1);
	 goto Block56;
	 //  @line: 31
Block57:
	 //  @line: 31
	 assume ($negInt(($neref(($r340), ($null))))==1);
	 //  @line: 32
	 call $r441 := Tree$Tree$createNode$2233();
	 assert ($neref((r543), ($null))==1);
	 //  @line: 32
	$HeapVar[r543, Tree$Tree$left254] := $r441;
	 //  @line: 33
	r543 := r034;
	 goto Block58;
	 //  @line: 38
Block60:
	 goto Block63, Block61;
	 //  @line: 35
Block56:
	 assert ($neref((r543), ($null))==1);
	 //  @line: 35
	r543 := $HeapVar[r543, Tree$Tree$left254];
	 goto Block59;
	 //  @line: 45
Block58:
	 //  @line: 45
	i142 := $addint((i142), (-1));
	 goto Block64;
	 //  @line: 38
Block63:
	 //  @line: 38
	 assume ($negInt(($neref(($r138), ($null))))==1);
	 //  @line: 39
	 call $r239 := Tree$Tree$createNode$2233();
	 assert ($neref((r543), ($null))==1);
	 //  @line: 39
	$HeapVar[r543, Tree$Tree$right255] := $r239;
	 //  @line: 40
	r543 := r034;
	 goto Block58;
	 //  @line: 38
Block61:
	 assume ($neref(($r138), ($null))==1);
	 goto Block62;
	 //  @line: 35
Block59:
	 goto Block58;
	 //  @line: 46
Block64:
	 goto Block46;
	 //  @line: 42
Block62:
	 assert ($neref((r543), ($null))==1);
	 //  @line: 42
	r543 := $HeapVar[r543, Tree$Tree$right255];
	 goto Block58;
}


// procedure is generated by joogie.
function {:inline true} $subint(x : int, y : int) returns (__ret : int) {
(x - y)
}


	 //  @line: 5
// <Tree: void <init>(Tree,Tree)>
procedure void$Tree$$la$init$ra$$2231(__this : ref, $param_0 : ref, $param_1 : ref)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var r024 : ref;
var r226 : ref;
var r125 : ref;
Block35:
	r024 := __this;
	r125 := $param_0;
	r226 := $param_1;
	 assert ($neref((r024), ($null))==1);
	 //  @line: 6
	 call void$java.lang.Object$$la$init$ra$$28((r024));
	 assert ($neref((r024), ($null))==1);
	 //  @line: 7
	$HeapVar[r024, Tree$Tree$left254] := r125;
	 assert ($neref((r024), ($null))==1);
	 //  @line: 8
	$HeapVar[r024, Tree$Tree$right255] := r226;
	 return;
}


