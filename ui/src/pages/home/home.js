"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var login_1 = require("../login/login");
var HomePage = (function () {
    function HomePage(navCtrl, loginService) {
        var _this = this;
        this.navCtrl = navCtrl;
        this.loginService = loginService;
        this.loginService.isUserLoggedIn().subscribe(function (res) {
            if (!res)
                _this.navCtrl.setRoot(login_1.LoginPage);
        });
    }
    HomePage.prototype.logout = function () {
        this.loginService.logOut();
        this.navCtrl.setRoot(login_1.LoginPage);
    };
    return HomePage;
}());
HomePage = __decorate([
    core_1.Component({
        selector: 'page-home',
        templateUrl: 'home.html'
    })
], HomePage);
exports.HomePage = HomePage;
