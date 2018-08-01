package com.scottlogic.deg.restriction;

/**
 * For a given combination of choices over the decision tree
 * Details every column's atomic constraints
 */
public class FieldSpecMerger {
    private final SetRestrictionsMerger setRestrictionsMerger = new SetRestrictionsMerger();
    private final NumericRestrictionsMerger numericRestrictionsMerger = new NumericRestrictionsMerger();
    private final StringRestrictionsMerger stringRestrictionsMerger = new StringRestrictionsMerger();
    private final NullRestrictionsMerger nullRestrictionsMerger = new NullRestrictionsMerger();
    private final TypeRestrictionsMerger typeRestrictionsMerger = new TypeRestrictionsMerger();

    public FieldSpec merge(FieldSpec left, FieldSpec right) {
        final FieldSpec merged = new FieldSpec(getMergedName(left.getName(), right.getName()));
        merged.setSetRestrictions(setRestrictionsMerger.merge(left.getSetRestrictions(), right.getSetRestrictions()));
        merged.setNumericRestrictions(numericRestrictionsMerger.merge(left.getNumericRestrictions(), right.getNumericRestrictions()));
        merged.setStringRestrictions(stringRestrictionsMerger.merge(left.getStringRestrictions(), right.getStringRestrictions()));
        merged.setNullRestrictions(nullRestrictionsMerger.merge(left.getNullRestrictions(), right.getNullRestrictions()));
        merged.setTypeRestrictions(typeRestrictionsMerger.merge(left.getTypeRestrictions(), right.getTypeRestrictions()));
        return merged;
    }

    private String getMergedName(String left, String right) {
        if (!left.equals(right)) {
            throw new IllegalStateException();
        }
        return left;
    }
}
