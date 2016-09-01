/**
 * Created by user on 15.08.2016.
 */


$(function () {
    'use strict';

    var app = {};


    app.Task = Backbone.Model.extend({});


    app.TaskList = Backbone.Collection.extend({
        model: app.Task,

        create: function (attributes) {
            var queryParameters = $.param(attributes);

            $.ajax({
                url: '/api/?' + queryParameters,
                type: 'PUT',
                // data: test,
                statusCode: {
                    200: function (result) {
                        var taskId = result.id;
                        var taskPosition = result.position;
                        var task = new app.Task({
                            name: attributes.name,
                            duration: attributes.duration,
                            id: taskId,
                            position: taskPosition
                        });
                        app.appView.addOne(task);
                    },
                    406: function (result) {
                        alert("Such parameters are not allowed or you are trying to create a task during Workday");
                    }
                }
            });
        }
    });

    app.taskList = new app.TaskList();


    app.TaskListView = Backbone.View.extend({

        tagName: 'li',
        template: _.template($('#item-template').html()),
        render: function () {
            this.$el.html(this.template(this.model.toJSON()));
            this.name = this.$('.edit')[0];
            this.duration = this.$('.edit')[1];
            return this; // enable chained calls
        },
        initialize: function () {
            this.model.on('change', this.render, this);
            this.model.on('remove', this.remove, this); // remove: Convenience Backbone's function for removing the view from the DOM.
        },
        events: {
            'dblclick label': 'edit',
            'keypress .edit': 'updateOnEnter',
            'click .remove': 'remove',
            'click .sendToArchive': 'sendToArchive',
            'click .up': 'up',
            'click .down': 'down'
        },
        edit: function () {
            this.$el.addClass('editing');
        },
        close: function () {

            if (this.name.value && this.duration.value) {
                var id = this.model.id;

                var queryParameters = $.param({
                    name: this.name.value,
                    duration: this.duration.value
                });

                $.ajax({
                    url: '/api/edit-' + id + "?" + queryParameters,
                    type: 'POST',
                    statusCode: {
                        200: function (result) {

                            app.appView.addAll();
                        },
                        406: function (result) {
                            alert("Such parameters are not allowed or you are trying to create a task during Workday");

                            app.appView.addAll();
                        }
                    }
                });
            }
            this.$el.removeClass('editing');
        },

        updateOnEnter: function (e) {
            if (e.which == 13) {
                this.close();
            }
        },
        remove: function () {
            var id = this.model.id;

            $.ajax({
                url: '/api/' + id,
                type: 'DELETE',
                statusCode: {
                    200: function (result) {

                        app.appView.addAll();
                    },
                    406: function (result) {
                        alert("Deleting tasks is impossible during Workday");

                        app.appView.addAll();
                    }
                }
            });
        },

        sendToArchive: function () {
            var id = this.model.id;

            $.ajax({
                url: '/api/send_to_archive-' + id,
                type: 'POST',
                statusCode: {
                    200: function (result) {

                        app.appView.addAll();
                    },
                    406: function (result) {
                        alert("Sending tasks to Archive is impossible during Workday");

                        app.appView.addAll();
                    }
                }
            });
        },

        up: function () {
            var id = this.model.id;

            $.ajax({
                url: '/api/move-up-' + id,
                type: 'POST',
                statusCode: {
                    200: function (result) {

                        app.appView.addAll();
                    },
                    406: function (result) {
                        alert("Moving tasks  is impossible during Workday");

                        app.appView.addAll();
                    }
                }
            });
        },

        down: function () {
            var id = this.model.id;

            $.ajax({
                url: '/api/move-down-' + id,
                type: 'POST',
                statusCode: {
                    200: function (result) {

                        app.appView.addAll();
                    },
                    406: function (result) {
                        alert("Moving tasks  is impossible during Workday");

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
            // app.taskList.bind('add', this.addOne, this);
            // app.taskList.bind('reset', this.addAll, this);
        },

        events: {
            'click #sb': 'createTaskOnButton',
            'click #startwork': 'startWork',
            'keypress #duration': 'createTaskOnEnter'
        },

        createTaskOnButton: function () {
            app.taskList.create(this.newAttributes());
            this.name.val('');
            this.duration.val('');
            this.name.focus();
        },

        createTaskOnEnter: function (e) {
            if (e.which == 13) {
                app.taskList.create(this.newAttributes());
                this.name.val('');
                this.duration.val('');
                this.name.focus();
            }
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
        newAttributes: function () {
            return {
                name: this.name.val().trim(),
                duration: this.duration.val().trim()
            }
        },

        startWork: function () {
            $.ajax({
                url: '/api/start_work',
                type: 'POST',
                statusCode: {
                    200: function (result) {
                        window.location.replace("workday.html");
                        app.appView.addAll();
                    },
                    406: function (result) {
                        alert("There are no tasks in Tasklist");

                        app.appView.addAll();
                    }
                }
            });
        },
    });

    app.appView = new app.AppView();

    app.appView.addAll();

});