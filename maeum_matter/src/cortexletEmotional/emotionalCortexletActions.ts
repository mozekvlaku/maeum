import { EmotionPipeline } from "./emotionPipeline";

class EmotionalCortexletActions {
    private app: any;
    private emotionPipeline: EmotionPipeline;
    private CORTEXLET_INFO: string = "EMOTIONAL_CL";

    constructor(app: any, emotionPipeline: EmotionPipeline) {
        this.app = app;
        this.emotionPipeline = emotionPipeline;
        console.log("[" + this.CORTEXLET_INFO + "] Starting actions on emotional cortexlet.")

        this.action_cortexlet_body();
    }

    action_cortexlet_body() {
        this.app.get('/matter/emotional/state', async (req: any, res: { json: (arg0: { message: string; code: number; state: Object; }) => void; }) => {
            res.json({
                message: "Returning emotional state",
                code: 1,
                state: this.emotionPipeline.get_state()
            })
        })
    }
}

export default EmotionalCortexletActions