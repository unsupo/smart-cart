import {BrowserModule} from '@angular/platform-browser';
import {ErrorHandler, NgModule} from '@angular/core';
import {IonicApp, IonicErrorHandler, IonicModule} from 'ionic-angular';
import {SplashScreen} from '@ionic-native/splash-screen';
import {StatusBar} from '@ionic-native/status-bar';

import {MyApp} from './app.component';
import {HomePage} from '../pages/home/home';
import {LoginPageModule} from "../pages/login/login.module";
import {LoginService} from "../utilities/LoginService";
import {HttpModule} from "@angular/http";
import {SplitPaneModule} from "ng2-split-pane/lib/ng2-split-pane";
import {Deeplinks} from "@ionic-native/deeplinks";
import {ServerService} from "../utilities/ServerService";
import {SocketService} from "../utilities/SocketService"; // and
import { StompService } from 'ng2-stomp-service';

@NgModule({
    declarations: [
        MyApp,
        HomePage,
    ],
    imports: [
        BrowserModule,
        HttpModule,
        LoginPageModule,
        SplitPaneModule,
        IonicModule.forRoot(MyApp)
    ],
    bootstrap: [IonicApp],
    entryComponents: [
        MyApp,
        HomePage
    ],
    providers: [
        StatusBar,
        SplashScreen,
        LoginService,
        ServerService,
        Deeplinks,
        StompService,
        SocketService,
        {provide: ErrorHandler, useClass: IonicErrorHandler}
    ],
})
export class AppModule {
}
