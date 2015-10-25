<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<tr>
    <td><s:property value="getText('text.report.visitorsroutes.date')" /></td>
    <td><s:textfield id="date" name="date" value="%{date}" /></td>
</tr>
<tr>
	<td><s:property value="getText('text.report.visitorsroutes.eventtype')" /></td>
	<td><s:select name="eventType" list="eventTypes" listKey="key" listValue="value" value="%{eventType}" /></td>
</tr>

<script type="text/javascript">
    var dateFields = ["date"];
</script>
