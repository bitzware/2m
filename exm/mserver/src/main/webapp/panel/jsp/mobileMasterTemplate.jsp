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
    <style type="text/css">
  html, body, .container
{
    height: 100%; 
    min-height: 100%;
    display:table; 
	background: rgb(51,51,51);
    color: #fff;
    padding:20px;
}

.first {
    float: left;
    width: 20%;
    height: 30%;
    background:#CD0000; 
	margin:0 5px 0 0;
    padding:2px;
}

.second{
    float: left;
    width: 12%;
    height: 12%;
	background:#DAA520;
	margin:0 5px 0 0;
    padding:2px;
   
}


.third{
    float: right;
    width: 12%;
    height: 12%;
    background:#4682B4;
	margin:0 5px 0 0;
    padding:2px;
}

.fourth {
    float: right;
    width: 12%;
    height: 12%;
    background-color: #2E8B57;
	margin:0 5px 0 0;
    padding:2px;
}

.last{
    float: right;
    width: 40%;
    height: 20%;
    background-color: yellow;
}
  </style>

    </head>
    
    <body>
        

        <tiles:insertAttribute name="body" />

    
    </body>
</html>
