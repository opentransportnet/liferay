'use strict';

define(['ol', 'core', 'api', 'toolbar', 'layermanager', 'map', 'query', 'search', 'permalink', 'measure', 'datasource_selector', 'ows', 'WfsSource', 'angular-gettext', 'translations', 'compositions', 'status_creator'],

    function(ol, toolbar, layermanager, WfsSource) {
        var module = angular.module('hs', [
            'hs.toolbar',
            'hs.layermanager',
            'hs.map',
            'hs.query',
            'hs.search', 'hs.permalink', 'hs.measure',
            'hs.core',
            'hs.datasource_selector',
            'hs.status_creator',
            'hs.api',
            'hs.ows',
            'gettext',
            'hs.compositions'
        ]);

        module.directive('hs', ['hs.map.service', 'Core', function(OlMap, Core) {
            return {
                templateUrl: hsl_path + 'hslayers.html',
                link: function(scope, element) {
                        var w = angular.element($(window));
                        w.bind('resize', function() {
                            var el = $(element[0]);
                            var windowHeight = $(window).height();
                            var headerHeight = $('.portlet-dockbar').outerHeight() + $('.breadcrumbs').outerHeight() + $('#header').outerHeight() + 10;
                            var elementHeight = windowHeight - headerHeight;
                            el.height(elementHeight);
                            $("#map").height(elementHeight);
                            OlMap.map.updateSize();
                        });
                        w.resize();
                }
            };
        }]);

        module.value('config', {
            default_layers: [
                new ol.layer.Tile({
                    source: new ol.source.OSM({
                        wrapX: false
                    }),
                    title: "Base layer",
                    base: true,
                    removable: false
                })
            ],
            project_name: 'otn/map',
            default_view: new ol.View({
                center: ol.proj.transform([17.474129, 52.574000], 'EPSG:4326', 'EPSG:3857'), //Latitude longitude    to Spherical Mercator
                zoom: 5,
                units: "m"
            }),
            'catalogue_url': caturl,
            'compositions_catalogue_url': caturl
        });

        module.controller('Main', ['$scope', 'Core', 'hs.query.service_infopanel', 'hs.compositions.service_parser',
            function($scope, Core, InfoPanelService, composition_parser) {
                if (console) console.log("Main called");
                $scope.hsl_path = hsl_path; //Get this from hslayers.js file
                $scope.Core = Core;
                Core.setMainPanel('composition_browser');
                $scope.$on('infopanel.updated', function(event) {
                    if (console) console.log('Attributes', InfoPanelService.attributes, 'Groups', InfoPanelService.groups);
                });
            }
        ]);

        return module;
    });
