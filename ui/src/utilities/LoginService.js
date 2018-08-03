"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var http_1 = require("@angular/http");
var Observable_1 = require("rxjs/Observable");
var Utility_1 = require("./Utility");
var User_1 = require("./User");
require("rxjs/add/operator/map");
var LoginService = (function () {
    function LoginService(http, toast) {
        var _this = this;
        this.http = http;
        this.toast = toast;
        this.isLoggedIn = false;
        this.isUserLoggedIn().subscribe(function (res) {
            if (res)
                _this.toast.create({ message: 'Welcome ' + _this.user.username, duration: 2000 }).present();
        });
    }
    LoginService.prototype.isUserLoggedIn = function () {
        var _this = this;
        return new Observable_1.Observable(function (observer) {
            var u = localStorage.getItem(Utility_1.Utility.user);
            if (u) {
                _this.user = JSON.parse(u);
                _this.isTokenValid(_this.user.token).subscribe(function (res) {
                    if (res) {
                        if (!_this.user.username)
                            _this.getUserData(_this.user.token).subscribe(function (res) {
                                _this.loggedIn();
                                observer.next(true);
                            });
                        else {
                            _this.loggedIn();
                            observer.next(true);
                        }
                    }
                    else {
                        _this.logOut();
                        observer.next(false);
                    }
                });
            }
            else {
                _this.logOut();
                observer.next(false);
            }
            observer.complete();
        });
    };
    LoginService.prototype.loggedIn = function () {
        this.isLoggedIn = true;
        localStorage.setItem(Utility_1.Utility.user, JSON.stringify(this.user));
    };
    LoginService.prototype.login = function (u) {
        var _this = this;
        this.user = u;
        var h = new http_1.Headers();
        h.set('username', u.username);
        h.set('password', u.password);
        // h.set('Access-Control-Allow-Origin','*');
        h.set('Content-Type', 'application/json;charset=UTF-8');
        return this.http.post(Utility_1.Utility.getHost().concat('/login'), "", {
            headers: h
        }).map(function (res) {
            _this.isLoggedIn = true;
            _this.user.token = res["_body"];
            localStorage.setItem(Utility_1.Utility.user, JSON.stringify(_this.user));
            return _this.user;
        }, function (err) {
            _this.user = null;
            return null;
        });
    };
    LoginService.prototype.isTokenValid = function (token) {
        var h = new http_1.Headers();
        h.set('token', token);
        return this.http.post(Utility_1.Utility.getHost().concat('/isValidToken'), "", {
            headers: h
        }).map(function (res) {
            return res["_body"] == 'true';
        }, function (err) {
            return false;
        });
    };
    LoginService.prototype.getUserData = function (token) {
        var _this = this;
        var h = new http_1.Headers();
        h.set('token', token);
        return this.http.post(Utility_1.Utility.getHost().concat('/getUserData'), "", {
            headers: h
        }).map(function (res) {
            var json = JSON.parse(res["_body"]);
            var u = new User_1.User('', '');
            u.token = json['sessionId'];
            u.username = json['emailAddress'];
            u.lastName = json['lastName'];
            u.firstName = json['firstName'];
            u.locale = json['locale'];
            u.userGroup = json['userGroup'];
            u.imageUrl = json['imageUrl'];
            u.userId = json['userId'];
            _this.user = u;
            localStorage.setItem(Utility_1.Utility.user, JSON.stringify(u));
            return u;
        }, function (err) {
            _this.user = null;
            return null;
        });
    };
    LoginService.prototype.logOut = function () {
        this.user = null;
        this.isLoggedIn = false;
        localStorage.clear();
        // localStorage.removeItem(Utility.user);
    };
    return LoginService;
}());
LoginService = __decorate([
    core_1.Injectable()
], LoginService);
exports.LoginService = LoginService;
