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



var int$LessLeavesRec.Random$index0 : int;
var LessLeavesRec.Tree$LessLeavesRec.Tree$l254 : Field ref;
var LessLeavesRec.Tree$LessLeavesRec.Tree$r255 : Field ref;
var java.lang.String$lp$$rp$$LessLeavesRec.Random$args256 : [int]ref;
var int$LessLeavesRec.Tree$value0 : Field int;


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


	 //  @line: 6
// <LessLeavesRec.Random: int random()>
procedure int$LessLeavesRec.Random$random$2237() returns (__ret : int)
  modifies int$LessLeavesRec.Random$index0, $stringSize;
 {
var $i147 : int;
var $i046 : int;
var r050 : ref;
var $i452 : int;
var $i553 : int;
var $r249 : [int]ref;
var $i351 : int;
var $i248 : int;
var $r145 : [int]ref;
	 //  @line: 7
Block65:
	 //  @line: 7
	$r145 := java.lang.String$lp$$rp$$LessLeavesRec.Random$args256;
	 //  @line: 7
	$i147 := $refArrSize[$r145[$arrSizeIdx]];
	 //  @line: 7
	$i046 := int$LessLeavesRec.Random$index0;
	 goto Block66;
	 //  @line: 7
Block66:
	 goto Block69, Block67;
	 //  @line: 7
Block69:
	 //  @line: 7
	 assume ($negInt(($gtint(($i147), ($i046))))==1);
	 //  @line: 8
	__ret := 0;
	 return;
	 //  @line: 7
Block67:
	 assume ($gtint(($i147), ($i046))==1);
	 goto Block68;
	 //  @line: 10
Block68:
	 //  @line: 10
	$r249 := java.lang.String$lp$$rp$$LessLeavesRec.Random$args256;
	 goto Block70;
	 //  @line: 10
Block70:
	 //  @line: 10
	$i248 := int$LessLeavesRec.Random$index0;
	 assert ($geint(($i248), (0))==1);
	 assert ($ltint(($i248), ($refArrSize[$r249[$arrSizeIdx]]))==1);
	 //  @line: 10
	r050 := $r249[$i248];
	 //  @line: 11
	$i351 := int$LessLeavesRec.Random$index0;
	 //  @line: 11
	$i452 := $addint(($i351), (1));
	 //  @line: 11
	int$LessLeavesRec.Random$index0 := $i452;
	 goto Block71;
	 //  @line: 12
Block71:
	 goto Block74, Block72;
	 //  @line: 12
Block74:
	 //  @line: 12
	 assume ($negInt(($neref((r050), ($null))))==1);
	 //  @line: 13
	__ret := 0;
	 return;
	 //  @line: 12
Block72:
	 assume ($neref((r050), ($null))==1);
	 goto Block73;
	 //  @line: 15
Block73:
	$i553 := $stringSize[r050];
	 goto Block75;
	 //  @line: 15
Block75:
	 //  @line: 15
	__ret := $i553;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $ltreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 4
// <LessLeavesRec.LessLeavesRec: LessLeavesRec.Tree append(LessLeavesRec.Tree,LessLeavesRec.Tree)>
procedure LessLeavesRec.Tree$LessLeavesRec.LessLeavesRec$append$2229($param_0 : ref, $param_1 : ref) returns (__ret : ref) {
var $r23 : ref;
var $r57 : ref;
var r14 : ref;
var $r35 : ref;
var r02 : ref;
var $r46 : ref;
Block17:
	r02 := $param_0;
	r14 := $param_1;
	 goto Block18;
	 //  @line: 5
Block18:
	 goto Block21, Block19;
	 //  @line: 5
Block21:
	 //  @line: 5
	 assume ($negInt(($neref((r02), ($null))))==1);
	 //  @line: 6
	__ret := r14;
	 return;
	 //  @line: 5
Block19:
	 assume ($neref((r02), ($null))==1);
	 goto Block20;
	 //  @line: 6
Block20:
	 //  @line: 6
	$r23 := $newvariable((22));
	 assume ($neref(($newvariable((22))), ($null))==1);
	 goto Block23;
	 //  @line: 6
Block23:
	 assert ($neref((r02), ($null))==1);
	 //  @line: 6
	$r46 := $HeapVar[r02, LessLeavesRec.Tree$LessLeavesRec.Tree$l254];
	 assert ($neref((r02), ($null))==1);
	 //  @line: 6
	$r35 := $HeapVar[r02, LessLeavesRec.Tree$LessLeavesRec.Tree$r255];
	 //  @line: 6
	 call $r57 := LessLeavesRec.Tree$LessLeavesRec.LessLeavesRec$append$2229(($r35), (r14));
	 assert ($neref(($r23), ($null))==1);
	 //  @line: 6
	 call void$LessLeavesRec.Tree$$la$init$ra$$2232(($r23), ($r46), ($r57));
	 //  @line: 6
	__ret := $r23;
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



// procedure is generated by joogie.
function {:inline true} $cmpreal(x : realVar, y : realVar) returns (__ret : int) {
if ($ltreal((x), (y)) == 1) then 1 else if ($eqreal((x), (y)) == 1) then 0 else -1
}


// procedure is generated by joogie.
function {:inline true} $addreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $gtreal($param00 : realVar, $param11 : realVar) returns (__ret : int);



	 //  @line: 25
// <LessLeavesRec.Tree: LessLeavesRec.Tree createTree()>
procedure LessLeavesRec.Tree$LessLeavesRec.Tree$createTree$2235() returns (__ret : ref)
  modifies $HeapVar;
 {
var r543 : ref;
var $r340 : ref;
var i142 : int;
var i037 : int;
var $r138 : ref;
var r034 : ref;
var $r441 : ref;
var $r239 : ref;
	 //  @line: 26
Block39:
	 //  @line: 26
	 call i142 := int$LessLeavesRec.Random$random$2237();
	 goto Block40;
	 //  @line: 27
Block40:
	 goto Block41, Block43;
	 //  @line: 27
Block41:
	 assume ($neint((i142), (0))==1);
	 goto Block42;
	 //  @line: 27
Block43:
	 //  @line: 27
	 assume ($negInt(($neint((i142), (0))))==1);
	 //  @line: 28
	__ret := $null;
	 return;
	 //  @line: 30
Block42:
	 //  @line: 30
	 call r034 := LessLeavesRec.Tree$LessLeavesRec.Tree$createNode$2234();
	 goto Block44;
	 //  @line: 31
Block44:
	 //  @line: 31
	r543 := r034;
	 goto Block45;
	 //  @line: 33
Block45:
	 goto Block48, Block46;
	 //  @line: 33
Block48:
	 //  @line: 33
	 assume ($negInt(($leint((i142), (0))))==1);
	 //  @line: 34
	 call i037 := int$LessLeavesRec.Random$random$2237();
	 goto Block49;
	 //  @line: 33
Block46:
	 assume ($leint((i142), (0))==1);
	 goto Block47;
	 //  @line: 35
Block49:
	 goto Block50, Block52;
	 //  @line: 53
Block47:
	 //  @line: 53
	__ret := r034;
	 return;
	 //  @line: 35
Block50:
	 assume ($leint((i037), (0))==1);
	 goto Block51;
	 //  @line: 35
Block52:
	 //  @line: 35
	 assume ($negInt(($leint((i037), (0))))==1);
	 assert ($neref((r543), ($null))==1);
	 //  @line: 36
	$r340 := $HeapVar[r543, LessLeavesRec.Tree$LessLeavesRec.Tree$l254];
	 goto Block53;
	 //  @line: 43
Block51:
	 assert ($neref((r543), ($null))==1);
	 //  @line: 43
	$r138 := $HeapVar[r543, LessLeavesRec.Tree$LessLeavesRec.Tree$r255];
	 goto Block59;
	 //  @line: 36
Block53:
	 goto Block56, Block54;
	 //  @line: 43
Block59:
	 goto Block62, Block60;
	 //  @line: 36
Block56:
	 //  @line: 36
	 assume ($negInt(($neref(($r340), ($null))))==1);
	 //  @line: 37
	 call $r441 := LessLeavesRec.Tree$LessLeavesRec.Tree$createNode$2234();
	 assert ($neref((r543), ($null))==1);
	 //  @line: 37
	$HeapVar[r543, LessLeavesRec.Tree$LessLeavesRec.Tree$l254] := $r441;
	 //  @line: 38
	r543 := r034;
	 goto Block57;
	 //  @line: 36
Block54:
	 assume ($neref(($r340), ($null))==1);
	 goto Block55;
	 //  @line: 43
Block62:
	 //  @line: 43
	 assume ($negInt(($neref(($r138), ($null))))==1);
	 //  @line: 44
	 call $r239 := LessLeavesRec.Tree$LessLeavesRec.Tree$createNode$2234();
	 assert ($neref((r543), ($null))==1);
	 //  @line: 44
	$HeapVar[r543, LessLeavesRec.Tree$LessLeavesRec.Tree$r255] := $r239;
	 //  @line: 45
	r543 := r034;
	 goto Block57;
	 //  @line: 43
Block60:
	 assume ($neref(($r138), ($null))==1);
	 goto Block61;
	 //  @line: 50
Block57:
	 //  @line: 50
	i142 := $addint((i142), (-1));
	 goto Block63;
	 //  @line: 40
Block55:
	 assert ($neref((r543), ($null))==1);
	 //  @line: 40
	r543 := $HeapVar[r543, LessLeavesRec.Tree$LessLeavesRec.Tree$l254];
	 goto Block58;
	 //  @line: 47
Block61:
	 assert ($neref((r543), ($null))==1);
	 //  @line: 47
	r543 := $HeapVar[r543, LessLeavesRec.Tree$LessLeavesRec.Tree$r255];
	 goto Block57;
	 //  @line: 51
Block63:
	 goto Block45;
	 //  @line: 40
Block58:
	 goto Block57;
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



// procedure is generated by joogie.
function {:inline true} $eqrefarray($param00 : [int]ref, $param11 : [int]ref) returns (__ret : int);



// procedure is generated by joogie.
function {:inline true} $reftointarr($param00 : ref) returns (__ret : [int]int);



// procedure is generated by joogie.
function {:inline true} $ltref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 1
// <LessLeavesRec.Random: void <init>()>
procedure void$LessLeavesRec.Random$$la$init$ra$$2236(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r044 : ref;
Block64:
	r044 := __this;
	 assert ($neref((r044), ($null))==1);
	 //  @line: 2
	 call void$java.lang.Object$$la$init$ra$$28((r044));
	 return;
}


	 //  @line: 16
// <LessLeavesRec.Tree: LessLeavesRec.Tree createNode()>
procedure LessLeavesRec.Tree$LessLeavesRec.Tree$createNode$2234() returns (__ret : ref)
  modifies $HeapVar;
 {
var $r128 : ref;
var r029 : ref;
var $i030 : int;
	 //  @line: 17
Block37:
	 //  @line: 17
	$r128 := $newvariable((38));
	 assume ($neref(($newvariable((38))), ($null))==1);
	 assert ($neref(($r128), ($null))==1);
	 //  @line: 17
	 call void$LessLeavesRec.Tree$$la$init$ra$$2233(($r128));
	 //  @line: 17
	r029 := $r128;
	 //  @line: 18
	 call $i030 := int$LessLeavesRec.Random$random$2237();
	 assert ($neref((r029), ($null))==1);
	 //  @line: 18
	$HeapVar[r029, int$LessLeavesRec.Tree$value0] := $i030;
	 //  @line: 19
	__ret := r029;
	 return;
}


// procedure is generated by joogie.
function {:inline true} $mulreal($param00 : realVar, $param11 : realVar) returns (__ret : realVar);



// procedure is generated by joogie.
function {:inline true} $shrref($param00 : ref, $param11 : ref) returns (__ret : int);



	 //  @line: 3
// <LessLeavesRec.Random: void <clinit>()>
procedure void$LessLeavesRec.Random$$la$clinit$ra$$2238()
  modifies int$LessLeavesRec.Random$index0;
 {
	 //  @line: 4
Block76:
	 //  @line: 4
	int$LessLeavesRec.Random$index0 := 0;
	 return;
}


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



	 //  @line: 22
// <LessLeavesRec.LessLeavesRec: void main(java.lang.String[])>
procedure void$LessLeavesRec.LessLeavesRec$main$2231($param_0 : [int]ref)
  modifies $stringSize, java.lang.String$lp$$rp$$LessLeavesRec.Random$args256;
 {
var r017 : [int]ref;
var r221 : ref;
var r119 : ref;

 //temp local variables 
var $freshlocal0 : int;

Block34:
	r017 := $param_0;
	 //  @line: 23
	java.lang.String$lp$$rp$$LessLeavesRec.Random$args256 := r017;
	 //  @line: 24
	 call r119 := LessLeavesRec.Tree$LessLeavesRec.Tree$createTree$2235();
	 //  @line: 25
	 call r221 := LessLeavesRec.Tree$LessLeavesRec.Tree$createTree$2235();
	 //  @line: 27
	 call $freshlocal0 := boolean$LessLeavesRec.LessLeavesRec$lessleaves$2230((r119), (r221));
	 return;
}


	 //  @line: 12
// <LessLeavesRec.Tree: void <init>()>
procedure void$LessLeavesRec.Tree$$la$init$ra$$2233(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r027 : ref;
Block36:
	r027 := __this;
	 assert ($neref((r027), ($null))==1);
	 //  @line: 13
	 call void$java.lang.Object$$la$init$ra$$28((r027));
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



// procedure is generated by joogie.
function {:inline true} $modref($param00 : ref, $param11 : ref) returns (__ret : ref);



	 //  @line: 1
// <LessLeavesRec.LessLeavesRec: void <init>()>
procedure void$LessLeavesRec.LessLeavesRec$$la$init$ra$$2228(__this : ref)  requires ($neref((__this), ($null))==1);
 {
var r01 : ref;
Block16:
	r01 := __this;
	 assert ($neref((r01), ($null))==1);
	 //  @line: 2
	 call void$java.lang.Object$$la$init$ra$$28((r01));
	 return;
}


	 //  @line: 10
// <LessLeavesRec.LessLeavesRec: boolean lessleaves(LessLeavesRec.Tree,LessLeavesRec.Tree)>
procedure boolean$LessLeavesRec.LessLeavesRec$lessleaves$2230($param_0 : ref, $param_1 : ref) returns (__ret : int) {
var $r311 : ref;
var $z016 : int;
var $r513 : ref;
var $r412 : ref;
var r18 : ref;
var $r715 : ref;
var $r614 : ref;
var $r210 : ref;
var r09 : ref;
Block24:
	r09 := $param_0;
	r18 := $param_1;
	 goto Block25;
	 //  @line: 11
Block25:
	 goto Block28, Block26;
	 //  @line: 11
Block28:
	 //  @line: 11
	 assume ($negInt(($neref((r18), ($null))))==1);
	 //  @line: 12
	__ret := 0;
	 return;
	 //  @line: 11
Block26:
	 assume ($neref((r18), ($null))==1);
	 goto Block27;
	 //  @line: 14
Block27:
	 goto Block31, Block29;
	 //  @line: 14
Block31:
	 //  @line: 14
	 assume ($negInt(($neref((r09), ($null))))==1);
	 goto Block32;
	 //  @line: 14
Block29:
	 assume ($neref((r09), ($null))==1);
	 goto Block30;
	 //  @line: 15
Block32:
	 //  @line: 15
	__ret := 1;
	 return;
	 //  @line: 17
Block30:
	 assert ($neref((r09), ($null))==1);
	 //  @line: 17
	$r311 := $HeapVar[r09, LessLeavesRec.Tree$LessLeavesRec.Tree$l254];
	 goto Block33;
	 //  @line: 17
Block33:
	 assert ($neref((r09), ($null))==1);
	 //  @line: 17
	$r210 := $HeapVar[r09, LessLeavesRec.Tree$LessLeavesRec.Tree$r255];
	 //  @line: 17
	 call $r412 := LessLeavesRec.Tree$LessLeavesRec.LessLeavesRec$append$2229(($r311), ($r210));
	 assert ($neref((r18), ($null))==1);
	 //  @line: 17
	$r614 := $HeapVar[r18, LessLeavesRec.Tree$LessLeavesRec.Tree$l254];
	 assert ($neref((r18), ($null))==1);
	 //  @line: 17
	$r513 := $HeapVar[r18, LessLeavesRec.Tree$LessLeavesRec.Tree$r255];
	 //  @line: 17
	 call $r715 := LessLeavesRec.Tree$LessLeavesRec.LessLeavesRec$append$2229(($r614), ($r513));
	 //  @line: 17
	 call $z016 := boolean$LessLeavesRec.LessLeavesRec$lessleaves$2230(($r412), ($r715));
	 //  @line: 17
	__ret := $z016;
	 return;
}


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



	 //  @line: 7
// <LessLeavesRec.Tree: void <init>(LessLeavesRec.Tree,LessLeavesRec.Tree)>
procedure void$LessLeavesRec.Tree$$la$init$ra$$2232(__this : ref, $param_0 : ref, $param_1 : ref)
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
	 //  @line: 8
	 call void$java.lang.Object$$la$init$ra$$28((r024));
	 assert ($neref((r024), ($null))==1);
	 //  @line: 9
	$HeapVar[r024, LessLeavesRec.Tree$LessLeavesRec.Tree$l254] := r125;
	 assert ($neref((r024), ($null))==1);
	 //  @line: 10
	$HeapVar[r024, LessLeavesRec.Tree$LessLeavesRec.Tree$r255] := r226;
	 return;
}


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


