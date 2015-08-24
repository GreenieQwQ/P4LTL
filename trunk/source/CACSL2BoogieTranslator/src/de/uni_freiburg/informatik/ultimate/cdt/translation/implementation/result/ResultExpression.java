/**
 * Describing a translation result for expressions.
 */
package de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;

import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.LocationFactory;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.CHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler.MemoryHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler.StructHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CArray;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CEnum;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CFunction;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CNamed;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CPointer;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CPrimitive;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CStruct;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CType;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.exception.UnsupportedSyntaxException;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.util.SFO;
import de.uni_freiburg.informatik.ultimate.cdt.translation.interfaces.Dispatcher;
import de.uni_freiburg.informatik.ultimate.model.annotation.Overapprox;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.ASTType;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.ArrayLHS;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.ArrayType;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Attribute;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Declaration;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.PrimitiveType;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.StructConstructor;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.StructLHS;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.VarList;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.VariableDeclaration;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.model.location.ILocation;

/**
 * @author Markus Lindenmann
 * @author Oleksii Saukh
 * @author Stefan Wissert
 * @date 01.02.2012
 */
public class ResultExpression extends Result {
	/**
	 * Statement list.
	 */
	public final ArrayList<Statement> stmt;
	/**
	 * the LRValue may contain the contents of a memory cell or its address or both
	 */
	public LRValue lrVal;
	/**
	 * Declaration list. Some translations need to declare some temporary
	 * variables, which we do here.
	 */
	public final ArrayList<Declaration> decl; //FIXME: could we also use the more special type VariableDeclaration here??

	/**
	 * A list of overapproximation flags.
	 */
	public final List<Overapprox> overappr;

	/**
	 * Auxiliary variables occurring in this result. The variable declaration
	 * of the var is mapped to the exact location for that it was constructed.
	 */
	public final Map<VariableDeclaration, ILocation> auxVars;
	
	
	/**
	 * especially for the havocs when writign a union.
	 * contains the field that must be havoced if this union
	 * is written.
	 */
	public final Map<StructLHS, CType> unionFieldIdToCType;
	
	/**
	 * Use this to lock this ResultExpression. If the ResultExpression is locked
	 * its fields should not obtain new elements any more.
	 * (alex:) The purpose is that once we have read those fields --> and added them to another
	 * ResultExpression <-- we want to be sure that they are not changed anymore in this,
	 * as then the other expression might contain too few/many entries in these fields.
	 * TODO: implement this consequently, make fields private
	 */
	private boolean m_Locked = false;
	
	/**
	 * Lock this ResultExpression after usage to forbid that someone switches
	 * to RValue.
	 */
	public void lock() {
		m_Locked = true;
	}

	/**
     * Constructor.
     * 
     * @param stmt
     *            the statement list to hold
     * @param expr
     *            the expression list to hold
     * @param decl
     *            the declaration list to hold
     * @param overapproxList
     *            list of overapproximation flags
     */
    public ResultExpression(ArrayList<Statement> stmt,
            LRValue lrVal,
            ArrayList<Declaration> decl,
            Map<VariableDeclaration, ILocation> auxVars,
            List<Overapprox> overapproxList,
            Map<StructLHS, CType> uField2CType) {
        super(null);
        this.stmt = stmt;
        this.lrVal = lrVal;
        this.decl = decl;
        this.auxVars = auxVars;
        this.overappr = overapproxList;
        this.unionFieldIdToCType = uField2CType;
    }
    
    
    public ResultExpression(ArrayList<Statement> stmt,
            LRValue lrVal,
            ArrayList<Declaration> decl,
            Map<VariableDeclaration, ILocation> auxVars,
            List<Overapprox> overapproxList) {
    	this(stmt, lrVal, decl, auxVars, overapproxList, null);
    }
    
    public ResultExpression(ArrayList<Statement> stmt,
            LRValue lrVal,
            ArrayList<Declaration> decl,
            Map<VariableDeclaration, ILocation> auxVars) {
        this(stmt, lrVal, decl,
                auxVars, new ArrayList<Overapprox>(), null);
    }

	public ResultExpression(
			LRValue lrVal,
			Map<VariableDeclaration, ILocation> auxVars,
			List<Overapprox> overapproxList) {
	    this(new ArrayList<Statement>(), lrVal, new ArrayList<Declaration>(),
	            auxVars, overapproxList, null);
	}
	
	public ResultExpression(ArrayList<Statement> stmt,
			LRValue lrVal) {
	    this(stmt, lrVal, new ArrayList<Declaration>(),
	            new LinkedHashMap<VariableDeclaration, ILocation>(), 
	            new ArrayList<Overapprox>(), null);
	}
	
	public ResultExpression(
            LRValue lrVal,
            Map<VariableDeclaration, ILocation> auxVars) {
        this(new ArrayList<Statement>(), lrVal, new ArrayList<Declaration>(),
                auxVars);
    }

    public ResultExpression(
            LRValue lrVal) {
        this(lrVal, new LinkedHashMap<VariableDeclaration, ILocation>());
    }
    
    /**
     * copy constructor
     * Note. Does _not_ construct a deep copy of the object.
     */
    public ResultExpression(ResultExpression rex) {
    	super(null);
    	this.stmt = rex.stmt;
    	this.lrVal = rex.lrVal;
    	this.decl = rex.decl;
    	this.auxVars = rex.auxVars;
    	this.overappr = rex.overappr;
    	this.unionFieldIdToCType = rex.unionFieldIdToCType;
    }
    

    /**
     * Construct a new {@link ResultExpression} without an {@link LRValue}
     * whose statements, declarations, auxVars and overapproximations contain
     * all respective elements of resExprs.
     * TODO: This could remove the old copy constructor one it is not used
     * any more.
     */
    public static ResultExpression copyStmtDeclAuxvarOverapprox(ResultExpression... resExprs) {
		ArrayList<Declaration> decl = new ArrayList<Declaration>();
		ArrayList<Statement> stmt = new ArrayList<Statement>();
		Map<VariableDeclaration, ILocation> auxVars = new LinkedHashMap<VariableDeclaration, ILocation>();
		List<Overapprox> overappr = new ArrayList<Overapprox>();
		for (ResultExpression resExpr : resExprs) {
			stmt.addAll(resExpr.stmt);
			decl.addAll(resExpr.decl);
			auxVars.putAll(resExpr.auxVars);
			overappr.addAll(resExpr.overappr);
		}
    	return new ResultExpression(stmt, null, decl, auxVars, overappr, null);
    }
    

	public ResultExpression switchToRValueIfNecessary(Dispatcher main, MemoryHandler memoryHandler, 
			StructHandler structHandler, ILocation loc) {
		ResultExpression toReturn = null;
		if (lrVal == null) {
			toReturn =  this;
		} else if (lrVal instanceof RValue) {
			toReturn =  this;
		} else if (lrVal instanceof LocalLValue) {
			RValue newRVal = new RValue(((LocalLValue) lrVal).getValue(),
					        lrVal.cType, lrVal.isBoogieBool);
			toReturn = new ResultExpression(
					this.stmt, newRVal, this.decl, this.auxVars,
					        this.overappr, this.unionFieldIdToCType);
		} else if (lrVal instanceof HeapLValue) {
			if (m_Locked) {
				throw new AssertionError("this ResultExpression is already locked");
			}
			HeapLValue hlv = (HeapLValue) lrVal;
			
			//retain already created stmt, decl, auxVars
			ArrayList<Statement> newStmt = new ArrayList<Statement>(this.stmt);
			ArrayList<Declaration> newDecl = new ArrayList<Declaration>(this.decl);
			LinkedHashMap<VariableDeclaration, ILocation> newAuxVars = 
					new LinkedHashMap<VariableDeclaration, ILocation>(this.auxVars); 
			RValue newValue = null;

			CType underlyingType = this.lrVal.cType.getUnderlyingType();
		
			//a pointer to a function is a special case..
			if (underlyingType instanceof CPointer 
					&& ((CPointer) underlyingType).pointsToType instanceof CFunction) {
				underlyingType = ((CPointer) underlyingType).pointsToType;
			}

			//has the type of what lies at that address
			RValue addressRVal = new RValue(hlv.getAddress(), hlv.cType,
					hlv.isBoogieBool, hlv.isIntFromPointer);

			if (underlyingType instanceof CPrimitive) {
				CPrimitive cp = (CPrimitive) underlyingType;
				switch (cp.getGeneralType()) {
				case INTTYPE: {
					ResultExpression rex = memoryHandler.getReadCall(
							main, addressRVal);
					newStmt.addAll(rex.stmt);
					newDecl.addAll(rex.decl);
					newAuxVars.putAll(rex.auxVars);	
					newValue = (RValue) rex.lrVal;
					break;
				}
				case FLOATTYPE: {
					ResultExpression rex = memoryHandler.getReadCall(
							main, addressRVal);
					newStmt.addAll(rex.stmt);
					newDecl.addAll(rex.decl);
					newAuxVars.putAll(rex.auxVars);	
					newValue = (RValue) rex.lrVal;	
					break;
				}
				case VOID:
					//(in this case we return nothing, because this should not be read anyway..)
					break;
				default:
					throw new UnsupportedSyntaxException(loc, "..");
				}
			} else if (underlyingType instanceof CPointer) {
				ResultExpression rex = memoryHandler.getReadCall(
						main, addressRVal);
				newStmt.addAll(rex.stmt);
				newDecl.addAll(rex.decl);
				newAuxVars.putAll(rex.auxVars);	
				newValue = (RValue) rex.lrVal;
			} else if (underlyingType instanceof CArray) {
				//						return null; //"you can't assign arrays in C"
				//						throw new AssertionError("you can't assign arrays in C");
				// if it is a HeapLValue, it must be on heap -> treat it as a pointer
									ResultExpression	rex = readArrayFromHeap(main, structHandler,
											memoryHandler, loc, addressRVal, (CArray) addressRVal.cType);
										newStmt.addAll(rex.stmt);
										newDecl.addAll(rex.decl);
										newAuxVars.putAll(rex.auxVars);	
										newValue = (RValue) rex.lrVal;
//				newStmt.addAll(this.stmt);
//				newDecl.addAll(this.decl);
//				newAuxVars.putAll(this.auxVars);	
//				newValue = new RValue(hlv.getAddress(), this.lrVal.cType);
			} else if (underlyingType instanceof CEnum) { //same as for an int
				ResultExpression rex = memoryHandler.getReadCall(
						main, addressRVal);
				newStmt.addAll(rex.stmt);
				newDecl.addAll(rex.decl);
				newAuxVars.putAll(rex.auxVars);	
				newValue = (RValue) rex.lrVal;
			} else if (underlyingType instanceof CStruct) {
				ResultExpression rex = readStructFromHeap(main, structHandler, memoryHandler, loc, 
						addressRVal);
				newStmt.addAll(rex.stmt);
				newDecl.addAll(rex.decl);
				newAuxVars.putAll(rex.auxVars);	
				newValue = (RValue) rex.lrVal;	
			} else if (underlyingType instanceof CNamed) {
				throw new AssertionError("This should not be the case as we took the underlying type.");
			} else if (underlyingType instanceof CFunction) {
				newValue = addressRVal;
			} else {
				throw new UnsupportedSyntaxException(loc, "..");
			}
			newValue.isBoogieBool = lrVal.isBoogieBool;
			toReturn = new ResultExpression(newStmt, newValue, newDecl,
					newAuxVars, this.overappr, this.unionFieldIdToCType);
		} else {
			throw new AssertionError("an LRValue that is not null, and no LocalLValue, RValue or HeapLValue???");
		}
		
		if(toReturn != null
				&& toReturn.lrVal != null)
			toReturn.lrVal.isIntFromPointer = this.lrVal.isIntFromPointer; //FIXME niceer
		
//		//special treatment for unsigned integer types
//		if (main.cHandler.getUnsignedTreatment() != UNSIGNED_TREATMENT.IGNORE
//				&& toReturn != null
//				&& toReturn.lrVal != null 
//				&& !toReturn.lrVal.isIntFromPointer
//				&& toReturn.lrVal.cType instanceof CPrimitive 
//				&& ((CPrimitive) toReturn.lrVal.cType).isUnsigned()) {
//			int exponentInBytes = memoryHandler.typeSizeConstants
//						.CPrimitiveToTypeSizeConstant.get(((CPrimitive) toReturn.lrVal.cType).getType());
//			BigInteger maxValue = new BigInteger("2")
//					.pow(exponentInBytes * 8);
//
//			if (main.cHandler.getUnsignedTreatment() == UNSIGNED_TREATMENT.ASSUME_ALL) {
//				AssumeStatement assumeGeq0 = new AssumeStatement(loc, new BinaryExpression(loc, BinaryExpression.Operator.COMPGEQ,
//						toReturn.lrVal.getValue(), new IntegerLiteral(loc, SFO.NR0)));
//				toReturn.stmt.add(assumeGeq0);
//				
//				AssumeStatement assumeLtMax = new AssumeStatement(loc, new BinaryExpression(loc, BinaryExpression.Operator.COMPLT,
//						toReturn.lrVal.getValue(), new IntegerLiteral(loc, maxValue.toString())));
//				toReturn.stmt.add(assumeLtMax);
//			} else if (main.cHandler.getUnsignedTreatment() == UNSIGNED_TREATMENT.WRAPAROUND) {
//				toReturn.lrVal = new RValue(new BinaryExpression(loc, BinaryExpression.Operator.ARITHMOD, 
//							toReturn.lrVal.getValue(), 
//							new IntegerLiteral(loc, maxValue.toString())), 
//						toReturn.lrVal.cType, 
//						toReturn.lrVal.isBoogieBool,
//						false);
//			}
//		}

		return toReturn;
	}

//	private ResultExpression readArrayFromHeap(Dispatcher main,
//			MemoryHandler memoryHandler, ILocation loc, RValue addressRVal) {
//		CArray arrayType = (CArray) addressRVal.cType.getUnderlyingType();
//		
//		Expression[] dims = arrayType.getDimensions();
//		
//		for (int i)
//				
//		return null;
//	}

	/**
	 * Read the contents of a struct (given as a pointer) from the heap recursively (for nested structs)
	 * returning a StructConstructor.
	 * @param main
	 * @param structHandler
	 * @param memoryHandler
	 * @param loc
	 * @param structOnHeapAddress
	 * @param structType
	 * @return A result whose value is a StructConstructor and whose statements make the necessary calls to
	 * fill the items inside the StructConstructor correctly
	 */
	ResultExpression readStructFromHeap(Dispatcher main, 
			StructHandler structHandler, MemoryHandler memoryHandler, ILocation loc,
			RValue address) {
		
		Expression structOnHeapAddress = address.getValue();
		CStruct structType = (CStruct) address.cType.getUnderlyingType();
		
		ResultExpression result = null;
		
		Expression startAddress = structOnHeapAddress;
		Expression currentStructBaseAddress = null;
		Expression currentStructOffset = null;
		if (startAddress instanceof StructConstructor) {
			currentStructBaseAddress = ((StructConstructor) startAddress).getFieldValues()[0];
			currentStructOffset = ((StructConstructor) startAddress).getFieldValues()[1];
		} else {
			currentStructBaseAddress = MemoryHandler.getPointerBaseAddress(startAddress, loc);
			currentStructOffset = MemoryHandler.getPointerOffset(startAddress, loc);
		}

		//everything for the new Result
		ArrayList<Statement> newStmt = new ArrayList<Statement>();
		ArrayList<Declaration> newDecl = new ArrayList<Declaration>();
		Map<VariableDeclaration, ILocation> newAuxVars = new LinkedHashMap<VariableDeclaration, ILocation>();

		String[] fieldIds = structType.getFieldIds();
		CType[] fieldTypes = structType.getFieldTypes();

		//the new Arrays for the StructConstructor
		ArrayList<String> fieldIdentifiers = new ArrayList<String>();
		ArrayList<Expression> fieldValues = new ArrayList<Expression>();

		for (int i = 0; i < fieldIds.length; i++) {
			fieldIdentifiers.add(fieldIds[i]);

			CType underlyingType;
			if (fieldTypes[i] instanceof CNamed)
				underlyingType = ((CNamed) fieldTypes[i]).getUnderlyingType();
			else
				underlyingType = fieldTypes[i];

//			ResultExpression fieldRead = null; 
			RValue fieldRVal = null;
			if(underlyingType instanceof CPrimitive) {
				ResultExpression fieldRead = (ResultExpression) structHandler.readFieldInTheStructAtAddress(
						main, memoryHandler, loc, fieldIds[i], 
						address);
				fieldRVal = (RValue) fieldRead.lrVal;
				newStmt.addAll(fieldRead.stmt);
				newDecl.addAll(fieldRead.decl);
				newAuxVars.putAll(fieldRead.auxVars);
			} else if (underlyingType instanceof CPointer) {
				ResultExpression fieldRead = (ResultExpression) structHandler.readFieldInTheStructAtAddress(
						main, memoryHandler, loc, fieldIds[i], 
						address);
				fieldRVal = (RValue) fieldRead.lrVal;
				newStmt.addAll(fieldRead.stmt);
				newDecl.addAll(fieldRead.decl);
				newAuxVars.putAll(fieldRead.auxVars);
			} else if (underlyingType instanceof CArray) {
				
				ResultExpression xres1 = readArrayFromHeap(main, structHandler,
						memoryHandler, loc, address, (CArray) underlyingType);
				ResultExpression xres = xres1;
				
				fieldRVal = (RValue) xres.lrVal;
				newStmt.addAll(xres.stmt);
				newDecl.addAll(xres.decl);
				newAuxVars.putAll(xres.auxVars);
				
				
//				fieldRead = (ResultExpression) structHandler.readFieldInTheStructAtAddress(
//						main, memoryHandler, loc, fieldIds[i], 
//						address);
//				newStmt.addAll(fieldRead.stmt);
//				newDecl.addAll(fieldRead.decl);
//				newAuxVars.putAll(fieldRead.auxVars);
//				throw new UnsupportedSyntaxException(loc, "..");
			} else if (underlyingType instanceof CEnum) {
				//like CPrimitive..
				ResultExpression fieldRead = (ResultExpression) structHandler.readFieldInTheStructAtAddress(
						main, memoryHandler, loc, fieldIds[i], 
						address);
				fieldRVal = (RValue) fieldRead.lrVal;
				newStmt.addAll(fieldRead.stmt);
				newDecl.addAll(fieldRead.decl);
				newAuxVars.putAll(fieldRead.auxVars);
//				throw new UnsupportedSyntaxException(loc, "..");
			} else if (underlyingType instanceof CStruct) {
				Expression innerStructOffset = 
						StructHandler.getStructOrUnionOffsetConstantExpression(loc, memoryHandler, fieldIds[i], structType);
				Expression innerStructAddress = MemoryHandler.constructPointerFromBaseAndOffset(currentStructBaseAddress, 
						new BinaryExpression(loc, BinaryExpression.Operator.ARITHPLUS, 
								currentStructOffset, 
								innerStructOffset),
								loc);
				
				RValue newAddress = new RValue(address);
				newAddress.value = innerStructAddress;
				newAddress.cType= underlyingType;

				ResultExpression fieldRead = readStructFromHeap(main, structHandler, memoryHandler, 
						loc, newAddress);

				fieldRVal = (RValue) fieldRead.lrVal;
				newStmt.addAll(fieldRead.stmt);
				newDecl.addAll(fieldRead.decl);
				newAuxVars.putAll(fieldRead.auxVars);
			} else if (underlyingType instanceof CNamed) {
				assert false : "This should not be the case as we took the underlying type.";
			} else {
				throw new UnsupportedSyntaxException(loc, "..");
			}	


//			assert fieldRead.lrVal instanceof RValue; //should be guaranteed by readFieldInTheStructAtAddress(..)
//			fieldValues.add(((RValue) fieldRead.lrVal).getValue());
			fieldValues.add(fieldRVal.getValue());

		}
		StructConstructor sc = new StructConstructor(loc, 
				fieldIdentifiers.toArray(new String[0]), 
				fieldValues.toArray(new Expression[0]));

		result = new ResultExpression(newStmt, new RValue(sc, structType),
		        newDecl, newAuxVars, this.overappr, this.unionFieldIdToCType);

		return result;
	}

	public ResultExpression readArrayFromHeap(Dispatcher main,
			StructHandler structHandler, MemoryHandler memoryHandler,
			ILocation loc, RValue address, CArray arrayType) {
		RValue xfieldRVal = null;
		ArrayList<Declaration> decl = new ArrayList<>();
		ArrayList<Statement> stmt = new ArrayList<>();
		Map<VariableDeclaration, ILocation> auxVars = new LinkedHashMap<>();
		ArrayList<Overapprox> overApp = new ArrayList<>();

		if (arrayType.getDimensions().length == 1
				&& arrayType.getDimensions()[0] instanceof IntegerLiteral) {
			int dim = Integer.parseInt(((IntegerLiteral) arrayType.getDimensions()[0]).getValue());

			String newArrayId = main.nameHandler.getTempVarUID(SFO.AUXVAR.ARRAYCOPY);
			VarList newArrayVl = new VarList(loc, new String[] { newArrayId }, 
					new ArrayType(loc, new String[0], new ASTType[] { new PrimitiveType(loc, SFO.INT) }, 
							main.typeHandler.ctype2asttype(loc, arrayType.getValueType())));
			VariableDeclaration newArrayDec = new VariableDeclaration(loc, new Attribute[0], new VarList[] { newArrayVl });
			xfieldRVal = new RValue(new IdentifierExpression(loc, newArrayId), arrayType);
			
			decl.add(newArrayDec);
			auxVars.put(newArrayDec, loc);
			
			
			Expression arrayStartAddress = address.getValue();
			Expression newStartAddressBase = null;
			Expression newStartAddressOffset = null;
			if (arrayStartAddress instanceof StructConstructor) {
				newStartAddressBase = ((StructConstructor) arrayStartAddress).getFieldValues()[0];
				newStartAddressOffset = ((StructConstructor) arrayStartAddress).getFieldValues()[1];
			} else {
				newStartAddressBase = MemoryHandler.getPointerBaseAddress(arrayStartAddress, loc);
				newStartAddressOffset = MemoryHandler.getPointerOffset(arrayStartAddress, loc);
			}

			Expression valueTypeSize = memoryHandler.calculateSizeOf(arrayType.getValueType(), loc);

			Expression arrayEntryAddressOffset = newStartAddressOffset;

			for (int pos = 0; pos < dim; pos++) {
				
				ResultExpression readRex;
				Expression readAddress = MemoryHandler.constructPointerFromBaseAndOffset(newStartAddressBase, arrayEntryAddressOffset, loc);
				if (arrayType.getValueType().getUnderlyingType() instanceof CStruct) {
					readRex = readStructFromHeap(main, structHandler, memoryHandler, loc, 
							new RValue(readAddress, arrayType.getValueType().getUnderlyingType()));
				} else {
					readRex = memoryHandler.getReadCall(main, new RValue(
							readAddress, 
							arrayType.getValueType()));
				}
				decl.addAll(readRex.decl);
				stmt.addAll(readRex.stmt);
				auxVars.putAll(readRex.auxVars);
				overApp.addAll(readRex.overappr);

				ArrayLHS aAcc = new ArrayLHS(loc, new VariableLHS(loc, newArrayId),
						new Expression[] { new IntegerLiteral(loc, new Integer(pos).toString())} );
				ResultExpression assRex = ((CHandler) main.cHandler).makeAssignment(main, loc, stmt, 
						new LocalLValue(aAcc, arrayType.getValueType()), (RValue) readRex.lrVal, decl, auxVars, overappr);
				decl = assRex.decl;
				stmt = assRex.stmt;
				auxVars = assRex.auxVars;
				overApp.addAll(assRex.overappr);

				arrayEntryAddressOffset = CHandler.createArithmeticExpression(IASTBinaryExpression.op_plus, 
						arrayEntryAddressOffset, valueTypeSize, loc);
			}
		} else {
			throw new UnsupportedSyntaxException(loc, "we need to generalize this to nested and/or variable length arrays");
		}
		ResultExpression xres1 = new ResultExpression(stmt, xfieldRVal, decl, auxVars, overApp);
		return xres1;
	}
	
	/**
	 * Add all declaration, statements, auxvars, etc. from another 
	 * ResultExpression. Lock the other ResultExpression afterwards to indicate
	 * that the other Result expression should not be used any more. 
	 */
	public void addAll(ResultExpression re) {
		this.decl.addAll(re.decl);
		this.stmt.addAll(re.stmt);
		this.auxVars.putAll(re.auxVars);
		this.overappr.addAll(overappr);
		re.lock();
	}
}