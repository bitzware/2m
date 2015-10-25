package com.bitzware.exm.util;

import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.commons.lang.ArrayUtils;

import com.bitzware.exm.model.report.NamedColumnsResult;


/**
 * This class wraps Hibernate query result for JasperReports.
 * Some Hibernate queries return a list of object arrays. These results cannot be used
 * with the standard JRBeanCollectionDataSource. 
 * 
 * @author finagle
 */
public class NamedColumnsDataSource implements JRDataSource {

	private final String[] columnNames;
	
	private final Iterator<?> iterator;
	
	private Object[] currentValue;
	
	public NamedColumnsDataSource(final NamedColumnsResult result) {
		this.columnNames = result.getColumnNames();
		this.iterator = result.getData().iterator();
	}
	
	protected NamedColumnsDataSource(final NamedColumnsResult result, final Iterator<?> iterator) {
		this.columnNames = result.getColumnNames();
		this.iterator = iterator;
	}
	
	public NamedColumnsDataSource(final List<?> results, final String[] columnNames) {
		this.columnNames = columnNames;
		this.iterator = results.iterator();
	}
	
	public Object getFieldValue(final JRField field) throws JRException {
		int valueIndex = ArrayUtils.indexOf(columnNames, field.getName());
		
		if (valueIndex >= 0) {
			return currentValue[valueIndex];
		} else {
			return null;
		}
	}

	@Override
	public boolean next() throws JRException {
		if (iterator.hasNext()) {
			currentValue = (Object[]) iterator.next();
			return true;
		} else {
			return false;
		}
	}

}
