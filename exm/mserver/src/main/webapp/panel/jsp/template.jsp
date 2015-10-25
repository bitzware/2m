<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<html>
    <head>
        <title><s:property value="getText('text.app.title')" /></title>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" >
        <!--link rel="stylesheet" type="text/css" href="<s:url value="/panel/css/smoothness/jquery-ui-1.7.2.custom.css" />" /-->
        <link rel="stylesheet" type="text/css" href="<s:url value="/panel/css/normal.css" />" />

        <!--<script type="text/javascript" src="<s:url value="/panel/js/ui.datepicker.js" />"></script>-->
        <!--<script type="text/javascript" src="<s:url value="/panel/js/firebug-lite-compressed.js" />"></script>-->


	<link rel="stylesheet" media="screen" href="<s:url value='/panel/resources/css/reset.css'/>"/>
	<link rel="stylesheet" media="screen" href="<s:url value='/panel/resources/css/grid.css'/>"/>
	<link rel="stylesheet" media="screen" href="<s:url value='/panel/resources/css/style.css'/>"/>
	<link rel="stylesheet" media="screen" href="<s:url value='/panel/resources/css/messages.css'/>"/>
	<link rel="stylesheet" media="screen" href="<s:url value='/panel/resources/css/forms.css'/>"/>
	<link rel="stylesheet" media="screen" href="<s:url value='/panel/resources/css/tables.css'/>" />


	<!--[if lt IE 8]>
	<link rel="stylesheet" media="screen" href="<s:url value='/panel/resources/js/ie.css'/>" />
	<![endif]-->

	<!--[if lt IE 9]>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/html5.js'/>"></script>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/PIE.js'/>"></script>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/IE9.js'/>"></script>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/canvas.js'/>"></script>
	<![endif]-->

	<!-- jquerytools -->
	    
	<script type="text/javascript" src="<s:url value='/panel/resources/js/jquery.tools.min.js'/>"></script>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/jquery.cookie.js'/>"></script>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/jquery.ui.min.js'/>"></script>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/jquery.tables.js'/>"></script>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/jquery.flot.js'/>"></script>
   
	<script type="text/javascript" src="<s:url value='/panel/resources/js/global.js'/>"></script>

	<!-- THIS SHOULD COME LAST -->
	<!--[if lt IE 9]>
	<script type="text/javascript" src="<s:url value='/panel/resources/js/ie.js'/>"></script>
	<![endif]-->

    </head>
    
    <body>
  
	<div>
        <tiles:insertAttribute name="menu" />
	</div>
      <div class="header">
            <tiles:insertAttribute name="header" />
        </div>
  <section>
  <tiles:insertAttribute name="body" />         
</section>
	<footer>
        <div id="footer-inner" class="container_8 clearfix">
            <div class="grid_8">
                &copy; Bitzware 2011
            </div>

        </div>
    </footer>
    
    <div class="widget modal" id="restartDialog">
        <header><h2><s:property value="getText('text.mainmenu.restart.dialog.title')" /></h2></header>
        <section>
            <p>
                <s:property value="getText('text.mainmenu.restart.dialog.text')" />?
            </p>
            <p>
                <button class="button button-gray close"><s:property value="getText('text.mainmenu.restart.dialog.yes')" /></button>
                <button class="button button-gray close"><s:property value="getText('text.mainmenu.restart.dialog.no')" /></button>
            </p>
        </section>
    </div>

    <div class="widget modal" id="shutdownDialog">
        <header><h2><s:property value="getText('text.mainmenu.shutdown.dialog.title')" /></h2></header>
        <section>
            <p>
                <s:property value="getText('text.mainmenu.shutdown.dialog.text')" />
            </p>
            <p>
                <button class="button button-gray close"><s:property value="getText('text.mainmenu.restart.dialog.yes')" /></button>
                <button class="button button-gray close"><s:property value="getText('text.mainmenu.restart.dialog.no')" /></button>
            </p>
        </section>
    </div>



<script>
$(function () {
    /**
     * Modal Dialog Boxes Setup
     */

    var triggers = $(".modalInput").overlay({
        // some mask tweaks suitable for modal dialogs
        mask: {
            color: '#000',
            loadSpeed: 200,
            opacity: 0.5
        },
        closeOnClick: false
    });


    var buttons1 = $("#shutdownDialog button").click(function(e) {
	
        // get user input
        var yes = buttons1.index(this) === 0;

        if (yes) {
	document.shutdownForm.submit();
            // do the processing here
        }
    });

    var buttons2 = $("#restartDialog button").click(function(e) {
	
        // get user input
        var yes = buttons2.index(this) === 0;

        if (yes) {
	document.restartForm.submit();
            // do the processing here
        }
    });

});
</script>

        
   
    </body>
</html>
