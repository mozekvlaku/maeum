import axios from "axios";
import StatefulObject from "./cortexletState/statefulObject";

class CentralDispatcher extends StatefulObject {
    dispatch: Array<DispatchTask>;
    public IDENTIFIER: string = "cd";
    private dispatchable = true;

    constructor() {
        super()
        this.dispatch = new Array<DispatchTask>();
        setInterval(() => this.to_dispatch(), 2000);
    }

    to_dispatch() : void {
        this.dispatch.forEach((task, index) => {
            task.dispatch();
            console.log("[DISPATCHER] Dispatching")
            this.emit_state("dispatcher");
            this.dispatch.splice(index, 1);
        })
    }

    get_state(): Object {
        return {
            "dispatch": this.dispatch
        }
    }

    timeout_dispatch() :void {
        this.dispatchable = false;

        setTimeout(() => {
            this.dispatchable = true;
        }, 40000)
    }

    add(source: any, target: any, command: string, return_value: string, return_interface: any, return_target: any) {
        let dispatchTask = new DispatchTask(source, target, command, return_value, return_interface, return_target);
        if(this.dispatchable)
            this.dispatch.push(dispatchTask);
        this.timeout_dispatch()
    }

    add_from_cdcall(cdcall: string)
    {
        let parts = cdcall.split('->');
        let first_part = parts[0].split(':');
        let second_part = parts[1];

        let source = first_part[0];
        let target = first_part[1];
        let command = first_part[2];
        
        let return_value = "void";
        let return_interface = null;
        let return_target = null;

        if(second_part != "(void)")
        {
            var regExp = /\(([^)]+)\)/;
            var matches = regExp.exec(second_part);
            if(matches != null)
            {
                return_value = matches[1];
            }
            let tmp:any = second_part.replace(/ *\([^)]*\) */g, "");
            tmp = tmp.split("/");
            return_interface = tmp[0];
            return_target = tmp[1];
        }

        return this.add(source, target, command, return_value, return_interface, return_target);
    }
}

class DispatchTask {
    source: string;
    target: string;
    command: string;
    return_value: any;
    return_interface: any;
    return_target: any;

    constructor(source: string,target:string, command:string, return_value:any, return_interface:any, return_target:any)
    {
        this.source = source;
        this.target = target;
        this.command = command;
        this.return_value = return_value;
        this.return_interface = return_interface;
        this.return_target = return_target;
    }

    dispatch() : void {
        let uri_target: string = DispatchTargetsEnum[this.target as keyof typeof DispatchTargetsEnum];
        let uri_return: string = "";
        if(this.return_target != null)
        {
            uri_return = DispatchTargetsEnum[this.return_target as keyof typeof DispatchTargetsEnum];
        }

        console.log("[DISPATCHER] " + uri_target + this.command)
        axios.get(uri_target + this.command).then((response)=>{
            console.log(response.data);
        })
        console.log("calling " + uri_target + this.command);
        
       /* let returned_return = "Seeya";
        console.log("calling " + uri_return + this.return_interface);
        console.log("getting " + this.return_target + " " + returned_return);*/
    }
}

enum DispatchTargetsEnum {
    VB = "http://localhost:8080/",
    VC = "http://localhost:8000/",
    MT = "http://localhost:3000/",
    MC = "http://localhost:4545/"
}


export default CentralDispatcher;