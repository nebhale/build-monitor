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

angular.module('moment', [])
    .factory('moment', ['$window', function ($window) {
        'use strict';

        $window.moment.lang('en', { relativeTime: { future: 'in %s', past: '%s ago', s: '%ds', m: '%dm', mm: '%dm', h: '%dh',
            hh: '%dh', d: '%dd', dd: '%dd', M: '%dmo', MM: '%dmo', y: '%dy', yy: '%dy' } });

        return $window.moment;
    }])

    .filter('moment', ['moment', function (moment) {
        'use strict';

        return function (input) {
            if (input) {
                return moment(input).fromNow(true);
            }

            return '';
        };
    }]);

angular.module('sockjs', [])
    .factory('SockJS', ['$window', function ($window) {
        'use strict';

        return $window.SockJS;
    }]);

angular.module('stomp', [])
    .factory('Stomp', ['$window', function ($window) {
        'use strict';

        return $window.Stomp;
    }]);

angular.module('underscore', [])
    .factory('_', ['$window', function ($window) {
        'use strict';

        return $window._;
    }]);

angular.module('links', ['underscore'])
    .factory('links', ['_', function (_) {
        'use strict';

        return {
            getLink: function (entity, rel) {
                return _.find(entity.links, { 'rel': rel }).href.replace(/http:/, 'https:'); // TODO: Remove replace once Cloud Foundry uses x-forwarded-for properly
            }
        };
    }]);
