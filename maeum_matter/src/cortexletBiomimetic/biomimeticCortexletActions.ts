import BlinkingEngine from "./blinkingEngine";
import MimicryEngine from "./mimicryEngine";

class BiomimeticCortexletActions {
    private app: any;
    private blinkingEngine: BlinkingEngine;
    private mimicryEngine: MimicryEngine;
    private CORTEXLET_INFO: string = "BIOMIMETIC_CL";

    constructor(app: any, mimicryEngine: MimicryEngine, blinkingEngine: BlinkingEngine) {
        this.app = app;
        this.blinkingEngine = blinkingEngine;
        this.mimicryEngine = mimicryEngine;
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
    }
}
export default BiomimeticCortexletActions