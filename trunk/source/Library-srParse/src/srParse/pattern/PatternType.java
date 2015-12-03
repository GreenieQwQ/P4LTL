package srParse.pattern;

import java.util.Vector;

import pea.BooleanDecision;
import pea.CDD;
import pea.PhaseEventAutomata;
import pea.reqCheck.PatternToPEA;
import srParse.srParseScope;

public class PatternType
{
	//contains all CDDs from the patter in reverse order
	protected Vector<CDD> cdds;
	
	protected static CDD q_cdd_default = BooleanDecision.create("Q");
	protected static CDD r_cdd_default = BooleanDecision.create("R");
	protected int duration;
	protected PhaseEventAutomata pea;
	
	protected srParseScope scope;
	protected PatternToPEA peaTransformator;
	
	public PatternType(){}
	public PatternType(srParseScope scope){
		setScope( scope );
	}
	
	public int getDuration() {
		return duration;
	}
	
	public Vector<CDD> getCdds() {
		return cdds;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	protected CDD getDefaultQ_cdd() {
		return q_cdd_default;
	}

	protected CDD getDefaultR_cdd() {
		return r_cdd_default;
	}
	
	public void transform(){
		throw new UnsupportedOperationException();
	}
	
	public Vector<Integer> getElemHashes()
	{
		int i;
    	Vector<Integer> res=new Vector<Integer>();
    	
    	for( i=0;i<cdds.size();i++ ){
    		res.addAll( cdds.get(i).getElemHashes() );
    	}
    	if( scope.getCdd1()!=null && scope.getCdd1()!= q_cdd_default && scope.getCdd1()!= r_cdd_default){
			res.addAll( scope.getCdd1().getElemHashes() );
		}
    	if( scope.getCdd2()!=null && scope.getCdd2()!= q_cdd_default && scope.getCdd2()!= r_cdd_default ){
			res.addAll( scope.getCdd2().getElemHashes() );
		}
    	return res;
	}
	
	public void mergeCDDs( Vector<CDD> cdds )
	{
		int i;
		
		if( cdds==null )
			return;
		if( this.cdds==null )
			this.cdds=new Vector<CDD>();	
		for(i=0;i<cdds.size();i++){
			this.cdds.add(cdds.get(i));
		}
	}
	
	
	public PhaseEventAutomata transformToPea(){
		this.transform();
		return pea;
	}
	
	public PatternToPEA getPeaTransformator() {
		return peaTransformator;
	}

	public void setPeaTransformator(PatternToPEA peaTransformator) {
		this.peaTransformator = peaTransformator;
	}
	
	public srParseScope getScope() {
		return scope;
	}

	public void setScope(srParseScope scope) {
		this.scope = scope;
	}		

	public String toString(){
		String res=new String();
		res=scope.toString()+this.getClass().toString();
		return res;
	}
}








