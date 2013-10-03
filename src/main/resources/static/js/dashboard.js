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

angular.module('dashboard', ['ng', 'links', 'moment', 'sockjs', 'stomp'])

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

    .controller('ProjectsController', ['$scope', '$http', '$log', '$timeout', 'SockJS', 'Stomp',
        function ($scope, $http, $log, $timeout, SockJS, Stomp) {
            'use strict';

            var DELAY_IN_SECONDS = 60;

            function createSocket() {
                $scope.socket = $scope.socket = new SockJS('/stomp');
            }

            $scope.$on('$destroy', function () {
                $scope.projectsSubscription.unsubscribe();
                $scope.stompClient.disconnect();
            });

            $scope.$watch('stompClient', function (stompClient) {
                if (stompClient) {
                    stompClient.connect(undefined, undefined, function () {

                        $scope.projectsSubscription = $scope.stompClient.subscribe('/app/projects', function (message) {
                            $scope.$apply(function () {
                                $scope.projects = JSON.parse(message.body);
                            });
                        });

                        $http.get('/projects').success(function (projects) {
                            $scope.projects = projects;
                        });

                    }, function () {
                        $log.warn('Connection lost.  Reconnecting in ' + DELAY_IN_SECONDS + ' seconds');
                        $timeout(createSocket, DELAY_IN_SECONDS * 1000);
                    });
                }
            });

            $scope.$watch('socket', function (socket) {
                if (socket) {
                    $scope.stompClient = Stomp.over(socket);
                    $scope.stompClient.debug = undefined;
                }
            });

            createSocket();

        }])

    .controller('ProjectController', ['$scope', function ($scope) {
        'use strict';

        $scope.$on('build', function (event, build) {
            $scope.state = build ? build.state : 'UNKNOWN';
        });

    }])

    .controller('BuildsController', ['$scope', '$http', 'links', function ($scope, $http, links) {
        'use strict';

        function getDestination() {
            return '/app/projects/' + $scope.project.key + '/builds';
        }

        $scope.$on('$destroy', function () {
            $scope.buildSubscription.unsubscribe();
        });

        $scope.$watch('builds', function (builds) {
            if (builds) {
                $scope.$emit('build', builds[0]);
            }
        });

        $scope.buildSubscription = $scope.stompClient.subscribe(getDestination(), function (message) {
            $scope.$apply(function () {
                $scope.builds = JSON.parse(message.body);
            });
        });

        $http.get(links.getLink($scope.project, 'builds')).success(function (builds) {
            if (builds) {
                $scope.builds = builds.content;
            }
        });

    }])

    .controller('LastBuildController', ['$scope', '$timeout', function ($scope, $timeout) {
        'use strict';

        var DELAY_IN_SECONDS = 60;

        function refresh() {
            $scope.timeout = $timeout(function () {
                $scope.$digest();
            }, DELAY_IN_SECONDS * 1000).then(refresh);
        }

        $scope.$on('$destroy', function () {
            $timeout.cancel($scope.timeout);
        });

        refresh();

    }]);