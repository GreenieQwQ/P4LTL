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



var java.lang.String$lp$$rp$$Random$args254 : [int]ref;
var int$Random$index0 : int;


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



	 //  @line: 2
// <Random: void <clinit>()>
procedure void$Random$$la$clinit$ra$$2232()
  modifies int$Random$index0;
 {
	 //  @line: 3
Block33:
	 //  @line: 3
	int$Random$index0 := 0;
	 return;
}


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


	 //  @line: 5
// <Random: int random()>
procedure int$Random$random$2231() returns (__ret : int)
  modifies $stringSize, int$Random$index0;
 {
var $i018 : int;
var r022 : ref;
var $i220 : int;
var $i525 : int;
var $i119 : int;
var $r117 : [int]ref;
var $i424 : int;
var $i323 : int;
var $r221 : [int]ref;
	 //  @line: 6
Block27:
	 //  @line: 6
	$i119 := int$Random$index0;
	 //  @line: 6
	$r117 := java.lang.String$lp$$rp$$Random$args254;
	 //  @line: 6
	$i018 := $refArrSize[$r117[$arrSizeIdx]];
	 goto Block28;
	 //  @line: 6
Block28:
	 goto Block29, Block31;
	 //  @line: 6
Block29:
	 assume ($ltint(($i119), ($i018))==1);
	 goto Block30;
	 //  @line: 6
Block31:
	 //  @line: 6
	 assume ($negInt(($ltint(($i119), ($i018))))==1);
	 //  @line: 7
	__ret := 0;
	 return;
	 //  @line: 9
Block30:
	 //  @line: 9
	$r221 := java.lang.String$lp$$rp$$Random$args254;
	 goto Block32;
	 //  @line: 9
Block32:
	 //  @line: 9
	$i220 := int$Random$index0;
	 assert ($geint(($i220), (0))==1);
	 assert ($ltint(($i220), ($refArrSize[$r221[$arrSizeIdx]]))==1);
	 //  @line: 9
	r022 := $r221[$i220];
	 //  @line: 10
	$i323 := int$Random$index0;
	 //  @line: 10
	$i424 := $addint(($i323), (1));
	 //  @line: 10
	int$Random$index0 := $i424;
	$i525 := $stringSize[r022];
	 //  @line: 11
	__ret := $i525;
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



// <Random: void <init>()>
procedure void$Random$$la$init$ra$$2230(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r016 : ref;
Block26:
	r016 := __this;
	 assert ($neref((r016), ($null))==1);
	 //  @line: 1
	 call void$java.lang.Object$$la$init$ra$$28((r016));
	 return;
}


// procedure is generated by joogie.
function {:inline true} $modref($param00 : ref, $param11 : ref) returns (__ret : ref);



// procedure is generated by joogie.
function {:inline true} $eqintarray($param00 : [int]int, $param11 : [int]int) returns (__ret : int);



	 //  @line: 2
// <Et4: void main(java.lang.String[])>
procedure void$Et4$main$2229($param_0 : [int]ref)
  modifies java.lang.String$lp$$rp$$Random$args254, $stringSize;
 {
var i313 : int;
var i515 : int;
var $i19 : int;
var r02 : [int]ref;
var $i212 : int;
var i011 : int;
var i414 : int;
Block17:
	r02 := $param_0;
	 //  @line: 3
	java.lang.String$lp$$rp$$Random$args254 := r02;
	 //  @line: 4
	 call i313 := int$Random$random$2231();
	 //  @line: 5
	 call i414 := int$Random$random$2231();
	 //  @line: 6
	 call i515 := int$Random$random$2231();
	 goto Block18;
	 //  @line: 7
Block18:
	 //  @line: 7
	$i19 := $subint((i414), (i515));
	 goto Block19;
	 //  @line: 7
Block19:
	 goto Block20, Block22;
	 //  @line: 7
Block20:
	 assume ($ltint(($i19), (1))==1);
	 goto Block21;
	 //  @line: 7
Block22:
	 //  @line: 7
	 assume ($negInt(($ltint(($i19), (1))))==1);
	 goto Block23;
	 //  @line: 13
Block21:
	 return;
	 //  @line: 13
Block23:
	 goto Block25, Block24;
	 //  @line: 13
Block25:
	 //  @line: 13
	 assume ($negInt(($neint((i313), (i515))))==1);
	 //  @line: 8
	 call i011 := int$Random$random$2231();
	 //  @line: 9
	i414 := 10;
	 //  @line: 10
	$i212 := $addint((i515), (1));
	 //  @line: 10
	i515 := $addint(($i212), (i011));
	 //  @line: 11
	i313 := i515;
	 goto Block18;
	 //  @line: 13
Block24:
	 assume ($neint((i313), (i515))==1);
	 goto Block21;
}


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



// <Et4: void <init>()>
procedure void$Et4$$la$init$ra$$2228(__this : ref)  requires ($neref((__this), ($null))==1);
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
function {:inline true} $negInt(x : int) returns (__ret : int) {
if (x == 0) then 1 else 0
}


// procedure is generated by joogie.
function {:inline true} $gereal($param00 : realVar, $param11 : realVar) returns (__ret : int);



// <java.lang.String: int length()>
procedure int$java.lang.String$length$59(__this : ref) returns (__ret : int);



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



// <java.lang.Object: void <init>()>
procedure void$java.lang.Object$$la$init$ra$$28(__this : ref);



// procedure is generated by joogie.
function {:inline true} $shlint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $xorint($param00 : int, $param11 : int) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $subint(x : int, y : int) returns (__ret : int) {
(x - y)
}


