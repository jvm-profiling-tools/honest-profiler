'use strict';

/* Controllers */

angular.module('hp.controllers', [])
    .controller('HomeCtrl', ['$scope', 'virtualMachines',
        function($scope, virtualMachines) {

            virtualMachines.add({
                id: "0",
                name: "Donnellan",
            });

            virtualMachines.add({
                id: "1",
                name: "Mendell"
            });

            virtualMachines.newProfile("0", {
               timeShare: 1.0,
               method: {
                   className: "Foo",
                   methodName: "bar"
               },
               children: [
                   {
                       timeShare: 0.5,
                       method: {
                           className: "Foo",
                           methodName: "baz"
                       },
                       children: []
                   },
                   {
                       timeShare: 0.5,
                       method: {
                           className: "Foo",
                           methodName: "bin"
                       },
                       children: []
                   }
               ]
            });

            $scope.machines = virtualMachines.list();
        }
    ]);
