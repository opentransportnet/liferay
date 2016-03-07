<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>

<portlet:defineObjects />

 <script type='text/javascript'>
         		var $jq = jQuery.noConflict(true);
     		</script>

<%@ page import="org.json.JSONArray,org.json.JSONObject"%>

<portlet:actionURL var="uploadDocument" name="uploadDocument"></portlet:actionURL>
<% 
	JSONArray licenses = (JSONArray)request.getAttribute("licenses");
    String ckanerror = (String)  request.getAttribute("ckanerror");
    String dTanksuccess = (String)  request.getAttribute("dTanksuccess");
    String dTankerror= (String)  request.getAttribute("dTankerror");
    String city = (String)request.getAttribute("city");
	int i;	
	JSONObject temp;	
%>

<div class="container white">
<div class="row bs-wizard">


  </div>
  
<liferay-ui:error key="ckanerror" message="<%=ckanerror%>" />
<liferay-ui:success key="ckansuccess" message="Dataset saved succesfully" />
<liferay-ui:success key="dTanksuccess" message="<%=dTanksuccess%>" />
<liferay-ui:error key="dTankerror" message="<%=dTankerror%>" />
    
    <form id="uploader_form" name="<portlet:namespace />uploader_form" action="<%=uploadDocument.toString()%>"  enctype="multipart/form-data" method="post">
          
          <div class="row">  
          <div class="col-md-7">
                <div class="form-group">                
                	<label class="registrationLegend  asteriskCentral"><liferay-ui:message key="title" /></label>                		
               		<div class="form-group" style="padding:0px !important;">	                 	
                 		<input class="inputText" type="text" id="dstitle"  name="<portlet:namespace />dstitle" required class="step_1">
                	 
                	</div>                	             
              	            
            	
				
				<div class="form-group">         
				<label><liferay-ui:message key="file" /></label>       
               		<div id="uploadDiv">	
                 	
                 		    <div class="btn-group" data-toggle="buttons-radio">
							  
							  <button type="button" class="btn btn-default" onclick="$('#linkDiv').hide();$('#fileDiv').show();"><i class="icon-cloud-upload"></i> <liferay-ui:message key="upload" /></button>
							  <button type="button" class="btn btn-default" onclick="$('#fileDiv').hide();$('#linkDiv').show();"><i class="icon-globe"></i> <liferay-ui:message key="link" /></button>
		
							</div>                               	 
                	</div>                	             
              	</div>            	
            
                <div class="form-group" id="fileDiv" style="display:none">
                
                	<label class="registrationLegend">File</label> 
               		
               		<div>	
                 	
                 		<input id="fileInput" class="upload-group file" type="file" name="<portlet:namespace />fileInput" data-show-upload="false" data-show-caption="true" data-show-preview="false">
                	 
                	</div>                	             
              	</div>
              	
              	<div class="form-group" id="linkDiv" style="display:none" >
                
                	<label class="registrationLegend"><liferay-ui:message key="link" /></label> 
               		
               		<div>	
                 	
                 		<input class="upload-group inputText" type="text" id="link" name="<portlet:namespace />link" >
                	 
                	</div>                	             
              	</div>
              	
              	 <div class="form-group">
                
                	<label class="registrationLegend  asteriskCentral" data-toggle="tooltip" data-placement="right" title='<liferay-ui:message key="format" />'><liferay-ui:message key="format" /></label> 
               		
               		<div>	
                 	
                 		<select class="inputText" id="format"  name="<portlet:namespace />format">							
							<option value="JSON">JSON</option>
							<option value="GEOJSON">GEOJSON</option>
							<option value="KML">KML</option>
							<option value="WMS">WMS</option>
							<option value="CSV">CSV</option>
							<option value="XLS">XLS</option>
							<option value="XLSX">XLSX</option>
						</select>
                	 
                	</div>
                	             
              	</div>
              	
              	<div class="form-group">
				
					<label class="registrationLegend  asteriskCentral" for="notes"><liferay-ui:message key="notes" /></label>
				
					<div class="form-group" style="padding:0px !important;">	
						
						<textarea class="inputTextArea" rows="3" id="notes" required name="<portlet:namespace />notes"></textarea>
					
					</div>					
				</div>
				
				          
            	<div class="form-group">
                
                	<label class="registrationLegend"><liferay-ui:message key="tags" /></label> 
               		
               		<div class="tags">	
                 	
                 		<input class="inputText" data-role="tagsinput" id="tags"  name="<portlet:namespace />tags"></input>
                	 
                	</div>
                	             
              	</div>
            	
            
            	 <div class="form-group">
                
                	<label class="registrationLegend"><liferay-ui:message key="license" /></label> 
               		
               		<div class="form-group" style="padding:0px !important;">	
                 	
                 		<select id="license"  class="step_1 validation inputText"  name="<portlet:namespace />license">
							
							<% 
								
							
								for(i=0;i<licenses.length();i++){
									
									temp = licenses.getJSONObject(i);
							%>
							
								<option value='<%=temp.get("id")%>'><%=temp.get("title") %></option>
									
							<%} %>
							
						</select>
                	 
                	</div>
                	             
              	</div>
              	
              	<div class="form-group">
                
                	<label class="registrationLegend"><liferay-ui:message key="source" /></label> 
               		
               		<div>	
                 	
                 		<input class="inputText" type="text" id="source"  name="<portlet:namespace />source" class="step_1">
                	 
                	</div>
                	             
              	</div>
              	
              	<div class="form-group">
                
                	<label class="registrationLegend"><liferay-ui:message key="version" /></label> 
               		
               		<div>	
                 	
                 		<input class="inputText" type="text" id="version"  name="<portlet:namespace />version"  class="step_1">
                	 
                	</div>
                	             
              	</div>
            
           		<div class="form-group">
                
                	<label class="registrationLegend"><liferay-ui:message key="author" /></label> 
               		
               		<div>	
                 	
                 		<input class="inputText" type="text" id="author"  name="<portlet:namespace />author" >
                	 
                	</div>                	             
              	</div>
                            	
              	</div>
              	
							
          
            
             	<div class="bs-wizard-step" style="margin:0;padding-bottom:30px;">
				
					<!-- <button class= "btn btn-default" type="reset"><liferay-ui:message key="previous-step" /></button> -->
				
				<br/><br/>
					<button class= "btn centralSubmissionButton" type="Submit"><liferay-ui:message key="upload-dataset" /></button>
				
				</div>
				
				</div> 
				
				<div class="col-sm-3 col-sm-offset-1 rightNoteRegistration">
					<label class="registrationLegend"><liferay-ui:message key="datasets" /></label>
						<br/><br/>
					<label><liferay-ui:message key="see-datasets" /></label>
					<br/><br/>
					
					<div style="border-bottom:#fff solid 10px;">
					<a href="/web/<%=city%>/all-datasets"	class="btn noCapitals centralSeeAllButton" >
					<liferay-ui:message key="see-all-datasets" /></a>
					</div>
					</div>	
				
				
		</div> <!-- close row -->
	</form>


</div>

<script>

window.onload = function() {
			
			init_uploader();
			$("#uploader_form").validate({
				  ignore: [],
				  errorPlacement: function(error, element) {
					  if(element.attr("id") == "fileInput") {
					    error.appendTo("#uploadDiv");
					  } else {
					    error.insertAfter(element);
					  }
					},
				  rules: {
					      <portlet:namespace/>fileInput: {
					        require_from_group: [1, ".upload-group"]
					      },
					      <portlet:namespace/>link: {
					        require_from_group: [1, ".upload-group"]
					      },
				  }
					});
			$(".glyphicon.glyphicon-folder-open").attr("class","icon-folder-open");  
			$(".glyphicon.glyphicon-trash").attr("class","icon-trash");
		 };
		
</script>
