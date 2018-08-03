import {Machine} from "../objects/Machine";
import {Metric} from "../objects/Metric";

export class Utility {
    private static host : string = window.location.origin;//"http://localhost:8981";//
    static user : string = "user";
    static currentPage : string = "currentPage";
    static machines : string = "machines";

    //35.192.142.172

    public static getHost() : string {
        return this.host;
    }

    public static uuid() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

    public static parseMachineMetricInfo(res : any) : Map<string,Machine>{
        if(!res || res === 'undefined'){
            console.log('metrics undefined');
            return;
        }
        var json = res;
        if (typeof res != 'object') //if res is not parsed already
            json = JSON.parse(res);
        var arr = new Map();
        for (var i in json){
            var machine = new Machine(i);
            for(var j in json[i]){
                var metric = new Metric(j);
                metric.date = new Date(json[i][j]['date']);
                for(var k in json[i][j]['metric'])
                    metric.metricData.set(k,json[i][j]['metric'][k]);
                for(var k in json[i][j]['hostInfo'])
                    metric.hostInfo.set(k,json[i][j]['hostInfo'][k]);
                for(var k in json[i][j]['metricValue'])
                    metric.metricValue.set(k,json[i][j]['metricValue'][k]);
                machine.metrics.push(metric);
                machine.hostInfo = metric.hostInfo;
            }
            arr.set(i,machine);
        }
        return arr;
    }

    public static copps(arr): Array<any> {
        var temp = Array.from(arr);
        return temp;
    }
}
