import { MObject, MPerson, VisualMemory } from "./visualMemory";

class MemoricCortexletActions {
    private app: any;
    private visualMemory: VisualMemory;
    private CORTEXLET_INFO: string = "MEMORIC_CL";

    constructor(app: any, visualMemory: VisualMemory) {
        this.app = app;
        this.visualMemory = visualMemory;
        console.log("[" + this.CORTEXLET_INFO + "] Starting actions on memoric cortexlet.")

        this.action_cortexlet_body();
    }

    action_cortexlet_body() {
        this.app.post('/matter/visual/maeumobj', async (req: {
            query: any; params: {
                type: string; name: String; id: String; prob: Number;
            };
        }, res: {
            json: (arg0: {
                message: string; code: number;
            }) => void;
        }) => {

            let type: string = req.query.type;

            if (type === 'person') {
                this.visualMemory.viewing_person(req.query.name);
            } else {
                let object = new MObject(req.query.name, req.query.id, req.query.prob);
                this.visualMemory.add_to_memory(object);
            }
            res.json({
                message: "Added to " + type +"  visual memory",
                code: 1
            })
        })

        this.app.post('/matter/visual/maeumobj_batch', async (req: {
            query: any; params: {
                type: string; batch: any;
            };
        }, res: {
            json: (arg0: {
                message: string; code: number;
            }) => void;
        }) => {

            let type: string = req.query.type;


            if (type === 'person') {
                let array = JSON.parse(req.query.batch);

                this.visualMemory.push_people_memory(array);
            } else {
                let array = JSON.parse(req.query.batch);
              
                this.visualMemory.push_object_memory(array);
            }
            res.json({
                message: "Added "+type+" batch to visual memory",
                code: 1
            })
        })

        this.app.post('/matter/visual/maeumemo', async (req: {
            query: any; params: {
                type: string; name: String; id: String; prob: Number;
            };
        }, res: {
            json: (arg0: {
                message: string; code: number;
            }) => void;
        }) => {

            this.visualMemory.update_emotions(req.query.id, req.query.emotion);

            res.json({
                message: "Emotion of " + req.query.id + " to " + req.query.emotion,
                code: 1
            })
        })

        this.app.post('/matter/visual/noforeground', async (req: {}, res: {
            json: (arg0: {
                message: string; code: number;
            }) => void;
        }) => {

            this.visualMemory.no_foreground();

            res.json({
                message: "No foreground ",
                code: 1
            })
        })


        this.app.post('/matter/visual/person/add/:name', async (req: {
            query: any; params: {
                name: String; id: String;
            };
        }, res: {
            json: (arg0: {
                message: string; code: number;
            }) => void;
        }) => {
            this.visualMemory.create_profile( req.params.name);
            res.json({
                message: "Adding " + req.params.name + " to database",
                code: 1
            })
        })

        this.app.get('/matter/visual/person/foreground', async (req: any, res: { json: (arg0: { message: string; code: number; name: String; }) => void; }) => {
            res.json({
                message: "Returning foreground",
                code: 1,
                name: this.visualMemory.people_in_view_for_gpt()
            })
        })

    }
}

export default MemoricCortexletActions