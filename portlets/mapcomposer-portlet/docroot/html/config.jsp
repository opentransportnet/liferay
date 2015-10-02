<%@include file="/html/init.jsp"%>

<div class="portlet-msg-info">
    <span class="displaying-article-id-holder "> Map Composer
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
            <td colspan="2"><input type="button"
                value="<liferay-ui:message key="save" />"
                onClick="submitForm(document.<portlet:namespace />fm);" /></td>
        </tr>
    </table>
</form>
