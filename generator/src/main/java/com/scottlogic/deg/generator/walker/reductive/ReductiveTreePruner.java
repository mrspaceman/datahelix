package com.scottlogic.deg.generator.walker.reductive;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.generator.constraints.atomic.AtomicConstraintsHelper;
import com.scottlogic.deg.generator.decisiontree.*;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecHelper;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecMerger;
import com.scottlogic.deg.generator.reducer.ConstraintReducer;
import com.scottlogic.deg.generator.walker.reductive.fieldselectionstrategy.FieldValue;

import java.util.*;
import java.util.stream.Collectors;

public class ReductiveTreePruner {

    private final FieldSpecMerger merger;
    private final ConstraintReducer constraintReducer;
    private final FieldSpecHelper fieldSpecHelper;

    @Inject
    public ReductiveTreePruner(FieldSpecMerger merger, ConstraintReducer constraintReducer, FieldSpecHelper fieldSpecHelper) {
        this.merger = merger;
        this.constraintReducer = constraintReducer;
        this.fieldSpecHelper = fieldSpecHelper;
    }

    /**
     * Prunes a tree of any branches that are contradictory to the value of the nextFixedField
     * @param constraintNode The Tree to be pruned
     * @param value the field and value to prune for.
     * @return A pruned tree if the new tree is valid, Combined.contradictory otherwise
     */
    public Merged<ConstraintNode> pruneConstraintNode(ConstraintNode constraintNode, FieldValue value) {
        Map<Field, FieldSpec> fieldToSpec = new HashMap<>();
        fieldToSpec.put(value.getField(), fieldSpecHelper.getFieldSpecForValue(value));
        return pruneConstraintNode(constraintNode, fieldToSpec);
    }

    private Merged<ConstraintNode> pruneConstraintNode(ConstraintNode constraintNode, Map<Field, FieldSpec> fieldSpecs) {
        Merged<Map<Field, FieldSpec>> newFieldSpecs = combineConstraintsWithParent(constraintNode, fieldSpecs);
        if (newFieldSpecs.isContradictory()){
            return Merged.contradictory();
        }

        PruningState state = new PruningState(constraintNode);

        for (DecisionNode decision : constraintNode.getDecisions()) {
            Merged<DecisionNode> prunedDecisionNode = pruneDecisionNode(decision, newFieldSpecs.get());
            if (prunedDecisionNode.isContradictory()) {
                return Merged.contradictory();
            }

            state.addPrunedDecision(prunedDecisionNode.get());
        }

        if (state.hasPulledUpDecisions()){
            return pruneConstraintNode(
                state.getNewConstraintNode(),
                state.addPulledUpFieldsToMap(fieldSpecs));
        }

        return Merged.of(state.getNewConstraintNode());
    }

    private Merged<DecisionNode> pruneDecisionNode(DecisionNode decisionNode,  Map<Field, FieldSpec> fieldSpecs) {
        Collection<ConstraintNode> newConstraintNodes = new ArrayList<>();

        for (ConstraintNode constraintNode : decisionNode.getOptions()) {
            pruneConstraintNode(constraintNode, fieldSpecs).ifPresent(newConstraintNodes::add);
        }

        if (newConstraintNodes.isEmpty()) {
            return Merged.contradictory();
        }

        return Merged.of(new TreeDecisionNode(newConstraintNodes));
    }

    private Merged<Map<Field, FieldSpec>> combineConstraintsWithParent(ConstraintNode constraintNode, Map<Field, FieldSpec> parentFieldspecs) {
        Set<Field> relevantFields = parentFieldspecs.keySet();
        Merged<Map<Field, FieldSpec>> mergedMap = createFieldSpecMap(constraintNode.getAtomicConstraints(), relevantFields);
        if (mergedMap.isContradictory()){
            return Merged.contradictory();
        }
        Map<Field, FieldSpec> newMap = mergedMap.get();

        for (Map.Entry<Field, FieldSpec> entry : parentFieldspecs.entrySet()) {
            if (!newMap.containsKey(entry.getKey())){
                newMap.put(entry.getKey(), entry.getValue());
            }else {
                Optional<FieldSpec> mergedSpec = merger.merge(entry.getValue(), newMap.get(entry.getKey()));
                if (!mergedSpec.isPresent()){
                    return Merged.contradictory();
                }
                newMap.put(entry.getKey(), mergedSpec.get());
            }
        }

        return Merged.of(newMap);
    }

    private Merged<Map<Field, FieldSpec>> createFieldSpecMap(Collection<AtomicConstraint> pulledUpAtomicConstraints, Set<Field> relevantFields){
        Map<Field, Collection<AtomicConstraint>> m = new HashMap<>();
        pulledUpAtomicConstraints.forEach(constraint -> {
            if (relevantFields != null && !relevantFields.contains(constraint.getField())){
                return;
            }

            if (!m.containsKey(constraint.getField())) {
                m.put(constraint.getField(), new ArrayList<>(Arrays.asList(constraint)));
            } else {
                Collection<AtomicConstraint> atomicConstraints = m.get(constraint.getField());
                atomicConstraints.add(constraint);
            }
        });


        Map<Field, FieldSpec> newFieldSpecs = new HashMap<>();

        for (Map.Entry<Field, Collection<AtomicConstraint>> fieldToConstraints : m.entrySet()) {
            Optional<FieldSpec> fieldSpec = constraintReducer.reduceConstraintsToFieldSpec(fieldToConstraints.getValue());
            if (!fieldSpec.isPresent()){
                return Merged.contradictory();
            }
            newFieldSpecs.put(fieldToConstraints.getKey(), fieldSpec.get());
        }

        return Merged.of(newFieldSpecs);
    }
}
