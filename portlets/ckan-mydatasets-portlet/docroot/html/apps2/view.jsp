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
		JSONArray apps = new JSONArray();
		apps = (JSONArray) request.getAttribute("apps");		
		int i;
		JSONObject temp;
		String city = (String)request.getAttribute("city");
		String renderButtons = (String)request.getAttribute("renderButtons");
		String thumbnail = "";
	%>

<%
		if (apps.length() > 0) {			
	%>
<div id="page"><!-- - - - - - - - - - SECTION PORTFOLIO - - - - - - - - - -->	
<div class="pi-section-w pi-section-white piCaptions">
	<div class="pi-section pi-padding-bottom-60">
		<div id="isotope" class="pi-row pi-liquid-col-xs-2 pi-liquid-col-sm-3 pi-gallery pi-gallery-small-margins pi-text-center isotope">
			
			<%
				for (i = 0; i < apps.length(); i++) {
							temp = apps.getJSONObject(i);	
							
					if (temp.isNull("pictureUrl")) {
						thumbnail = "/otn_marketplace/images/default-icon-app.jpg";
					} else {
						thumbnail = temp.get("pictureUrl").toString();
					}		
			%>
			
			<!-- Portfolio item -->
			<div class="Beautiful Morning pi-gallery-item isotope-item">
				<div class="pi-img-w pi-img-shadow pi-img-with-overlay pi-no-margin-bottom">
					<a href="#">
					<img style="width: 333px;height: 222px;" src="<%=thumbnail%>" alt=""></a>
					  <div class="pi-img-overlay pi-overlay-slide pi-show-heading">
						<h6 class="pi-weight-700 pi-uppercase pi-letter-spacing pi-margin-top-minus-5 pi-text-shadow">
							<a href="#" class="pi-link-white"><%=temp.get("title")%></a>
						</h6>
						
						<p style="max-width: 250px;max-height: 50px;overflow: hidden;" class="pi-margin-top-minus-5 pi-margin-bottom-15"><%=temp.get("appAbstract")%></p>

						 <ul class="pi-caption-links pi-margin-bottom-20">
							 <li><!-- <i class="icon-tag"></i> <a href="/web/<%=city%>/edit-app?id=<%=temp.get("id")%>">Edit</a>  <a href="#">Delete</a>-->
							 	<%if (!temp.isNull("otnUrl")){ %>
									<a style="float:left; margin-right: 2px;" href="<%=temp.get("otnUrl") %>" target="_blank"><img src="/otn_marketplace/images/OTN-logo.png"></img></a>
								<%} %>
								
								<%if (!temp.isNull("googleUrl")){ %>
									<a style="float:left; margin-right: 2px;" href="<%=temp.get("googleUrl") %>" target="_blank"><img src="/otn_marketplace/images/google.png"></img></a>
								<%} %>
								
								<%if (!temp.isNull("appleUrl")){ %>
									<a style="float:left; margin-right: 2px;" href="<%=temp.get("appleUrl") %>" target="_blank"><img style="    position: relative; top: -2px;" src="/otn_marketplace/images/apple.png"></img></a>
								<%} %> 
								
								<%if (!temp.isNull("windowsUrl")){ %>
									<a style="float:left; margin-right: 2px;" href="<%=temp.get("windowsUrl") %>" target="_blank"><img src="/otn_marketplace/images/windows.png"></img></a>
								<%} %>
							 </li> 
						</ul>
						
						<a <%=renderButtons %> href="/web/<%=city%>/edit-app?id=<%=temp.get("id")%>" class="btn pi-btn-small pi-btn-base pi-uppercase pi-weight-600 pi-letter-spacing">Edit</a> 
						
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
