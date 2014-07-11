'use strict';

/* Services */

angular.module('hp.services', [])
    .value('version', '0.1')

    .factory('events', [
        function() {
            // TODO: remove hard coding
            var wsUri = "ws://localhost:8080/clients";

            return {
                start: function(listener) {

                    var parser = {
                        addJvm: function(msg) {
                            listener.add(msg.machine);
                        },
                        removeJvm: function(msg) {
                            listener.remove(msg.id);
                        },
                        newProfile: function(msg) {
                            listener.newProfile(msg.id, msg.profile);
                        }
                    };

                    var webSocket = new WebSocket(wsUri);
                    webSocket.onopen = function(event) {
                        console.log(event);
                    };
                    webSocket.onclose = function(event) {
                        console.log(event);
                    };
                    webSocket.onmessage = function(event) {
                        console.log(event.data);
                        var msg = JSON.parse(event.data);
                        parser[msg.type](msg);
                    };
                }
            };
        }
    ])

    .factory('virtualMachines', [ 'events', '$rootScope',
        function(events, $rootScope) {

            var machineList = [];

            function notify() {
                // TODO: force re-rendering in a less inefficient manner
                if(!$rootScope.$$phase) {
                    $rootScope.$apply();
                }
            }

            var virtualMachines = {
                add: function(machine) {
                    machineList.push(machine);
                    notify();
                },

                remove: function(id) {
                    machineList = _.reject(machineList, function(machine) {
                        return machine.id === id;
                    });
                    notify();
                },

                newProfile: function(id, profile) {
                    _.each(machineList, function(machine) {
                        if (machine.id === id) {
                            machine.profile = profile;
                        }
                    });
                    notify();
                },

                list: function() {
                    return machineList;
                }
            };

            events.start(virtualMachines);

            return virtualMachines;
        }
    ]);
