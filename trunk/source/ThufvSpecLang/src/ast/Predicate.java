package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import de.uni_freiburg.informatik.ultimate.boogie.ast.Procedure;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VarList;
import de.uni_freiburg.informatik.ultimate.ltl2aut.ast.AstNode;
import de.uni_freiburg.informatik.ultimate.ltl2aut.ast.Name;
import edu.tsinghua.ss.thufv.specLang.ThufvSpecLangObserver;

public class Predicate extends AstNode {
	PredicateType type;
	Arguments args;
	
    public Predicate(PredicateType type, final AstNode args) throws Exception{
    	this.type = type;
    	this.args = (Arguments) args;
    	// this.addOutgoing(args);
    }
    
	public Predicate(final PredicateType type) throws Exception {
		this.type = type;
		this.args = null;
	}
	
	public String getOp() {
		String op = "??";
		switch (this.type) {
			case match:
				op = "match";
				break;
			case drop:
				op = "drop";
				break;
			default:
				throw new IllegalArgumentException();
		}
		return op;
	}

	@Override
	public String toString() {
		String str = "AP(" + this.getBoogieName() + ")";
		// original string
//		String str = this.getOp();
//		if(args != null)
//		{
//			str += "(";
//			for (AstNode arg: args.getArgs())
//			{
//				str += arg.toString() + ",";
//			}
//			str = str.substring(0, str.length()-1);	// pop last ","
//			str += ")";
//		}
		return str;
	}
	
	// getName of the instrumented Predicate in boogie
	public String getBoogieName() {
		String identifier = this.getOp();
		if(args != null)
		{
			switch (getType()) {
				case match:
					// match_h1_v1_h2_v2....
					// TODO: fix arg order
					identifier += "_";
					for (AstNode arg: args.getArgs())
					{
						ExtendedComparativeOperator eq = (ExtendedComparativeOperator) arg;
						for(AstNode eq_oprand: eq.getOutgoingNodes())
						{
							identifier += eq_oprand.toString() + "_";
						}
					}
					identifier = identifier.substring(0, identifier.length()-1);	// pop last "_"
					break;
				default:
					break;
			}
			
		}
		return identifier;
	}
	
	public Arguments getArgs() {
		return args;
	}
	
	public PredicateType getType() {
		return type;
	}
}
