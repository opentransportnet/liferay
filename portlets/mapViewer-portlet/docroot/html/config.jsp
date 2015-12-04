<%@include file="/html/init.jsp"%>

<div class="portlet-msg-info">
    <span class="displaying-article-id-holder "> Map Viewer
        configuration page. </span>
</div>

<form action="<liferay-portlet:actionURL portletConfiguration="true" />"
    method="post" name="<portlet:namespace />fm">

    <input name="<portlet:namespace /><%=Constants.CMD%>" type="hidden"
        value="<%=Constants.UPDATE%>" />

    <table class="lfr-table">
        <tr>
            <td>Catalogue URL</td>
            <td><input width=150 type="text"
                name="<portlet:namespace />caturl" value="<%=caturl%>" /></td>
            <td>http://liferay.local/php/metadata/csw/index.php</td>
        </tr>
        <tr>
            <td>Longitude</td>
            <td><input width=50 type="text"
                name="<portlet:namespace />centerX" value="<%=centerX%>" /></td>
            <td>Number</td>
        </tr>
        <tr>
            <td>Latitude</td>
            <td><input width=50 type="text"
                name="<portlet:namespace />centerY" value="<%=centerY%>" /></td>
            <td>Number</td>
        </tr>
        <tr>
            <td>Zoom</td>
            <td><input width=10 type="text"
                name="<portlet:namespace />zoom" value="<%=zoom%>" /></td>
            <td>Number</td>
        </tr>
        <tr>
            <td colspan="2"><input type="button"
                value="<liferay-ui:message key="save" />"
                onClick="submitForm(document.<portlet:namespace />fm);" /></td>
        </tr>
    </table>
</form>
