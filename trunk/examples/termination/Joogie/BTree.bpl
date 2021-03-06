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



var BTree$BTree$left254 : Field ref;
var BTree$BTree$right255 : Field ref;


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



// procedure is generated by joogie.
function {:inline true} $negReal($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $ushrint($param00 : int, $param11 : int) returns (__ret : int);



	 //  @line: 14
// <BTree: void <init>(int)>
procedure void$BTree$$la$init$ra$$2228(__this : ref, $param_0 : int)
  modifies $HeapVar;
  requires ($neref((__this), ($null))==1);
 {
var $i14 : int;
var $r13 : ref;
var $i26 : int;
var r01 : ref;
var i02 : int;
var $r25 : ref;
Block16:
	r01 := __this;
	i02 := $param_0;
	 assert ($neref((r01), ($null))==1);
	 //  @line: 15
	 call void$java.lang.Object$$la$init$ra$$28((r01));
	 goto Block17;
	 //  @line: 16
Block17:
	 goto Block18, Block20;
	 //  @line: 16
Block18:
	 assume ($leint((i02), (1))==1);
	 goto Block19;
	 //  @line: 16
Block20:
	 //  @line: 16
	 assume ($negInt(($leint((i02), (1))))==1);
	 //  @line: 17
	$r13 := $newvariable((21));
	 assume ($neref(($newvariable((21))), ($null))==1);
	 //  @line: 17
	$i14 := $subint((i02), (1));
	 assert ($neref(($r13), ($null))==1);
	 //  @line: 17
	 call void$BTree$$la$init$ra$$2228(($r13), ($i14));
	 assert ($neref((r01), ($null))==1);
	 //  @line: 17
	$HeapVar[r01, BTree$BTree$left254] := $r13;
	 //  @line: 18
	$r25 := $newvariable((22));
	 assume ($neref(($newvariable((22))), ($null))==1);
	 //  @line: 18
	$i26 := $subint((i02), (1));
	 assert ($neref(($r25), ($null))==1);
	 //  @line: 18
	 call void$BTree$$la$init$ra$$2228(($r25), ($i26));
	 assert ($neref((r01), ($null))==1);
	 //  @line: 18
	$HeapVar[r01, BTree$BTree$right255] := $r25;
	 goto Block19;
	 //  @line: 20
Block19:
	 return;
}


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


	 //  @line: 22
// <BTree: int height()>
procedure int$BTree$height$2229(__this : ref) returns (__ret : int)  requires ($neref((__this), ($null))==1);
 {
var $r414 : ref;
var $r29 : ref;
var $r18 : ref;
var $i012 : int;
var $i113 : int;
var $r518 : ref;
var $i317 : int;
var $r310 : ref;
var r07 : ref;
var $i216 : int;
Block23:
	r07 := __this;
	 assert ($neref((r07), ($null))==1);
	 //  @line: 23
	$r18 := $HeapVar[r07, BTree$BTree$left254];
	 goto Block24;
	 //  @line: 23
Block24:
	 goto Block25, Block27;
	 //  @line: 23
Block25:
	 assume ($neref(($r18), ($null))==1);
	 goto Block26;
	 //  @line: 23
Block27:
	 //  @line: 23
	 assume ($negInt(($neref(($r18), ($null))))==1);
	 assert ($neref((r07), ($null))==1);
	 //  @line: 25
	$r518 := $HeapVar[r07, BTree$BTree$right255];
	 goto Block28;
	 //  @line: 24
Block26:
	 assert ($neref((r07), ($null))==1);
	 //  @line: 24
	$r29 := $HeapVar[r07, BTree$BTree$left254];
	 goto Block31;
	 //  @line: 25
Block28:
	 goto Block29, Block30;
	 //  @line: 24
Block31:
	 goto Block32, Block34;
	 //  @line: 25
Block29:
	 assume ($neref(($r518), ($null))==1);
	 goto Block26;
	 //  @line: 25
Block30:
	 //  @line: 25
	 assume ($negInt(($neref(($r518), ($null))))==1);
	 //  @line: 24
	__ret := 1;
	 return;
	 //  @line: 24
Block32:
	 assume ($neref(($r29), ($null))==1);
	 goto Block33;
	 //  @line: 24
Block34:
	 //  @line: 24
	 assume ($negInt(($neref(($r29), ($null))))==1);
	 assert ($neref((r07), ($null))==1);
	 //  @line: 25
	$r414 := $HeapVar[r07, BTree$BTree$right255];
	 assert ($neref(($r414), ($null))==1);
	 //  @line: 25
	 call $i216 := int$BTree$height$2229(($r414));
	 //  @line: 25
	$i317 := $addint((1), ($i216));
	 //  @line: 25
	__ret := $i317;
	 return;
	 //  @line: 25
Block33:
	 assert ($neref((r07), ($null))==1);
	 //  @line: 25
	$r310 := $HeapVar[r07, BTree$BTree$left254];
	 goto Block35;
	 //  @line: 25
Block35:
	 assert ($neref(($r310), ($null))==1);
	 //  @line: 25
	 call $i012 := int$BTree$height$2229(($r310));
	 //  @line: 25
	$i113 := $addint((1), ($i012));
	 //  @line: 25
	__ret := $i113;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $realtoint($param00 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $geref($param00 : ref, $param11 : ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $orreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $eqint(x : int, y : int) returns (__ret : int) {
if (x == y) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $ushrref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 28
// <BTree: void main(java.lang.String[])>
procedure void$BTree$main$2230($param_0 : [int]ref) {
var $r219 : ref;
var r022 : [int]ref;
var r120 : ref;

 //temp local variables 
var $freshlocal0 : int;

Block36:
	r022 := $param_0;
	 //  @line: 29
	$r219 := $newvariable((37));
	 assume ($neref(($newvariable((37))), ($null))==1);
	 assert ($neref(($r219), ($null))==1);
	 //  @line: 29
	 call void$BTree$$la$init$ra$$2228(($r219), (5));
	 //  @line: 29
	r120 := $r219;
	 assert ($neref((r120), ($null))==1);
	 //  @line: 30
	 call $freshlocal0 := int$BTree$height$2229((r120));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $modref($param00 : ref, $param11 : ref) returns (__ret : ref);



// <java.lang.Object: void <init>()>
procedure void$java.lang.Object$$la$init$ra$$28(__this : ref);



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


