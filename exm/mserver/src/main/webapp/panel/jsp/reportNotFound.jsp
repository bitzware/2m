<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<div class="pageHeader">
    <s:property value="getText('text.result.title')" />
</div>
<div class="pageBody">
    <div class="<s:property value="messageType" />">
        <s:property value="message" />
    </div>
    <s:url var="url" action="displayReports" />
    <s:a href="%{url}" cssClass="button">
        <s:property value="getText('text.button.back')" />
    </s:a>
</div>
