<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<script type="text/javascript">

    function displayStationConfig(ip) {
    	window.open("http://" + ip + ":80/mstation/panel/");
    }
    
</script>

<div class="infoHeader">
    <s:property value="getText('text.mserver.title')" /> &gt; <s:property value="getText('text.unconfiguredstations.title')" />
</div>
<div class="pageBody">

    <s:if test="%{message != null}">
        <div class="<s:property value="messageType" />">
            <s:property value="message" />
        </div>
    </s:if>
    
  

    <table class="resultList">
        <thead>
            <tr class="resultHeaderRow">
                <td><s:property value="getText('text.unconfiguredstations.name')" /></td>
                <td><s:property value="getText('text.unconfiguredstations.mac')" /></td>
                <td><s:property value="getText('text.unconfiguredstations.ip')" /></td>
                <td><s:property value="getText('text.unconfiguredstations.registeredOn')" /></td>
                <td><s:property value="getText('text.unconfiguredstations.status')" /></td>
                <td></td>
            </tr>
        </thead>
        <tbody>
        <s:iterator value="unconfiguredStations" var="station">
        <s:set value="getStationStatus(#station)" var="stationStatus" />
        
            <tr>
                <s:if test="%{name != null}">
                    <td class="pageHeader"><s:property value="name" /></td>
                </s:if>
                <s:else>
                    <td class="pageHeader"><s:property value="getText('text.unconfiguredstations.namenotset')" /></td>
                </s:else>
                
                <td class="pageHeader"><s:property value="macAddress" /></td>
                
                <td class="pageHeader"><s:property value="ipAddress" /></td>
                
                <td><s:date name="registeredOn" format="%{getText('format.timestamp.java')}" /></td>
                
                <s:if test="#stationStatus.isActive()">
                    <s:set value="'stationActive'" var="statusClass" />
                </s:if>
                <s:else>
                    <s:set value="'stationInactive'" var="statusClass" />
                </s:else>
                <td class="<s:property value="#statusClass" />">
                    <s:property value="getText(#stationStatus.getKey())" />
                </td>
                
                <s:if test="#stationStatus.isActive()">
                    <td><span class="link" onclick="displayStationConfig('<s:property value="ipAddress" />');"><s:property value="getText('text.unconfiguredstations.stationconfig')" /></span></td>
                </s:if>
                <s:else>
                    <s:url var="url" action="unconfiguredStations">
                        <s:param name="action" value="'turnOn'" />
                        <s:param name="stationmac" value="macAddress" />
                    </s:url>
                    
                    <td>
                        <s:a href="%{url}">
                            <s:property value="getText('text.rooms.turnOn')" />
                        </s:a>
                    </td>
                </s:else>
            </tr>
            
        </s:iterator>
        </tbody>
    </table>
    
</div>
