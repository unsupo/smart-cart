"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var ionic_angular_1 = require("ionic-angular");
var User_1 = require("../../utilities/User");
var home_1 = require("../home/home");
/**
 * Generated class for the LoginPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */
var LoginPage = (function () {
    function LoginPage(navCtrl, navParams, loginService) {
        var _this = this;
        this.navCtrl = navCtrl;
        this.navParams = navParams;
        this.loginService = loginService;
        this.username = "";
        this.password = "";
        this.usernameError = null;
        this.passwordError = null;
        this.error = null;
        this.loginService.isUserLoggedIn().subscribe(function (res) {
            if (res)
                _this.navCtrl.setRoot(home_1.HomePage);
        });
    }
    LoginPage.prototype.ionViewDidLoad = function () {
        console.log('ionViewDidLoad LoginPage');
    };
    LoginPage.prototype.lostFocus = function (v) {
        var u = 'username';
        var p = 'password';
        var r = '#f67575';
        var b = '#76a9f4';
        if (v === u) {
            document.getElementById(u).style.borderBottomColor = b;
            document.getElementById(u + '-label').style.color = b;
            if (!this.username) {
                document.getElementById(u).style.borderBottomColor = r;
                document.getElementById(u + '-label').style.color = r;
            }
        }
        if (v === p) {
            document.getElementById(p).style.borderBottomColor = b;
            document.getElementById(p + '-label').style.color = b;
            if (!this.password) {
                document.getElementById(p).style.borderBottomColor = r;
                document.getElementById(p + '-label').style.color = r;
            }
        }
    };
    LoginPage.prototype.validate = function (key) {
        //TODO form validation is both signIn and lostFocus
    };
    LoginPage.prototype.signIn = function () {
        // console.log(this.username+","+this.password);
        // usernameError = null; passwordError = null;
        // if(!username){
        //   usernameError = "Username is invalid";
        // }if(!password){
        //   passwordError = "Password is invalid";
        // }
        //TODO form validation here
        var _this = this;
        this.loginService.login(new User_1.User(this.username, this.password)).subscribe(function (res) {
            // console.log('result='+res);
            _this.navCtrl.setRoot(home_1.HomePage);
        }, function (err) {
            console.log('error=' + err);
        });
    };
    return LoginPage;
}());
LoginPage = __decorate([
    ionic_angular_1.IonicPage(),
    core_1.Component({
        selector: 'page-login',
        templateUrl: 'login.html',
    })
], LoginPage);
exports.LoginPage = LoginPage;
