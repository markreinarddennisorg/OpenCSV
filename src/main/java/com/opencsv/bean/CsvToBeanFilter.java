/*
 * Copyright 2013 Mark Rogers.
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

/**
 * Filters allow lines of input to be ignored before a bean is created.
 * <p>Using a filter means you are looking at the data from the input after it
 * has been parsed, but before a bean has been created and populated.</p>
 * <p>Filters <em>must</em> be thread-safe.</p>
 * <p>Here's an example showing how to use {@link CsvToBean} with a column
 * name mapping and line filtering.  It assumes that there is a class named
 * <code>Feature</code> defined with setters <code>setName(String)</code> and
 * <code>setState(String)</code>.  The FEATURE_NAME and STATE columns in the
 * CSV file will be used.  Any additional columns will be ignored.  The filter
 * will eliminate any lines where the STATE value is "production".</p>
 *
 * <pre>
 * {@code
 * private class StateFilter implements CsvToBeanFilter {
 *
 * 	private final MappingStrategy strategy;
 *
 * 	public StateFilter(MappingStrategy strategy) {
 * 		this.strategy = strategy;
 * 	}
 *
 * 	public boolean allowLine(String[] line) {
 * 		String value = line[1];
 * 		boolean result = !"production".equals(value);
 * 		return result;
 * 	}
 *
 * }
 *
 * public List<Feature> parseCsv(InputStreamReader streamReader) {
 * 	HeaderColumnNameTranslateMappingStrategy<Feature> strategy = new HeaderColumnNameTranslateMappingStrategy();
 * 	Map<String, String> columnMap = new HashMap();
 * 	columnMap.put("FEATURE_NAME", "name");
 * 	columnMap.put("STATE", "state");
 * 	strategy.setColumnMapping(columnMap);
 * 	strategy.setType(Feature.class);
 * 	CSVReader reader = new CSVReader(streamReader);
 * 	CsvToBeanFilter filter = new StateFilter(strategy);
 * 	return new CsvToBean().parse(strategy, reader, filter);
 * }
 * }
 * </pre>
 *
 * @see BeanVerifier
 * @deprecated Please use {@link BeanVerifier} instead.
 */
@Deprecated
public interface CsvToBeanFilter {

   /**
    * Determines if a line from the CSV file will be included in the
    * output of {@link CsvToBean}.
    *
    * @param line A line of data from the CSV file
    * @return True if the line is to be included in the output. Otherwise,
    * false.
    */
   boolean allowLine(String[] line);

}
