int nondet() { int a; return a; }
_Bool nondet_bool() { _Bool a; return a; }
int main() {
int v1 = nondet();
int v2 = nondet();
int v3 = nondet();
int v4 = nondet();
int v5 = nondet();
goto loc_9;
loc_9:
 if (nondet_bool()) {
  goto loc_8;
 }
 goto end;
loc_CP_0:
 if (nondet_bool()) {
  goto loc_1;
 }
 goto end;
loc_CP_2:
 if (nondet_bool()) {
  goto loc_3;
 }
 goto end;
loc_CP_6:
 if (nondet_bool()) {
  goto loc_7;
 }
 goto end;
loc_4:
 if (nondet_bool()) {
  goto loc_5;
 }
 goto end;
loc_7:
 if (nondet_bool()) {
  goto loc_CP_6;
 }
 goto end;
loc_3:
 if (nondet_bool()) {
  if (!( 1 <= v5 )) goto end;
  goto loc_CP_2;
 }
 if (nondet_bool()) {
  if (!( v5 <= 0 )) goto end;
  v2 = 1;
  v2 = 0;
  v3 = nondet();
  v4 = v3;
  goto loc_CP_0;
 }
 goto end;
loc_1:
 if (nondet_bool()) {
  if (!( v4 <= 0 )) goto end;
  v1 = 1;
  v1 = 0;
  v5 = nondet();
  if (!( 1 <= v5 )) goto end;
  goto loc_CP_2;
 }
 if (nondet_bool()) {
  if (!( 1 <= v4 )) goto end;
  goto loc_CP_6;
 }
 goto end;
loc_8:
 if (nondet_bool()) {
  v1 = 0;
  v2 = 0;
  v3 = nondet();
  v4 = v3;
  goto loc_CP_0;
 }
 goto end;
loc_5:
end:
;
}
