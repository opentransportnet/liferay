'use strict';

var hsl_path = '/wwwlibs/hslayers-ng/';
var jans_path = 'http://home.zcu.cz/~jezekjan/webglayer-snapshot/js/'; //http://localhost:9999/js/webglayer/js/

require.config({
    paths: {
        toolbar: hsl_path + 'components/toolbar/toolbar',
        layermanager: hsl_path + 'components/layermanager/layermanager',
        map: hsl_path + 'components/map/map',
        ows: hsl_path + 'components/ows/ows',
        'ows.wms': hsl_path + 'components/ows/ows_wms',
        'ows.nonwms': hsl_path + 'components/ows/ows_nonwms',
        'ows.wmsprioritized': hsl_path + 'components/ows/ows_wmsprioritized',
        query: hsl_path + 'components/query/query',
        search: hsl_path + 'components/search/search',
        print: hsl_path + 'components/print/print',
        permalink: hsl_path + 'components/permalink/permalink',
        lodexplorer: hsl_path + 'components/lodexplorer/lodexplorer',
        geolocation: hsl_path + 'components/geolocation/geolocation',
        measure: hsl_path + 'components/measure/measure',
        legend: hsl_path + 'components/legend/legend',
        app: '/caraccident-portlet/js/app',
        xml2json: hsl_path + 'lib/xml2json.min',
        panoramio: hsl_path + 'components/panoramio/panoramio',
        core: hsl_path + 'components/core/core',
        WfsSource: hsl_path + 'extensions/hs.source.Wfs',
        api: hsl_path + 'components/api/api',
        translations: hsl_path + 'components/translations/js/translations',
        dimension: jans_path + '/Dimension',
        glutils: jans_path + 'GLUtils',
        manager: jans_path + 'Manager',
        mapcontroller: jans_path + 'MapController',
        heatmapdimension: jans_path + 'HeatMapDimension',
        mapdimension: jans_path + 'MapDimension',
        stackedbarchart: jans_path + 'StackedBarChart',
        histogramdimension: jans_path + 'HistDimension',
        floatrasterreader: jans_path + 'FloatRasterReader',
        floatreaderhistogram: jans_path + 'FloatReaderHistogram',
        heatmaprenderer: jans_path + 'HeatMapRenderer',
        heatmaplegend: jans_path + 'HeatMapLegend',
        maxcalculator: jans_path + 'MaxCalculator',
        linearfilter: jans_path + 'LinearFilter',
        extentfilter: jans_path + 'ExtentFilter',
        mappolyfilter: jans_path + 'MapPolyFilter',
        multibrush: jans_path + 'd3.svg.multibrush',
        WGL: jans_path + 'WGL',
        filter: jans_path + 'Filter',
        dataloader: '/caraccident-portlet/js/DataLoader',
        ol: hsl_path + 'lib/ol3/ol-full',
        wglinit: '/caraccident-portlet/js/webglinit',
        chart_panel: hsl_path + 'examples/webgl_viz/chart_panel/chart_panel',


    },
    shim: {
        d3: {
            exports: 'd3'
        },
        multibrush: {
            deps: ['d3']
        }
    }

});

//http://code.angularjs.org/1.2.1/docs/guide/bootstrap#overview_deferred-bootstrap
window.name = "NG_DEFER_BOOTSTRAP!";

require(['core'], function(app) {

    require(['app'], function(app) {
        var $html = angular.element(document.getElementsByTagName('html')[0]);
        angular.element().ready(function() {
            angular.resumeBootstrap([app['name']]);
        });
    });
});
