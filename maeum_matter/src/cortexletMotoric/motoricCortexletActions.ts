import MotoricManager from "./motoricManager";

class MotoricCortexletActions {
    private app: any;
    private motoricManager: MotoricManager;
    private CORTEXLET_INFO: string = "MOTORIC_CL";

    constructor(app: any, motoricManager: MotoricManager) {
        this.app = app;
        this.motoricManager = motoricManager;
        console.log("[" + this.CORTEXLET_INFO + "] Starting actions on motoric cortexlet.")

        this.action_cortexlet_body();
    }

    action_cortexlet_body() {
        this.app.post('/matter/motoric/headmove', async (req: any, res: { json: (arg0: { message: string; code: number; }) => void; }) => {
            if (req.query.where !== undefined) {
                this.motoricManager.headmove(req.query.where)
            }
            res.json({
                code: 1,
                message: "Moving"
            })
        })
    }
}

export default MotoricCortexletActions;