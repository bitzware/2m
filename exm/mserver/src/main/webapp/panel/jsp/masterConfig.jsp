<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>


<div class="infoHeader">
 <s:property value="getText('text.mserver.title')" /> &gt; <s:property value="getText('text.masterconfig.title')" />
</div>

<div class="pageBody" style="background-image: url('<s:url value="/panel/img/configuration.svg" />'); background-repeat:no-repeat;" >
    <s:if test="%{message != null}">
        <div class="<s:property value="messageType" />">
            <s:property value="message" />
        </div>
    </s:if>

    <s:form name="masterConfigForm" namespace="/panel" action="masterConfig" method="POST">
        
    
        <table class="formTable">
            <tbody>
                <tr>
                    <td><s:property value="getText('text.masterconfig.name')" /></td>
                    <td><s:textfield name="name" value="%{name}" /></td>
                    <td></td>
                </tr>
                <tr>
                    <td><s:property value="getText('text.masterconfig.description')" /></td>
                    <td><s:textfield name="description" value="%{description}" /></td>
                    <td></td>
                </tr>
                <tr>
                    <td><s:property value="getText('text.masterconfig.rfidtimeout')" /></td>
                    <td><s:textfield name="rfidTimeout" value="%{rfidTimeout}" /></td>
                    <td></td>
                </tr>  
                <tr>
                    <td><s:property value="getText('text.masterconfig.rfidtimeofentry')" /></td>
                    <td><s:textfield name="rfidTimeOfEntry" value="%{rfidTimeOfEntry}" /></td>
                    <td></td>
                </tr>  
            </tbody>
        </table>
        
        <s:hidden name="action" value="save" />
        <div class="toolBox">
            <s:url var="url" action="mainMenu" />
            <s:a href="%{url}" cssClass="button" cssStyle="float:left;">
                <s:property value="getText('text.button.back')" />
            </s:a>
	    &nbsp;
            <s:a href="JavaScript:document.masterConfigForm.submit();" cssClass="button" cssStyle="float:left;">
                <s:property value="getText('text.button.save')" />
            </s:a>
            <div class="space"></div>
        </div>

   
      
    
    </s:form>
</div>
