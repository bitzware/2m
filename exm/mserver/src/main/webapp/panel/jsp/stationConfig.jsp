<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<div class="container_8 clearfix">                
                <section class="main-section grid_8">
                    <!-- Styles Section -->
                    <div class="main-content">
                        <header><h2>Konfiguracja stanowiska</h2></header>
                        <section class="container_6 clearfix">
                            <div class="grid_6">

<div class="pageBody">
    <s:if test="%{message != null}">
        <div class="message success closeable">
<!-- s:property value="messageType" />"-->
            <s:property value="message" />
        </div>
    </s:if>

    <s:form name="stationConfigForm" namespace="/panel" action="stationConfig" method="POST">
        
            
        <table class="formTable">
            <tbody>
                <tr>
                    <td><s:property value="getText('text.stationconfig.name')" /></td>
                    <td><s:textfield name="name" value="%{name}" /></td>
                </tr>
                <tr>
                    <td><s:property value="getText('text.stationconfig.description')" /></td>
                    <td><s:textfield name="description" value="%{description}" /></td>
                </tr>
                <tr>
                    <td><s:property value="getText('text.stationconfig.room')" /></td>
                    <s:if test="rooms != null">
                        <td><s:select name="room" list="rooms" listKey="id" listValue="name" value="room" /></td>
                    </s:if>
                    <s:else>
                        <td><select name="room" disabled="true"></select>
   <div class="message warning closeable">
<s:property value="getText('text.stationconfig.cannotgetrooms')"/>
</div></td> 
                    </s:else>
                </tr>
            </tbody>
        </table>
        
        <div class="toolBox">
            <s:a href="JavaScript:document.stationConfigForm.submit();" cssClass="button button-gray" cssStyle="float:left;">
                <s:property value="getText('text.button.save')" />
            </s:a>
            
            <div class="space"></div>
        </div>
        <s:hidden name="action" value="save" />
    
    </s:form>
</div>
</section>
 	    	   </div>
		</section>
	     </div>
