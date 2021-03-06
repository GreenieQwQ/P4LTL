#!/usr/bin/python
# Generates a scalable example of the form
#
# assume(X >= 1); # X is a vector
# while (y >= 0) {
#   y = y - sum X;
#   X = X .+ 1;
# }
#
# author: Jan Leike
# date: 2013-01-09

import sys
import os.path

size = 10;
if len(sys.argv) > 1:
	size = int(sys.argv[1])

vars = ["x%d" % i for i in range(size)]

with open(os.path.splitext(__file__)[0] + str(size) + ".bpl", 'w') as f:
	f.write("// Auto-generated by %s\n" % os.path.basename(__file__))
	f.write("// size = %d\n\n" % size)
	f.write("procedure main() returns (y: int)\n{\n")
	f.write("  var " + ", ".join(vars) + ": int;\n")
	f.write("\n".join(["  assume(%s >= 1);" % v for v in vars]) + "\n")
	f.write("  while (y >= 0) {\n")
	f.write("    y := y - (" + " + ".join(vars) + ");\n")
	f.write("\n".join(["    %s := %s + 1;" % (s,s) for s in vars]) + "\n");
	f.write("  }\n}\n");
