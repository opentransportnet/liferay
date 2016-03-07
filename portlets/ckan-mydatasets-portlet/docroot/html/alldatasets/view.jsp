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
		JSONArray datasets = new JSONArray();
		datasets = (JSONArray) request.getAttribute("datasets");		
		int i;
		JSONObject temp;
		
		String mode = request.getAttribute("mode").toString();
		if(mode.equals("mini"))
		{
	%>
<div class="container1">
<div class="row">
	<div class="col-sm-4"><h5>Date</h5></div>
	<div class="col-sm-8"><h5>Dataset</h5></div>
</div>

<%
		for (i = 0; i < Math.min(datasets.length(),10); i++) {
		temp = datasets.getJSONObject(i);
		
		 String date_s = temp.get("metadata_created").toString();

	        // *** 2015-10-02T15:01:21.227211"  
	        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
	        Date date = dt.parse(date_s);

	        // *** same for the format String below
	        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");	       
	       
	%>
<div class="row">
	<div class="col-sm-4">
		<%=dt1.format(date)%>
	</div>
	<div class="col-sm-8">
		<h5><%-- <a href="${viewDatasetUrl}&amp;datasetId=<%=temp.get("id")%>"><%=temp.get("title")%></a> --%>
			<a href="/web/guest/dataset-details?&amp;datasetId=<%=temp.get("id")%>"><%=temp.get("title")%></a>				
		</h5>
	</div>
</div>

<%		
		}// end for 
		
	%>
</div>
<div class="row">
	<div class="col-sm-12">
		<a class="pull-right dlink" href="/all-datasets">Explore all
			datasets</a>
	</div>
</div>
<%	
		}// end mini mode
		else if (mode.equalsIgnoreCase("full"))
		{
	%>
<div class="container datasetList">
	<%
		if (datasets.length() > 0) {			
	%>

	<div class="row">
		<div class="col-md-12">
			<h3>
				<%=datasets.length()%>
				datasets found
			</h3>
		</div>

		<ul class="list-unstyled">
			<%
				for (i = 0; i < datasets.length(); i++) {
							temp = datasets.getJSONObject(i);
			%>
			<li>
				<h4>
					<a class="dataTitle" href="/web/guest/dataset-details?&amp;datasetId=<%=temp.get("id")%>"><%=temp.get("title")%></a>				
				
					<%-- <a class="dataTitle"
						href="${viewDatasetUrl}&amp;datasetId=<%=temp.get("id")%>"><%=temp.get("title")%></a> --%>
				</h4>
				<div><%=temp.get("notes")%></div> <small><%=temp.get("license_title")%></small>
			</li>

			<%
				}
			%>
		</ul>
	</div>

	<%
		} else {
		%>
	<div class="col-md-12">
		<h5>
			<i>No datasets found.</i>
		</h5>
	</div>

	<%
		 } %>
</div><!-- end container -->
<%
		}
	%>
