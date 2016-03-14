var zeusModule = angular.module('Zeus', ['ui.router', 'angular-growl']);

//Growl configuraton
zeusModule.config(['growlProvider', function(growlProvider) {
    growlProvider.globalTimeToLive(5000);
    growlProvider.onlyUniqueMessages(false);
    growlProvider.globalReversedOrder(true);
}]);


zeusModule.service('BuildService', function($http, $rootScope) {
    var service = this;
    var buildDetails = {};
    
    service.setBuildDetails = function(buildName, buildPath){
    	buildDetails["name"] = buildName;
    	buildDetails["path"] = buildPath;
    	$rootScope.$broadcast('buildDetailsSetEvent', {
            data: buildDetails
        });
    }
    
    service.getBuildDetails = function(){
    	return buildDetails;
    }
    
});

zeusModule.controller('MainCtrl', function(BuildService, $scope, $http, $state, growl) {

	$scope.getFiles = function(directory){
		$scope.directoryPath = directory;
		$http({
			url : "/files",
			method : "GET",
			params : {directory : directory} 
		}).success(function(response) {
     		$scope.result = response;
     	}).error(function(response){
     		growl.error("Error in loading data");
     	});
	}
	
	$scope.buildSection = function(name, path){
		BuildService.setBuildDetails(name, path);
	}
	
});

zeusModule.controller('BuildController', function(BuildService, $scope, $http, $state, growl) {
	$scope.$on('buildDetailsSetEvent', function(event, data) {
        loadBuildData();
    })
    
	$scope.showProcessList = false;
	$scope.showBuildSection = false;
	
	loadBuildData = function(){
		var details = BuildService.getBuildDetails();
		if(Object.keys(details).length > 0){
			$scope.buildDetails = details;
			$scope.buildDetails.environment = 'development';
			$scope.buildDetails.comments = "Building file.";
			//check the other java processes if running
			$scope.showProcessList = true;
			$scope.showBuildSection = true;
			getProcessDetails();
			getBuildHistory($scope.buildDetails.name, $scope.buildDetails.path);
			
		}
	}
	
	getBuildHistory = function(name, path){
		$http({
			url : "/build_history",
			method : "GET",
			params : {file : name, path : path} 
		}).success(function(response) {
     		$scope.buildHistory = response;
     	}).error(function(response){
     		growl.error('Error in loading build history.');
     	});
	}
	
	$scope.showProcesses = function(){
		$scope.showProcessList = !$scope.showProcessList;
		if($scope.showProcessList)
		 getProcessDetails();
	}
	
	$scope.build = function(){
		$http({
	        url: '/build',
	        method: "POST",
	        data: JSON.stringify($scope.buildDetails),
	    }).success(function(response) {
	    	growl.success("Build deployed successfully");	
		}).error(function(response){
			growl.error("Build deployment unsuccessful");
		});

	}
		
	getProcessDetails = function(){
		$http.get("/show_java_processes").success(function(response){
			if(response.length > 0){
				$scope.processDetailsList = response;
			}
		}).error(function(response){
			growl.error("Error in loading process data");
	 	});
	}
	
	$scope.killProcess = function(pid){
		var r = confirm("Killing a process. Are you sure dude!");
	    if (r == true) {
	    	$http.get("/kill_process/" + pid).success(function(response){
				growl.success("Process killed successfully");
				getProcessDetails();
			}).error(function(response){
				growl.error("Process kill unsuccessful");
		 	});
	    } else {
	        alert("Phewww. You saved it.");
	    }
	}
	
});

zeusModule.directive('breadcrumb', function() {
    return {
        scope: true,
        replace: true,
        templateUrl: 'src/zeus/app/breadcrumb.html'
    }
});


