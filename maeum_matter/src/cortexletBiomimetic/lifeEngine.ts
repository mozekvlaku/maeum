import { EmotionManager, EmotionalState } from "../cortexletEmotional/emotionManager";
import LongTermManager from "../cortexletMemoric/longTermManager";
import { VisualMemory } from "../cortexletMemoric/visualMemory";
import MotoricManager from "../cortexletMotoric/motoricManager";
import StatefulObject from "../cortexletState/statefulObject"

class LifeEngine extends StatefulObject {
    public IDENTIFIER: string = "le";
    private activatedLooking: Boolean = true;
    private emotionalEngine: EmotionManager;
    private longTermMemory: LongTermManager;
    private motoricEngine: MotoricManager;
    private visualMemory: VisualMemory;

    private x_axis: number;
    private x_eyes: number;
    private can_do_headmove: Boolean;

    private plus_minus_counter_eyes: number = 0;
    private plus_minus_max_eyes: Number = 2;
    private plus_minus_counter_neck: number = 0;
    private plus_minus_max_neck: Number = 5;
    private plussing_neck: Boolean;
    private plussing_eyes: Boolean;

    constructor (emotionalEngine: EmotionManager, visualMemory:VisualMemory, longTermMemory: LongTermManager, motoricEngine: MotoricManager) {
        super()
        this.emotionalEngine = emotionalEngine;
        this.longTermMemory = longTermMemory;
        this.motoricEngine = motoricEngine;
        this.visualMemory = visualMemory;
        this.calibrate();
        this.activatedLooking = this.longTermMemory.get_from_memory("biomimeticSettings").lifestate;
        this.plussing_neck = Math.random() < 0.5;
        this.plussing_eyes = Math.random() < 0.5;
        this.emit_state("Calibrate");
        let self = this;
        let min: any = 700;
        let max: any = 1400;
        this.can_do_headmove = false;
        (function loop() {
            var rand = Math.floor(Math.random() * (max - min + 1) + min);
            setTimeout(() => {
                let viewing = self.visualMemory.in_foreground();
                
                if(viewing == "" || viewing == "Unknown")
                {
                    if(self.can_do_headmove == false)
                    {
                        self.motoricEngine.get_degrees((response) => {
                            self.x_eyes = response.right[7];
                            self.x_axis = response.left[5];
                        })
                    }
                    self.can_do_headmove = true;
                }
                else
                {
                    self.can_do_headmove = false;
                }
                if (self.activatedLooking && self.can_do_headmove) {
                    self.look_around();
                }
                loop();
            }, rand);
        }());
      
    }

    get_state(): Object {
        return {
            "activated_looking": this.activatedLooking,
            "can_do_headmove": this.can_do_headmove,
            "x_eyes": this.x_eyes,
            "x_axis": this.x_axis
        }
    }

    calibrate(): void {
            this.x_axis = 90;
            this.motoricEngine.send_single_ease("neck", 90, 0.9);
            this.x_eyes = 90;
            this.motoricEngine.send_single_ease("x_eyes", 90, 0.9);
        
    }

    look_around(): void {
        let neck_max = 20;
        let neck_min = 1;
        let eyes_max = 20;
        let eyes_min = 1;
        let plusminus_eyes = 3;
        let plusminus_neck = 3;

        switch(this.emotionalEngine.get_state())
        {
            case EmotionalState.Angry:
                neck_max = 50
                neck_min = 40;
                eyes_max = 50
                eyes_min = 30;
                plusminus_eyes = 1;
                plusminus_neck = 1;
            break;
            case EmotionalState.Happy:
                neck_max = 23
                neck_min = 1;
                eyes_max = 20
                eyes_min = 1;
                plusminus_eyes = 3;
                plusminus_neck = 3;
                break;
            case EmotionalState.Sleepy:
                neck_max = 2;
                neck_min = 1;
                eyes_max = 2;
                eyes_min = 1;
                plusminus_eyes = 3;
                plusminus_neck = 1;
                break;
            case EmotionalState.Surprised:
                neck_max = 40;
                neck_min = 30;
                eyes_max = 80
                eyes_min = 60;
                plusminus_eyes = 1;
                plusminus_neck = 1;
                break;
            case EmotionalState.Fearful:
                neck_max = 2;
                neck_min = 1;
                eyes_max = 2;
                eyes_min = 1;
                plusminus_eyes = 5;
                plusminus_neck = 5;
                break;
        }


        if (this.plus_minus_counter_eyes == this.plus_minus_max_eyes) {
            this.plussing_eyes = !this.plussing_eyes;
            this.plus_minus_counter_eyes = 0;
            this.plus_minus_max_eyes = Math.floor(Math.random() * (plusminus_eyes - 1 + 1) + 1)
        }
        else {
            this.plus_minus_counter_eyes++
        }
     


        if (this.plussing_eyes) {
            this.x_eyes = this.x_eyes + Math.floor(Math.random() * (eyes_max - eyes_min + 1) + eyes_min)
        }
        else {
            this.x_eyes = this.x_eyes - Math.floor(Math.random() * (eyes_max - eyes_min + 1) + eyes_min)
        }


        if (this.x_eyes > 120) {
            this.plussing_eyes = false;
            this.x_eyes = 120;
        }

        if (this.x_eyes < 60) {
            this.plussing_eyes = true;
            this.x_eyes = 60
        }

        console.log(this.x_eyes)
        this.motoricEngine.send_single_ease("x_eyes", Math.round(this.x_eyes), 0.3)


        if (Math.random() < 0.3)
        {
            if (this.plus_minus_counter_neck == this.plus_minus_max_neck) {
                this.plussing_neck = !this.plussing_neck;
                this.plus_minus_counter_neck = 0;
                this.plus_minus_max_neck = Math.floor(Math.random() * (plusminus_neck - 1 + 1) + 1)
            }
            else {
                this.plus_minus_counter_neck++
            }


            if (this.x_axis > 120) {
                this.plussing_neck = false;
                this.x_axis = 120;
            }

            if (this.x_axis < 60) {
                this.plussing_neck = true;
                this.x_axis = 60
            }


            // Koukat očima do strany stejný
            //this.plussing_eyes = this.plussing_neck;

            if (this.plussing_neck) {
                this.x_axis += (Math.floor(Math.random() * (neck_max - 1 + neck_min) + neck_min));
            }
            else {
                this.x_axis -= (Math.floor(Math.random() * (neck_max - 1 + neck_min) + neck_min));
            }
            this.motoricEngine.send_single_ease("neck", Math.round(this.x_axis), 0.3)
        }
            

    }

    set_life(state: Boolean): void {
        this.activatedLooking = state;
        this.longTermMemory.set_value((memoryObject) => {
            memoryObject["biomimeticSettings"].lifestate = state
        });
        this.emit_state("change")
    }

}

export default LifeEngine