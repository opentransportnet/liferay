wglinit = function(that) {

    var map = that.map;
    var ol = that.ol;

    var data = new DataLoader(visualize);
    data.loadPosData("/caraccident-portlet/js/acc_snap3k.json");



    var tlwgs = [-20037508.34, 20037508.34];

    function visualize(data) {

        WGL = new WGL(data.num, '/caraccident-portlet/js/');


        WGL.addMapDimension(data.pts, 'map');

        //var sev   = {data: data.sev,  domain: ['1','2','3'] ,  name: 'sev'  };		
        //var road_surf = {data: data.road_surf, domain:['1','2','3','4','5'], name: 'road_surface'};
        var dayes = {
            data: data.dayes,
            min: 0,
            max: 7,
            num_bins: 7,
            name: 'dayes'
        };
        var hours = {
            data: data.hours,
            min: 0,
            max: 24,
            num_bins: 24,
            name: 'hours'
        };

        //WGL.addOrdinalHistDimension(sev);
        //WGL.addOrdinalHistDimension(road_surf);	


        WGL.addLinearHistDimension(dayes);
        WGL.addLinearFilter(dayes, 7, 'dayesF');

        WGL.addLinearHistDimension(hours);
        WGL.addLinearFilter(hours, 24 * 4, 'hoursF');
        //WGL.initHistograms();

        var charts = [];
        //	charts['sev']   = new StackedBarChart(sev, "chart1", "accident servelity",'servelity');
        charts['dayes'] = new StackedBarChart(dayes, "chart2", "day of the week", 'dayes');
        charts['hours'] = new StackedBarChart(hours, "chart3", "hour of the day", 'hours');
        //charts['road_surface'] = new StackedBarChart(road_surf, "chart4", "road surface", 'road surface');
        map.getView().on('change:center', onMove, 0);
        map.getView().on('change:resolution', onMove, 0);
        map.on('moveend', onMove, 0);

        WGL.addCharts(charts);

        WGL.render();
        onMove();
        WGL.render();



    }





    var onMove = function() {
        var getTopLeftTC = function() {


            var s = Math.pow(2, map.getView().getZoom());
            //	console.log(s);
            tlpixel = map.getPixelFromCoordinate(tlwgs);
            //	console.log(tlpixel);
            res = {
                x: -tlpixel[0] / s,
                y: -tlpixel[1] / s
            }
            return res
        }

        // var timer = null;
        // if (timer != null) clearTimeout(timer);
        // timer = setTimeout(function() {
        WGL.mcontroller.zoommove(map.getView().getZoom(), getTopLeftTC());
        //	console.log(getTopLeftTC());
        //}, 1000);


    }
}
