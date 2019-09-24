/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.common.profile.Types;
import com.scottlogic.deg.generator.restrictions.MergeResult;
import com.scottlogic.deg.generator.restrictions.NumericRestrictions;
import com.scottlogic.deg.generator.restrictions.NumericRestrictionsMerger;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.scottlogic.deg.common.profile.Types.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NumericRestrictionsMergeOperationTests {
    private NumericRestrictionsMerger merger;
    private NumericRestrictionsMergeOperation operation;
    private FieldSpec left;
    private FieldSpec right;

    @BeforeEach
    public void beforeEach(){
        merger = mock(NumericRestrictionsMerger.class);
        operation = new NumericRestrictionsMergeOperation(merger);
        left = FieldSpec.fromType(NUMERIC).withNumericRestrictions(
            new NumericRestrictions());
        right = FieldSpec.fromType(NUMERIC).withNumericRestrictions(
            new NumericRestrictions());
    }

    @Test
    public void applyMergeOperation_withNoNumericRestrictions_shouldNotApplyAnyRestriction(){
        FieldSpec merging = FieldSpec.fromType(NUMERIC);
        when(merger.merge(left.getNumericRestrictions(), right.getNumericRestrictions()))
            .thenReturn(new MergeResult<>(null));

        FieldSpec result = operation.applyMergeOperation(left, right);

        Assert.assertEquals(result, merging);
    }

    @Test
    public void applyMergeOperation_withContradictoryNumericRestrictionsAndNoTypeRestrictions_shouldPreventAnyNumericValues(){
        FieldSpec merging = FieldSpec.fromType(NUMERIC);
        when(merger.merge(left.getNumericRestrictions(), right.getNumericRestrictions()))
            .thenReturn(MergeResult.unsuccessful());

        FieldSpec result = operation.applyMergeOperation(left, right);

        Assert.assertEquals(result, FieldSpec.nullOnlyFromType(NUMERIC));
    }

    @Test
    public void applyMergeOperation_withContradictoryNumericRestrictions_shouldPreventAnyNumericValues(){
        FieldSpec merging = FieldSpec.fromType(NUMERIC);

        when(merger.merge(left.getNumericRestrictions(), right.getNumericRestrictions()))
            .thenReturn(MergeResult.unsuccessful());

        FieldSpec result = operation.applyMergeOperation(left, right);

        Assert.assertEquals(result, FieldSpec.nullOnlyFromType(NUMERIC));
    }

    @Test
    public void applyMergeOperation_withContradictoryNumericRestrictionsAndNumericTypeOnlyPermittedType_shouldPreventAnyNumericValues(){
        FieldSpec merging = FieldSpec.fromType(NUMERIC);

        when(merger.merge(left.getNumericRestrictions(), right.getNumericRestrictions()))
            .thenReturn(MergeResult.unsuccessful());

        FieldSpec result = operation.applyMergeOperation(left, right);

        Assert.assertThat(result, not(sameInstance(merging)));
        Assert.assertThat(result.getWhitelist().set(), is(empty()));
    }

    @Disabled("same instance check not working due to being casted")
    @Test
    public void applyMergeOperation_withMergableNumericRestrictions_shouldApplyMergedNumericRestrictions(){
        FieldSpec merging = FieldSpec.fromType(NUMERIC);
        NumericRestrictions merged = new NumericRestrictions();
        when(merger.merge(left.getNumericRestrictions(), right.getNumericRestrictions()))
            .thenReturn(new MergeResult<>(merged));

        FieldSpec result = operation.applyMergeOperation(left, right);

        Assert.assertThat(result, not(sameInstance(merging)));
        Assert.assertThat(result.getNumericRestrictions(), sameInstance(merged));
        Assert.assertThat(result.getType(), sameInstance(merging.getType()));
    }
}