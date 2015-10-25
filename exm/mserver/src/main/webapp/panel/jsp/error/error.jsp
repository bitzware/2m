<%@ page isErrorPage="true" contentType="text/html; charset=UTF-8" %>
<jsp:useBean id="helper"
	class="com.bitzware.exm.util.ErrorPageHelper"
	scope="request" />
	
<%
	helper.setLocale(request.getLocale());

	int iStatusCode = 0;
	String statusCode = "";
	String key = "";
	String url = "";
	Throwable ex = null;
	
	if (pageContext != null) {
		ErrorData ed = null;
		try {
			ed = pageContext.getErrorData();
		} catch (NullPointerException ne) {
		}

		if (ed != null) {
			iStatusCode = ed.getStatusCode();
			statusCode = String.valueOf(iStatusCode);
			key = "error.c" + statusCode;
			url = ed.getRequestURI();
			ex = ed.getThrowable();
			
			if (ex != null) {
				helper.logException(ex);
			}
		}
	}
	
	if (iStatusCode == 401) {
		response.addHeader("WWW-Authenticate", "BASIC realm=\"Event Server Realm\"");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
%>
<html>
	<head>
		<title><%= helper.getText(key + ".title") %></title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" >
		<style type="text/css">
			.status {
				font-weight: bold;
				margin: 10px;
			}
			
			.requestUrl {
				margin: 10px 10px 10px 10px;
			}
			
			.description {
				margin: 10px 10px 10px 10px;
			}
			
			.exception {
				margin: 10px 10px 10px 10px;
			}
		</style>
	</head>
	<body>
		<div class="requestUrl">
			<%= url %>
		</div>
		<div class="status">
			<%= helper.getText(key + ".status") %>
		</div>
		<div class="description">
			<%= helper.getText(key + ".description") %>
		</div>
	</body>
</html>
