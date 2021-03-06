/**
 * Created by user on 15.08.2016.
 */


$(function () {
    'use strict';

    var app = {};


    app.Task = Backbone.Model.extend({});


    app.TaskList = Backbone.Collection.extend({
        model: app.Task,


    });

    app.taskList = new app.TaskList();


    app.TaskListView = Backbone.View.extend({

        tagName: 'li',

        render: function () {

            return this; // enable chained calls
        },
        initialize: function () {
            this.model.on('change', this.render, this);
            this.model.on('remove', this.remove, this); // remove: Convenience Backbone's function for removing the view from the DOM.
        },
        events: {}

    });


    app.AppView = Backbone.View.extend({
        el: '#tasklistapp',

        initialize: function () {
            this.name = this.$('#name');
            this.duration = this.$('#duration');
            $.ajax({
                url: '/api/workday/delete-completed-tasks',
                type: 'POST',
                success: function (result) {
                    document.getElementById("commonTaskDuration").innerHTML = result.commonTaskDuration;
                    document.getElementById("numberOfCompletedTasks").innerHTML = result.numberOfCompletedTasks;
                    document.getElementById("startHour").innerHTML = result.startHour;
                    document.getElementById("startMinute").innerHTML = result.startMinute;
                }
            });
        },

        events: {
            'click #getstatistics': 'getStatistics'
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
                            position: result[i].position,
                            completed: result[i].completed
                        });

                        app.appView.addOne(task);
                    }
                }
            });
            // app.taskList.each(this.addOne, this);
        },
        newAttributes: function () {
            return {
                name: this.name.val().trim(),
                duration: this.duration.val().trim()
            }
        }
    });

    app.appView = new app.AppView();

    app.appView.addAll();


});