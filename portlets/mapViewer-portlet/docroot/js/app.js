'use strict';

define(['angular', 'ol', 'core', 'api', 'sidebar', 'toolbar', 'layermanager', 'map', 'query', 'search', 'permalink', 'measure', 'datasource_selector', 'angular-gettext', 'translations', 'compositions'],

    function(angular, ol, toolbar, layermanager, WfsSource) {
        var module = angular.module('hs', [
            'hs.sidebar',
            'hs.toolbar',
            'hs.layermanager',
            'hs.map',
            'hs.query',
            'hs.search',
            'hs.permalink',
            'hs.core',
            'hs.api',
            'gettext',
            'hs.compositions'
        ]);

        module.directive('hs', ['hs.map.service', 'Core', function(OlMap, Core) {
            return {
                templateUrl: hsl_path + '/hslayers.html',
                link: function(scope, element) {
                    var w = angular.element($(window));
                    w.bind('resize', function() {
                        $("html").css('overflow', 'hidden');
                        var el = $(element[0]);
                        var windowHeight = $(window).height();
                        var headerHeight = $('.portlet-dockbar').outerHeight();
                        var elementHeight = windowHeight - headerHeight;
                        el.height(elementHeight);
                        $("#map").height(elementHeight);
                        $("#panelplace").height(elementHeight);
                        Core.updateMapSize();
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
                center: ol.proj.transform([OTNcenterX, OTNcenterY], 'EPSG:4326', 'EPSG:3857'), //Latitude longitude    to Spherical Mercator
                zoom: OTNzoom,
                units: "m"
            }),
            datasources: [{
                   title: "CKAN",
                   url: "/otnServices-1.0/platform/ckanservices/datasets",
                   language: 'eng',
                   type: "ckan"
               }, {
                   title: "Micka",
                   url: "/php/metadata/csw/",
                   language: 'eng',
                   type: "micka",
                   code_list_url: '/php/metadata/util/codelists.php?_dc=1440156028103&language=eng&page=1&start=0&limit=25&filter=%5B%7B%22property%22%3A%22label%22%7D%5D'
               }
            ],
            'catalogue_url': caturl,
            'compositions_catalogue_url': caturl,
            status_manager_url: '/wwwlibs/statusmanager2/index.php',
        });

        module.controller('Main', ['$scope', 'Core', 'hs.query.service_infopanel', 'hs.compositions.service_parser',
            function($scope, Core, InfoPanelService, composition_parser) {
                if (console) console.log("Main called");
                $scope.hsl_path = hsl_path; //Get this from hslayers.js file
                $scope.Core = Core;
                Core.setMainPanel('composition_browser');
                Core.sidebarRight=false;
                $scope.$on('infopanel.updated', function(event) {
                    if (console) console.log('Attributes', InfoPanelService.attributes, 'Groups', InfoPanelService.groups);
                });
            }
        ]);

        return module;
    });
