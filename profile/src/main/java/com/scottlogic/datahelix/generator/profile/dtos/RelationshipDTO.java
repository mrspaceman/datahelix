package com.scottlogic.datahelix.generator.profile.dtos;

import com.scottlogic.datahelix.generator.core.profile.Profile;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.ConstraintDTO;

import java.util.List;

public class RelationshipDTO {
    public String name;
    public String cardinality;
    public int minItems;
    public int maxItems;
    public Profile profile;
}
