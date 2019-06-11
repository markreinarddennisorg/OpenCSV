/*
 * Copyright 2017 Andrew Rucker Jones.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opencsv.bean.mocks;

import com.opencsv.CSVReader;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.exceptions.CsvBadConverterException;
import org.apache.commons.lang3.ArrayUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Locale;

public class ErrorHeaderMappingStrategy<T> implements MappingStrategy<T> {
    @Override
    public void captureHeader(CSVReader reader) throws IOException {
       throw new IOException("This is the test exception");
    }

    @Override
    public String[] generateHeader(T bean) {
        return new String[0];
    }
    
    @Override
    public void setType(Class type) throws CsvBadConverterException {}

    @Override
    public T populateNewBean(String[] line) {
        return null;
    }

    @Override
    public String[] transmuteBean(T bean) {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
}
