import BlinkingEngine from "./blinkingEngine";
import LifeEngine from "./lifeEngine";
import MimicryEngine from "./mimicryEngine";

class BiomimeticCortexletActions {
    private app: any;
    private blinkingEngine: BlinkingEngine;
    private lifeEngine: LifeEngine;
    private mimicryEngine: MimicryEngine;
    private CORTEXLET_INFO: string = "BIOMIMETIC_CL";

    constructor(app: any, mimicryEngine: MimicryEngine, blinkingEngine: BlinkingEngine, lifeEngine: LifeEngine) {
        this.app = app;
        this.blinkingEngine = blinkingEngine;
        this.mimicryEngine = mimicryEngine;
        this.lifeEngine = lifeEngine;
        console.log("[" + this.CORTEXLET_INFO + "] Starting actions on biomimetic cortexlet.")

        this.action_cortexlet_body();
    }

    action_cortexlet_body() {
        this.app.post('/matter/lifesimulator/blinking/on', async (req: any, res: any) => {
            this.blinkingEngine.set_blinking(true);
            console.log("Changed blinking")
            res.json({
                code: 1,
                message: "Blinking off"
            })
        });

        this.app.post('/matter/lifesimulator/blinking/off', async (req: any, res: any) => {
            this.blinkingEngine.set_blinking(false);
            console.log("Changed blinking")
            res.json({
                code: 1,
                message: "Blinking off"
            })
        });

        this.app.post('/matter/lifesimulator/mimics/on', async (req: any, res: any) => {
            this.mimicryEngine.change_mimics_state(true);
            console.log("Changed mimics")
            res.json({
                code: 1,
                message: "Mimics on"
            })
        });

        this.app.post('/matter/lifesimulator/mimics/off', async (req: any, res: any) => {
            this.mimicryEngine.change_mimics_state(false);
            console.log("Changed mimics")
            res.json({
                code: 1,
                message: "Mimics off"
            })
        });

        this.app.post('/matter/lifesimulator/auto/on', async (req: any, res: any) => {
            this.lifeEngine.set_life(true);
            console.log("Changed life")
            res.json({
                code: 1,
                message: "Life on"
            })
        });

        this.app.post('/matter/lifesimulator/auto/off', async (req: any, res: any) => {
            this.lifeEngine.set_life(false);
            console.log("Changed life")
            res.json({
                code: 1,
                message: "Life off"
            })
        });

        this.app.post('/matter/lifesimulator/actions/close_eyes', async (req: any, res: any) => {
            this.blinkingEngine.close()
            res.json({
                code: 1,
                message: ""
            })
        });

        this.app.post('/matter/lifesimulator/actions/open_eyes', async (req: any, res: any) => {
            this.blinkingEngine.open()
            res.json({
                code: 1,
                message: ""
            })
        });
    }
}
export default BiomimeticCortexletActions