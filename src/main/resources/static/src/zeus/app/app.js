var zeusModule = angular.module('Zeus', ['ui.router', 'angular-growl', 'ui.bootstrap']);

//Growl configuraton
zeusModule.config(['growlProvider', function(growlProvider) {
    growlProvider.globalTimeToLive(5000);
    growlProvider.onlyUniqueMessages(false);
    growlProvider.globalReversedOrder(true);
}]);


zeusModule.service('BuildService', function($http, $rootScope, $q) {
    var service = this;
    var buildDetails = {};
    
    service.setBuildDetails = function(buildName, buildPath){
    	buildDetails["name"] = buildName;
    	buildDetails["path"] = buildPath;
    	$rootScope.$broadcast('buildDetailsSetEvent', {
            data: buildDetails
        });
    }
    
    service.refreshProcesses = function(){
    	$rootScope.$broadcast('refreshProcesses');
    }
    
    service.getDirectoryData = function(directory){
    	var defer = $q.defer();
    	$http({
			url : "/files",
			method : "GET",
			params : {directory : directory}
		}).success(function(response) {
     		defer.resolve(response);
     	})
        return defer.promise;
    }
    
    service.getBuildHistory = function(name, path){
    	var defer = $q.defer();
		$http({
			url : "/build_history",
			method : "GET",
			params : {file : name, path : path} 
		}).success(function(response) {
			defer.resolve(response);
     	});
		
		return defer.promise;
	}
    
    service.getProcessDetails = function(){
    	var defer = $q.defer();
		$http.get("/show_java_processes").success(function(response){
			defer.resolve(response);
		});
		return defer.promise;
	}
    
    service.getBuildDetails = function(){
    	return buildDetails;
    }
    
});

zeusModule.controller('DirectoryController', function(BuildService, $scope, $http, $state, growl) {

	$scope.showDirectoryDetails = true;
	$scope.getFiles = function(directory){
		$scope.directoryPath = directory;
		BuildService.getDirectoryData(directory).then(function(data){
			$scope.result = data;
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
    
	$scope.showBuildSection = true;
	
	loadBuildData = function(){
		var details = BuildService.getBuildDetails();
		if(Object.keys(details).length > 0){
			$scope.buildDetails = details;
			$scope.buildDetails.environment = 'development';
			$scope.buildDetails.comments = "Building file.";
			$scope.showCommentsSection = true;
			$scope.showBuildSection = true;
			loadComments($scope.buildDetails.name, $scope.buildDetails.path);
		}
	}
	
	loadComments = function(buildName, buildPath){
		BuildService.getBuildHistory(buildName, buildPath).then(function(data){
			$scope.buildHistory = data;
		});
	}
	
	$scope.build = function(){
		$http({
	        url: '/build',
	        method: "POST",
	        data: JSON.stringify($scope.buildDetails),
	    }).success(function(response) {
	    	loadComments($scope.buildDetails.name, $scope.buildDetails.path);
	    	BuildService.refreshProcesses();
	    	growl.success("Build deployed successfully");	
		}).error(function(response){
			growl.error("Build deployment unsuccessful");
		});
	}
	
});

zeusModule.controller('ProcessController', function(BuildService, $scope, $http, $state, growl) {
	$scope.showProcessList = true;
	
	$scope.$on('refreshProcesses', function(event, data) {
		BuildService.getProcessDetails().then(function(data){
			$scope.processDetailsList = data;
		});	
    })
	
	
	$scope.getProcessList = function(){
		BuildService.getProcessDetails().then(function(data){
			$scope.processDetailsList = data;
		});	
	}
	
	$scope.showProcesses = function(){
		$scope.showProcessList = !$scope.showProcessList;
		if($scope.showProcessList){
			BuildService.getProcessDetails().then(function(data){
				$scope.processDetailsList = data;
			});
		}
	}
	
	$scope.killProcess = function(pid){
		var r = confirm("Killing a process. Are you sure dude!");
	    if (r == true) {
	    	$http.get("/kill_process/" + pid).success(function(response){
				growl.success("Process killed successfully");
				$scope.getProcessList();
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


