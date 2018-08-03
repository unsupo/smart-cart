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
var home_1 = require("../pages/home/home");
var grafana_1 = require("../pages/Grafana/grafana");
var sda_installer_1 = require("../pages/sda-installer/sda-installer");
var login_1 = require("../pages/login/login");
var Utility_1 = require("../utilities/Utility");
var MyApp = (function () {
    function MyApp(platform, statusBar, splashScreen, auth, plt) {
        var _this = this;
        this.platform = platform;
        this.statusBar = statusBar;
        this.splashScreen = splashScreen;
        this.auth = auth;
        this.plt = plt;
        this.rootPage = home_1.HomePage;
        this.initializeApp();
        this.pages = [
            { title: 'Monitoring', component: home_1.HomePage },
            { title: 'Grafana', component: grafana_1.GrafanaPage },
            { title: 'SDA Installer', component: sda_installer_1.SdaInstallerPage },
        ];
        var curr = localStorage.getItem(Utility_1.Utility.currentPage);
        if (curr) {
            curr = JSON.parse(curr).title;
            for (var i in this.pages)
                if (this.pages[i].title == curr)
                    this.rootPage = this.pages[i].component;
        }
        auth.isUserLoggedIn().subscribe(function (res) {
            if (res)
                console.log("token exists");
            else
                _this.rootPage = login_1.LoginPage;
        }, function (err) {
            _this.rootPage = login_1.LoginPage;
        });
    }
    MyApp.prototype.initializeApp = function () {
        var _this = this;
        this.platform.ready().then(function () {
            // Okay, so the platform is ready and our plugins are available.
            // Here you can do any higher level native things you might need.
            _this.statusBar.styleDefault();
            _this.splashScreen.hide();
        });
    };
    MyApp.prototype.openPage = function (page) {
        // Reset the content nav to have just this page
        // we wouldn't want the back button to show in this scenario
        localStorage.setItem(Utility_1.Utility.currentPage, JSON.stringify(page));
        this.nav.setRoot(page.component);
    };
    return MyApp;
}());
__decorate([
    core_1.ViewChild(ionic_angular_1.Nav)
], MyApp.prototype, "nav", void 0);
MyApp = __decorate([
    core_1.Component({
        templateUrl: 'app.html'
    })
], MyApp);
exports.MyApp = MyApp;
