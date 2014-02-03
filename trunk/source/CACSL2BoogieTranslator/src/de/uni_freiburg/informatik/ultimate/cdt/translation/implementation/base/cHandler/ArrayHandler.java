package de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.cHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;

import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.CACSLLocation;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.CHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.base.TypeHandler;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.InferredType;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.InferredType.Type;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CArray;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CNamed;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CPointer;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CPrimitive;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CStruct;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.container.c.CType;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.exception.UnsupportedSyntaxException;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.HeapLValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.LRValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.LocalLValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.RValue;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.Result;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.ResultExpression;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.ResultExpressionListRec;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.result.ResultTypes;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.util.SFO;
import de.uni_freiburg.informatik.ultimate.cdt.translation.interfaces.Dispatcher;
import de.uni_freiburg.informatik.ultimate.model.annotation.Overapprox;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.ArrayAccessExpression;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.ArrayLHS;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Declaration;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.StructConstructor;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.VariableDeclaration;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.model.location.ILocation;

/**
 * Class that handles translation of arrays.
 * 
 * @author Markus Lindenmann
 * @date 12.10.2012
 */
public class ArrayHandler {
	
	boolean modifyingTheHeapGlobally = false;
	HashSet<String> modifiedGlobals = new HashSet<String>();
	
	public HashSet<String> getModifiedGlobals() {
		if (modifyingTheHeapGlobally) {
			for (String t : new String[] { SFO.INT, SFO.POINTER,
					SFO.REAL, SFO.BOOL }) {
				modifiedGlobals.add(SFO.MEMORY + "_" + t);
			}
			modifiedGlobals.add(SFO.LENGTH);
		}
		return modifiedGlobals;
	}

	public ResultExpression handleArrayDeclarationOnHeap(Dispatcher main,
			MemoryHandler memoryHandler, StructHandler structHandler,
			FunctionHandler functionHandler,
			IASTArrayDeclarator d, IASTDeclSpecifier iastDeclSpecifier, ResultTypes resType, String bId, CACSLLocation loc) {

		ArrayList<Statement> stmt = new ArrayList<Statement>();
		ArrayList<Declaration> decl = new ArrayList<Declaration>();
		HashMap<VariableDeclaration, ILocation> auxVars =
				new HashMap<VariableDeclaration, ILocation>();
		ArrayList<Overapprox> overappr = new ArrayList<Overapprox>();

		ArrayList<Expression> sizeConstants = new ArrayList<Expression>();
		Expression overallSize = new IntegerLiteral(loc, /*new InferredType(Type.Integer),*/ "1");
		for (IASTArrayModifier am : d.getArrayModifiers()) {
			ResultExpression constEx = (ResultExpression) main.
					dispatch(am.getConstantExpression());
			constEx = constEx.switchToRValueIfNecessary(main, //just to be safe..
					memoryHandler, structHandler, loc);
//			assert constEx.lrVal instanceof RValue : "we only allow arrays of constant size";
			sizeConstants.add(constEx.lrVal.getValue());
//			Integer constAsInt =  Integer.parseInt(((IntegerLiteral) constEx.lrVal.getValue()).getValue());
			overallSize = CHandler.createArithmeticExpression(IASTBinaryExpression.op_multiply, 
					overallSize, constEx.lrVal.getValue(), loc);//
		}

		Expression sizeOfCell = memoryHandler.calculateSizeOf(resType.cType, loc);
		CArray arrayType = new CArray(
				sizeConstants.toArray(new Expression[0]), resType.cType);
		LocalLValue arrayId = new LocalLValue(new VariableLHS(loc, /*new InferredType(Type.Pointer),*/ bId), arrayType);

		//malloc the space on the heap for the array
		functionHandler.addMallocedAuxPointer(main, arrayId);

		//handle initialization
		if (d.getInitializer() != null) {			
			
			//evaluate the initializer and fill the heapspace of the array
			ResultExpressionListRec init = (ResultExpressionListRec) main.dispatch(d.getInitializer());
			ArrayList<Statement> arrayWrites = initArrayOnHeap(main, memoryHandler, structHandler, loc, init.list, 
					arrayId.getValue(), functionHandler, arrayType);
			stmt.addAll(arrayWrites);
		}
		return new ResultExpression(stmt, arrayId, decl, auxVars, overappr);
	}

	public ArrayList<Statement> initArrayOnHeap(Dispatcher main, MemoryHandler memoryHandler, StructHandler structHandler, ILocation loc, 
			ArrayList<ResultExpressionListRec> list, Expression startAddress, //Expression sizeOfCell, 
			FunctionHandler functionHandler, CArray arrayType) {
		ArrayList<Statement> arrayWrites = new ArrayList<Statement>();
		
		for (String t : new String[] { SFO.INT, SFO.POINTER,
				SFO.REAL, SFO.BOOL }) {
			functionHandler.getModifiedGlobals()
			.get(functionHandler.getCurrentProcedureID())
			.add(SFO.MEMORY + "_" + t);
		}
		
		Expression sizeOfCell = memoryHandler.calculateSizeOf(arrayType.getValueType(), loc); 
		Expression[] dimensions = arrayType.getDimensions();
		Integer currentSizeInt = null;
		try {
			currentSizeInt = Integer.parseInt(((IntegerLiteral) dimensions[0]).getValue());
		} catch (NumberFormatException nfe) {
			throw new UnsupportedSyntaxException(loc, "trying to initialize an array whose size we don't know");
		}

		Expression newStartAddressBase = null;
		Expression newStartAddressOffset = null;
		if (startAddress instanceof StructConstructor) {
			newStartAddressBase = ((StructConstructor) startAddress).getFieldValues()[0];
			newStartAddressOffset = ((StructConstructor) startAddress).getFieldValues()[1];
		} else {
			newStartAddressBase = MemoryHandler.getPointerBaseAddress(startAddress, loc);
			newStartAddressOffset = MemoryHandler.getPointerOffset(startAddress, loc);
		}

		if (dimensions.length == 1) {
			RValue val = null;

			for (int i = 0; i < currentSizeInt; i++) {
				//TODO: we may need to pass statements, decls, ...
				if (list != null && list.size() > i && list.get(i).lrVal != null) {
					val = (RValue) list.get(i).lrVal; 
				} else {
					CType valueType = arrayType.getValueType().getUnderlyingType();

					if (valueType instanceof CArray) {
						throw new AssertionError("this should not be the case as we are in the inner/outermost array right??");
					} else if  (valueType instanceof CStruct) {
						ResultExpression sInit = structHandler.makeStructConstructorFromRERL(main, loc, memoryHandler, 
								this, functionHandler, null, (CStruct) valueType, true);
						arrayWrites.addAll(sInit.stmt);
						assert sInit.decl.size() == 0 && sInit.auxVars.size() == 0 : "==> change return type of initArray..";
						val = (RValue) sInit.lrVal;
					} else if (valueType instanceof CPrimitive 
							|| valueType instanceof CPointer) {
						val = new RValue(CHandler.getInitExpr(valueType), valueType);
					} else {
						throw new UnsupportedSyntaxException(loc, "trying to init unknown type");
					}
				}


				Expression writeOffset = CHandler.createArithmeticExpression(IASTBinaryExpression.op_multiply, 
						new IntegerLiteral(null, new InferredType(Type.Integer), new Integer(i).toString()), 
						sizeOfCell,
						null);	
				writeOffset = CHandler.createArithmeticExpression(IASTBinaryExpression.op_plus,
						newStartAddressOffset,
						writeOffset, 
						loc);

				Expression writeLocation = MemoryHandler.constructPointerFromBaseAndOffset(
						newStartAddressBase,
						writeOffset, 
						loc);

				arrayWrites.addAll(memoryHandler.getWriteCall(new HeapLValue(writeLocation, null), val));
			}
		} else {
			for (int i = 0; i < currentSizeInt; i++) { 
				Expression newStartAddressOffsetInner = newStartAddressOffset;

				Expression blockOffset = sizeOfCell;
				for (int j = 1; j < dimensions.length; j++) {
					blockOffset = 
							CHandler.createArithmeticExpression(IASTBinaryExpression.op_multiply,
									dimensions[j],
									blockOffset,
									loc);
				}
				blockOffset = 
						CHandler.createArithmeticExpression(IASTBinaryExpression.op_multiply,
								new IntegerLiteral(loc, new InferredType(Type.Integer), new Integer(i).toString()),
								blockOffset,
								loc);	
				newStartAddressOffsetInner = 
						CHandler.createArithmeticExpression(IASTBinaryExpression.op_plus,
								newStartAddressOffsetInner,
								blockOffset,
								loc);	
				
				ArrayList<Expression> innerDims = new ArrayList<Expression>(Arrays.asList(arrayType.getDimensions()));
				innerDims.remove(0);//TODO ??
				CArray innerArrayType = new CArray(innerDims.toArray(new Expression[0]), 
						arrayType.getValueType());

				arrayWrites.addAll(
						initArrayOnHeap(main, memoryHandler, structHandler, loc, 
								list != null ? list.get(i).list : null,
								MemoryHandler.constructPointerFromBaseAndOffset(
										newStartAddressBase,
										newStartAddressOffsetInner, 
										loc),
										functionHandler, innerArrayType)); 
			}
		}
		return arrayWrites;
	}
	
	public ArrayList<Statement> initBoogieArray(Dispatcher main, MemoryHandler memoryHandler, StructHandler structHandler,
			FunctionHandler functionHandler,  ILocation loc, 
			ArrayList<ResultExpressionListRec> list, LeftHandSide innerArrayAccessLHS,
			CArray arrayType) {
		ArrayList<Statement> arrayWrites = new ArrayList<Statement>();
		
		Expression[] dimensions = arrayType.getDimensions();
		Integer currentSizeInt = null;
		try {
			currentSizeInt = Integer.parseInt(((IntegerLiteral) dimensions[0]).getValue());
		} catch (NumberFormatException nfe) {
			throw new UnsupportedSyntaxException(loc, "trying to initialize an array whose size we don't know");
		}

		if (dimensions.length == 1) {
			RValue val = null;

			for (int i = 0; i < currentSizeInt; i++) {
				//TODO: we may need to pass statements, decls, ...
				if (list != null && list.size() > i && list.get(i).lrVal != null) {
					val = (RValue) list.get(i).lrVal; //if not enough values are given, fill the rest with the last --> wrong? FIXME
				} else {
					CType valueType = arrayType.getValueType().getUnderlyingType();

					if (valueType instanceof CArray) {
						throw new AssertionError("this should not be the case as we are in the inner/outermost array right??");
					} else if  (valueType instanceof CStruct) {
						ResultExpression sInit = structHandler.makeStructConstructorFromRERL(main, loc, memoryHandler, this, functionHandler, 
								null, (CStruct) valueType, false);
						arrayWrites.addAll(sInit.stmt);
						assert sInit.decl.size() == 0 && sInit.auxVars.size() == 0 : "==> change return type of initArray..";
						val = (RValue) sInit.lrVal;
					} else if (valueType instanceof CPrimitive 
							|| valueType instanceof CPointer) {
						val = new RValue(CHandler.getInitExpr(valueType), valueType);
					} else {
						throw new UnsupportedSyntaxException(loc, "trying to init unknown type");
					}
				}
				Expression[] newIndices = null;
				LeftHandSide newLHS = null;
				if (innerArrayAccessLHS instanceof ArrayLHS) {
					ArrayList<Expression> innerIndices = 
							new ArrayList<Expression>(Arrays.asList(((ArrayLHS) innerArrayAccessLHS).getIndices()));
					innerIndices.add(new IntegerLiteral(loc, new Integer(i).toString()));
					newIndices = innerIndices.toArray(new Expression[0]);
					newLHS = ((ArrayLHS) innerArrayAccessLHS).getArray();
				} else {
					newIndices = new Expression[] { new IntegerLiteral(loc, new Integer(i).toString()) };
					newLHS = innerArrayAccessLHS;
				}
				
				ArrayLHS arrayAccessLHS = new ArrayLHS(loc, newLHS, newIndices);
				arrayWrites.add(new AssignmentStatement(loc, 
						new LeftHandSide[] { arrayAccessLHS }, new Expression[] { val.getValue() }));
			}
		} else {
			for (int i = 0; i < currentSizeInt; i++) { 

				Expression[] newIndices = null;
				LeftHandSide newLHS = null;
				if (innerArrayAccessLHS instanceof ArrayLHS) {
					ArrayList<Expression> innerIndices = 
							new ArrayList<Expression>(Arrays.asList(((ArrayLHS) innerArrayAccessLHS).getIndices()));
					innerIndices.add(new IntegerLiteral(loc, new Integer(i).toString()));
					newIndices = innerIndices.toArray(new Expression[0]);
					newLHS = ((ArrayLHS) innerArrayAccessLHS).getArray();
				} else { 
					newIndices = new Expression[] { new IntegerLiteral(loc, new Integer(i).toString()) };
					newLHS = innerArrayAccessLHS;
				}
			
				ArrayList<Expression> innerDims = new ArrayList<Expression>(Arrays.asList(arrayType.getDimensions()));
				innerDims.remove(0);//TODO ??
				CArray innerArrayType = new CArray(innerDims.toArray(new Expression[0]), 
						arrayType.getValueType());

				arrayWrites.addAll(
						initBoogieArray(main, memoryHandler, structHandler, functionHandler, loc,
								list != null ? list.get(i).list : null,
								new ArrayLHS(loc, newLHS, newIndices), innerArrayType)); 
			}
		}
		return arrayWrites;
	}
	
	
	/**
	 * This stack stack collects ResultExpressions from subscripts. When going down
	 * into nested subscripts recursively a ResultExpression is pushed for each subscript.
	 * When going up again the ResultExpressions are popped/used.
	 */
	Stack<ResultExpression> mCollectedSubscripts = new Stack<ResultExpression>();
	
	public ResultExpression handleArraySubscriptExpression(Dispatcher main,
			MemoryHandler memoryHandler, StructHandler structHandler,
			IASTArraySubscriptExpression node) {		
		ILocation loc = new CACSLLocation(node);
		
		ResultExpression subscript = (ResultExpression) main.dispatch(node.getArgument());
		ResultExpression subscriptR = subscript.switchToRValueIfNecessary(main, memoryHandler, structHandler, loc);
		mCollectedSubscripts.push(subscriptR);
		
		//have we arrived at the innermost array subscript? (i.e. a[i] instead of a[j][i])
		boolean innerMostSubscript = 
				!(node.getArrayExpression() instanceof IASTArraySubscriptExpression);
		ResultExpression innerResult = null;
		if (innerMostSubscript) {
			innerResult = ((ResultExpression) main.dispatch(node.getArrayExpression()));
		} else {
			innerResult = handleArraySubscriptExpression(main, memoryHandler, 
					structHandler, (IASTArraySubscriptExpression) node.getArrayExpression());
		}
		
		ResultExpression result = new ResultExpression(innerResult);
		
		ResultExpression currentSubscriptRex = mCollectedSubscripts.pop();
		result.stmt.addAll(currentSubscriptRex.stmt);
		result.decl.addAll(currentSubscriptRex.decl);
		result.auxVars.putAll(currentSubscriptRex.auxVars);
		result.overappr.addAll(currentSubscriptRex.overappr);
		
		if (result.lrVal.cType instanceof CPointer) {
			//we have a pointer that is accessed like an array
			result.lrVal = ((CHandler) main.cHandler).doPointerArith(
					main, IASTBinaryExpression.op_plus, loc, 
					(RValue) result.lrVal, 
					(RValue) currentSubscriptRex.lrVal,
					((CPointer) result.lrVal.cType).pointsToType);
		} else {
			assert result.lrVal.cType instanceof CArray;
			ArrayList<Expression> newDimensions = 
					new ArrayList<Expression>(Arrays.asList(((CArray) result.lrVal.cType)
							.getDimensions()));
			CType newCType = null;
			if (newDimensions.size() == 1) {
				newCType = ((CArray) result.lrVal.cType).getValueType();
			} else {
				newDimensions.remove(0);
				newCType = new CArray(newDimensions.toArray(new Expression[0]), 
						((CArray) result.lrVal.cType).getValueType());
			}

			if (result.lrVal instanceof HeapLValue) {
				result.lrVal = new HeapLValue(((CHandler) main.cHandler).doPointerArith(
						main, IASTBinaryExpression.op_plus, loc, 
						((HeapLValue) result.lrVal).getAddress(),
						currentSubscriptRex.lrVal.getValue(),
						newCType), newCType);
			} else if (result.lrVal instanceof LocalLValue) {
				LocalLValue newLLVal = new LocalLValue((LocalLValue) result.lrVal);
				LeftHandSide innerArrayLHS = ((LocalLValue) result.lrVal).getLHS();
				if (innerArrayLHS instanceof ArrayLHS) {
					ArrayList<Expression> innerIndices = new ArrayList<Expression>(
							Arrays.asList(((ArrayLHS) innerArrayLHS).getIndices()));
					innerIndices.add(currentSubscriptRex.lrVal.getValue());
					newLLVal.lhs = new ArrayLHS(loc, 
							((ArrayLHS) innerArrayLHS).getArray(), innerIndices.toArray(new Expression[0]));
				} else {
					newLLVal.lhs = new ArrayLHS(loc, 
							innerArrayLHS, new Expression[] { currentSubscriptRex.lrVal.getValue() });	
				}
				
				newLLVal.cType = newCType;
				result.lrVal = newLLVal;
			} else {
				throw new AssertionError("should not happen");
			}
		}
		
		return result;
	}

	private Expression computeSubscriptMultiplier(Dispatcher main,
			MemoryHandler memoryHandler, ILocation loc, CArray arrayCType,
			Expression offset) {
		for (int i = 1; i < arrayCType.getDimensions().length; i++) {
			offset = CHandler.createArithmeticExpression(IASTBinaryExpression.op_multiply,
					offset, 
					arrayCType.getDimensions()[i], 
					loc);
		}
		offset = CHandler.createArithmeticExpression(IASTBinaryExpression.op_multiply,
				offset, 
				memoryHandler.calculateSizeOf(arrayCType.getValueType(), loc), 
				loc);
		return offset;
	}
}
