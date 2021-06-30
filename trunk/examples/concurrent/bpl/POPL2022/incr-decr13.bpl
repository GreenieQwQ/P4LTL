//#Safe
var x : int;

procedure ULTIMATE.start()
modifies x;
{
  x := 0;

  fork 1 Thread();
  fork 2,2 Thread();
  fork 3,3,3 Thread();
  fork 4,4,4,4 Thread();
  fork 5,5,5,5,5 Thread();
  fork 6,6,6,6,6,6 Thread();
  fork 7,7,7,7,7,7,7 Thread();
  fork 8,8,8,8,8,8,8,8 Thread();
  fork 9,9,9,9,9,9,9,9,9 Thread();
  fork 0,0,0,0,0,0,0,0,0,0 Thread();
  fork 1,1,1,1,1,1,1,1,1,1,1 Thread();
  fork 2,2,2,2,2,2,2,2,2,2,2,2 Thread();
  fork 3,3,3,3,3,3,3,3,3,3,3,3,3 Thread();
  join 1;
  join 2,2;
  join 3,3,3;
  join 4,4,4,4;
  join 5,5,5,5,5;
  join 6,6,6,6,6,6;
  join 7,7,7,7,7,7,7;
  join 8,8,8,8,8,8,8,8;
  join 9,9,9,9,9,9,9,9,9;
  join 0,0,0,0,0,0,0,0,0,0;
  join 1,1,1,1,1,1,1,1,1,1,1;
  join 2,2,2,2,2,2,2,2,2,2,2,2;
  join 3,3,3,3,3,3,3,3,3,3,3,3,3;

  assert x == 0;
}

procedure Thread()
modifies x;
{
  while (*) {
    x := x + 1;
    x := x - 1;
  }
}
