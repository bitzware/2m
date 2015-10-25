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


<div class="container" >

    <s:if test="%{message != null}">
        <div class="<s:property value="messageType" />">
            <s:property value="message" />
        </div>
    </s:if>

	<s:form name="roomsForm" namespace="/panel" action="mrooms" method="POST">
		<s:hidden id="action" name="action" value="save" />
		<s:hidden id="stationmac" name="stationmac" value="" />		
		<s:hidden id="roomId" name="roomId" value="" />
    
    	<div class="second">
                	obiekt<br/>
                	
                		<span class="link" onclick="turnOnAll();">
                    		<s:property value="getText('text.rooms.turnOnAll')" />
                    	</span>
                	
                		<span class="link" onclick="turnOffAll();">
                    		<s:property value="getText('text.rooms.turnOffAll')" />
                    	</span>
        </div>
        	<s:iterator value="roomsWithStations" var="room">
	       		<div class="third">
	            		<s:property value="name" />, <s:property value="floor" />,  <s:property value="description" /><br/>

            			<span class="link" onclick="turnOnRoom('<s:property value="id" />');">
                    		<s:property value="getText('text.rooms.turnOn')" />
                    	</span>
            		
            			<span class="link" onclick="turnOffRoom('<s:property value="id" />');">
                    		<s:property value="getText('text.rooms.turnOff')" />
                    	</span>
            		
            			<s:property value="%{getErrorMessage(id)}" />
            	</div>

 			 <s:if test="%{getSubRoomIterator(#room) != null}">
 			 <!-- this is  2nd level -->		
 				
 				<s:iterator value="getSubRoomIterator(#room)" var="subroom">
					<div class="fourth">
			            			
	       		
	            		<s:property value="name" />, <s:property value="floor" />,  <s:property value="description" /><br/>

            		
            			<span class="link" onclick="turnOnRoom('<s:property value="id" />');">
                    		<s:property value="getText('text.rooms.turnOn')" />
                    	</span>
            		
            			<span class="link" onclick="turnOffRoom('<s:property value="id" />');">
                    		<s:property value="getText('text.rooms.turnOff')" />
                    	</span>
            		
            			<s:property value="%{getErrorMessage(id)}" />
            	</div>
    
       	</s:iterator>	
 						
 			
 				</s:if>		
 					   
        	</s:iterator>
        	
    </s:form>
</div>
