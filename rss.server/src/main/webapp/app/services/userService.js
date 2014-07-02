app.service('userService', function ($http, $resource) {

	this.createUser = function (user, successCallback, failureCallback) {
        var userObject = $resource('/users');

        var newUser = new userObject({
        	firstName : user.firstName,
        	lastName : user.lastName,
        	userName : user.email,
        	password : user.password
        });
        return newUser.$save(successCallback, failureCallback);
    };

});