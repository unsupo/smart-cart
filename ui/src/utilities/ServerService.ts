import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {Utility} from "./Utility";
import {Http, Headers} from "@angular/http";
import {Machine} from "../objects/Machine";

@Injectable()
export class ServerService{
    constructor(private http: Http) { }

    public getData(token: string): Observable<Map<string,Machine>> {
        var h = new Headers();
        h.set('token', token);
        // h.set('Access-Control-Allow-Origin','*');
        h.set('Content-Type', 'application/json;charset=UTF-8');
        return this.http.post(Utility.getHost().concat('/getMetrics'), "", {
            headers: h
        }).map(
            res => {
                var machines = Utility.parseMachineMetricInfo(res['_body']);
                var lmachines = localStorage.getItem(Utility.machines);
                if(lmachines){
                    var vmachines = Utility.parseMachineMetricInfo(lmachines);
                    for(var m in vmachines){
                        machines[m].isDropDown = vmachines[m].isDropDown;
                        for(var me in vmachines[m].metrics)
                            machines[m].metrics[me].isDropDown = vmachines[m].metrics[me].isDropDown;
                    }
                }
                localStorage.setItem(Utility.machines,JSON.stringify(machines));
                return machines;
            }, err => {
                return err;
            }
        );
    }
}