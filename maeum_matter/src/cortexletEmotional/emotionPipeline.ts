import MimicryEngine from "../cortexletBiomimetic/mimicryEngine";
import StatefulObject from "../cortexletState/statefulObject";

class EmotionPipeline extends StatefulObject {
    public IDENTIFIER: string = "ep";

    private emotionalState: EmotionalState;
    private pipeline: Array<Emotion>;
    private mimicryEngine: MimicryEngine;

    private last_emotional_state: EmotionalState = EmotionalState.Neutral;

    constructor(mimicryEngine: MimicryEngine) {
        super();
        this.emotionalState = EmotionalState.Neutral;
        this.pipeline = new Array<Emotion>()
        this.mimicryEngine = mimicryEngine;
        this.flush_emotional_state();
    }

    get_state(): Object {
        return {
            pipeline: this.pipeline,
            em_state: EmotionalState[this.emotionalState]
        }
    }

    push_emotional_state(emotional_state: EmotionalState, cadence: number, origin: EmotionOrigin = EmotionOrigin.Undefined): EmotionalState {
        if (this.pipeline.length == 1) {
            this.pipeline.pop();
        }
        console.log("[EMOTIONAL] Pushing " + emotional_state + " to pipeline");
        this.pipeline.push(new Emotion(emotional_state, cadence, origin));
        console.log("[EMOTIONAL] Pipeline state: ");
      
        return this.update_emotional_state();
    }

    push_emotional_state_from_vm(emotional_state: string, origin: EmotionOrigin = EmotionOrigin.VisualPerson): string {
        switch(emotional_state) {
            case "angry":
                this.push_emotional_state(EmotionalState.Angry, 50, origin);
            break;
            case "neutral":
                this.push_emotional_state(EmotionalState.Neutral, 50, origin);
            break;
            case "happy":
                this.push_emotional_state(EmotionalState.Happy, 20, origin);
                break;
            case "fear":
                this.push_emotional_state(EmotionalState.Fearful, 50, origin);
                break;
            case "disgust":
                this.push_emotional_state(EmotionalState.Disgusted, 50, origin);
                break;
            case "surprise":
                this.push_emotional_state(EmotionalState.Surprised, 50, origin);
                break;
        }
        return "[EMOTIONAL] Pushed " + emotional_state;
    }

    flush_emotional_state(): void
    {
        this.pipeline = new Array<Emotion>();
        this.emotionalState = EmotionalState.Neutral;
    }

    get_emotional_state() : EmotionalState {
        return this.emotionalState;
    }

    update_emotional_state(): EmotionalState {
        if (this.pipeline.length == 0) {
            return EmotionalState.Neutral;
        }

        type state = {
            identifier: EmotionalState;
            cadence: Number;
        };

        let states: Array<state> = [{
                identifier: EmotionalState.Neutral,
                cadence: 0
            },
            {
                identifier: EmotionalState.Happy,
                cadence: 0
            },
            {
                identifier: EmotionalState.Sad,
                cadence: 0
            },
            {
                identifier: EmotionalState.Angry,
                cadence: 0
            },
            {
                identifier: EmotionalState.Curious,
                cadence: 0
            },
            {
                identifier: EmotionalState.Disgusted,
                cadence: 0
            },
            {
                identifier: EmotionalState.Fearful,
                cadence: 0
            },
            {
                identifier: EmotionalState.Suspicious,
                cadence: 0
            }
        ]

        let highest_cadence: Number = 0;
        let highest_emotion: EmotionalState = EmotionalState.Neutral;

        this.pipeline.forEach(function (e) {
            states.forEach(function (s) {
                if(e.type == s.identifier)
                {
                    s.cadence = +s.cadence + +e.cadence;
                }
                if(s.cadence > highest_cadence)
                {
                    highest_cadence = s.cadence;
                    highest_emotion = s.identifier;
                }
            });
            
        });

        this.emotionalState = highest_emotion;

        if(highest_emotion != this.last_emotional_state)
        {
            this.emit_state();
        }

        this.last_emotional_state = highest_emotion;
        console.log("[EMOTIONAL] Current emotion: " + EmotionalState[highest_emotion]);
        
        this.mimicryEngine.do_emotional_expression(this.emotionalState);
        return highest_emotion;
    }
}

enum EmotionalState {
    Neutral,
    Happy,
    Sad,
    Angry,
    Curious,
    Disgusted,
    Fearful,
    Suspicious,
    Surprised
}

enum EmotionOrigin {
    VerbalWord,
    VerbalTone,
    VisualPerson,
    VisualObject,
    Undefined
}

class Emotion {
    type: EmotionalState;
    origin: EmotionOrigin;
    cadence: number;
    constructor(type: EmotionalState, cadence: number, origin: EmotionOrigin = EmotionOrigin.Undefined) {
        this.type = type;
        this.origin = origin;
        if (cadence < 0 || cadence > 100) {
            throw console.error("Cadence out of bounds");
        } else {
            this.cadence = cadence;
        }
        console.log("[EMOTIONAL] Adding emotion: " + EmotionalState[type] + " with cadence " +cadence + " pocházející z " + EmotionOrigin[origin]);
    }
}


export { EmotionPipeline, EmotionalState, Emotion, EmotionOrigin };