import {Component, ViewChild} from '@angular/core';
import {Platform, Nav, ToastController} from 'ionic-angular';
import {StatusBar} from '@ionic-native/status-bar';
import {SplashScreen} from '@ionic-native/splash-screen';

import {HomePage} from '../pages/home/home';
import {LoginPage} from "../pages/login/login";
import {LoginService} from "../utilities/LoginService";
import {Utility} from "../utilities/Utility";
import {Deeplinks} from "@ionic-native/deeplinks";

@Component({
    templateUrl: 'app.html'
})
export class MyApp {
    @ViewChild(Nav) nav: Nav;

    rootPage: any = HomePage;
    pages: Array<{ title: string, component: any, link: string}>;

    constructor(public platform: Platform, public statusBar: StatusBar, public splashScreen: SplashScreen,
                public toast:ToastController,
                public auth: LoginService, public plt: Platform, public deeplinks : Deeplinks) {
        this.initializeApp();
        this.pages = [
            {title: 'Monitoring', component: HomePage, link:'/home'},
        ];
        var curr = localStorage.getItem(Utility.currentPage);
        if (curr) {
            curr = JSON.parse(curr).title;
            for (var i in this.pages)
                if (this.pages[i].title == curr)
                    this.rootPage = this.pages[i].component;
        }
        this.auth.isUserLoggedIn().subscribe(res => {
            if (res)
                console.log("token exists");
            else
                this.rootPage = LoginPage;
        }, err => {
            this.rootPage = LoginPage;
        });
    }
    public doesTokenExist():void{
        this.auth.isUserLoggedIn().subscribe(res => {
            if (res)
                console.log("token exists");
            else {
                this.toast.create({message: 'Logging Out token doesn\'t exist', duration: 2000}).present();
                this.auth.logOut();
                this.nav.setRoot(LoginPage);
            }
        }, err => {
            this.toast.create({message: 'Logging Out: ' + err, duration: 2000}).present();
            this.auth.logOut();
            this.nav.setRoot(LoginPage);
        });
    }

    initializeApp() {
        this.platform.ready().then(() => {
            // Okay, so the platform is ready and our plugins are available.
            // Here you can do any higher level native things you might need.
            this.statusBar.styleDefault();
            this.splashScreen.hide();

            // Convenience to route with a given nav
            this.deeplinks.routeWithNavController(this.nav, {
                '/login': LoginPage,
                '/home':HomePage,
            }).subscribe((match) => {
                console.log('Successfully routed', match);
                this.doesTokenExist();
            }, (nomatch) => {
                console.warn('Unmatched Route', nomatch);
                this.doesTokenExist();
            });
        });
    }

    openPage(page) {
        // Reset the content nav to have just this page
        // we wouldn't want the back button to show in this scenario
        localStorage.setItem(Utility.currentPage, JSON.stringify(page));
        this.nav.setRoot(page.component,null,null,()=>{
            this.doesTokenExist();
        });
    }
}

