<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>


<portlet:defineObjects />
<%@ page import="org.json.JSONArray,org.json.JSONObject,java.net.URLEncoder"%>
<%
	JSONObject datasetDetails = new JSONObject();
	datasetDetails = (JSONObject) request
			.getAttribute("datasetDetails");
	JSONArray resources = new JSONArray();
	resources = datasetDetails.getJSONArray("resources");
	JSONArray tags = new JSONArray();
	tags = datasetDetails.getJSONArray("tags");
	JSONObject res = new JSONObject();
	int i;
	String format;
%>
<div class="container datasetDetails">
 <div class="row">
 <div class="col-md-12"><h1><%=datasetDetails.get("title")%></h1></div>
 </div>
	
	
	<p><%=datasetDetails.get("notes")%></p>
    <div class="panel panel-default">
    <ul class="list-unstyled">
			<%
				for (i = 0; i < resources.length(); i++) {
					res = resources.getJSONObject(i);
					format = res.get("format").toString();
					boolean showformat = false;
					if(format.toLowerCase().equals("kml") || 
					 format.toLowerCase().equals("wms") ||
					 format.toLowerCase().equals("geojson") ||
					 format.toLowerCase().equals("json"))
					{
						showformat=true;
						format = format.toLowerCase();	
						if(format.equals("json"))
						{
							format = "geojson";
						}
					}
			%>
			<li>				
				<%  if(showformat)
    			{  %>	
				 <div class="pull-right">
    				<a target="_blank" href="/maps-and-charts?<%=format%>_to_connect=<%=URLEncoder.encode(res.get("url").toString(), "UTF-8")%>" class="btn btn-primary"><liferay-ui:message key="visualisemap" /></a>
  				</div>	
  				<%
				}
			   %>	
				<div><span class="glyphicon glyphicon-link"></span> URL: <a href="<%=res.get("url")%>"><%=res.get("url")%></a></div><br />
				<div><span class="glyphicon glyphicon-file"></span> Format: <%=res.get("format")%></div> 
			</li>

			<%
				}
			%>
		</ul>
    </div>
    
    <%  if(tags.length()>0)
    {%>
    	<div class="row">
    	 <div class="col-md-12">
    	<%	for (i = 0; i < tags.length(); i++) {
					res = tags.getJSONObject(i);
			%>			
				<span class="tag">
					 <%=res.get("display_name")%>
				</span>	
			<%
				}%>
		</div></div>
		<%}
			%>

	<h4>Additional Info</h4>
	<table class="table">
		<tr>
			<td>Source</td>
			<td><%=datasetDetails.get("url")%></td>
		</tr>
		<tr>
			<td>License</td>
			<td><%=datasetDetails.get("license_title")%></td>
		</tr>
		<tr>
			<td>Last Updated</td>
			<td><%=datasetDetails.get("metadata_modified")%></td>
		</tr>
		<tr>
			<td>Created</td>
			<td><%=datasetDetails.get("metadata_created")%></td>
		</tr>
		<tr>
			<td>Author</td>
			<td><%=datasetDetails.get("author")%></td>
		</tr>
	</table>			
</div>
