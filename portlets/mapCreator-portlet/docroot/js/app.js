'use strict';

define(['angular', 'ol', 'toolbar', 'layermanager', 'sidebar', 'map', 'ows', 'query', 'search', 'permalink', 'measure', 'legend', 'bootstrap', 'geolocation', 'core', 'datasource_selector', 'api', 'angular-gettext', 'translations', 'compositions', 'status_creator', 'info', 'ngcookies'],
    function(angular, ol, toolbar, layermanager) {
        var module = angular.module('hs', [
            'hs.sidebar',
            'hs.toolbar',
            'hs.layermanager',
            'hs.map',
            'hs.query',
            'hs.search', 'hs.permalink', 'hs.measure',
            'hs.geolocation', 'hs.core',
            'hs.datasource_selector',
            'hs.status_creator',
            'hs.api',
            'hs.ows',
            'gettext',
            'hs.compositions',
            'ngCookies',
            'hs.info'
        ]);

        module.directive('hs', ['hs.map.service', 'Core', function(OlMap, Core) {
            return {
                templateUrl: '/mapCreator-portlet/html/hslayers.html',
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
                    source: new ol.source.OSM(),
                    title: "Topographic",
                    base: true,
                    visible: true,
                    removable: false
                }),
                new ol.layer.Tile({
                    title: "Satellite",
                    base: true,
                    visible: false,
                    source: new ol.source.XYZ({
                        url: 'http://api.tiles.mapbox.com/v4/mapbox.streets-satellite/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoicmFpdGlzYmUiLCJhIjoiY2lrNzRtbGZnMDA2bXZya3Nsb2Z4ZGZ2MiJ9.g1T5zK-bukSbJsOypONL9g'
                    })
                })
            ],
            project_name: 'otn/map',
            default_view: new ol.View({
                center: ol.proj.transform([OTNcenterX, OTNcenterY], 'EPSG:4326', 'EPSG:3857'), //Latitude longitude    to Spherical Mercator
                zoom: OTNzoom,
                units: "m"
            }),
            dsPaging: OTNdsPaging,
            hostname: {
                "default": {
                    "title": "Default",
                    "type": "default",
                    "editable": false,
                    "url": getHostname()
                }
            },
            datasources: [{
                title: "Hub layers",
                url: "/php/metadata/csw/",
                language: 'eng',
                type: "micka",
                code_list_url: '/php/metadata/util/codelists.php?_dc=1440156028103&language=eng&page=1&start=0&limit=25&filter=%5B%7B%22property%22%3A%22label%22%7D%5D'
            }],
            'catalogue_url': caturl || "/php/metadata/csw",
            'compositions_catalogue_url': caturl  || "/php/metadata/csw",
            status_manager_url: '/wwwlibs/statusmanager2/index.php'
        });

        function getHostname(){
            var url = window.location.href
            var urlArr = url.split("/");
            var otnDomains = ['liferay.local', 'opentnet.eu', 'www.opentnet.eu', 'www.opentransportnet.eu', 'opentransportnet.eu', 'otn-production.intrasoft-intl.com', '138.201.51.26'];
            var domain = urlArr[2];
            if ($.inArray(domain, otnDomains) > -1) {
                return urlArr[0] + "//" + domain;
            } else {
                return 'http://opentnet.eu';
            }
        };

        module.controller('Main', ['$scope', 'Core', 'hs.query.service_infopanel', 'hs.compositions.service_parser', 'config', '$cookies',
            function($scope, Core, InfoPanelService, composition_parser, config, $cookies) {
                if (console) console.log("Main called");
                $scope.hsl_path = hsl_path; //Get this from hslayers.js file
                $scope.Core = Core;
                Core.sidebarRight = false;
                Core.singleDatasources = true;
                hslayers_api.gui.setLanguage($cookies.get('GUEST_LANGUAGE_ID'));
                $scope.$on('infopanel.updated', function(event) {
                    if (console) console.log('Attributes', InfoPanelService.attributes, 'Groups', InfoPanelService.groups);
                });
            }
        ]);

        return module;
    });
