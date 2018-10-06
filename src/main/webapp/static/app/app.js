(function () {
    "use strict";

    angular.module('app', [
        'ng',
        'ngResource'
    ])
        .run(function ($rootScope, $compile) {

            $rootScope.appData = appData || {};

        })
        .directive('chatMessage', function () {
            return {
                templateUrl: '/chat-message.html',
                scope: {
                    message: '='
                }
            }
        })
        .controller('AppController', function ($rootScope, $scope, $http) {

            $scope.master = {
                message: '',
                interlocutorId: ''
            };

            $scope.messagesBuffer = [];

            $scope.isPolling = false;

            var isMessageBuffered = function (msgId) {
                return !!$scope.messagesBuffer.filter(function (msg) {
                    return msg.id === msgId;
                }).length;
            };

            var pollNext = function () {
                $http.get('/api/chat/' + $scope.master.interlocutorId + '/poll')
                    .then(function (r) {
                        (r.data.messages || []).filter(function (msg) {
                            return !isMessageBuffered(msg.id);
                        }).forEach(function (msg) {
                            msg.sent = $rootScope.appData.selfId === msg.sender.id;
                            $scope.messagesBuffer.push(msg);
                        });
                        pollNext();
                    })
                    .catch(function (e) {
                        if (e.status !== 404) {
                            pollNext();
                        }
                    });
            };

            $scope.startPolling = function () {
                if(!$scope.isPolling) {
                    $scope.isPolling = true;
                    pollNext();
                }
            };

            $scope.sendMessage = function () {
                if (!$scope.master.message) {
                    return;
                }
                $http({
                    url: '/api/chat/' + $scope.master.interlocutorId + '/send',
                    method: "POST",
                    data: { 'messagetext' : $scope.master.message }
                })
                    .then(function (r) {
                        if(!isMessageBuffered(r.data.message.id)) {
                            $scope.messagesBuffer.push(r.data.message);
                        }
                    });
            };

        });
})();