/**
 * Created by user on 15.08.2016.
 */


$(function () {
    'use strict';

    var stompClient = null;

    function connect() {
        var socket = new SockJS('/rest_time');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/rest_time', function (restTime) {
                // console.log(restTime);
                // document.getElementById("minutes").innerHTML = result.minutesRest;
                document.getElementById("minutes").innerHTML = JSON.parse(restTime.body).minutesRest;
                document.getElementById("seconds").innerHTML = JSON.parse(restTime.body).secondsRest;

                if (JSON.parse(restTime.body).minutesRest < 0) {
                    window.location.replace("finishwork.html");
                    disconnect();
                }

            });
        });
    }

    function disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    function sendTime(minutesRest) {
        stompClient.send("/rest_time", {}, JSON.stringify({'minutesRest': minutesRest, 'secondsRest': secondsRest}));
    }

    connect();

    var app = {};


    app.Task = Backbone.Model.extend({});


    app.TaskList = Backbone.Collection.extend({
        model: app.Task


    });

    app.taskList = new app.TaskList();


    app.TaskListView = Backbone.View.extend({

        tagName: 'li',
        template: _.template($('#item-template').html()),
        render: function () {
            this.$el.html(this.template(this.model.toJSON()));

            return this; // enable chained calls
        },
        initialize: function () {
            this.model.on('change', this.render, this);
        },
        events: {}
    });


    app.AppView = Backbone.View.extend({
        el: '#tasklistapp',


        initialize: function () {
            this.name = this.$('#name');
            this.duration = this.$('#duration');
        },

        events: {
            'click #sb': 'createTaskOnButton'
        },

        doTasks: function () {
            var taskNumber = 0;

            $.ajax({
                url: '/api/workday/',
                type: 'GET',
                statusCode: {
                    200: function (result) {
                        var name = result.name;
                        var duration = result.duration;

                        document.getElementById("current-task-name").innerHTML = name;

                        // var deadline = new Date(Date.parse(new Date()) + duration * 60 * 1000);
                        // initializeClock('clockdiv', deadline);

                    },
                    406: function (result) {
                        // alert("All tasks are finished");
                        window.location.replace("finishwork.html");
                        disconnect();
                    }
                }
            });
        },

        connectThroughWebSocket: function () {

        },

        addOne: function (task) {
            var view = new app.TaskListView({model: task});
            $('#task-list').append(view.render().el);
        },

        addAll: function () {
            this.$('#task-list').html(''); // clean the task list
            $.ajax({
                url: '/api/',
                type: 'GET',
                success: function (result) {
                    var parsed = _(result).toArray();
                    for (var i in parsed) {
                        var task = new app.Task({
                            name: result[i].name,
                            duration: result[i].duration,
                            id: result[i].id,
                            position: result[i].position
                        });

                        app.appView.addOne(task);
                    }
                }
            });
            // app.taskList.each(this.addOne, this);
        },
    });


    app.appView = new app.AppView();

    app.appView.connectThroughWebSocket();
    app.appView.doTasks();

    app.appView.addAll();

});
