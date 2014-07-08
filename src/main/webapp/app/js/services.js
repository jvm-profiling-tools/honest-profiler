'use strict';

/* Services */

angular.module('hp.services', [])
    .value('version', '0.1')

    .factory('virtualMachines', [
        function() {

            var machineList = [];

            var virtualMachines = {
                add: function(machine) {
                    machineList.push(machine);
                },

                remove: function(id) {
                    machineList = _.reject(machineList, function(machine) {
                        return machine.id === id;
                    });
                },

                newProfile: function(id, profile) {
                    _.each(machineList, function(machine) {
                        if (machine.id === id) {
                            machine.profile = profile;
                        }
                    });
                },

                list: function() {
                    return machineList;
                }
            };

            return virtualMachines;
        }
    ]);
