<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>


<div class="pageBody">
<div class="serverHeader">
    <s:property value="getText('text.mserver.title')" />
</div>
    <div class="mainMenuEntry">           
            <s:url var="url" action="masterConfig" />
            <s:a href="%{url}" cssClass="mainmenubutton">
		<img src="<s:url value="/panel/img/configuration.png" />" alt="Konfiguracja" /><br/>
                <s:property value="getText('text.mainmenu.masterConfig')" />
            </s:a>
        </div>
        
            
        <div class="mainMenuEntry">
	 <s:url var="url" action="rooms" />
		<s:a href="%{url}" cssClass="mainmenubutton">
               <img src="<s:url value="/panel/img/rooms.png" />" alt="Pokoje" /><br/>
                <s:property value="getText('text.mainmenu.rooms')" />
            </s:a>
            <s:url var="url" action="unconfiguredStations" />
            <s:a href="%{url}" cssClass="mainmenubutton">
                <s:property value="getText('text.mainmenu.unconfiguredStations')" />
            </s:a>
            <s:url var="url" action="roomsConfig" />
            <s:a href="%{url}" cssClass="mainmenubutton">
                <s:property value="getText('text.masterconfig.rooms')" />
            </s:a>
            
                        	 
        </div>
            
        <div class="mainMenuEntry">
          <s:url var="url" action="displayReports" />
            <s:a href="%{url}" cssClass="mainmenubutton">
               <img src="<s:url value="/panel/img/reports.png" />" alt="<s:property value="getText('text.reports.menu.displayreports')" />" /><br/>
		<s:property value="getText('text.mainmenu.reports')" />                
            </s:a>

	
           
         <ul>
            <li><s:url var="url" action="eventsOnStationReport" />
            <s:a href="%{url}" cssClass="mainmenubutton">
                <s:property value="getText('text.reports.menu.eventsonstation')" />
            </s:a>
            </li><li>
            <s:url var="url" action="eventsInRoomReport" />
            <s:a href="%{url}" cssClass="mainmenubutton">
                <s:property value="getText('text.reports.menu.eventsinroom')" />
            </s:a>
            </li><li>
            <s:url var="url" action="eventsOnFloorReport" />
            <s:a href="%{url}" cssClass="mainmenubutton">
                <s:property value="getText('text.reports.menu.eventsonfloor')" />
            </s:a>
            </li><li>
            <s:url var="url" action="visitorsRoutesReport" />
            <s:a href="%{url}" cssClass="mainmenubutton">
                <s:property value="getText('text.reports.menu.visitorsroutes')" />
            </s:a>
            </li><li>
            <s:url var="url" action="popularStationsReport" />
            <s:a href="%{url}" cssClass="mainmenubutton">
                <s:property value="getText('text.reports.menu.popularstations')" />
            </s:a>
            </li><li>
            <s:url var="url" action="dailyVisitsReport" />
            <s:a href="%{url}" cssClass="mainmenubutton">
                <s:property value="getText('text.reports.menu.dailyvisits')" />
            </s:a>
	</ul>

        </div>
        
    </div>

</div>
