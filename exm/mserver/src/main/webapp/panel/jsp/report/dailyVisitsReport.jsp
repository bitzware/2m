<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<tr>
    <td><s:property value="getText('text.report.dailyvisits.from')" /></td>
    <td><s:textfield id="from" name="from" value="%{from}" /></td>
</tr>
<tr>
    <td><s:property value="getText('text.report.dailyvisits.to')" /></td>
    <td><s:textfield id="to" name="to" value="%{to}" /></td>
</tr>
<tr>
    <td><s:property value="getText('text.report.dailyvisits.chart')" /></td>
    <td><s:checkbox name="chart" value="%{chart}" /></td>
</tr>

<script type="text/javascript">
    var dateFields = ["from", "to"];
</script>
