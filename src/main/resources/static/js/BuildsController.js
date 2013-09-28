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

angular.module('BuildMonitor')
    .controller('BuildsController', ['$scope', '$http', '_', function ($scope, $http, _) {
        'use strict';

        function getLink(entity, rel) {
            return _.find(entity.links, { 'rel': rel }).href;
        }

        $http.get(getLink($scope.project, 'builds')).success(function (page) {
            $scope.builds = page.content;
            $scope.$emit('build', $scope.builds[0]);
        });

    }])

    .controller('LastController', ['$scope', '$window', function ($scope, $window) {
        'use strict';

        $window.setInterval(function () {
            $scope.$digest();
        }, 60 * 1000);

    }]);

