import axios from "axios";
import StatefulObject from "../cortexletState/statefulObject";
import LongTermManager from "../cortexletMemoric/longTermManager";

class MotoricManager extends StatefulObject {
    public IDENTIFIER: string = "mm";
    private address: string = "";
    private servoClassification: { [index: string]: any } = {
        "lids": {
            number: 1,
            side: "virtual",
            min: 0,
            max: 180
        },
        "mouth": {
            number: 0,
            side: "virtual",
            min: 0,
            max: 180
        },
        "brows": {
            number: 0,
            side: "left",
            min: 0,
            max: 180
        },
        "x_eyes": {
            number: 7,
            side: "right",
            min: 0,
            max: 180
        },
        "y_eyes": {
            number: 4,
            side: "right",
            min: 0,
            max: 180
        },
        "lua": {
            number: 4,
            side: "left",
            min: 0,
            max: 180
        },
        "lba": {
            number: 6,
            side: "left",
            min: 0,
            max: 180
        },
        "rua": {
            number: 5,
            side: "right",
            min: 0,
            max: 180
        },
        "rba": {
            number: 6,
            side: "right",
            min: 0,
            max: 180
        },
        "mu": {
            number: 0,
            side: "right",
            min: 0,
            max: 180
        },
        "ml": {
            number: 15,
            side: "left",
            min: 0,
            max: 180
        },
        "neck": {
            number: 5,
            side: "left"
        }
    }

    constructor(ltmem: LongTermManager) {
       
        super()
        let settings = ltmem.get_from_memory("nestorSettings");
        this.address = settings.ipAddress + ":" + settings.port;
    }

    get_state(): Object {
        return {
            "nestor_online": true
        }
    }

    headmove(where: string) {
        let position:string = "+";
        switch(where) {
            case "left":
                if (Math.floor(Math.random() * 10) in [5,6,7,8,9])
                    axios.get("http://" + this.address + "/api/servo_inc/left/5/-")
                if (Math.floor(Math.random() * 10) in [5, 6, 7, 8, 9])
                    axios.get("http://" + this.address + "/api/servo_direct/right/7/90")
                axios.get("http://" + this.address + "/api/servo_inc/right/7/+")
            break;
            case "right":
                if (Math.floor(Math.random() * 10) in [5, 6, 7, 8, 9])
                    axios.get("http://" + this.address + "/api/servo_inc/left/5/+")
                if (Math.floor(Math.random() * 10) in [5, 6, 7, 8, 9])
                    axios.get("http://" + this.address + "/api/servo_direct/right/7/90")
                axios.get("http://" + this.address + "/api/servo_inc/right/7/-")
            break;
            case "up":
                //axios.get("http://" + this.address + "/api/servo_inc/left/5/-")
                axios.get("http://" + this.address + "/api/servo_inc/right/4/+")
                break;
            case "down":
                //axios.get("http://" + this.address + "/api/servo_inc/left/5/+")
                axios.get("http://" + this.address + "/api/servo_inc/right/4/-")
                break;
            default: break;
        }
        
    }

    send_single(servo:string, degrees: Number) :void {
        let servoClass = this.servoClassification[servo];
        axios.get("http://" + this.address + "/api/servo_direct/"+servoClass.side+"/"+servoClass.number+"/"+degrees).catch((err)=>console.log());
    }

    send_set(set: Object): void {
        let values = Object.values(set);
        let keys = Object.keys(set);
        let post = {};
        let array = [];

        for(let i = 0; i < values.length; i++)
        {
            array.push({
                servo: this.servoClassification[keys[i]].number,
                side: this.servoClassification[keys[i]].side,
                degrees: values[i]
            });
        }

        post = {
            data: array
        }
        axios.post("http://" + this.address + "/api/servo_batch", post).catch((err)=>console.error());
        
    }
}
export default MotoricManager;