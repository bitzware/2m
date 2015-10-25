<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<script type="text/javascript">
    
    function removeEntry(id) {
        $("#" + id).remove();
    }
    
    function addEntry() {
        var entryId = "ce_" + idGenerator;
        var nameId = "n_" + idGenerator;
        var descId = "d_" + idGenerator;
        var floorId = "f_" + idGenerator;

        var string =
            '<tr id="' + entryId + '" class="configEntry">' +
                '<td class="roomNameField">' +
                ' <input type="text" name="' + nameId + '"></input>' +
                '</td>' +
                '<td class="roomDescField">' +
                ' <input type="text" name="' + descId + '" />' +
                '</td>' +
                '<td class="roomFloorField">' +
                ' <input type="text" name="' + floorId + '"></input>' +
                '</td>' +
                '<td class="configRemove">' +
                ' <span class="link" onclick="removeEntry(\'' + entryId + '\');"><s:property value="getText('text.roomsconfig.remove')" /></span>' +
                '</td>' +
            '</tr>';
        
        $("#entries").append(string);
        
        idGenerator++;
    }
</script>

<div class="infoHeader">
    <s:property value="getText('text.mserver.title')" /> &gt; <s:property value="getText('text.roomsconfig.title')" />
</div>
<div class="pageBody">
    <s:if test="%{message != null}">
        <div class="<s:property value="messageType" />">
            <s:property value="message" />
        </div>
    </s:if>
    
    <s:form name="roomsConfigForm" namespace="/panel" action="roomsConfig" method="POST">
        
        <s:set var="idGenerator" value="%{0}" />
        
        <table class="roomsConfig" cellpadding="10">
            <thead>
                <tr class="configHeaderRow">
                    <td>
                        <s:property value="getText('text.roomsconfig.name')"/>
                    </td>
                    <td>
                        <s:property value="getText('text.roomsconfig.description')"/>
                    </td>
                    <td>
                        <s:property value="getText('text.roomsconfig.floor')"/>
                    </td>
                    <td>
                      Przypisanie
                    </td>
                </tr>
            </thead>
            <tbody id="entries" class="configEntries">
            <s:iterator value="roomsFormData">
        
                <s:set var="entryId" value="'ce_' + #idGenerator" />
                    
                <tr id="<s:property value="#entryId" />" class="stations">
                    <td class="roomNameField">
                        <s:hidden name="%{'i_' + #idGenerator}" value="%{id}" />
                        <s:textfield name="%{'n_' + #idGenerator}" value="%{name}" />
                    </td>
                    <td class="roomDescField">
                        <s:textfield name="%{'d_' + #idGenerator}" value="%{description}" />
                    </td>
                    <td class="roomFloorField">
                        <s:textfield name="%{'f_' + #idGenerator}" value="%{floor}" />
                    </td>
                  
                    <td>
                    <s:property value="parent" />
                    </td>
                </tr>
        
                <s:set var="idGenerator" value="#idGenerator + 1" />
            </s:iterator>
            </tbody>
        </table>
        
        <div class="configAdd">
            <span class="link" onclick="addEntry();"><s:property value="getText('text.roomsconfig.add')" /></span>
        </div>
        
        <s:hidden name="action" value="save" />
          <div class="toolBox">
            <s:a href="JavaScript:document.roomsConfigForm.submit();" cssClass="button" cssStyle="float:left;">
                <s:property value="getText('text.button.save')" />
            </s:a>
            <div class="space">
            </div>
        </div>
    </s:form>
    </div>
   
    <div class="pageHeader">
    <s:form name="roomsAssignmentForm" namespace="/panel" action="roomsConfig" method="POST">
    Przypisz 
    <s:select name="assignMe" list="roomsFormData" listKey="id" listValue="name" value="" /> 
    do
    <s:select name="toRoom" list="roomsFormData" listKey="id" listValue="name" value="" />   
      <s:hidden name="action" value="assignRooms" />
      
        <div class="toolBox">
            <s:a href="JavaScript:document.roomsAssignmentForm.submit();" cssClass="button" cssStyle="float:left;">
                <s:property value="getText('text.button.save')" />
            </s:a>
            <div class="space">
            </div>
        </div>
    </s:form>
    </div>       
   
<script type="text/javascript">
    var idGenerator = <s:property value="#idGenerator" />;
</script>
