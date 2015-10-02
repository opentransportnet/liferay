window.addEventListener ? window.addEventListener('load', fullSize, false) : window.attachEvent('onload', fullSize);
window.addEventListener ? window.addEventListener('resize', fullresize, false) : window.attachEvent('onresize', fullresize);

function fullSize() {
    if ($('.iframefull').length > 0){
        $('html').css('overflow','hidden');
        $('.portlet-column-content').css('padding','0');
        var iframe = $('iframe');
        var contDiv = iframe.parent();
        contDiv.css('overflow','hidden');
        contDiv.css('width','100%');
        contDiv.css('height','200px');
        iframe.css('width','100%');
        contDiv.parent().css('overflow','hidden');
        $('#content').resize(fullresize());
        fullresize();
    }
}

function fullresize() {
    if ($('.iframefull').length > 0){
        var iframe = $('iframe');
        var contDiv = $('iframe').parent();
        var width = $('#content').width();
        var windowHeight = $(window).height();
        var headerHeight = $('.portlet-dockbar').outerHeight() + $('.breadcrumbs').outerHeight() + $('#header').outerHeight() + 10;
        var iframeHeight = windowHeight - headerHeight;
        contDiv.height(iframeHeight);
        iframe.height(iframeHeight);
        contDiv.width(width);
        iframe.width(width);
        iframe.attr('width',width);
        iframe.attr('height',iframeHeight);
    }
}
