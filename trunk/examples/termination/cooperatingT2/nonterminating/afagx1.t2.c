int nondet() { int a; return a; }
_Bool nondet_bool() { _Bool a; return a; }
int main() {
int v1 = nondet();
int v2 = nondet();
goto loc_8;
loc_8:
 if (nondet_bool()) {
  goto loc_7;
 }
 goto end;
loc_CP_1:
 if (nondet_bool()) {
  v1 = nondet();
  goto loc_2;
 }
 goto end;
loc_CP_3:
 if (nondet_bool()) {
  goto loc_4;
 }
 goto end;
loc_0:
 if (nondet_bool()) {
  goto loc_CP_1;
 }
 goto end;
loc_2:
 if (nondet_bool()) {
  if (!( v1 <= 0 )) goto end;
  if (!( 0 <= v1 )) goto end;
  v2 = 0;
  v2 = 1;
  goto loc_CP_3;
 }
 if (nondet_bool()) {
  if (!( 1 <= v1 )) goto end;
  goto loc_0;
 }
 if (nondet_bool()) {
  if (!( 1+v1 <= 0 )) goto end;
  goto loc_0;
 }
 goto end;
loc_4:
 if (nondet_bool()) {
  goto loc_CP_3;
 }
 goto end;
loc_5:
 if (nondet_bool()) {
  goto loc_6;
 }
 goto end;
loc_7:
 if (nondet_bool()) {
  v2 = 1;
  goto loc_CP_1;
 }
 goto end;
loc_6:
end:
;
}
