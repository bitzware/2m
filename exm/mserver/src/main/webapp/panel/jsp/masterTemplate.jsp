<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<html>
    <head>
        <title><s:property value="getText('text.app.title')" /></title>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" >
        <link rel="stylesheet" type="text/css" href="<s:url value="/panel/css/smoothness/jquery-ui-1.7.2.custom.css" />" />
        <link rel="stylesheet" type="text/css" href="<s:url value="/panel/css/normal.css" />" />
        <script type="text/javascript" src="<s:url value="/panel/js/jquery-1.3.2.js" />"></script>
        <script type="text/javascript" src="<s:url value="/panel/js/jquery-ui-1.7.2.custom.min.js" />"></script>
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

        <!-- THIS SHOULD COME LAST -->
        <!--[if lt IE 9]>
        <script type="text/javascript" src="<s:url value='/panel/resources/js/ie.js'/>"></script>
        <![endif]-->



    </head>
    
    <body>
        

        <tiles:insertAttribute name="body" />

    
    </body>
</html>
