/*
 * Copyright 2013-2014 the original author or authors.
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
                return 'fa-check';
            } else if ('FAIL' === state) {
                return 'fa-times';
            } else if ('IN_PROGRESS' === state) {
                return 'fa-spinner fa-spin';
            }

            return 'fa-question';
        };
    })

    .filter('stateClass', function () {
        'use strict';

        return function (state) {
            if ('good' === state || 'none' === state || 'up' === state) {
                return 'good';
            } else if ('minor' === state) {
                return 'minor';
            } else if ('critical' === state || 'down' === state || 'major' === state) {
                return 'major';
            }

            return 'unknown';
        };
    })

    .controller('ProjectsController', ['$scope', '$http', '$log', '$rootScope', '$timeout', 'SockJS', 'Stomp',
        function ($scope, $http, $log, $rootScope, $timeout, SockJS, Stomp) {
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

                        $scope.$apply(function () {
                            $rootScope.connectionState = 'up';
                        });

                        $scope.projectsSubscription = $scope.stompClient.subscribe('/app/projects', function (message) {
                            $scope.$apply(function () {
                                $scope.projects = JSON.parse(message.body);
                            });
                        });

                        $http.get('/projects').success(function (projects) {
                            $scope.projects = projects;
                        });

                    }, function () {

                        $scope.$apply(function () {
                            $rootScope.connectionState = 'down';
                        });

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

        $scope.$on('$destroy', function () {
            $timeout.cancel($scope.timeout);
        });

        function refresh() {
            $scope.timeout = $timeout(function () {
                $scope.$digest();
            }, DELAY_IN_SECONDS * 1000).then(refresh);
        }

        refresh();

    }])

    .controller('BuildController', ['$scope', function ($scope) {
        'use strict';

        var MINIMUM_OPACITY = 0.3;

        $scope.fade = function (index) {
            var increment = (1 - MINIMUM_OPACITY) / $scope.builds.length;
            var opacity = 1 - (index * increment);

            return {'opacity': opacity };
        };

    }])

    .controller('GitHubStatusController', ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
        'use strict';

        var DELAY_IN_MINUTES = 5;

        var GITHUB_STATUS_URI = 'https://status.github.com/api/status.json?callback=JSON_CALLBACK';

        function getState() {
            $http.jsonp(GITHUB_STATUS_URI).success(function (payload) {
                $scope.state = payload.status;
            });
        }

        $scope.$on('$destroy', function () {
            $timeout.cancel($scope.timeout);
        });

        function refresh() {
            $scope.timeout = $timeout(getState, DELAY_IN_MINUTES * 60 * 1000).then(refresh);
        }

        refresh();
        getState();

    }])

    .controller('TravisCIStatusController', ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
        'use strict';

        var DELAY_IN_MINUTES = 5;

        var TRAVIS_STATUS_URI = 'http://status.travis-ci.com/index.json';

        function getState() {
            $http.get(TRAVIS_STATUS_URI).success(function (payload) {
                $scope.state = payload.status.indicator;
            });
        }

        $scope.$on('$destroy', function () {
            $timeout.cancel($scope.timeout);
        });

        function refresh() {
            $scope.timeout = $timeout(getState, DELAY_IN_MINUTES * 60 * 1000).then(refresh);
        }

        refresh();
        getState();

    }])

    .controller('PivotalWebServicesStatusController', ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
        'use strict';

        var DELAY_IN_MINUTES = 5;

        var PWS_STATUS_URI = 'http://status.run.pivotal.io/index.json';

        function getState() {
            $http.get(PWS_STATUS_URI).success(function (payload) {
                $scope.state = payload.status.indicator;
            });
        }

        $scope.$on('$destroy', function () {
            $timeout.cancel($scope.timeout);
        });

        function refresh() {
            $scope.timeout = $timeout(getState, DELAY_IN_MINUTES * 60 * 1000).then(refresh);
        }

        refresh();
        getState();

    }]);
