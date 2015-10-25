<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<script type="text/javascript">

    function displayStationConfig(ip) {
    	window.open("http://" + ip + ":80/mstation/panel/");
    }
    
    function onSuccess(data, textStatus) {
 	   $('#okDialog').dialog('open');
 }
 
 function onError(XMLHttpRequest, textStatus, errorThrown) {
 	   $('#okDialog').dialog('open');    }
 
 function turn(toTurn) {
     $.ajax({
         type: 'GET',
         url: toTurn,
         dataType: 'text',
         success: onSuccess,
         error: onError
     });
 }
</script>

<div class="pageHeader">

    <s:if test="%{message != null}">
        <div class="<s:property value="messageType" />">
            <s:property value="message" />
        </div>
    </s:if>


		Ekspozycja - konfiguracja urządzeń Crestron<br/>
		<br/>
		Przypisanie:
			 <s:form name="crestronDevicesForm" namespace="/panel" action="crestronDevices" method="POST">
        <select name="device"> 
			<s:iterator value="chamberz" var="chamber">
		<s:set value="name" var="chamberName" />
		<s:iterator value="devices" var="device"> 
		<option> 
		<s:property value="chamberName" />|<s:property value="name" />
		</option>
		</s:iterator>
		 </s:iterator>
		 </select>
		 <select name="room">  <s:iterator value="rooms" var="room"><option value='<s:property value="id" />'> <s:property value="name" /></option> </s:iterator></select> <input type="submit" value="Przypisz">
		  <s:hidden name="action" value="save" />
		  </s:form>
 <br/>
		
</div>
