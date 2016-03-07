<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>

<portlet:defineObjects />

<script type='text/javascript'>
         		var $jq = jQuery.noConflict(true);
     		</script>

<%@ page import="org.json.JSONArray,org.json.JSONObject,java.net.URLEncoder"%>

<portlet:actionURL var="editDocument" name="editDocument"></portlet:actionURL>

<%
	JSONObject datasetDetails = new JSONObject();     

	datasetDetails = (JSONObject) request.getAttribute("datasetDetails");
	String mode = (String) request.getAttribute("mode");
	String urlStr = "";
	String licenceStr = "";
	String authorStr = "";
	
	if(datasetDetails == null){%>
		 <div class="noDatasetSelected">No dataset selected.Please click on a dataset to edit it.</div>
	<% }
	 else{	 
		JSONArray resources = new JSONArray();
	 	resources = datasetDetails.getJSONArray("resources");
	 	JSONArray tags = new JSONArray();
	 	tags = datasetDetails.getJSONArray("tags");
	 	JSONObject res = new JSONObject();
	 	int i;
	 	String format;	
	 	JSONArray licenses = (JSONArray)request.getAttribute("licenses");
		int l;	
		JSONObject temp;	

%>

 <%if(mode != null && mode.equals("edit")){%>
<br/><br/>				
<form id="edit_form" name="<portlet:namespace />edit_form" action="<%=editDocument.toString()%>"  enctype="multipart/form-data" method="post">
	<div class="datasetDetailsEdit">
 <div class="row">
 </div>
    <div class="panel panel-default">
    </div>
	<h4>Update Info</h4>
	<table class="table">
		<tr>	
			<td>Title</td>
			<td><input id="dstitle" name="<portlet:namespace />dstitle" value="<%=datasetDetails.get("title")%>"/></td>
		</tr>
		<tr>
			<td>Description</td>
			<td><textarea id="notes" name="<portlet:namespace />notes" cols="10" rows="5"><%=datasetDetails.get("notes")%></textarea></td>
		</tr>
		
		<tr>
		<%if (datasetDetails.isNull("url") )
					urlStr = "";
				  else
					urlStr = (String)datasetDetails.get("url");%>
			<td>Source</td>
			<td> <input value="<%=urlStr%>" id="source"  name="<portlet:namespace />source"/></td>
		</tr>
		<tr>
			<td>License</td>
			<td>
			<div class="input-group col-md-6">	    
			<select id="license"  class="validation"  name="<portlet:namespace />license">
							
							<% 							
								for(l=0;l<licenses.length();l++){									
									temp = licenses.getJSONObject(l);
							%>							
								<option value='<%=temp.get("id")%>' 
								<% if (datasetDetails.get("license_id").equals(temp.get("id"))){%>
				selected
				<%}%>
				>
				<%=temp.get("title") %></option>									
							<%} %>							
						</select> 
						</div>
						</td>
		</tr>
		<tr>
		<%if (datasetDetails.isNull("author") )
					authorStr = "";
				  else
					authorStr = (String)datasetDetails.get("author");%>
			<td>Author</td>
			<td><input id="author" name="<portlet:namespace />author" value="<%=authorStr%>"/></td>
		</tr>
		<tr>
			<td>Tags</td>
			<td>
			<div class="input-group tags">
			<input data-role="tagsinput" id="tags"  name="<portlet:namespace />tags"  
			
			value="<%  if(tags.length()>0)
    {
    %><% StringBuilder tagsString = new StringBuilder();
    for (i = 0; i < tags.length(); i++) {
					res = tags.getJSONObject(i);
					String tag = res.get("display_name").toString();
					tagsString.append(","+tag);
			%><%=tagsString%><%
				}%><%}%>,"></input>
				
				</div></td>
			
			
		</tr>
	</table>		
	  


<div style="display:none"  class="form-group">                
                	<label>Dataset</label>                		
               		<div class="input-group">	                		                	
                 		<input type="text" id="dataset" value="<%=URLEncoder.encode(datasetDetails.toString(), "UTF-8")%>" name="<portlet:namespace  />dataset" >                	 
                	</div>                	             
              	</div>
<div style="display:none"  class="form-group">                
                	<label>Id</label>                		
               		<div class="input-group">	                		                	
                 		<input id="id" name="<portlet:namespace />id" value="<%=datasetDetails.get("id")%>"/>            	 
                	</div>                	             
              	</div> 
	<div class="bs-wizard-step" style="margin:0">				
					<button class= "btn btnCentral" type="Update"><liferay-ui:message key="submit" /></button>				
					<a class="btn btn-default" href="/organization-datasets">Cancel</a>
				</div>
	</div>			
	</form>
	
	
	<% } else {%>
 <br/><br/>
<div class="datasetDetails">
 <div class="row">
 <div class="col-md-12"><h1><%=datasetDetails.get("title")%></h1></div>
 </div>
	
	
	<div class="datasetNotes"><p><%=datasetDetails.get("notes")%></p></div>
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
    				<a target="_blank" href="/create-maps?<%=format%>_to_connect=<%=URLEncoder.encode(res.get("url").toString(), "UTF-8")%>" class="btn btnCentral"><liferay-ui:message key="visualisemap" /></a>
  				</div>	
  				<%
				}
			   %>	
				<div><span class="glyphicon glyphicon-link"></span> URL: <a href="<%=res.get("url")%>"><%=res.get("url")%></a></div>
				<div><span class="glyphicon glyphicon-file"></span> Format: <%=res.get("format")%></div> <br />
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
		<%if (datasetDetails.isNull("url") )
					urlStr = "";
				  else
					urlStr = (String)datasetDetails.get("url");%>
			<td>Source</td>
			<td><%=urlStr%></td>
		</tr>
		<tr>
		<%if (datasetDetails.isNull("license_title") )
					licenceStr = "License not specified";
				  else
					licenceStr = (String)datasetDetails.get("license_title");%>
			<td>License</td>
			<td><%=licenceStr%></td>
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
		<%if (datasetDetails.isNull("author") )
					authorStr = "";
				  else
					  authorStr = (String)datasetDetails.get("author");%>
			<td>Author</td>
			<td><%=authorStr%></td>
		</tr>
	</table>		
	  
</div>
<% }} %>
