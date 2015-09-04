<%@include file="/html/init.jsp"%>

<div class="portlet-msg-info">
    <span class="displaying-article-id-holder "> Thematic Map Viewer
        configuration page. </span>
</div>

<form action="<liferay-portlet:actionURL portletConfiguration="true" />"
    method="post" name="<portlet:namespace />fm">

    <input name="<portlet:namespace /><%=Constants.CMD%>" type="hidden"
        value="<%=Constants.UPDATE%>" />

    <table class="lfr-table">
        <tr>
            <td>Map Compositions Catalogue URL</td>
            <td><input width=50 type="text"
                name="<portlet:namespace />compurl" value="<%=compurl%>" /></td>
            <td>http://liferay.local/cat/libs/cswclient/cswClientRun.php</td>
        </tr>
        <tr>
            <td colspan="2"><input type="button"
                value="<liferay-ui:message key="save" />"
                onClick="submitForm(document.<portlet:namespace />fm);" /></td>
        </tr>
    </table>
</form>
