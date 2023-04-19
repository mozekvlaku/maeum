import StatefulObject from "./statefulObject";

class StateCollector {
    private stateful_objects:Array<StatefulObject> = [];
    private subscribe_for_changes: boolean = false;
    private io:any;

    constructor(io: any, subscribe: boolean) {
        this.io = io;
        this.subscribe_for_changes = subscribe;
    }

    observe_state(object: StatefulObject) : void {

        this.stateful_objects.push(object);
        if(this.subscribe_for_changes)
        {
            object.subscribe_state_change((message:String) => {
                this.io.emit("state", this.get_states());
                console.log("[STATE_CL] Emiting change instantiated by " + object.IDENTIFIER + " ["+message+"]");
            })
            console.log("[STATE_CL] Subscribing to " + object.IDENTIFIER);
        }
        console.log("[STATE_CL] Registering " + object.IDENTIFIER);
    }

    get_states() : Object {
        const obj = {};
        for (const object of this.stateful_objects) {
            obj[object.IDENTIFIER] = object.get_state();
        }
        return obj;
    }
}

export default StateCollector