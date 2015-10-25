<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<div class="infoHeader">
    <s:property value="getText('text.mserver.title')" /> &gt; <s:property value="getText('text.displayreports.title')" />
</div>
<div class="pageBody">
    <s:set var="data" value="reports.data" />
    <table class="resultList">
        <thead>
            <tr class="resultHeaderRow">
                <td><s:property value="getText('text.displayreports.date')" /></td>
                <td><s:property value="getText('text.displayreports.type')" /></td>
                <td><s:property value="getText('text.displayreports.description')" /></td>
                <td><s:property value="getText('text.displayreports.format')" /></td>
                <td></td>
            </tr>
        </thead>
        <tbody>
        <s:iterator value="%{data}">
            <tr>
                <td><s:date name="timestamp" format="%{getText('format.timestamp.java')}" /></td>
                <td><s:property value="getReportTypeText(type)" /></td>
                <td><s:property value="description" /></td>
                <td><s:property value="getOutputFormatText(format)" /></td>
                <td>
                    <s:url action="getReport" var="url">
                        <s:param name="reportId" value="id" />
                    </s:url>
                    <s:a href="%{url}"><s:property value="getText('text.displayreports.download')"/></s:a>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
    <s:set var="page" value="%{reports.page}" />
    <s:set var="pagesCount" value="%{reports.pagesCount}" />
    <s:set var="firstPage" value="%{0}" />
    <s:set var="prevPage" value="%{reports.page - 1}" />
    <s:set var="nextPage" value="%{reports.page + 1}" />
    <s:set var="lastPage" value="%{pagesCount}" />
    <table class="pagerControl">
        <tbody>
            <tr>
                <td>
                    <s:if test="%{#page > 0}">
                        <s:url var="url" action="displayReports">
                            <s:param name="page" value="%{#firstPage}" />
                        </s:url>
                        <s:a href="%{url}">&lt;&lt;</s:a>
                    </s:if>
                    <s:else>
                        &lt;&lt;
                    </s:else>
                </td>
                <td>
                    <s:if test="%{#page > 0}">
                        <s:url var="url" action="displayReports">
                            <s:param name="page" value="%{#prevPage}" />
                        </s:url>
                        <s:a href="%{url}">&lt;</s:a>
                    </s:if>
                    <s:else>
                        &lt;
                    </s:else>
                </td>
                <td class="pagerControlSep">
                    <s:if test="%{!#data.isEmpty()}">
                        <s:set var="offset" value="%{reports.offset + 1}" />
                        <s:property value="#offset" />
                        -
                        <s:property value="%{#offset + #data.size() - 1}" />
                        &nbsp;
                        /
                        &nbsp;
                        <s:property value="%{reports.size}" />
                    </s:if>
                </td>
                <td>
                    <s:if test="%{#page < #pagesCount - 1}">
                        <s:url var="url" action="displayReports">
                            <s:param name="page" value="%{#nextPage}" />
                        </s:url>
                        <s:a href="%{url}">&gt;</s:a>
                    </s:if>
                    <s:else>
                        &gt;
                    </s:else>
                </td>
                <td>
                    <s:if test="%{#page < #pagesCount - 1}">
                        <s:url var="url" action="displayReports">
                            <s:param name="page" value="%{#lastPage}" />
                        </s:url>
                        <s:a href="%{url}">&gt;&gt;</s:a>
                    </s:if>
                    <s:else>
                        &gt;&gt;
                    </s:else>
                </td>
            </tr>
        </tbody>
    </table>

</div>
