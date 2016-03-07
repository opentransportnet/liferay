<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>


<portlet:defineObjects />

<portlet:actionURL var="viewDatasetUrl" name="viewDatasetUrl"></portlet:actionURL>


	<%@ page import= "org.json.JSONArray,org.json.JSONObject"	%>
	<%
		JSONArray datasets = new JSONArray();
		datasets = (JSONArray) request.getAttribute("datasets");
		
		String city = (String)request.getAttribute("city");
		String licStr = "";
		String datasetId = "";
		int i;
		JSONObject temp;
	%>
<body>
		  <div class="datasetList" id="orgDatasets">
    <%
		if (datasets.length() > 0) {
			if (datasets.getJSONObject(0).has("ckan-user")) {
	%>
	<div class="col-md-12">
		<a href="#" class="btn-default">Create my Datasets space</a>
	</div>
	<%
		} else {
	%>
	
	<div class="row">
		<div class="col-md-12 datasetsFound">
			<h4>
				<%=datasets.length()%>
				datasets found
			</h4>
		</div>
		
		
		<div class="col-md-12" style="text-align: center">
			<a href="/web/<%=city%>/upload-organization-dataset"
				class="btn btn-default">Add a new dataset</a>
		</div>
<br/>

	<div id="test-list">
		
		<input type="text" class="search" placeholder=" Search dataset"/>
		
		<ul  class="list">
			<%datasetId = (String)request.getAttribute("datasetId");
				for (i = 0; i < datasets.length(); i++) {
							temp = datasets.getJSONObject(i);
			%>
			<li <% if ( datasetId != null && datasetId.contains(temp.get("id").toString())){%>
			 class="activeDataset"
			 <%}%>>
				<h4>
					 <!-- <a class="dataTitle" href="${viewDatasetUrl}&amp;datasetId=<%=temp.get("id")%>"><%=temp.get("title")%></a>-->
					 <a class="dataTitle" href="/web/<%=city%>/organization-datasets?&amp;datasetId=<%=temp.get("id")%>"><%=temp.get("title")%></a>				
				</h4>
				<div class="datasetDescription"><%=temp.get("notes")%></div> 
				<%if (temp.isNull("license_title") )
					licStr = "No licence specified";
				  else
					licStr = (String)temp.get("license_title");%>
				<span><%=licStr%></span>
				<br/>
				
				<div class="editDelete">
				<a class="editData" href="/web/<%=city%>/organization-datasets?&amp;datasetId=<%=temp.get("id")%>&amp;mode=edit"><span class="glyphicon glyphicon-edit"></span>Edit</a>
				<a class="editData" href="/web/<%=city%>/delete-organization-dataset?&amp;datasetId=<%=temp.get("id")%>"><span class="glyphicon glyphicon-remove"></span>Delete</a>
				</div>
				<br/>
				<hr class="separator">
			</li>

			<%
				}
			%>
		</ul>  <ul class="pagination"></ul></div>
				
	</div>
	

</div> <!-- end container -->


	<%
		}
		} else {
	%>
	<div class="col-md-12">
		<a href="/web/<%=city%>/upload-organization-dataset"
				class="btn btn-primary">Add a new dataset</a>
	</div>
	
	<%
		}
	%>
	
</body>



