<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />
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
		JSONArray services = new JSONArray();
		services = (JSONArray) request.getAttribute("services");		
		int i;
		JSONObject temp;
		JSONObject category;
		String city = (String)request.getAttribute("city");
	%>

<%
		if (services.length() > 0) {			
	%>
<div id="page"><!-- - - - - - - - - - SECTION PORTFOLIO - - - - - - - - - -->	
<div class="pi-section-w pi-section-white piCaptions">
	<div class="pi-section pi-padding-bottom-60">
		<div id="isotope" class="pi-row pi-liquid-col-xs-2 pi-liquid-col-sm-3 pi-gallery pi-gallery-small-margins pi-text-center isotope">
			
			<%
				for (i = 0; i < services.length(); i++) {
							temp = services.getJSONObject(i);		
			%>
			
			<!-- Portfolio item -->
			<div class="Beautiful Morning pi-gallery-item isotope-item">
				<div class="pi-img-w pi-img-shadow pi-img-with-overlay pi-no-margin-bottom">
					<a href="/web/<%=city%>/service-details?id=<%=temp.get("id")%>">
					<img style="width: 333px;height: 222px;" src="<%=temp.get("thumbnail")%>" alt=""></a>
					  <div class="pi-img-overlay pi-overlay-slide pi-show-heading">
						<h6 class="pi-weight-700 pi-uppercase pi-letter-spacing pi-margin-top-minus-5 pi-text-shadow">
							<%=temp.get("name")%></h6>
						
						<p style="max-width: 250px;max-height: 73px;overflow: hidden;" class="pi-margin-top-minus-5 pi-margin-bottom-15"><%=temp.get("description")%></p>
						
						<% category = (JSONObject)temp.get("category"); %>
						
						<div style="margin-top: -5px;margin-bottom: -14px;font-style: italic;"> <%=category.get("name")%> </div>
						<ul class="pi-caption-links pi-margin-bottom-20">
							<!--  <li><i class="icon-tag"></i><a href="#">Beautiful</a>, <a href="#">Morning</a></li> -->
						</ul>
						
						<a href="/web/<%=city%>/service-details?id=<%=temp.get("id")%>" class="btn pi-btn-small pi-btn-base pi-uppercase pi-weight-600 pi-letter-spacing">Details</a>
					</div>
				</div>
			<div class="pi-img-shadow-gap pi-shadow-effect8"></div>
		</div>
		
		<%
			}
		%>
		
	</div>
</div>
</div>
</div>

<%
		} else {
		%>
	<div class="col-md-12">
		<h5>
			<i>No services found.</i>
		</h5>
	</div>
	<%
		} 
		%>
			<!-- End portfolio item -->
