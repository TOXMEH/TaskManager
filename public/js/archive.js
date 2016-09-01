/**
 * Created by user on 15.08.2016.
 */


$(function () {
    'use strict';

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
            this.model.on('remove', this.remove, this); // remove: Convenience Backbone's function for removing the view from the DOM.
        },
        events: {
            'click .remove': 'remove',
            'click .returnToTaskList': 'returnToTaskList'
        },

        remove: function () {
            var id = this.model.id;

            $.ajax({
                url: '/api/archive/' + id,
                type: 'DELETE'
            });

            app.appView.addAll();
        },

        returnToTaskList: function () {
            var id = this.model.id;

            $.ajax({
                url: '/api/archive/return_from_archive-' + id,
                type: 'POST',
                statusCode: {
                    200: function (result) {
                        app.appView.addAll();
                    },
                    406: function (result) {
                        alert("Returning tasks to TaskLIst is impossible during Workday");

                        app.appView.addAll();
                    }
                }
            });
        }
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

        createTaskOnButton: function () {
            app.taskList.create(this.newAttributes());
            this.name.val('');
            this.duration.val('');
        },

        addOne: function (task) {
            var view = new app.TaskListView({model: task});
            $('#task-list').append(view.render().el);
        },
        addAll: function () {
            this.$('#task-list').html(''); // clean the task list
            $.ajax({
                url: '/api/archive/',
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