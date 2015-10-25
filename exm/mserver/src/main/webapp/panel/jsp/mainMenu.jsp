<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>


<div class="serverHeader">
    <s:property value="getText('text.mainmenu.title')" />
</div>

<div id="wrapper">
 <div class="clear"></div>

        <header style="opacity: 0.4;">

<div class="clearfix">
                <nav>
                    <ul class="clearfix">
                        <li><a href="#" class="arrow-down"><!--s:property value="getText('text.mainmenu.header.configuration')" /-->Główne</a>
                            <ul>
                                <li><s:url var="url" action="stationConfig">
		                    </s:url>
                		    <s:a href="%{url}">
					Konfiguracja
		                        <!--s:property value="getText('text.mainmenu.stationConfig')" /-->
	                    </s:a></li>
                                <li><a rel="#shutdownDialog"  class="modalInput">
				        <s:property value="getText('text.mainmenu.shutdownSystem')" />
				    </a>
				    <s:form name="shutdownForm" action="closeSystem" method="GET" cssClass="formWithoutBorder">
				        <s:hidden name="action" value="%{'shutdown'}" />
		                    </s:form></li>
                                <li>  <a rel="#restartDialog"  class="modalInput">
					<s:property value="getText('text.mainmenu.restartSystem')" /></a>
				    <s:form name="restartForm" action="closeSystem" method="GET" cssClass="formWithoutBorder">
				        <s:hidden name="action" value="%{'restart'}" />
				    </s:form></li>                                
	                            </ul>
	                        </li>
 			<li><a href="#" class="arrow-down"><s:property value="getText('text.mainmenu.header.devices')" /></a>
                            <ul>

                                <li> <s:url var="url" action="devicesManage">
				        <s:param name="action" value="%{'stopall'}" />
				    </s:url>
				    <s:a href="%{url}">
				        <s:property value="getText('text.mainmenu.stopAllDevices')" />
		                    </s:a></li>
                                <li>  <s:url var="url" action="devicesManage">
				        <s:param name="action" value="%{'startall'}" />
				    </s:url>
				    <s:a href="%{url}">
				        <s:property value="getText('text.mainmenu.startAllDevices')" />
				    </s:a></li>
                                <li>  <s:url var="url" action="devicesConfig" />
		                    <s:a href="%{url}">
                		        <s:property value="getText('text.mainmenu.devicesConfig')" />
		                    </s:a></li>
				<li>  <s:url var="url" action="interactionsConfig" />
		                    <s:a href="%{url}">
                		        <s:property value="getText('text.mainmenu.interactionsConfig')" />
		                    </s:a></li>
                                <li><s:url var="url" action="actionList" />
            			<s:a href="%{url}" >
                		<s:property value="getText('text.interactionsconfig.actionlist')" />
		            		</s:a></li>
                            </ul>
                        </li>
			 <li><a href="#" class="arrow-down"> <s:property value="getText('text.mainmenu.header.presentation')" /></a>
						    <ul>
						        <li><s:url var="url" action="managePresentationFiles" />
							    <s:a href="%{url}">
								<s:property value="getText('text.mainmenu.managePresentationFiles')" />
							    </s:a></li>
							    <li><s:url var="url" action="presentation">
							<s:param name="action" value="%{'start'}" />
							    </s:url>
						    <s:form name="launchPresentationForm" action="presentation">
						        <s:a href="JavaScript:document.launchPresentationForm.submit();" >
					            <s:property value="getText('text.mainmenu.launchPresentation')" />
						        </s:a>
					<div style="display:none;">
					        <s:select name="player" list="players" listKey="id" listValue="name" value="defaultPlayerId" />
						        <s:select name="soundcard" list="soundcards" listKey="currentId" listValue="name" />	
					</div>
							<s:hidden name="action" value="%{'start'}" />
						    </s:form></li>
						        <li><s:url var="url" action="presentation">
							<s:param name="action" value="%{'stop'}" />
						    </s:url>
						    <s:a href="%{url}" >
							<s:property value="getText('text.mainmenu.stopPresentation')" />
						    </s:a></li>
						    <li>  <s:if test="isPresentationRunning()">
								<s:property value="getText('text.mainmenu.presentation.on')" />
							</s:if>
							<s:else>
								<s:property value="getText('text.mainmenu.presentation.off')" />
							</s:else>
							</li>
						    </ul>
						</li>
					<li><a href="#" class="arrow-down"><s:property value="getText('text.mainmenu.header.status')" /></a>
						    <ul>

						        <li><s:url var="url" action="taskList" />
						    <s:a href="%{url}">
							<s:property value="getText('text.mainmenu.getTasks')" />
						    </s:a></li>
						        <li>  <s:url var="url" action="driverList" />
						    <s:a href="%{url}">
							<s:property value="getText('text.mainmenu.getDrivers')" />
						    </s:a></li>
						        <li> <s:url var="url" action="systemInfo" />
						    <s:a href="%{url}">
							<s:property value="getText('text.mainmenu.getSystemInfo')" />
						    </s:a></li>
						        <li>    <s:url var="url" action="ipConfig" />
							    <s:a href="%{url}">
							<s:property value="getText('text.mainmenu.getIpConfig')" />
						    </s:a></li>
							<li><a href="#"><s:url var="url" action="macList" />
						    <s:a href="%{url}">
							<s:property value="getText('text.mainmenu.getMacAddresses')" />
						    </s:a></li>
						    </ul>
						</li>
   				<li class="fr"><a href="#" class="arrow-down">Administrator</a>
                            <ul>
                                <li><a href="index.jsp">Wyloguj</a></li>
                            </ul>
			</ul>

		   </nav>
	</div>
</header>
</div>


