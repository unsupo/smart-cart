import {Utility} from "../utilities/Utility";

export class Metric{
    public name : string;
    public date : Date;
    public metricData : Map<string,any> = new Map();
    public hostInfo : Map<string,any> = new Map();
    public metricValue : Map<string,any> = new Map();
    public isDropDown : boolean = false;

    public constructor(public metricName : string){
        this.name = metricName;
    }

    public getMetricNames() : Array<string> {
        return Utility.copps(this.metricValue.keys());
    }
}