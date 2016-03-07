<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>

<portlet:defineObjects />

<portlet:actionURL var="viewDatasetUrl" name="viewDatasetUrl"></portlet:actionURL>

<%@ page
	import="org.json.JSONArray,org.json.JSONObject,java.text.SimpleDateFormat,
		java.util.Date"%>
<%
		JSONArray mapCompositions = new JSONArray();
		mapCompositions = (JSONArray) request.getAttribute("mapCompositions");		
		String baseurl = request.getAttribute("baseurl").toString();
		int i;
		JSONObject temp;
	%>

<div class="container white">
<div class="row">	
	<h5>My Maps</h5>
</div>
<%
	for (i = 0; i < Math.min(mapCompositions.length(),10); i++) {
		temp = mapCompositions.getJSONObject(i);		       
		String abstrac = temp.get("abstract").toString().substring(0, Math.min(temp.get("abstract").toString().length(), 150));   
	%>
<div class="row">
	<div class="col-md-12">
		<h5><a href="<%=baseurl%>/create-maps?composition=<%=temp.get("id")%>"><%=temp.get("title")%></a></h5>
	</div>
</div>
<div class="row">
	<div class="col-md-12 small">
		<%=temp.get("abstract")%>
	</div>
</div>

<%		
		}// end for 		
	%>
	</div> <!--  end container -->

