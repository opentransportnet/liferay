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

<portlet:actionURL var="deleteDocument" name="deleteDocument"></portlet:actionURL>

<%
	JSONObject datasetDetails = new JSONObject();     

	datasetDetails = (JSONObject) request.getAttribute("datasetDetails");
	//String mode = (String) request.getAttribute("mode");
		JSONObject temp;	

%>
 
<br/><br/>				
<form id="delete_form" name="<portlet:namespace />delete_form" action="<%=deleteDocument.toString()%>"  enctype="multipart/form-data" method="post">
	<div class="datasetDetailsDelete">
 <div class="row">
 </div>
    <div class="panel panel-default">
    </div>
	<h4>Delete Dataset "<span><%=datasetDetails.get("title")%>"</span>?</h4>
	<br/>
	
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
					<button class= "btn btnCentral" type="Update"><liferay-ui:message key="delete" /></button>	
					<button class= "btn btn-default" type="button" onclick="window.location = 'my-datasets';"><liferay-ui:message key="cancel" /></button>				
				</div>
				<br/>
	</div>			
	</form>