import {Metric} from "./Metric";

export class Machine{
    public name : string;
    public metrics : Array<Metric> = new Array();
    public hostInfo : Map<string,any> = new Map();
    public isDropDown : boolean = false;

    public constructor(public machineName : string){
        this.name = machineName;
    }
}