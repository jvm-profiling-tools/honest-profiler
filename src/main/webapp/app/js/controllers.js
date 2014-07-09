'use strict';

/* Controllers */

angular.module('hp.controllers', [])
    .controller('HomeCtrl', ['$scope', 'virtualMachines',
        function($scope, virtualMachines) {
            $scope.machines = virtualMachines.list();
        }
    ]);
