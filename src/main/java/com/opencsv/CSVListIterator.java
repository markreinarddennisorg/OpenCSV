package com.opencsv;

import java.util.*;

public class CSVListIterator implements ListIterator<String[]> {

	private final List<String[]> list;
	private int index = 0;
	private Locale errorLocale = Locale.getDefault();

	public CSVListIterator(List<String[]> list) {
		this.list = list;
	}

	@Override
	public boolean hasNext() {
		return index < list.size();
	}

	@Override
	public String[] next() {
		if (!hasNext())
			throw new NoSuchElementException();
		String[] retval = list.get(index);
		index++;
		return retval;
	}

	@Override
	public boolean hasPrevious() {
		return index > 0;
	}

	@Override
	public String[] previous() {
		if (!hasPrevious())
			throw new NoSuchElementException();
		index--;
		return list.get(index);
	}

	@Override
	public int nextIndex() {
		return index;
	}

	@Override
	public int previousIndex() {
		return index - 1;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale).getString("read.only.iterator"));
	}

	@Override
	public void set(String[] strings) {
		throw new UnsupportedOperationException(ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale).getString("read.only.iterator"));
	}

	@Override
	public void add(String[] strings) {
		throw new UnsupportedOperationException(ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale).getString("read.only.iterator"));
	}

	public void setErrorLocale(Locale errorLocale) {
		this.errorLocale = errorLocale;
	}
}
