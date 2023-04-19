import VerbalEmotionParser from "./verbalEmotionParser";
import VerbalManager from "./verbalManager";

class VerbalCortexletActions {
    private app: any;
    private verbalEmotionParser: VerbalEmotionParser;
    private verbalManager: VerbalManager;
    private CORTEXLET_INFO: string = "VERBAL_CL";

    constructor(app: any, verbalManager: VerbalManager, verbalEmotionParser: VerbalEmotionParser) {
        this.app = app;
        this.verbalEmotionParser = verbalEmotionParser;
        this.verbalManager = verbalManager;
        console.log("[" + this.CORTEXLET_INFO + "] Starting actions on verbal cortexlet.")

        this.action_cortexlet_body();
    }

    action_cortexlet_body() {
        this.app.post('/matter/verbal/input', async (req: any, res: { json: (arg0: { message: string; code: number; returned: string }) => void; }) => {
            if (req.query.text !== undefined) {
                this.verbalEmotionParser.tag_emotion(req.query.text);
                this.verbalManager.send_to_rasa(req.query.text, function (data: string) {
                    res.json({
                        code: 1,
                        message: "Sent to Rasa",
                        returned: data
                    })
                })

            }
            else {
                res.json({
                    code: 1,
                    message: "Sent to Rasa",
                    returned: "Nene"
                })

            }
        })

        this.app.get('/matter/verbal/askname', async (_req: any, res: { json: (arg0: { message: string; code: number; returned: string }) => void; }) => {
            
                this.verbalManager.send_to_rasa("PROMPT--ASK_NAME", function (data: string) {
                    res.json({
                        code: 1,
                        message: "Sent to Rasa",
                        returned: data
                    })
                })

        })

        this.app.post('/matter/verbal/output', async (req: any, res: any) => {
            console.log(req)
            //vr.speak(req.query.text);
        });
    }
}

export default VerbalCortexletActions