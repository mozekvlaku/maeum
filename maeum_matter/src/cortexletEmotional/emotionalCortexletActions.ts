import { EmotionManager } from "./emotionManager";

class EmotionalCortexletActions {
    private app: any;
    private emotionPipeline: EmotionManager;
    private CORTEXLET_INFO: string = "EMOTIONAL_CL";

    constructor(app: any, emotionPipeline: EmotionManager) {
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
        this.app.post('/matter/emotions/set', async (req: any, res: { json: (arg0: { message: string; code: number; }) => void; }) => {
            this.emotionPipeline.set_emotion_manually(req.query.emotion);
            res.json({
                message: "Emostate Set",
                code: 1
            })
        })
    }
}

export default EmotionalCortexletActions