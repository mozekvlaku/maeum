import axios from "axios";
import StatefulObject from "../cortexletState/statefulObject";
import LongTermManager from "../cortexletMemoric/longTermManager";
import io from 'socket.io-client'

class MotoricManager extends StatefulObject {
    public IDENTIFIER: string = "mm";
    private address: string = "";
    private restAddress: string = "";

    private nestorOnline: Boolean = false;
    private nestorState: any;
    private servoTransformed: any = {};
    private socket: any;
    private servoClassification: {
        [index: string]: any
    } = {


        "lids": {
            "number": 1,
            "side": "virtual",
            "min": 0,
            "max": 180,
            "friendly": "víčka"
        },
        "mouth": {
            "number": 0,
            "side": "virtual",
            "min": 0,
            "max": 180,
            "friendly": "čelist"
        },
        "brows": {
            "number": 0,
            "side": "left",
            "min": 0,
            "max": 180,
            "friendly": "obočí"
        },
        "x_eyes": {
            "number": 7,
            "side": "right",
            "min": 0,
            "max": 180,
            "friendly": "oči (osa x)"
        },
        "y_eyes": {
            "number": 4,
            "side": "right",
            "min": 0,
            "max": 180,
            "friendly": "oči (osa y)"
        },
        "lua": {
            "number": 4,
            "side": "left",
            "min": 0,
            "max": 180,
            "friendly": "levý vrchní aktuátor"
        },
        "lba": {
            "number": 6,
            "side": "left",
            "min": 0,
            "max": 180,
            "friendly": "levý spodní aktuátor"
        },
        "rua": {
            "number": 5,
            "side": "right",
            "min": 0,
            "max": 180,
            "friendly": "pravý vrchní aktuátor"
        },
        "rba": {
            "number": 6,
            "side": "right",
            "min": 0,
            "max": 180,
            "friendly": "pravý spodní aktuátor"
        },
        "mu": {
            "number": 0,
            "side": "right",
            "min": 0,
            "max": 180,
            "friendly": "horní ret"
        },
        "ml": {
            "number": 15,
            "side": "left",
            "min": 0,
            "max": 180,
            "friendly": "spodní ret"
        },
        "neck": {
            "number": 5,
            "side": "left",
            "friendly": "krk"
        }
    }


    constructor(ltmem: LongTermManager) {

        super()
        let settings = ltmem.get_from_memory("nestorSettings");
        this.address = settings.ipAddress + ":" + settings.wsPort;
        this.restAddress = settings.ipAddress + ":" + settings.port;
        this.socket = io("http://" + this.address);


        // Přijmout snímky ze Socket.io serveru
        this.socket.on('state', (data) => {
            this.nestorState = data
        });

        this.socket.on('connect', () => {
            this.nestorOnline = true;
            this.emit_state("nestor")
        });

        this.socket.on('disconnect', () => {
            this.nestorOnline = false;
            this.emit_state("nestor")
        });
    
        this.socket.on('state', (data: Object) => {
            this.nestorState = data;
            this.emit_state("nestor")
        });

        this.servoTransformed = this.transformServoClassification(this.servoClassification)
       
    }

    get_degrees(callback: any) {
        axios.get("http://" + this.restAddress + "/api/servo_get").then(function (response) {
           
            callback(response)
        }).catch((err) => { console.log(err.message);this.nestorOnline = false })
    }

    change_servo_power(side: String, state: String) {
        this.socket.emit("power", {
            side: side,
            state: state
        })
    }

    get_state(): Object {
        return {
            "nestor_online": this.nestorOnline,
            "servos": this.servoTransformed,
            "nestor_state": this.nestorState
        }
    }

    headmove(where: string) {
        let position: string = "+";
        switch (where) {
            case "left":
                if (Math.floor(Math.random() * 10) in [5, 6, 7, 8, 9])
                {
                    this.socket.emit("inc_servo_direct", {
                        servoid:5,
                        servoinc:"-",
                        servocontroller:"left"
                    })
                }
                //    axios.get("http://" + this.address + "/api/servo_inc/left/5/-")
                if (Math.floor(Math.random() * 10) in [5, 6, 7, 8, 9])
                {
                    this.socket.emit("move_servo_direct_ease", {
                        servoid: 7,
                        servodegrees: 90,
                        speed: 0.4,
                        servocontroller: "right"
                    })
                }
                this.socket.emit("inc_servo_direct", {
                    servoid: 7,
                    servoinc: "+",
                    servocontroller: "right"
                })
                //    axios.get("http://" + this.address + "/api/servo_direct/right/7/90")
                //axios.get("http://" + this.address + "/api/servo_inc/right/7/+")
                break;
            case "right":
                if (Math.floor(Math.random() * 10) in [5, 6, 7, 8, 9])
                {
                    this.socket.emit("inc_servo_direct", {
                        servoid: 5,
                        servoinc: "+",
                        servocontroller: "left"
                    })
                }
                //    axios.get("http://" + this.address + "/api/servo_inc/left/5/+")
                if (Math.floor(Math.random() * 10) in [5, 6, 7, 8, 9])
                {
                    this.socket.emit("move_servo_direct", {
                        servoid: 7,
                        servodegrees: 90,
                        speed: 0.4,
                        servocontroller: "right"
                    })
                }
                this.socket.emit("inc_servo_direct", {
                    servoid: 7,
                    servoinc: "-",
                    servocontroller: "right"
                })
                //    axios.get("http://" + this.address + "/api/servo_direct/right/7/90")
                //axios.get("http://" + this.address + "/api/servo_inc/right/7/-")
                break;
            case "up":
                this.socket.emit("inc_servo_direct", {
                    servoid: 4,
                    servoinc: "+",
                    servocontroller: "right"
                })
                //axios.get("http://" + this.address + "/api/servo_inc/right/4/+")
                break;
            case "down":
                this.socket.emit("inc_servo_direct", {
                    servoid: 4,
                    servoinc: "-",
                    servocontroller: "right"
                })
                //axios.get("http://" + this.address + "/api/servo_inc/right/4/-")
                break;
            default:
                break;
        }

    }

    transformServoClassification(servoClassification: {
        [index: string]: any
    }): {
        [index: string]: any
    } {
        let indexes: string[] = Object.keys(servoClassification)

        let leftSide: any = {}
        let rightSide: any = {}
        let virtualSide: any = {}

        for (let i = 0; i < indexes.length; i++) {
            let {
                number,
                side,
                friendly
            } = servoClassification[indexes[i]];
            friendly = friendly.charAt(0).toUpperCase() + friendly.slice(1);
            switch (side) {
                case "left":
                    leftSide["s"+number] = {
                        "name": indexes[i],
                        "friendly": friendly
                    }
                    break;
                case "right":
                    rightSide["s"+number] = {
                        "name": indexes[i],
                        "friendly": friendly
                    }
                    break;
                case "virtual":
                    virtualSide["s"+number] = {
                        "name": indexes[i],
                        "friendly": friendly
                    }
                    break;
                default:
                    break;
            }
        }
        return {
            "left": leftSide,
            "right": rightSide,
            "virtual": virtualSide
        }
    }

    send_single(servo: string, degrees: Number): void {
        let servoClass = this.servoClassification[servo];
        let obj = {
            servoid: servoClass.number,
            servodegrees: degrees,
            servocontroller: servoClass.side
        }

        this.socket.emit("move_servo_direct", obj)
        // Legacy GET
        //axios.get("http://" + this.address + "/api/servo_direct/" + servoClass.side + "/" + servoClass.number + "/" + degrees).catch((err) => console.log());
    }

    send_single_ease(servo: string, degrees: Number, speed: Number): void {
        let servoClass = this.servoClassification[servo];
        let obj = {
            servoid: servoClass.number,
            servodegrees: degrees,
            servocontroller: servoClass.side,
            speed: speed
        }

        this.socket.emit("move_servo_direct_ease", obj)
        // Legacy GET
        //axios.get("http://" + this.address + "/api/servo_direct/" + servoClass.side + "/" + servoClass.number + "/" + degrees).catch((err) => console.log());
    }

    send_set(set: Object): void {
        let values = Object.values(set);
        let keys = Object.keys(set);
        let post = {};
        let array: Array<Object> = [];

        for (let i = 0; i < values.length; i++) {
            array.push({
                "servo": Number(this.servoClassification[keys[i]].number),
                "side": String(this.servoClassification[keys[i]].side),
                "degrees": Number(values[i])
            })
        }

        post = {
            data: array
        }
        this.socket.emit("move_servo_batch", post)
        // Legacy POST
        //axios.post("http://" + this.restAddress + "/api/servo_batch", post).catch((err) => console.error());

    }
}
export default MotoricManager;