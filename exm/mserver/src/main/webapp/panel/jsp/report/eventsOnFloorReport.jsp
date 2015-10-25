<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<script type="text/javascript">
	function onUpdateFrom() {
		var fromDate = $('#from').datepicker('getDate');
		if (fromDate != null) {
			var toDate = $('#to').datepicker('getDate');
			if (toDate == null || toDate < fromDate) {
				$('#to').datepicker('setDate', fromDate);
			}
		} 
	}
	
	function onUpdateTo() {
		var toDate = $('#to').datepicker('getDate');
		if (toDate != null) {
			var fromDate = $('#from').datepicker('getDate');
			if (fromDate == null || fromDate > toDate) {
				$('#from').datepicker('setDate', toDate);
			}
		} 
	}

	function onUpdateAll() {
		if ($('#all').attr('checked')) {
			$('#floor').attr('disabled', true);
		} else {
			$('#floor').attr('disabled', false);
		}
	}
</script>

<tr>
    <td><s:property value="getText('text.report.eventsonfloor.floor')" /></td>
    <td><s:select id="floor" name="floor" list="floors" /></td>
</tr>
<tr>
    <td><s:property value="getText('text.report.eventsonfloor.allFloors')" /></td>
    <td><s:checkbox id="all" name="all" value="%{all}" /></td>
</tr>
<tr>
    <td><s:property value="getText('text.report.eventsonfloor.from')" /></td>
    <td><s:textfield id="from" name="from" value="%{from}" onchange="onUpdateFrom()" /></td>
</tr>
<tr>
    <td><s:property value="getText('text.report.eventsonfloor.to')" /></td>
    <td><s:textfield id="to" name="to" value="%{to}" onchange="onUpdateTo()" /></td>
</tr>
<tr>
	<td><s:property value="getText('text.report.eventsonfloor.eventtype')" /></td>
	<td><s:select name="eventType" list="eventTypes" listKey="key" listValue="value" value="%{eventType}" /></td>
</tr>

<script type="text/javascript">
	var dateFields = ["from", "to"];
</script>
