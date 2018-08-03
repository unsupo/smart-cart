
import {Injectable} from '@angular/core';
import 'rxjs/add/operator/map'
import {Utility} from "./Utility";
import { StompService } from 'ng2-stomp-service';
import {HomePage} from "../pages/home/home";
import {Machine} from "../objects/Machine";
import {Metric} from "../objects/Metric";

@Injectable()
export class SocketService {

    private v: string = 'init';
    // private subscriptionMessaging: any;
    private subscriptionEvent: any;
    private data: any;
    // public objs: Map<string, EventObj> = new Map();
    public home: HomePage;

    constructor(private stomp: StompService) {
        //configuration
        stomp.configure({
            host: Utility.getHost().concat('/stomp'),
            debug: true,
            queue: {'init': false}
        });

        //start connection
        stomp.startConnect().then(() => {
            stomp.done(this.v);
            console.log('connected');

            //subscribe
            // this.subscriptionMessaging = stomp.subscribe('/topic/messaging', res => {
            //     this.data = res;
            // });
            this.subscriptionEvent = stomp.subscribe('/topic/metric', res => {
                this.parseResults(res);
            });
        });
    }

    public parseResults(res) {
        if(!this.home)
            return
        // this.home.machines = Utility.parseMachineMetricInfo(res)
        var machines = Utility.parseMachineMetricInfo(res);
        if(machines.size == 0) {
            console.error("EMPTY MACHINES");
            return;
        }
        machines.forEach((value: Machine, key: string) => {
            var host = this.home.machines.get(key);
            if(!host)
                this.home.machines.set(key,value);
            else {
                value.isDropDown = host.isDropDown;
                for(var i in value.metrics){
                    var v;
                    host.metrics.forEach((metricvalue: Metric)=>{
                        if(metricvalue.name == value.metrics[i].name)
                            value.metrics[i].isDropDown = metricvalue.isDropDown;
                    });
                }
            }
            // console.log(key, value);
        });
        this.home.machines = machines;
        // console.log(res);
        // this.parseResults(res);
        // this.objs = new Map();
        // console.log(res);
        // var json = res;
        // if (typeof res != 'object')
        //     json = JSON.parse(res);
        // for (var i in json)
        //     if (!this.objs.has(i))
        //         this.objs.set(i, new EventObj(i, json[i], this.home, this.googleMapService));
    }


    public sendMessage(data: string) {
        this.stomp.after(this.v).then(() => {
            this.stomp.send('/app/messages', data);
        });
    }

    public sendMessageWithDestination(data: string, destination: string) {
        this.stomp.after(this.v).then(() => {
            this.stomp.send(destination, data);
        });
    }

    public getStomp(): any {
        return this.stomp;
    }

    public getResponse(): any {
        return this.data;
    }

    public getEventData() {
        // return this.objs;
    }

    //response
    public response = (data) => {
        return data;
    };

    setHome(home: HomePage) {
        this.home = home;
    }
}