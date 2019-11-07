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

package com.scottlogic.deg.profile.creation.serialisation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scottlogic.deg.profile.creation.dtos.ProfileDTO;
import com.scottlogic.deg.profile.reader.FileReader;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ProfileDeserialiser
{

    public static ProfileDTO deserialise(String json, FileReader fileReader)
    {
        ConstraintDeserializer.fileReader = fileReader;
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        try {
            return mapper.readerFor(ProfileDTO.class).readValue(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

