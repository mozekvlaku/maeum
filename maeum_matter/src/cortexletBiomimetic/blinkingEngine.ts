import { EmotionalState, EmotionPipeline } from "../cortexletEmotional/emotionPipeline";
import MotoricManager from "../cortexletMotoric/motoricManager";
import StatefulObject from "../cortexletState/statefulObject";

class BlinkingEngine extends StatefulObject {
    private motoricManager: MotoricManager;
    public IDENTIFIER: string = "vb";
    private emotionPipeline: EmotionPipeline;
    private blinkingEnabled: Boolean = false;
    private blinks: { [index in EmotionalState]?: any } = {
        [EmotionalState.Angry]: [
            {
                openState: 90,
                closedState: 151
            },
            {
                openState: 105,
                closedState: 151
            },
            {
                openState: 75,
                closedState: 169
            }
        ],
        [EmotionalState.Happy]: [
            {
                openState: 44,
                closedState: 151
            },
            {
                openState: 54,
                closedState: 169
            }
        ],
        [EmotionalState.Neutral]: [
            {
                openState: 60,
                closedState: 151
            },
            {
                openState: 54,
                closedState: 169
            }
        ],
        [EmotionalState.Surprised]: [
            {
                openState: 0,
                closedState: 88
            },
            {
                openState: 0,
                closedState: 169
            }
        ],
        [EmotionalState.Curious]: [
            {
                openState: 60,
                closedState: 151
            },
            {
                openState: 54,
                closedState: 169
            }
        ],
        [EmotionalState.Disgusted]: [
            {
                openState: 87,
                closedState: 150
            },
            {
                openState: 40,
                closedState: 169
            }
        ],
        [EmotionalState.Fearful]: [
            {
                openState: 10,
                closedState: 88
            },
            {
                openState: 10,
                closedState: 169
            }
        ],
        [EmotionalState.Sad]: [
            {
                openState: 90,
                closedState: 156
            },
            {
                openState: 60,
                closedState: 169
            }
        ],
        [EmotionalState.Suspicious]: [
            {
                openState: 90,
                closedState: 156
            },
            {
                openState: 60,
                closedState: 169
            }
        ],
    }

    constructor(motoricManager: MotoricManager, emotionPipeline: EmotionPipeline) {
        super();
        this.motoricManager = motoricManager;
        this.emotionPipeline = emotionPipeline;
        var self = this;
        let min: any = 2000;
        let max: any = 5000;
        (function loop() {
            var rand = Math.floor(Math.random() * (max - min + 1) + min);
            setTimeout( () => {
                if(self.blinkingEnabled)
                {
                    self.blink();
                }
                loop();
            }, rand);
        }());
    }

    get_state(): Object {
        return {
            blinking: this.blinkingEnabled,
            
        }
    }

    set_blinking(state: Boolean) : void
    {
        this.blinkingEnabled = state;
    }

    close() : void {
        this.blinkingEnabled = false;
        this.motoricManager.send_single("lids", 170);
    }

    open(): void {
        this.blinkingEnabled = true;
        this.motoricManager.send_single("lids", 170);
    }

    blink() : void {
        let emotion: EmotionalState = this.emotionPipeline.get_emotional_state();
        let blinkCandidates: Array<Object> = this.blinks[emotion];
        let blinkSelected: any = blinkCandidates[Math.floor(Math.random() * blinkCandidates.length)];
        this.motoricManager.send_single("lids", blinkSelected.closedState);
        let min: any = 200;
        let max: any = 500;
        let timeout: any = Math.floor(Math.random() * (max - min + 1) + min);
        setTimeout(()=> {
            this.motoricManager.send_single("lids", blinkSelected.openState);
        }, timeout);
    }

}

export default BlinkingEngine