window.addEventListener ? window.addEventListener('load', fullSize, false) : window.attachEvent('onload', fullSize);
window.addEventListener ? window.addEventListener('resize', fullresize, false) : window.attachEvent('onresize', fullresize);

function fullSize() {
    if ($jq('.iframefull').length > 0){
        $jq('html').css('overflow','hidden');
        $jq('.portlet-column-content').css('padding','0');
        var iframe = $jq('iframe');
        var contDiv = iframe.parent();
        contDiv.css('overflow','hidden');
        contDiv.css('width','100%');
        contDiv.css('height','200px');
        iframe.css('width','100%');
        contDiv.parent().css('overflow','hidden');
        $jq('#content').resize(fullresize());
        fullresize();
    }
}

function fullresize() {
    if ($jq('.iframefull').length > 0){
        var iframe = $jq('iframe');
        var contDiv = $jq('iframe').parent();
        var width = $jq('#content').width();
        var windowHeight = $jq(window).height();
        var headerHeight = $jq('.portlet-dockbar').height() + $jq('.breadcrumbs').height() + $jq('.navbar').height() + 1;
        var iframeHeight = windowHeight - headerHeight;
        contDiv.height(iframeHeight);
        iframe.height(iframeHeight);
        contDiv.width(width);
        iframe.width(width);
        iframe.attr('width',width);
        iframe.attr('height',iframeHeight);
    }
}
