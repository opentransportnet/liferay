<%@include file="/html/init.jsp"%>


    <div hs ng-app="hs" ng-controller="Main" style="position: relative;"></div>

<script>
    var caturl = "<%=caturl%>" || '/php/metadata/csw/index.php';
    var OTNcenterX = parseFloat("<%=centerX%>") || 17.474129;
    var OTNcenterY = parseFloat("<%=centerY%>") || 52.574000;
    var OTNzoom = parseInt("<%=zoom%>") || 5;
</script>
