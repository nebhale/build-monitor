/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*global angular:false*/

angular.module('dashboard', ['ng', 'links', 'moment', 'stomp'])

    .filter('stateIcon', function () {
        'use strict';

        return function (state) {
            if ('PASS' === state) {
                return 'icon-ok';
            } else if ('FAIL' === state) {
                return 'icon-remove';
            } else if ('IN_PROGRESS' === state) {
                return 'icon-spinner icon-spin';
            }

            return 'icon-question';
        };
    })

    .controller('ProjectsController', ['$scope', '$http', '$window', 'Stomp', function ($scope, $http, $window, Stomp) {
        'use strict';

        function getWebSocketScheme(window) {
            return window.location.protocol === 'https:' ? 'wss' : 'ws';
        }

        function getWebSocketUri(window) {
            return getWebSocketScheme(window) + '://' + window.location.host + '/websocket';
        }

        $scope.stompClient = Stomp.client(getWebSocketUri($window));

        $scope.stompClient.connect(null, null, function () {

            $scope.projectsSubscription = $scope.stompClient.subscribe('/app/projects', function (message) {
                $scope.$apply(function () {
                    $scope.projects = JSON.parse(message.body);
                });
            });

            $scope.$on('$destroy', function () {
                $scope.projectsSubscription.unsubscribe();
                $scope.stompClient.disconnect();
            });

            $http.get('/projects').success(function (projects) {
                $scope.projects = projects;
            });

        });

    }])

    .controller('ProjectController', ['$scope', function ($scope) {
        'use strict';

        $scope.$on('build', function (event, build) {
            $scope.state = build ? build.state : 'UNKNOWN';
        });

    }])

    .controller('BuildsController', ['$scope', '$http', 'links', function ($scope, $http, links) {
        'use strict';

        $scope.$watch('builds', function (builds) {
            if (builds) {
                $scope.$emit('build', builds[0]);
            }
        });

        var buildsDestination = '/app/projects/' + $scope.project.key + '/builds';
        $scope.buildSubscription = $scope.stompClient.subscribe(buildsDestination, function (message) {
            $scope.$apply(function () {
                $scope.builds = JSON.parse(message.body);
            });
        });

        $scope.$on('$destroy', function () {
            $scope.buildSubscription.unsubscribe();
        });

        $http.get(links.getLink($scope.project, 'builds')).success(function (builds) {
            if (builds) {
                $scope.builds = builds.content;
            }
        });

    }])

    .controller('LastBuildController', ['$scope', '$window', function ($scope, $window) {
        'use strict';

        $window.setInterval(function () {
            $scope.$digest();
        }, 60 * 1000);

    }]);