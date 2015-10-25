<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<div class="pageHeader">
    <s:property value="getText('text.presentation.upload.title')" />
</div>
<div class="pageBody">
    <s:if test="%{message != null}">
        <div class="<s:property value="messageType" />">
            <s:property value="message" />
        </div>
    </s:if>

    <s:form name="presUploadForm" enctype="multipart/form-data" namespace="/panel" action="uploadPresentation" method="POST">
        <div class="fileUploadForm">
            <div class="fileUpload">
                <s:file name="presentationFile" />
            </div>
        
            <s:url var="url" action="panel/mainMenu" />
            <s:a href="%{url}" cssClass="button" cssStyle="float:left;">
                <s:property value="getText('text.button.back')" />
            </s:a>
            <s:a href="JavaScript:document.presUploadForm.submit();" cssClass="button" cssStyle="float:left;">
                <s:property value="getText('text.button.upload')" />
            </s:a>
        </div>
    </s:form>

</div>
