/*
 * Copyright 2016 Andrew Rucker Jones.
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
package com.opencsv.bean;

import com.opencsv.*;
import com.opencsv.bean.mocks.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Tests {@link StatefulBeanToCsv}.
 *
 * @author Andrew Rucker Jones (modified by Scott Conway to use CSVWriter for 4.2)
 */
public class StatefulBeanToCsvWithCSVWriterTest {

    private static Locale systemLocale;
    private static final String EXTRA_STRING_FOR_WRITING = "extrastringforwritinghowcreative";
    private static final String GOOD_DATA_1 = "test string;value: true;false;1;2;3;4;123,101.101;123.202,202;123303.303;123.404,404;123101.1;1.000,2;2000.3;3.000,4;5000;6.000;2147476647;8.000;9000;10.000;11000;12.000;13000;14.000;15000;16.000;a;b;123101.101;123.102,102;101;102;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;01/15/1978;13. Dez\\.? 2018;19780115T063209;19780115T063209;1.01;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;";
    private static final String GOOD_DATA_2 = "test string;;false;1;2;3;4;123,101.101;123.202,202;123303.303;123.404,404;123101.1;1.000,2;2000.3;3.000,4;5000;6.000;2147476647;8.000;9000;10.000;11000;12.000;13000;14.000;15000;16.000;a;b;123101.101;123.102,102;101;102;19780115T063209;19780115T163209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;01/15/1978;13. Dez\\.? 2018;19780115T063209;19780115T063209;2.02;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;";
    private static final String GOOD_DATA_OPTIONALS_NULL = "test string;value: true;false;1;2;3;4;123,101.101;123.202,202;123303.303;123.404,404;;1.000,2;2000.3;3.000,4;5000;6.000;2147476647;8.000;9000;10.000;11000;12.000;13000;14.000;15000;16.000;a;b;123101.101;123.102,102;101;102;19780115T063209;19780115T063209;;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;01/15/1978;13. Dez\\.? 2018;19780115T063209;19780115T063209;1.01;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;";
    private static final String GOOD_DATA_CUSTOM_1 = "inside custom converter;wahr;falsch;127;127;127;;1.7976931348623157E308;1.7976931348623157E308;1.7976931348623157E308;1.7976931348623157E308;3.4028235E38;3.4028235E38;3.4028235E38;3.4028235E38;2147483647;2147483647;2147483647;2147483647;9223372036854775807;9223372036854775807;9223372036854775807;9223372036854775807;32767;32767;32767;32767;\uFFFF;\uFFFF;10;10;10;10;;;;;;;;;;;;;falsch;wahr;really long test string, yeah!;1.a.long,long.string1;2147483645.z.Inserted in setter methodlong,long.string2;3.c.long,long.derived.string3;inside custom converter";
    private static final String HEADER_NAME_FULL = "BIGDECIMAL1;BIGDECIMAL2;BIGINTEGER1;BIGINTEGER2;BOOL1;BOOLPRIMITIVE;BYTE1;BYTE2;BYTE3;BYTE4;CHAR1;CHAR2;DATE1;DATE10;DATE11;DATE12;DATE13;DATE14;DATE15;DATE16;DATE2;DATE3;DATE4;DATE5;DATE6;DATE7;DATE8;DATE9;DOUBLE1;DOUBLE2;DOUBLE3;DOUBLE4;FLOAT1;FLOAT2;FLOAT3;FLOAT4;FLOAT5;INTEGER1;INTEGER2;INTEGER3;INTEGER4;ITNOGOODCOLUMNITVERYBAD;LONG1;LONG2;LONG3;LONG4;SHORT1;SHORT2;SHORT3;SHORT4;STRING1";
    private static final String GOOD_DATA_NAME_1 = "123101.101;123.102,102;101;102;value: true;false;1;2;3;4;a;b;19780115T063209;19780115T063209;19780115T063209;19780115T063209;01/15/1978;13. Dez\\.? 2018;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;123,101.101;123.202,202;123303.303;123.404,404;123101.1;1.000,2;2000.3;3.000,4;1.01;5000;6.000;2147476647;8.000;;9000;10.000;11000;12.000;13000;14.000;15000;16.000;test string";
    private static final String HEADER_NAME_FULL_CUSTOM = "BIGDECIMAL1;BIGDECIMAL2;BIGINTEGER1;BIGINTEGER2;BOOL1;BOOL2;BOOL3;BOOLPRIMITIVE;BYTE1;BYTE2;BYTE3;CHAR1;CHAR2;COMPLEX1;COMPLEX2;COMPLEX3;DOUBLE1;DOUBLE2;DOUBLE3;DOUBLE4;FLOAT1;FLOAT2;FLOAT3;FLOAT4;INTEGER1;INTEGER2;INTEGER3;INTEGER4;LONG1;LONG2;LONG3;LONG4;REQUIREDWITHCUSTOM;SHORT1;SHORT2;SHORT3;SHORT4;STRING1;STRING2";
    private static final String GOOD_DATA_NAME_CUSTOM_1 = "10;10;10;10;wahr;falsch;wahr;falsch;127;127;127;\uFFFF;\uFFFF;1.a.long,long.string1;2147483645.z.Inserted in setter methodlong,long.string2;3.c.long,long.derived.string3;1.7976931348623157E308;1.7976931348623157E308;1.7976931348623157E308;1.7976931348623157E308;3.4028235E38;3.4028235E38;3.4028235E38;3.4028235E38;2147483647;2147483647;2147483647;2147483647;9223372036854775807;9223372036854775807;9223372036854775807;9223372036854775807;inside custom converter;32767;32767;32767;32767;inside custom converter;really long test string, yeah!";
    private static final String GOOD_DATA_NAME_CUSTOM_2 = "10;10;10;10;wahr;falsch;wahr;falsch;127;127;127;\uFFFF;\uFFFF;4.d.long,long.string4;2147483642.z.Inserted in setter methodlong,long.derived.string5;6.f.long,long.string6;1.7976931348623157E308;1.7976931348623157E308;1.7976931348623157E308;1.7976931348623157E308;3.4028235E38;3.4028235E38;3.4028235E38;3.4028235E38;2147483647;2147483647;2147483647;2147483647;9223372036854775807;9223372036854775807;9223372036854775807;9223372036854775807;inside custom converter;32767;32767;32767;32767;inside custom converter;really";
    private static final String HEADER_NAME_FULL_DERIVED = "BIGDECIMAL1;BIGDECIMAL2;BIGINTEGER1;BIGINTEGER2;BOOL1;BOOLPRIMITIVE;BYTE1;BYTE2;BYTE3;BYTE4;CHAR1;CHAR2;DATE1;DATE10;DATE11;DATE12;DATE13;DATE14;DATE15;DATE16;DATE2;DATE3;DATE4;DATE5;DATE6;DATE7;DATE8;DATE9;DOUBLE1;DOUBLE2;DOUBLE3;DOUBLE4;FLOAT1;FLOAT2;FLOAT3;FLOAT4;FLOAT5;INT IN SUBCLASS;INTEGER1;INTEGER2;INTEGER3;INTEGER4;ITNOGOODCOLUMNITVERYBAD;LONG1;LONG2;LONG3;LONG4;SHORT1;SHORT2;SHORT3;SHORT4;STRING1";
    private static final String GOOD_DATA_NAME_DERIVED_1 = "123101.101;123.102,102;101;102;value: true;false;1;2;3;4;a;b;19780115T063209;19780115T063209;19780115T063209;19780115T063209;01/15/1978;13. Dez\\.? 2018;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;123,101.101;123.202,202;123303.303;123.404,404;123101.1;123.202,203;123303.305;123.404,406;1.01;7;5000;6.000;2147476647;8.000;;9000;10.000;11000;12.000;13000;14.000;15000;16.000;test string";
    private static final String GOOD_DATA_NAME_DERIVED_SUB_1 = "123101.101;123.102,102;101;102;value: true;false;1;2;3;4;a;b;19780115T063209;19780115T063209;19780115T063209;19780115T063209;01/15/1978;13. Dez\\.? 2018;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;123,101.101;123.202,202;123303.303;123.404,404;123101.1;123.202,203;123303.305;123.404,406;1.01;5000;6.000;2147476647;8.000;;9000;10.000;11000;12.000;13000;14.000;15000;16.000;test string";


    private StringWriter writer;
    private CSVWriterBuilder csvWriterBuilder;

    @BeforeAll
    public static void storeSystemLocale() {
        systemLocale = Locale.getDefault();
    }

    @BeforeEach
    public void setSystemLocaleToValueNotGerman() {
        Locale.setDefault(Locale.US);
    }

    @BeforeEach
    public void createWriters() {
        writer = new StringWriter(ICSVWriter.INITIAL_STRING_SIZE);
        csvWriterBuilder = new CSVWriterBuilder(writer);
    }

    @AfterEach
    public void setSystemLocaleBackToDefault() {
        Locale.setDefault(systemLocale);
    }

    private ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> createTwoGoodBeans()
            throws IOException {
        List<AnnotatedMockBeanFull> beans = new CsvToBeanBuilder<AnnotatedMockBeanFull>(
                new FileReader("src/test/resources/testinputwriteposfullgood.csv"))
                .withType(AnnotatedMockBeanFull.class).withSeparator(';').build().parse();
        return new ImmutablePair<>(beans.get(0), beans.get(1));
    }

    private ImmutablePair<AnnotatedMockBeanCustom, AnnotatedMockBeanCustom> createTwoGoodCustomBeans()
            throws IOException {
        List<AnnotatedMockBeanCustom> beans = new CsvToBeanBuilder<AnnotatedMockBeanCustom>(
                new FileReader("src/test/resources/testinputwritecustomposfullgood.csv"))
                .withType(AnnotatedMockBeanCustom.class).withSeparator(';').build().parse();
        return new ImmutablePair<>(beans.get(0), beans.get(1));
    }

    private ImmutablePair<AnnotatedMockBeanFullDerived, AnnotatedMockBeanFullDerived> createTwoGoodDerivedBeans()
            throws IOException {
        HeaderColumnNameMappingStrategy<AnnotatedMockBeanFullDerived> strat = new HeaderColumnNameMappingStrategy<>();
        strat.setType(AnnotatedMockBeanFullDerived.class);
        List<AnnotatedMockBeanFullDerived> beans = new CsvToBeanBuilder<AnnotatedMockBeanFullDerived>(
                new FileReader("src/test/resources/testinputderivedgood.csv"))
                .withType(AnnotatedMockBeanFullDerived.class)
                .withSeparator(';')
                .withMappingStrategy(strat)
                .build()
                .parse();
        return new ImmutablePair<>(beans.get(0), beans.get(1));
    }

    /**
     * Test of writing a single bean.
     * This also incidentally covers the following conditions because of the
     * datatypes and annotations in the bean used in testing:<ul>
     * <li>Writing every primitive data type</li>
     * <li>Writing every wrapped primitive data type</li>
     * <li>Writing String, BigDecimal and BigInteger</li>
     * <li>Writing all locale-sensitive data without locales</li>
     * <li>Writing all locale-sensitive data with locales</li>
     * <li>Writing a date type without an explicit format string</li>
     * <li>Writing a date type with an explicit format string</li>
     * <li>Writing with mixed @CsvBindByName and @CsvBindByPosition annotation
     * types (expected behavior: The column position mapping strategy is
     * automatically selected)</li></ul>
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeSingleBeanNoQuotes() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .build();
        btcsv.write(beans.left);
        assertTrue(Pattern.matches(GOOD_DATA_1 + "\n", writer.toString()));
    }

    @Test
    public void writeSingleOptionallyQuotedBean() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        RFC4180ParserBuilder parserBuilder = new RFC4180ParserBuilder();
        RFC4180Parser parser = parserBuilder.withSeparator(';').build();
        ICSVWriter csvWriter = csvWriterBuilder
                .withParser(parser)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .build();
        beans.left.setStringClass("Quoted \"air quotes\" string");
        btcsv.write(beans.left);
        String output = writer.toString();
        assertTrue(Pattern.matches(
                "\"Quoted \"\"air quotes\"\" string\";\"value: true\";\"false\";\"1\";\"2\";\"3\";\"4\";\"123,101\\.101\";\"123\\.202,202\";\"123303\\.303\";\"123\\.404,404\";\"123101\\.1\";\"1\\.000,2\";\"2000\\.3\";\"3\\.000,4\";\"5000\";\"6\\.000\";\"2147476647\";\"8\\.000\";\"9000\";\"10\\.000\";\"11000\";\"12\\.000\";\"13000\";\"14\\.000\";\"15000\";\"16\\.000\";\"a\";\"b\";\"123101\\.101\";\"123\\.102,102\";\"101\";\"102\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"01/15/1978\";\"13\\. Dez\\.? 2018\";\"19780115T063209\";\"19780115T063209\";\"1\\.01\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\"\n",
                output));
    }

    @Test
    public void writeSingleOptionallyQuotedBeanWithCSVParser() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        CSVParserBuilder parserBuilder = new CSVParserBuilder();
        CSVParser parser = parserBuilder.withSeparator(';').build();
        ICSVWriter csvWriter = csvWriterBuilder
                .withParser(parser)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .build();
        beans.left.setStringClass("Quoted \"air quotes\" string");
        btcsv.write(beans.left);
        String output = writer.toString();
        assertTrue(Pattern.matches(
                "\"Quoted \"\"air quotes\"\" string\";\"value: true\";\"false\";\"1\";\"2\";\"3\";\"4\";\"123,101\\.101\";\"123\\.202,202\";\"123303\\.303\";\"123\\.404,404\";\"123101\\.1\";\"1\\.000,2\";\"2000\\.3\";\"3\\.000,4\";\"5000\";\"6\\.000\";\"2147476647\";\"8\\.000\";\"9000\";\"10\\.000\";\"11000\";\"12\\.000\";\"13000\";\"14\\.000\";\"15000\";\"16\\.000\";\"a\";\"b\";\"123101\\.101\";\"123\\.102,102\";\"101\";\"102\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"01/15/1978\";\"13\\. Dez\\.? 2018\";\"19780115T063209\";\"19780115T063209\";\"1\\.01\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\"\n",
                output));
    }

    @Test
    public void writeSingleOptionallyQuotedBeanWithPlainCSVWriter() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withApplyQuotesToAll(true)
                .build();
        beans.left.setStringClass("Quoted \"air quotes\" string");
        btcsv.write(beans.left);
        String output = writer.toString();
        assertTrue(Pattern.matches(
                "\"Quoted \"\"air quotes\"\" string\";\"value: true\";\"false\";\"1\";\"2\";\"3\";\"4\";\"123,101\\.101\";\"123\\.202,202\";\"123303\\.303\";\"123\\.404,404\";\"123101\\.1\";\"1\\.000,2\";\"2000\\.3\";\"3\\.000,4\";\"5000\";\"6\\.000\";\"2147476647\";\"8\\.000\";\"9000\";\"10\\.000\";\"11000\";\"12\\.000\";\"13000\";\"14\\.000\";\"15000\";\"16\\.000\";\"a\";\"b\";\"123101\\.101\";\"123\\.102,102\";\"101\";\"102\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"19780115T063209\";\"01/15/1978\";\"13\\. Dez\\.? 2018\";\"19780115T063209\";\"19780115T063209\";\"1\\.01\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\"\n",
                output));
    }

    @Test
    public void writeSingleQuotedBean() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withApplyQuotesToAll(false)
                .build();
        beans.left.setStringClass("Quoted \"air quotes\" string");
        btcsv.write(beans.left);
        assertTrue(Pattern.matches(
                "\"Quoted \"\"air quotes\"\" string\";value: true;false;1;2;3;4;123,101\\.101;123\\.202,202;123303\\.303;123\\.404,404;123101.1;1\\.000,2;2000\\.3;3\\.000,4;5000;6\\.000;2147476647;8\\.000;9000;10\\.000;11000;12\\.000;13000;14\\.000;15000;16\\.000;a;b;123101\\.101;123\\.102,102;101;102;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;19780115T063209;01/15/1978;13\\. Dez\\.? 2018;19780115T063209;19780115T063209;1\\.01;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n",
                writer.toString()));
    }

    /**
     * Test of writing multiple beans at once when order counts.
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeMultipleBeansOrdered() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        List<AnnotatedMockBeanFull> beanList = new ArrayList<>();
        beanList.add(beans.left);
        beanList.add(beans.right);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withApplyQuotesToAll(true)
                .build();
        btcsv.write(beanList);
        assertTrue(Pattern.matches(GOOD_DATA_1 + "\n" + GOOD_DATA_2 + "\n", writer.toString()));
    }

    /**
     * Test of writing multiple beans at once when order doesn't matter.
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeMultipleBeansUnordered() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        List<AnnotatedMockBeanFull> beanList = new ArrayList<>();
        beanList.add(beans.left);
        beanList.add(beans.right);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withApplyQuotesToAll(true)
                .build();
        btcsv.write(beanList);
        String r = writer.toString();
        assertTrue(Pattern.matches(GOOD_DATA_1 + "\n" + GOOD_DATA_2 + "\n", r) || Pattern.matches(GOOD_DATA_2 + "\n" + GOOD_DATA_1 + "\n", r));
    }

    /**
     * Test of writing a mixture of single beans and multiple beans.
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeMixedSingleMultipleBeans() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        List<AnnotatedMockBeanFull> beanList = new ArrayList<>();
        beanList.add(beans.left);
        beanList.add(beans.right);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withLineEnd("arj\n")
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withApplyQuotesToAll(true)
                .build();
        btcsv.write(beanList);
        btcsv.write(beans.left);
        assertTrue(Pattern.matches(GOOD_DATA_1 + "arj\n" + GOOD_DATA_2 + "arj\n" + GOOD_DATA_1 + "arj\n", writer.toString()));
    }

    /**
     * Test of writing optional fields whose values are null.
     * We test:<ul>
     * <li>A wrapped primitive, and</li>
     * <li>A date</li></ul>
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeOptionalFieldsWithNull() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        beans.left.setFloatWrappedDefaultLocale(null);
        beans.left.setCalDefaultLocale(null);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar('|')
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withApplyQuotesToAll(true)
                .build();
        btcsv.write(beans.left);
        assertTrue(Pattern.matches(GOOD_DATA_OPTIONALS_NULL + "\n", writer.toString()));
    }

    /**
     * Test of writing an optional field with a column position not adjacent
     * to the other column positions.
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeOptionalNonContiguousField() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        beans.left.setColumnDoesntExist(EXTRA_STRING_FOR_WRITING);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withApplyQuotesToAll(true)
                .build();
        btcsv.write(beans.left);
        assertTrue(Pattern.matches(GOOD_DATA_1 + EXTRA_STRING_FOR_WRITING + "\n", writer.toString()));
    }

    /**
     * Test of writing using a specified mapping strategy.
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeSpecifiedStrategy() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        HeaderColumnNameMappingStrategy<AnnotatedMockBeanFull> strat = new HeaderColumnNameMappingStrategy<>();
        strat.setType(AnnotatedMockBeanFull.class);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withMappingStrategy(strat)
                .build();
        btcsv.write(beans.left);
        assertTrue(Pattern.matches(HEADER_NAME_FULL + "\n" + GOOD_DATA_NAME_1 + "\n", writer.toString()));
    }

    /**
     * Test of writing with @CsvBindByPosition attached to unknown type.
     * Expected behavior: Data are written with toString().
     *
     * @throws CsvException Never
     */
    @Test
    public void writeBindByPositionUnknownType() throws CsvException {
        BindUnknownType byNameUnsupported = new BindUnknownType();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<BindUnknownType> btcsv = new StatefulBeanToCsvBuilder<BindUnknownType>(csvWriter)
                .build();
        btcsv.write(byNameUnsupported);
        assertEquals(BindUnknownType.TOSTRING + "\n", writer.toString());
    }

    /**
     * Test of writing with @CsvBindByName attached to unknown type.
     * Expected behavior: Data are written with toString().
     *
     * @throws CsvException Never
     */
    @Test
    public void writeBindByNameUnknownType() throws CsvException {
        BindUnknownType byNameUnsupported = new BindUnknownType();
        HeaderColumnNameMappingStrategy<BindUnknownType> strat = new HeaderColumnNameMappingStrategy<>();
        strat.setType(BindUnknownType.class);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<BindUnknownType> btcsv = new StatefulBeanToCsvBuilder<BindUnknownType>(csvWriter)
                .withMappingStrategy(strat)
                .build();
        btcsv.write(byNameUnsupported);
        assertEquals("TEST\n" + BindUnknownType.TOSTRING + "\n", writer.toString());
    }

    /**
     * Test writing with no annotations.
     *
     * @throws CsvException Never
     */
    @Test
    public void writeWithoutAnnotations() throws CsvException {
        ComplexClassForCustomAnnotation cc = new ComplexClassForCustomAnnotation();
        cc.c = 'A';
        cc.i = 1;
        cc.s = "String";
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<ComplexClassForCustomAnnotation> btcsv = new StatefulBeanToCsvBuilder<ComplexClassForCustomAnnotation>(csvWriter)
                .build();
        btcsv.write(cc);
        assertEquals("C;I;S\nA;1;String\n", writer.toString());
    }

    /**
     * Writing a subclass with annotations in the subclass and the superclass.
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeDerivedSubclass() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFullDerived, AnnotatedMockBeanFullDerived> derivedList = createTwoGoodDerivedBeans();
        HeaderColumnNameMappingStrategy<AnnotatedMockBeanFullDerived> strat = new HeaderColumnNameMappingStrategy<>();
        strat.setType(AnnotatedMockBeanFullDerived.class);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFullDerived> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFullDerived>(csvWriter)
                .withMappingStrategy(strat)
                .build();
        btcsv.write(derivedList.left);
        assertTrue(Pattern.matches(HEADER_NAME_FULL_DERIVED + "\n" + GOOD_DATA_NAME_DERIVED_1 + "\n", writer.toString()));
    }

    /**
     * Specifying a superclass, but writing a subclass.
     * Expected behavior: Data from superclass are written.
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeDerivedSuperclass() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFullDerived, AnnotatedMockBeanFullDerived> derivedList = createTwoGoodDerivedBeans();
        HeaderColumnNameMappingStrategy<AnnotatedMockBeanFull> strat = new HeaderColumnNameMappingStrategy<>();
        strat.setType(AnnotatedMockBeanFull.class);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withMappingStrategy(strat)
                .build();
        btcsv.write(derivedList.left);
        assertTrue(Pattern.matches(HEADER_NAME_FULL + "\n" + GOOD_DATA_NAME_DERIVED_SUB_1 + "\n", writer.toString()));
    }

    /**
     * Tests of writing when getter is missing.
     * Also tests incidentally:<ul>
     * <li>Writing bad data without exceptions captured</li></ul>
     *
     * @throws CsvException         Never
     */
    @Test
    public void writeGetterMissing() throws CsvException {
        GetterMissing getterMissing = new GetterMissing();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<GetterMissing> sbtcsv = new StatefulBeanToCsvBuilder<GetterMissing>(csvWriter)
                .build();
        sbtcsv.write(getterMissing);
        assertEquals("TEST\n123\n", writer.toString());
    }

    /**
     * Tests writing when getter is private.
     *
     * @throws CsvException         Never
     */
    @Test
    public void writeGetterPrivate() throws CsvException {
        GetterPrivate getterPrivate = new GetterPrivate();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<GetterPrivate> sbtcsv = new StatefulBeanToCsvBuilder<GetterPrivate>(csvWriter)
                .build();
        sbtcsv.write(getterPrivate);
        assertEquals("TEST\n123\n", writer.toString());
    }

    /**
     * Writing a required wrapped primitive field that is null.
     * Also tests incidentally:<ul>
     * <li>Writing bad data with exceptions captured</li></ul>
     *
     * @throws IOException          Never
     * @throws CsvException         Never
     * @throws NoSuchFieldException Never
     */
    @Test
    public void writeNullRequiredWrappedPrimitive() throws IOException, CsvException, NoSuchFieldException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> sbtcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withThrowExceptions(false)
                .build();
        beans.left.setByteWrappedSetLocale(null); // required
        sbtcsv.write(beans.left);
        List<CsvException> csves = sbtcsv.getCapturedExceptions();
        assertNotNull(csves);
        assertEquals(1, csves.size());
        CsvException csve = csves.get(0);
        assertTrue(csve instanceof CsvRequiredFieldEmptyException);
        CsvRequiredFieldEmptyException rfe = (CsvRequiredFieldEmptyException) csve;
        assertEquals(1L, rfe.getLineNumber());
        assertEquals(AnnotatedMockBeanFull.class, rfe.getBeanClass());
        assertEquals(beans.left.getClass().getDeclaredField("byteWrappedSetLocale"),
                rfe.getDestinationField());
    }

    /**
     * Writing a required field with a custom converter that is null.
     *
     * @throws IOException          Never
     * @throws CsvException         Never
     * @throws NoSuchFieldException Never
     */
    @Test
    public void writeNullRequiredCustom() throws IOException, CsvException, NoSuchFieldException {
        ImmutablePair<AnnotatedMockBeanCustom, AnnotatedMockBeanCustom> beans = createTwoGoodCustomBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanCustom> sbtcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanCustom>(csvWriter)
                .withThrowExceptions(false)
                .build();
        beans.left.setRequiredWithCustom(null); // required
        sbtcsv.write(beans.left);
        List<CsvException> csves = sbtcsv.getCapturedExceptions();
        assertNotNull(csves);
        assertEquals(1, csves.size());
        CsvException csve = csves.get(0);
        assertTrue(csve instanceof CsvRequiredFieldEmptyException);
        CsvRequiredFieldEmptyException rfe = (CsvRequiredFieldEmptyException) csve;
        assertEquals(1L, rfe.getLineNumber());
        assertEquals(AnnotatedMockBeanCustom.class, rfe.getBeanClass());
        assertEquals(beans.left.getClass().getDeclaredField("requiredWithCustom"),
                rfe.getDestinationField());
    }

    /**
     * Writing a bad bean at the beginning of a long list to trigger shutting
     * down the ExecutorService.
     *
     * @throws IOException          Never
     * @throws CsvException         Never
     * @throws NoSuchFieldException Never
     */
    @Test
    public void writeManyFirstBeanIsBad() throws IOException, CsvException, NoSuchFieldException {
        ImmutablePair<AnnotatedMockBeanCustom, AnnotatedMockBeanCustom> beans = createTwoGoodCustomBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanCustom> sbtcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanCustom>(csvWriter)
                .withThrowExceptions(true)
                .build();
        assertTrue(sbtcsv.isThrowExceptions());
        beans.left.setRequiredWithCustom(null); // required
        List<AnnotatedMockBeanCustom> beanList = new ArrayList<>(1000);
        beanList.add(beans.left);
        for (int i = 0; i < 999; i++) {
            beanList.add(beans.right);
        }
        try {
            sbtcsv.write(beanList);
        } catch (CsvRequiredFieldEmptyException rfe) {
            assertEquals(1L, rfe.getLineNumber());
            assertEquals(AnnotatedMockBeanCustom.class, rfe.getBeanClass());
            assertEquals(beans.left.getClass().getDeclaredField("requiredWithCustom"),
                    rfe.getDestinationField());
        }
    }

    /**
     * Writing a bad bean when exceptions are not thrown and the results are
     * unordered.
     *
     * @throws IOException          Never
     * @throws CsvException         Never
     * @throws NoSuchFieldException Never
     */
    @Test
    public void writeBadBeanUnorderedCaptureExceptions() throws IOException, CsvException, NoSuchFieldException {
        ImmutablePair<AnnotatedMockBeanCustom, AnnotatedMockBeanCustom> beans = createTwoGoodCustomBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanCustom> sbtcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanCustom>(csvWriter)
                .withThrowExceptions(false)
                .withOrderedResults(false)
                .build();
        beans.left.setRequiredWithCustom(null); // required
        List<AnnotatedMockBeanCustom> beanList = new ArrayList<>(10);
        beanList.add(beans.left);
        for (int i = 0; i < 9; i++) {
            beanList.add(beans.right);
        }
        sbtcsv.write(beanList);
        List<CsvException> exceptionList = sbtcsv.getCapturedExceptions();
        assertNotNull(exceptionList);
        assertEquals(1, exceptionList.size());
        CsvException csve = exceptionList.get(0);
        assertTrue(csve instanceof CsvRequiredFieldEmptyException);
        CsvRequiredFieldEmptyException rfe = (CsvRequiredFieldEmptyException) csve;
        assertEquals(1L, rfe.getLineNumber());
        assertEquals(AnnotatedMockBeanCustom.class, rfe.getBeanClass());
        assertEquals(beans.left.getClass().getDeclaredField("requiredWithCustom"),
                rfe.getDestinationField());
    }

    /**
     * Writing a required date field that is null.
     *
     * @throws IOException          Never
     * @throws CsvException         Never
     * @throws NoSuchFieldException Never
     */
    @Test
    public void writeNullRequiredDate() throws IOException, CsvException, NoSuchFieldException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        beans.right.setDateDefaultLocale(null); // required
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> sbtcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withThrowExceptions(false)
                .build();
        sbtcsv.write(beans.left);
        sbtcsv.write(beans.right);
        List<CsvException> csves = sbtcsv.getCapturedExceptions();
        assertNotNull(csves);
        assertEquals(1, csves.size());
        CsvException csve = csves.get(0);
        assertTrue(csve instanceof CsvRequiredFieldEmptyException);
        CsvRequiredFieldEmptyException rfe = (CsvRequiredFieldEmptyException) csve;
        assertEquals(2L, rfe.getLineNumber());
        assertEquals(AnnotatedMockBeanFull.class, rfe.getBeanClass());
        assertEquals(beans.right.getClass().getDeclaredField("dateDefaultLocale"),
                rfe.getDestinationField());
    }

    /**
     * Reading captured exceptions twice in a row.
     *
     * @throws IOException          Never
     * @throws CsvException         Never
     */
    @Test
    public void readCapturedExceptionsIsDestructive() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        beans.left.setByteWrappedSetLocale(null); // required
        beans.right.setDateDefaultLocale(null); // required
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> sbtcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withThrowExceptions(false)
                .build();
        sbtcsv.write(beans.left);
        sbtcsv.write(beans.right);
        sbtcsv.getCapturedExceptions(); // First call
        List<CsvException> csves = sbtcsv.getCapturedExceptions(); // Second call
        assertTrue(csves.isEmpty());
    }

    /**
     * Tests writing multiple times with exceptions from each write.
     *
     * @throws IOException          Never
     * @throws CsvException         Never
     */
    @Test
    public void multipleWritesCapturedExceptions() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanFull, AnnotatedMockBeanFull> beans = createTwoGoodBeans();
        beans.left.setByteWrappedSetLocale(null); // required
        beans.right.setDateDefaultLocale(null); // required
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanFull> sbtcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanFull>(csvWriter)
                .withThrowExceptions(false)
                .build();
        sbtcsv.write(beans.left);
        sbtcsv.write(beans.right);
        List<CsvException> csves = sbtcsv.getCapturedExceptions();
        assertEquals(2, csves.size());
    }

    /**
     * Tests binding a custom converter to the wrong data type.
     * Also incidentally tests that the error locale works.
     *
     * @throws CsvException         Never
     */
    @Test
    public void bindCustomConverterToWrongDataType() throws CsvException {
        BindCustomToWrongDataType wrongTypeBean = new BindCustomToWrongDataType();
        wrongTypeBean.setWrongType(GOOD_DATA_1);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .build();
        StatefulBeanToCsv<BindCustomToWrongDataType> sbtcsv = new StatefulBeanToCsvBuilder<BindCustomToWrongDataType>(csvWriter)
                .withThrowExceptions(false)
                .build();
        sbtcsv.write(wrongTypeBean);
        List<CsvException> csves = sbtcsv.getCapturedExceptions();
        assertNotNull(csves);
        assertEquals(1, csves.size());
        CsvException csve = csves.get(0);
        assertTrue(csve instanceof CsvDataTypeMismatchException);
        CsvDataTypeMismatchException dtm = (CsvDataTypeMismatchException) csve;
        assertEquals(1L, dtm.getLineNumber());
        assertTrue(dtm.getSourceObject() instanceof BindCustomToWrongDataType);
        assertEquals(String.class, dtm.getDestinationClass());
        String englishErrorMessage = dtm.getLocalizedMessage();

        // Now with another locale
        writer = new StringWriter();
        sbtcsv = new StatefulBeanToCsvBuilder<BindCustomToWrongDataType>(csvWriter)
                .withThrowExceptions(false)
                .withErrorLocale(Locale.GERMAN)
                .build();
        sbtcsv.write(wrongTypeBean);
        csves = sbtcsv.getCapturedExceptions();
        assertNotNull(csves);
        assertEquals(1, csves.size());
        csve = csves.get(0);
        assertTrue(csve instanceof CsvDataTypeMismatchException);
        dtm = (CsvDataTypeMismatchException) csve;
        assertEquals(1L, dtm.getLineNumber());
        assertTrue(dtm.getSourceObject() instanceof BindCustomToWrongDataType);
        assertEquals(String.class, dtm.getDestinationClass());
        assertNotSame(englishErrorMessage, dtm.getLocalizedMessage());
    }

    /**
     * Test of good data with custom converters and a column position mapping
     * strategy.
     * Incidentally covers the following behavior by virtue of the beans
     * written:<ul>
     * <li>Writing with ConvertGermanToBoolean</li>
     * <li>Writing with ConvertSplitOnWhitespace</li>
     * </ul>
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeCustomByPosition() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanCustom, AnnotatedMockBeanCustom> beans = createTwoGoodCustomBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanCustom> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanCustom>(csvWriter)
                .build();
        btcsv.write(beans.left);
        assertEquals(GOOD_DATA_CUSTOM_1 + "\n", writer.toString());
    }

    /**
     * Test of good data with custom converters and a header name mapping
     * strategy.
     * Incidentally test writing a mixture of single and multiple beans with
     * custom converters.
     *
     * @throws IOException  Never
     * @throws CsvException Never
     */
    @Test
    public void writeCustomByName() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanCustom, AnnotatedMockBeanCustom> beans = createTwoGoodCustomBeans();
        HeaderColumnNameMappingStrategy<AnnotatedMockBeanCustom> strat = new HeaderColumnNameMappingStrategy<>();
        strat.setType(AnnotatedMockBeanCustom.class);
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanCustom> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanCustom>(csvWriter)
                .withMappingStrategy(strat)
                .build();
        btcsv.write(beans.right);
        btcsv.write(Arrays.asList(beans.left, beans.right));
        assertEquals(
                HEADER_NAME_FULL_CUSTOM + "\n" + GOOD_DATA_NAME_CUSTOM_2 + "\n" + GOOD_DATA_NAME_CUSTOM_1 + "\n" + GOOD_DATA_NAME_CUSTOM_2 + "\n",
                writer.toString());
    }

    /**
     * Tests writing an empty field annotated with the custom converter
     * {@link com.opencsv.bean.customconverter.ConvertGermanToBoolean} with
     * required set to true.
     *
     * @throws IOException          Never
     * @throws CsvException         Never
     */
    @Test
    public void writeEmptyFieldWithConvertGermanToBooleanRequired() throws IOException, CsvException {
        ImmutablePair<AnnotatedMockBeanCustom, AnnotatedMockBeanCustom> beans = createTwoGoodCustomBeans();
        ICSVWriter csvWriter = csvWriterBuilder
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(';')
                .build();
        StatefulBeanToCsv<AnnotatedMockBeanCustom> btcsv = new StatefulBeanToCsvBuilder<AnnotatedMockBeanCustom>(csvWriter)
                .build();
        beans.left.setBoolWrapped(null);
        try {
            btcsv.write(beans.left);
            fail("Exception should have been thrown!");
        } catch (CsvRequiredFieldEmptyException e) {
            assertEquals(1, e.getLineNumber());
            assertEquals(AnnotatedMockBeanCustom.class, e.getBeanClass());
            assertEquals("boolWrapped", e.getDestinationField().getName());
        }
    }
}
