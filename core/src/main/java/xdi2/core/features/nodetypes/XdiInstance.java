package xdi2.core.features.nodetypes;

public interface XdiInstance<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiInstanceUnordered<EQC, EQI, C, U, O, I>, O extends XdiInstanceOrdered<EQC, EQI, C, U, O, I>, I extends XdiInstance<EQC, EQI, C, U, O, I>> extends XdiSubGraph<EQI> {

	public C getXdiCollection();
}
