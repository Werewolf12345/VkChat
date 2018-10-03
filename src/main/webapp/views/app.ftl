<#ftl encoding="UTF-8"/>
<html>

    <head>
        <meta charset="UTF-8">
        <title>VkChat</title>
        <link rel="stylesheet" href="/static/css/lib/bootstrap.min.css">
        <link rel="stylesheet" href="/static/css/style.css">
        <script>
            var appData = ${appData};
        </script>
        <script type="text/javascript" src="/static/js/lib/bootstrap.min.js"></script>
        <script type="text/javascript" src="/static/js/lib/angular.min.js"></script>
        <script type="text/javascript" src="/static/js/lib/angular-resource.min.js"></script>
        <script type="text/javascript" src="/static/app/app.js"></script>
    </head>

    <body data-ng-app="app" style="background: #ebebeb">

        <div class="row" data-ng-controller="AppController">
            <div class="col-md-4 offset-md-4 card mt-3">
                <h2 class="text-center m-3">Chat</h2>
                <div class="row p-4">
                    <input type="text" ng-model="master.interlocutorId" class="form-control col-md-8" placeholder="Interlocutor VK ID">
                    <button class="btn btn-success col-md-3 offset-md-1" ng-disabled="!master.interlocutorId || isPolling"
                            data-ng-click="startPolling()">
                        Start polling
                    </button>
                </div>
                <div class="chat">
                    <div class="chat-messages">
                        <chat-message message="msg" data-ng-repeat="msg in messagesBuffer"></chat-message>
                    </div>
                    <div class="send-wrap row p-4">
                        <textarea class="form-control col-md-9" ng-model="master.message" rows="3"></textarea>
                        <button class="btn btn-primary col-md-2 offset-md-1" data-ng-click="sendMessage()">Send</button>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/ng-template" id="/chat-message.html">
            <div data-ng-class="message.sent ? 'text-right' : 'text-left'">
                <div class="message card d-inline-block m-2 p-3"
                     data-ng-class="message.sent ? 'bg-light' : 'bg-info'">
                    <div class="card-title text-left">
                        <img class="circle-avatar" data-ng-src="{{message.sender.avatarUrl}}">
                        <div class="d-inline-block pl-1">
                            <h6 class="m-0" ng-bind="message.sender.name" data-ng-class="message.sent ? '' : 'text-white'"></h6>
                            <small class="d-block" data-ng-class="message.sent ? 'text-muted' : 'text-white'">
                                {{message.sentAt | date:'dd.MM.yyyy HH:mm:ss'}}
                            </small>
                        </div>
                    </div>
                    <p class="message-text text-left" data-ng-class="message.sent ? '' : 'text-white'" ng-bind="message.text"></p>
                </div>
            </div>
        </script>
    </body>
</html>