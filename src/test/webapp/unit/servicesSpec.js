'use strict';

/* jasmine specs for services go here */

describe('service', function() {
  beforeEach(module('hp.services'));

    var machine = {
        id: "1",
        name: "Mendell"
    };

    var profile = {
        "traceCount":81,
        "trees":[{
            "rootNode": {
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
            }
        }]
    };

    var machineWithProfile = {
        id: "1",
        name: "Mendell",
        profile: profile
    };

  describe('virtualMachines', function() {
    it('should initially have no machines', inject(function(virtualMachines) {
        expect(virtualMachines.list()).toEqual([]);
    }));

    it('should add machines to its list', inject(function(virtualMachines) {
        virtualMachines.add(machine);

        expect(virtualMachines.list()).toEqual([machine]);
    }));

    it('should be able to remove machines', inject(function(virtualMachines) {
        virtualMachines.add(machine);
        virtualMachines.remove(machine.id);

        expect(virtualMachines.list()).toEqual([]);
    }));

    it('should be able to add profiles', inject(function(virtualMachines) {

        virtualMachines.add(machine);
        virtualMachines.newProfile(machine.id, profile);

        expect(virtualMachines.list()).toEqual([machineWithProfile]);
    }));
  });


});
