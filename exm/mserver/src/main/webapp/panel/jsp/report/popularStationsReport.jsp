<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<tr>
    <td><s:property value="getText('text.report.popularstations.from')" /></td>
    <td><s:textfield id="from" name="from" value="%{from}" /></td>
</tr>
<tr>
    <td><s:property value="getText('text.report.popularstations.to')" /></td>
    <td><s:textfield id="to" name="to" value="%{to}" /></td>
</tr>
<tr>
	<td><s:property value="getText('text.report.popularstations.eventtype')" /></td>
	<td><s:select name="eventType" list="eventTypes" listKey="key" listValue="value" value="%{eventType}" /></td>
</tr>

<script type="text/javascript">
    var dateFields = ["from", "to"];
</script>
