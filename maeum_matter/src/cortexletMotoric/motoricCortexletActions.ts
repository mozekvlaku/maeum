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

        this.app.post('/matter/motoric/:side/:state', async (req: any, res: any) => {
            const { side, state } = req.params;
            this.motoricManager.change_servo_power(side, state);
            console.log(`Changed power for ${side} to ${state}`);
            res.json({
                code: 1,
                message: `${side} ${state}`
            });
        });

    }
}

export default MotoricCortexletActions;