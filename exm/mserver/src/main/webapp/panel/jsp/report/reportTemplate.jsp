<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<!-- Template for report generation pages. -->

<div class="infoHeader">
    <s:property value="getText('text.mserver.title')" /> &gt; <s:property value="title" />
</div>
<div class="pageBody">
    <!-- Message -->
    <s:if test="%{message != null}">
        <div class="<s:property value="messageType" />">
            <s:property value="message" />
        </div>
    </s:if>

    <s:form name="reportForm">
    
        <div class="toolBox">
        <!-- If report is being generated, this part displays state. -->
        <s:if test="%{reportId != null}">
            <div id="reportGen" class="reportGenProgress">
                <s:property value="getText('text.report.generating')" />
            </div>
        </s:if>
    
        <table class="formTable">
            <tbody>
            
                <tiles:insertAttribute name="reportParams" />
                <!-- Report language field -->
                <tr>
                    <td><s:property value="getText('text.report.language')" /></td>
                    <td><s:select name="language" list="languages" listKey="shortName" listValue="description" value="language" /></td>
                </tr>
                <!-- Report output format field -->
                <tr>
                    <td><s:property value="getText('text.report.outputformat')" /></td>
                    <td><s:select name="outputformat" list="supportedOutputFormats" listKey="index" listValue="description" /></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <s:a href="JavaScript:document.reportForm.submit();" cssClass="button" cssStyle="float:left;">
                            <s:property value="getText('text.report.generate')" />
                        </s:a>
                    </td>
                </tr>
            </tbody>
        </table>
        <s:hidden name="action" value="generate" />
    </s:form>
    
</div>
<script type="text/javascript">
    // Default properties of the datepicker component.
    var datepickerProperties = {
        duration: '', // Disable animation
        dateFormat: '<s:property value="getText('format.date.js')" />',
        dayNamesMin: <s:property value="getText('array.daynames.min')" />,
        dayNamesShort: <s:property value="getText('array.daynames.short')" />,
        dayNames: <s:property value="getText('array.daynames')" />,
        monthNamesShort: <s:property value="getText('array.monthnames.short')" />,
        monthNames: <s:property value="getText('array.monthnames')" />,
        firstDay: <s:property value="getText('number.firstday')" />
    };

    $(function() {
        for (var i in dateFields) {
            $("#" + dateFields[i]).datepicker(datepickerProperties);
        }
    });
<s:if test="%{reportId != null}">
    <s:url action="isReportReady" var="isReportReadyUrl">
        <s:param name="reportId" value="reportId" />
    </s:url>
    <s:url action="getReport" var="getReportUrl">
        <s:param name="reportId" value="reportId" />
    </s:url>
    
    var checkingReport = false;
    var checkingReportInterval = 0;
    
    function onCheckReportSuccess(data, textStatus) {
        if (data == 'READY') {
            var reportGen = $('#reportGen');
            reportGen.attr('class', 'reportGenSuccess');
            reportGen.html('<a href="<s:property value="%{getReportUrl}" />"><s:property value="getText('text.report.generation.success')" /></a>');
                
            clearInterval(checkingReportInterval);
        } else if (data == 'FAILED') {
            var reportGen = $('#reportGen');
            reportGen.attr('class', 'reportGenFailed');
            reportGen.html('<s:property value="getText('text.report.generation.failed')" />');
                
            clearInterval(checkingReportInterval);
        }
            
        checkingReport = false;
    }
    
    function onCheckReportError(XMLHttpRequest, textStatus, errorThrown) {
        var reportGen = $('#reportGen');
        reportGen.attr('class', 'reportGenFailed');
        reportGen.html('<s:property value="getText('text.report.generation.connectionerror')" />');
        
        clearInterval(checkingReportInterval);
    }
    
    function checkReport() {
        if (checkingReport) {
            return;
        }
        
        checkingReport = true;
        
        $.ajax({
            type: 'GET',
            url: '<s:property value="%{isReportReadyUrl}" />',
            dataType: 'text',
            success: onCheckReportSuccess,
            error: onCheckReportError
        });
    }
    
    checkingReportInterval = setInterval('checkReport()', <s:property value="checkReportInterval" />);
</s:if>
</script>            
