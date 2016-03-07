var zeusModule = angular.module('Zeus', ['ui.router']);

/*zeusModule.factory('AngelloHelper', function() {
    var buildIndex = function (source, property) {
        var tempArray = [];

        for (var i = 0, len = source.length; i < len; ++i) {
            tempArray[source[i][property]] = source[i];
        }

        return tempArray;
    };

    return {
        buildIndex: buildIndex
    };
});*/

zeusModule.service('DirectoryService', function($http) {
    var service = this;

    service.getFiles = function (directory) {
    	$http.get("/directory/"+directory+"/files").success(function(response) {
     		if(response.responseCode == "OK"){
     			addresses = JSON.parse(response.responseContent);
     			addresses.forEach(function(address){
     			   if(address.addressType.toLowerCase() == "shipping"){
     				  $scope.customerAddress = address;
     			   }	
     			})
     			
     		}
     	}).error(function(response){
     		console.log("Customer Address loading error.");
     	});
    };
});

zeusModule.controller('MainCtrl', function(DirectoryService, $scope, $http, $state) {
    var main = this;

    main.files = DirectoryService.getFiles('archive');
    console.log(main.files);
});

/*zeusModule.directive('story', function() {
    return {
        scope: true,
        replace: true,
        template: '<div><h4>{{story.title}}</h4><p>{{story.description}}</p></div>'
    }
});*/