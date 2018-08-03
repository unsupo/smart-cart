import {Component} from '@angular/core';
import {NavController} from 'ionic-angular';
import {LoginService} from "../../utilities/LoginService";
import {LoginPage} from "../login/login";
import {ServerService} from "../../utilities/ServerService";
import {Utility} from "../../utilities/Utility";
import {Machine} from "../../objects/Machine";
import {SocketService} from "../../utilities/SocketService";

@Component({
    selector: 'page-home',
    templateUrl: 'home.html'
})
export class HomePage {
    public pos : any = {top:2,left:2,height:1,width:1};
    public hovered : boolean = false;
    public machines : Map<string,Machine> = new Map();

    constructor(public navCtrl: NavController, public loginService: LoginService,
                public serverService: ServerService, public socketService: SocketService) {
        this.loginService.isUserLoggedIn().subscribe(res => {
            if (!res) {
                this.navCtrl.setRoot(LoginPage);
            }
        }, err=>{
            loginService.logOut(); this.navCtrl.setRoot(LoginPage);
        });
        this.socketService.setHome(this);
    }

    ngAfterViewInit(){
        this.initialGetData();
    }

    public getMachineMap():Map<string,Machine>{
        return this.machines;
    }

    public getMachines():Array<string>{
        return Utility.copps(this.machines.keys());
    }

    private initialGetData() {
        this.serverService.getData(this.loginService.user.token).subscribe(
            res=>{
                this.machines = res;
            },err=>{
                console.log(err);
            }
        );
    }

    public logout() {
        this.loginService.logOut();
        this.navCtrl.setRoot(LoginPage);
    }


    public barChartOptions:any = {
        scaleShowVerticalLines: false,
        responsive: true
    };
    public barChartLabels:string[] = ['2006', '2007', '2008', '2009', '2010', '2011', '2012'];
    public barChartType:string = 'bar';
    public barChartLegend:boolean = false;

    public barChartData:any[] = [
        {data: [65, 59, 80, 81, 56, 55, 40], label: 'Series A'},
        {data: [28, 48, 40, 19, 86, 27, 90], label: 'Series B'}
    ];

    // events
    public chartClicked(e:any):void {
        console.log(e);
    }

    public chartHovered(e:any):void {
        this.hovered = true;
        console.log(e);
    }

    public randomize():void {
        // Only Change 3 values
        let data = [
            Math.round(Math.random() * 100),
            59,
            80,
            (Math.random() * 100),
            56,
            (Math.random() * 100),
            40];
        let clone = JSON.parse(JSON.stringify(this.barChartData));
        clone[0].data = data;
        this.barChartData = clone;
        /**
         * (My guess), for Angular to recognize the change in the dataset
         * it has to change the dataset variable directly,
         * so one way around it, is to clone the data, change it and then
         * assign it;
         */
    }
}
