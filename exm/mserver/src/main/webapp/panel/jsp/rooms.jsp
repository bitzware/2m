<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<script type="text/javascript">

	$(function() {
		$('#duplicateRoomDialog').dialog({
	    	autoOpen: false,
    		bgiframe: true,
    		resizable: false,
    		height: 300,
    		modal: true,
    		overlay: {
		        backgroundColor: '#000',
        		opacity: 0.5
    		},
	    	buttons: {
	        	'<s:property value="getText('text.button.no')" />': function() {
	            	$(this).dialog('close');
        		},	
        		'<s:property value="getText('text.button.yes')" />': function() {
            		$("#duplicateRoomCopyingDialog").dialog("open");
		            document.duplicateRoomForm.submit();
        		}
    		}
		});
		
		$('#turnOffStationDialog').dialog({
	    	autoOpen: false,
    		bgiframe: true,
    		resizable: false,
    		height: 300,
    		modal: true,
    		overlay: {
		        backgroundColor: '#000',
        		opacity: 0.5
    		},
	    	buttons: {
	        	'<s:property value="getText('text.button.no')" />': function() {
	            	$(this).dialog('close');
        		},	
        		'<s:property value="getText('text.button.yes')" />': function() {
		            document.turnOffStationForm.submit();
        		}
    		}
		});
		
		$('#turnOffRoomDialog').dialog({
	    	autoOpen: false,
    		bgiframe: true,
    		resizable: false,
    		height: 300,
    		modal: true,
    		overlay: {
		        backgroundColor: '#000',
        		opacity: 0.5
    		},
	    	buttons: {
	        	'<s:property value="getText('text.button.no')" />': function() {
	            	$(this).dialog('close');
        		},	
        		'<s:property value="getText('text.button.yes')" />': function() {
		            document.turnOffRoomForm.submit();
        		}
    		}
		});
		
		$('#turnOffAllDialog').dialog({
	    	autoOpen: false,
    		bgiframe: true,
    		resizable: false,
    		height: 300,
    		modal: true,
    		overlay: {
		        backgroundColor: '#000',
        		opacity: 0.5
    		},
	    	buttons: {
	        	'<s:property value="getText('text.button.no')" />': function() {
	            	$(this).dialog('close');
        		},	
        		'<s:property value="getText('text.button.yes')" />': function() {
		            document.turnOffAllForm.submit();
        		}
    		}
		});
		
		$('#duplicateRoomCopyingDialog').dialog({
	    	autoOpen: false,
    		bgiframe: true,
    		resizable: false,
    		height: 300,
    		modal: true,
    		overlay: {
		        backgroundColor: '#000',
        		opacity: 0.5
    		},
	    	buttons: {
    		}
		});
		
	});

	function duplicateRoom(stationId) {
		$('#duplicateRoomStationId').attr('value', stationId);
		$('#duplicateRoomDialog').dialog('open');
	}

	function displayStationConfig(ip) {
		window.open("http://" + ip + ":80/mstation/panel/");
    }
	
	function displayStationAMT(ip) {
		var chunks = ip.split('.');
		 chunks[3] = (parseInt(chunks[3])+74).toString();

		window.open("http://" + chunks.join('.') + ":16992");
    }

    function turnOnStation(mac) {
        $('#action').attr('value', 'turnOn');
        $('#stationmac').attr('value', mac);
        document.roomsForm.submit();
    }
	function deleteStation(mac) {
        $('#action').attr('value', 'delete');
        $('#stationmac').attr('value', mac);
        document.roomsForm.submit();
    }

    function turnOffStation(ip) {
        $('#turnOffStationIpAddress').attr('value', ip);
        $('#turnOffStationDialog').dialog('open');
    }

    function turnOnRoom(roomId) {
    	$('#action').attr('value', 'turnOnRoom');
    	$('#roomId').attr('value', roomId);
    	document.roomsForm.submit();
    }

    function turnOffRoom(roomId) {
    	$('#turnOffRoomId').attr('value', roomId);
        $('#turnOffRoomDialog').dialog('open');
    }

    function turnOnAll() {
    	$('#action').attr('value', 'turnOnAll');
    	document.roomsForm.submit();
    }

    function turnOffAll() {
        $('#turnOffAllDialog').dialog('open');
    }

    function onSuccess(data, textStatus) {
 	   $('#okDialog').dialog('open');
 }
 
 function onError(XMLHttpRequest, textStatus, errorThrown) {
 	   $('#okDialog').dialog('open');    }
 
 function turnCrestron(toTurn) {
     $.ajax({
         type: 'GET',
         url: toTurn,
         dataType: 'text',
         success: onSuccess,
         error: onError
     });
 }
</script>

<div id="duplicateRoomDialog" style="display:none;" title="<s:property value="getText('text.rooms.duplicateRoom.title')" />">
	<s:form name="duplicateRoomForm" action="rooms" method="POST">
		<s:hidden name="action" value="duplicateRoom" />
		<s:hidden id="duplicateRoomStationId" name="stationId" value="" />
		
    	<p>
    		<s:property value="getText('text.rooms.duplicateRoom.text')" />
    	</p>
    </s:form>
</div>

<div id="turnOffStationDialog" style="display:none;" title="<s:property value="getText('text.rooms.turnOffStation.title')" />">
	<s:form name="turnOffStationForm" action="rooms" method="POST">
		<s:hidden name="action" value="turnOff" />
		<s:hidden id="turnOffStationIpAddress" name="stationIpAddress" value="" />
		
    	<p>
    		<s:property value="getText('text.rooms.turnOffStation.text')" />
    	</p>
    </s:form>
</div>

<div id="turnOffRoomDialog" style="display:none;" title="<s:property value="getText('text.rooms.turnOffRoom.title')" />">
	<s:form name="turnOffRoomForm" action="rooms" method="POST">
		<s:hidden name="action" value="turnOffRoom" />
		<s:hidden id="turnOffRoomId" name="roomId" value="" />
		
    	<p>
    		<s:property value="getText('text.rooms.turnOffRoom.text')" />
    	</p>
    </s:form>
</div>

<div id="turnOffAllDialog" style="display:none;" title="<s:property value="getText('text.rooms.turnOffAll.title')" />">
	<s:form name="turnOffAllForm" action="rooms" method="POST">
		<s:hidden name="action" value="turnOffAll" />
		
    	<p>
    		<s:property value="getText('text.rooms.turnOffAll.text')" />
    	</p>
    </s:form>
</div>

<div id="duplicateRoomCopyingDialog" style="display:none;" title="<s:property value="getText('text.rooms.duplicateRoom.title')" />">
    <p>
    	<s:property value="getText('text.rooms.duplicateRoom.copying')" />
    </p>
</div>
<div class="infoHeader">
    <s:property value="getText('text.mserver.title')" /> &gt; <s:property value="getText('text.rooms.title')" />
</div>
<div class="pageBody" style="background-image: url('<s:url value="/panel/img/rooms.svg" />'); background-repeat:no-repeat; background-size: 100% 100%;">

    <s:if test="%{message != null}">
        <div class="<s:property value="messageType" />">
            <s:property value="message" />
        </div>
    </s:if>

	<s:form name="roomsForm" namespace="/panel" action="rooms" method="POST">
		<s:hidden id="action" name="action" value="save" />
		<s:hidden id="stationmac" name="stationmac" value="" />		
		<s:hidden id="roomId" name="roomId" value="" />
	
    	<div class="toolBox">
        	<s:a href="JavaScript:document.roomsForm.submit();" cssClass="button" cssStyle="float:left;">
                <s:property value="getText('text.button.save')" />
            </s:a>
            <div class="space"></div>
    	</div>


    
    	<div class="workTime">
	    	<span class="openTime">
    			<s:property value="getText('text.rooms.openMuseumTime')" />
    			<s:textfield name="openMuseumTime" value="%{openMuseumTime}" />
    		</span>
    	
    		<span class="closeTime">
	    		<s:property value="getText('text.rooms.closeMuseumTime')" />
    			<s:textfield name="closeMuseumTime" value="%{closeMuseumTime}" />
    		</span>
    	</div>
    
    	<table class="resultList">
        	<thead>
	            <tr class="resultHeaderRow">
                	<td><s:property value="getText('text.rooms.name')" /></td>
                	<td><s:property value="getText('text.rooms.status')" /></td>
                	<td>
                		<span class="link" onclick="turnOnAll();">
                    		<s:property value="getText('text.rooms.turnOnAll')" />
                    	</span>
                	</td>
                	<td>
                		<span class="link" onclick="turnOffAll();">
                    		<s:property value="getText('text.rooms.turnOffAll')" />
                    	</span>
                	</td>
                	<td>
                        	<s:property value="getText('text.rooms.duplicateRoom')" />
                	</td>
                	<td>
                	</td>
                	<td>
                		<s:property value="getText('text.rooms.openTime')" />
                	</td>
                	<td>
                		<s:property value="getText('text.rooms.closeTime')" />
                	</td>
                	<td>
                	</td>
            	</tr>	
        	</thead>
        	<tbody>
        	<s:iterator value="roomsWithStations" var="room">
        
			            			<!-- tr><td colspan="5"></td></tr -->
	       		<tr class="resultSectionRow">
	            	<td colspan="2">
	            		<s:property value="name" />, <s:property value="floor" />,  <s:property value="description" />

            		</td>
            		<td>
            			<span class="link" onclick="turnOnRoom('<s:property value="id" />');">
                    		<s:property value="getText('text.rooms.turnOn')" />
                    	</span>
            		</td>
            		<td>
            			<span class="link" onclick="turnOffRoom('<s:property value="id" />');">
                    		<s:property value="getText('text.rooms.turnOff')" />
                    	</span>
            		</td>
            		<td>
            		</td>
            		<td>
            		</td>
            		<td>
            			<s:textfield name="%{'ro_' + escapeId(id)}" value="%{getRoomOpenTime(#room)}" />
            		</td>
            		<td>
            			<s:textfield name="%{'rc_' + escapeId(id)}" value="%{getRoomCloseTime(#room)}" />
            		</td>
            		<td class="formFieldError">
            			<s:property value="%{getErrorMessage(id)}" />
            		</td>
        		</tr>

            	<s:iterator value="getStationIterator(#room)" var="station">
            	<s:set value="getStationStatus(#station)" var="stationStatus" />
            
            	<tr class="stations">
                	<td><s:property value="name" />, <s:property value="ipAddress" />  </td>
                
                	<s:if test="#stationStatus.isActive()">
                    	<s:set value="'stationActive'" var="statusClass" />
                	</s:if>
                	<s:else>
                    	<s:set value="'stationInactive'" var="statusClass" />
                	</s:else>
                	<td class="<s:property value="#statusClass" />">
                    	<s:property value="getText(#stationStatus.getKey())" />
                	</td>
                
                	<td class="roomsTurnOnCell">
                		<s:if test="!#stationStatus.isActive()">
                    		<span class="link" onclick="turnOnStation('<s:property value="macAddress" />');">
                    			<s:property value="getText('text.rooms.turnOn')" />
                    		</span>
                		</s:if>
                	</td>
                
                	<td class="roomsTurnOffCell">
                		<s:if test="#stationStatus.isActive()">
                    		<span class="link" onclick="turnOffStation('<s:property value="ipAddress" />');">
                    			<s:property value="getText('text.rooms.turnOff')" />
                    		</span>
                		</s:if>
                	</td>
                	<td>
	                	<s:a href="%{'JavaScript:duplicateRoom(' + id + ')'}">[k]

                    	</s:a>
                	</td>
                	<td>
                		<span class="link" onclick="displayStationConfig('<s:property value="ipAddress" />');"><s:property value="getText('text.rooms.stationconfig')" /></span>
                		<span class="link" onclick="displayStationAMT('<s:property value="ipAddress" />');">AMT</span>
                		
                	</td>
                	<td>
                	<span class="link" onclick="deleteStation('<s:property value="macAddress" />');">Usuń </span>
                	  
                	</td>
                	<td>
                	</td>
            	</tr>
            
        	    </s:iterator>
        	      <s:iterator value="getDeviceIterator(#room)" var="device">
        	    <tr class="stations">
                	<td>
	            		<s:property value="location" />
						</td>
            		<s:if test="state">
                    
				 <td class="stationActive" />
                    	<s:property value="getText(\"text.station.status.playing\")" />
                	</td>
                	</s:if>
                	<s:else>
						<td class="stationInactive" />
                    	<s:property value="getText(\"text.station.status.inactive\")" />
                	</td>
                    	<s:set value="'stationInactive'" var="statusClass" />
                	</s:else>
                	
            		
            		</td>
            		<td class="roomsTurnOnCell">
                		<s:if test="!state">
                    		<span class="link" onclick="turnCrestron('/mxcc/on/<s:property value="location" />');">
                    			<s:property value="getText('text.rooms.turnOn')" />
                    		</span>
                		</s:if>
                	</td>
                
                	<td class="roomsTurnOffCell">
                		<s:if test="state">
                    		<span class="link" onclick="turnCrestron('/mxcc/off/<s:property value="name" />');">
                    			<s:property value="getText('text.rooms.turnOff')" />
                    		</span>
                		</s:if>
                	</td>
            		</tr>
        	    </s:iterator>
 				
 			
 			 <s:if test="%{getSubRoomIterator(#room) != null}">
 			 <!-- this is  2nd level -->		
 				<tr class="stations">
				
 				<td colspan="6" align="right">&nbsp;&nbsp;&nbsp;
 				<table>
 				<s:iterator value="getSubRoomIterator(#room)" var="subroom">
        
			            			<!-- tr><td colspan="5"></td></tr -->
	       		<tr class="resultSectionRow">
	            	<td colspan="2">
	            		<s:property value="name" />, <s:property value="floor" />,  <s:property value="description" />

            		</td>
            		<td>
            			<span class="link" onclick="turnOnRoom('<s:property value="id" />');">
                    		<s:property value="getText('text.rooms.turnOn')" />
                    	</span>
            		</td>
            		<td>
            			<span class="link" onclick="turnOffRoom('<s:property value="id" />');">
                    		<s:property value="getText('text.rooms.turnOff')" />
                    	</span>
            		</td>
            		<td>
            		</td>
            		<td>
            		</td>
            		<td>
            			<s:textfield name="%{'ro_' + escapeId(id)}" value="%{getRoomOpenTime(#subroom)}" />
            		</td>
            		<td>
            			<s:textfield name="%{'rc_' + escapeId(id)}" value="%{getRoomCloseTime(#subroom)}" />
            		</td>
            		<td class="formFieldError">
            			<s:property value="%{getErrorMessage(id)}" />
            		</td>
        		</tr>

            	<s:iterator value="getStationIterator(#subroom)" var="station">
            	<s:set value="getStationStatus(#station)" var="stationStatus" />
            
            	<tr class="stations">
                	<td><s:property value="name" />, <s:property value="ipAddress" />  </td>
                
                	<s:if test="#stationStatus.isActive()">
                    	<s:set value="'stationActive'" var="statusClass" />
                	</s:if>
                	<s:else>
                    	<s:set value="'stationInactive'" var="statusClass" />
                	</s:else>
                	<td class="<s:property value="#statusClass" />">
                    	<s:property value="getText(#stationStatus.getKey())" />
                	</td>
                
                	<td class="roomsTurnOnCell">
                		<s:if test="!#stationStatus.isActive()">
                    		<span class="link" onclick="turnOnStation('<s:property value="macAddress" />');">
                    			<s:property value="getText('text.rooms.turnOn')" />
                    		</span>
                		</s:if>
                	</td>
                
                	<td class="roomsTurnOffCell">
                		<s:if test="#stationStatus.isActive()">
                    		<span class="link" onclick="turnOffStation('<s:property value="ipAddress" />');">
                    			<s:property value="getText('text.rooms.turnOff')" />
                    		</span>
                		</s:if>
                	</td>
                	<td>
	                	<s:a href="%{'JavaScript:duplicateRoom(' + id + ')'}">[k]

                    	</s:a>
                	</td>
                	<td>
                		<span class="link" onclick="displayStationConfig('<s:property value="ipAddress" />');"><s:property value="getText('text.rooms.stationconfig')" /></span>
                	    <span class="link" onclick="displayStationAMT('<s:property value="ipAddress" />');">AMT</span>
                		
                	</td>
                	<td>
                	<span class="link" onclick="deleteStation('<s:property value="macAddress" />');">Usuń </span>
                	  
                	</td>
                	<td>
                	</td>
            	</tr>
            
        	    </s:iterator>
        	      <s:iterator value="getDeviceIterator(#subroom)" var="device">
        	    <tr class="stations">
                	<td>
	            		<s:property value="location" />
						</td>
            		<s:if test="state">
                    
				 <td class="stationActive" />
                    	<s:property value="getText(\"text.station.status.playing\")" />
                	</td>
                	</s:if>
                	<s:else>
						<td class="stationInactive" />
                    	<s:property value="getText(\"text.station.status.inactive\")" />
                	</td>
                    	<s:set value="'stationInactive'" var="statusClass" />
                	</s:else>
                	
            		
            		</td>
            		<td class="roomsTurnOnCell">
                		<s:if test="!state">
                    		<span class="link" onclick="turnCrestron('/mxcc/on/<s:property value="name" />');">
                    			<s:property value="getText('text.rooms.turnOn')" />
                    		</span>
                		</s:if>
                	</td>
                
                	<td class="roomsTurnOffCell">
                		<s:if test="state">
                    		<span class="link" onclick="turnCrestron('/mxcc/off/<s:property value="name" />');">
                    			<s:property value="getText('text.rooms.turnOff')" />
                    		</span>
                		</s:if>
                	</td>
            		</tr>
        	    </s:iterator>
            
        	</s:iterator>
 			
 				</table>
 				</td> 
 				</tr>			
 							<!-- end of 2nd level -->	
 				</s:if>		
 						 <tr><td colspan=7>&nbsp;</td></tr>          
        	</s:iterator>
        	</tbody>
    	</table>
    
    </s:form>
    
</div>
