import StateCollector from "./stateCollector";

class StateCortexletActions {
    private app: any;
    private stateCollector:StateCollector;
    private CORTEXLET_INFO: string = "STATE_CL";

    constructor(app: any, stateCollector: StateCollector) {
        this.app = app;
        this.stateCollector = stateCollector;
        console.log("["+this.CORTEXLET_INFO+"] Starting actions on state cortexlet.")
 
        this.action_cortexlet_body();
    }

    action_cortexlet_body() {
        this.app.get('/matter/state', async (req: any, res: { json: (arg0: { message: string; code: number; result: Object; }) => void; }) => {
            
            res.json({
                message: "Returning state",
                code: 1,
                result: this.stateCollector.get_states()
            })
        })
    }
}

export default StateCortexletActions