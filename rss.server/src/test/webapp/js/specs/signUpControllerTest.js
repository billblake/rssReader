describe("RssReader", function() {

    beforeEach(module('viewApp'));

    describe("SignUpController", function() {

        var $controller;

        var mockUserService;

        readerConstants = {appContextPath : "reader"};
        var $httpBackend, location;
        ga = function(){};


        beforeEach(function() {
            var mockUserService = {};
            module('viewApp', function($provide) {
              $provide.value('userService', mockUserService);
            });

            mockUserService.createUser = function(user, userCreatedSuccessfully, userCreationFailure) {
                if (user.createdSuccessfully) {
                    userCreatedSuccessfully();
                } else {
                    var response = {data : "Error"};
                    userCreationFailure(response);
                }
            };

          });


        beforeEach(inject(function(_$rootScope_, _$controller_, $injector, _userService_) {
            // The injector unwraps the underscores (_) from around the parameter names when matching
            $controller = _$controller_;
            $rootScope = _$rootScope_;
            scope = $rootScope.$new();

            $controller('SignUpController',
                  {'$scope': scope, "userService" : _userService_});
        }));


        it("validate form", function() {
            var user = {
                    password : "password",
                    confirmPassword : "password"
            };
            var signUpForm = {$valid : true};
            expect(scope.invalidForm).toBe(true);
            scope.validateForm(user, signUpForm);
            expect(scope.invalidForm).toBe(false);
        });


        it("validate form with passwords that don't match", function() {
            var user = {
                    password : "password",
                    confirmPassword : "differentPassword"
            };
            var signUpForm = {$valid : true};
            expect(scope.invalidForm).toBe(true);
            scope.validateForm(user, signUpForm);
            expect(scope.invalidForm).toBe(true);
        });


        it("validate invalid form", function() {
            var user = {
                    password : "password",
                    confirmPassword : "password"
            };
            var signUpForm = {$valid : false};
            expect(scope.invalidForm).toBe(true);
            scope.validateForm(user, signUpForm);
            expect(scope.invalidForm).toBe(true);
        });


        it("verify email error not displayed initially", function() {
            var emailField = {
                    $invalid : true,
                    $dirty : false
            };
            scope.validateEmail(emailField);
            expect(scope.showEmailError).toBe(false);
            expect(scope.emailErrorClass).toBe("");
        });


        it("verify email error is displayed for an invalid email", function() {
            var emailField = {
                    $invalid : true,
                    $dirty : true
            };
            scope.validateEmail(emailField);
            expect(scope.showEmailError).toBe(true);
            expect(scope.emailErrorClass).toBe("has-error");
        });


        it("verify email error is not displayed for a valid email", function() {
            var emailField = {
                    $invalid : false,
                    $dirty : true
            };
            scope.validateEmail(emailField);
            expect(scope.showEmailError).toBe(false);
            expect(scope.emailErrorClass).toBe("");
        });


        it("verify email error is not displayed for a valid email", function() {
            var emailField = {
                    $invalid : false,
                    $dirty : true
            };
            scope.validateEmail(emailField);
            expect(scope.showEmailError).toBe(false);
            expect(scope.emailErrorClass).toBe("");
        });


        it("verify valid password", function() {
            var password = "Password123";
            scope.validatePassword(password);
            expect(scope.showPasswordError).toBe(false);
            expect(scope.passwordErrorClass).toBe("");
        });


        it("verify password meets minimum length criteria", function() {
            var password = "P123";
            scope.validatePassword(password);
            expect(scope.showPasswordError).toBe(true);
            expect(scope.passwordErrorClass).toBe("has-error");
        });


        it("verify password contains a capital letter", function() {
            var password = "password123";
            scope.validatePassword(password);
            expect(scope.showPasswordError).toBe(true);
            expect(scope.passwordErrorClass).toBe("has-error");
        });


        it("verify password contains a number", function() {
            var password = "Passwordabc";
            scope.validatePassword(password);
            expect(scope.showPasswordError).toBe(true);
            expect(scope.passwordErrorClass).toBe("has-error");
        });


        it("verify password matches confirm passwords", function() {
            var user = {
                    password : "password",
                    confirmPassword : "password"
            };
            scope.validateConfirmPassword(user);
            expect(scope.showConfirmPasswordError).toBe(false);
            expect(scope.confirmPasswordErrorClass).toBe("");
        });


        it("verify different passwords display an error", function() {
            var user = {
                    password : "password",
                    confirmPassword : "differentPassword"
            };
            scope.validateConfirmPassword(user);
            expect(scope.showConfirmPasswordError).toBe(true);
            expect(scope.confirmPasswordErrorClass).toBe("has-error");
        });


        it("verify user creation is handled successfully", function() {
            var user = {createdSuccessfully : true};
            scope.signUp(user);
            expect(scope.userCreated).toBe(true);
            expect(scope.userCreatedError).toBe(false);
            expect(scope.errorMessage).toBe("");
            expect(scope.user).toEqual({});
        });


        it("verify a failed user creation handled successfully", function() {
            var user = {createdSuccessfully : false};
            scope.signUp(user);
            expect(scope.userCreated).toBe(false);
            expect(scope.userCreatedError).toBe(true);
            expect(scope.errorMessage).toBe("Error");
        });
    });
});