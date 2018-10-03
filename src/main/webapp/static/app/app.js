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
                        (r.messages || []).filter(function (msg) {
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
                $http.post('/api/chat/' + $scope.master.interlocutorId + '/send')
                    .then(function (r) {
                        r = {
                            message:
                                {
                                    id: 3,
                                    sent: true,
                                    sender: {
                                        avatarUrl: 'https://vgy.me/8zipf9.png',
                                        name: 'Я грустный кот'
                                    },
                                    sentAt: '2018-10-01T18:12:01Z',
                                    text: '!!!Хочу смеяться 15 минут!'
                                }
                        };
                        if(!isMessageBuffered(r.message.id)) {
                            $scope.messagesBuffer.push(r.message);
                        }
                    });
            };

        });
})();