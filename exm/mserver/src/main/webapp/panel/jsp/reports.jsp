<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<div class="serverHeader">
    <s:property value="getText('text.reports.title')" />
</div>
<div class="pageBody">
 
    
    <div class="menu">
            
        <div class="menuEntry">
            <s:url var="url" action="displayReports" />
            <s:a href="%{url}" cssClass="button">
                <s:property value="getText('text.reports.menu.displayreports')" />
            </s:a>
        </div>
        
        <div class="space"></div>
            
        <div class="menuEntry">
            <s:url var="url" action="eventsOnStationReport" />
            <s:a href="%{url}" cssClass="button">
                <s:property value="getText('text.reports.menu.eventsonstation')" />
            </s:a>
        </div>
            
        <div class="menuEntry">
            <s:url var="url" action="eventsInRoomReport" />
            <s:a href="%{url}" cssClass="button">
                <s:property value="getText('text.reports.menu.eventsinroom')" />
            </s:a>
        </div>
            
        <div class="menuEntry">
            <s:url var="url" action="eventsOnFloorReport" />
            <s:a href="%{url}" cssClass="button">
                <s:property value="getText('text.reports.menu.eventsonfloor')" />
            </s:a>
        </div>
            
        <div class="menuEntry">
            <s:url var="url" action="visitorsRoutesReport" />
            <s:a href="%{url}" cssClass="button">
                <s:property value="getText('text.reports.menu.visitorsroutes')" />
            </s:a>
        </div>
            
        <div class="menuEntry">
            <s:url var="url" action="popularStationsReport" />
            <s:a href="%{url}" cssClass="button">
                <s:property value="getText('text.reports.menu.popularstations')" />
            </s:a>
        </div>
            
        <div class="menuEntry">
            <s:url var="url" action="dailyVisitsReport" />
            <s:a href="%{url}" cssClass="button">
                <s:property value="getText('text.reports.menu.dailyvisits')" />
            </s:a>
        </div>
        
    </div>

</div>
