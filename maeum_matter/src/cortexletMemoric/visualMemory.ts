import fs from 'fs';
import CentralDispatcher from "../centralDispatcher";
import { EmotionPipeline } from "../cortexletEmotional/emotionPipeline";
import StatefulObject from "../cortexletState/statefulObject";

class VisualMemory extends StatefulObject {
    public IDENTIFIER            : string = "vm";
    private people_memory        : Array<MPerson>;
    private object_memory        : Array<MObject>;
    private object_class         : any;

    private people_in_view       : Array<String>;
    private person_in_foreground : String;

    private dispatcher           : CentralDispatcher;
    private emotional            : EmotionPipeline;

    private last_array           : Array<String> = [];

    // Implementovat triggery

    constructor(dispatcher: CentralDispatcher, emotional: EmotionPipeline) {
        super();
        this.people_memory = new Array<MPerson>();
        this.object_memory = new Array<MObject>();

        this.people_in_view = new Array<String>();
        this.person_in_foreground = "";

        this.dispatcher = dispatcher;
        this.emotional = emotional;

        this.fill_memory_from_relations();
        this.fill_memory_from_profiles();
    }

    update_object_memory(array: Array<MObject>) : void {
        this.last_array = [];
        this.object_memory.forEach(item => {
            this.last_array.push(item.id);
        });
        let emit:Boolean = false;
        let object:String = "";
        array.forEach(item=> {
            if(!this.last_array.includes(item.id))
            {
                emit = true;
                object = item.id;
            }
        });
        if(emit)
        {
            this.emit_state("object update - " + object);
        }
        this.object_memory = array
    }

    push_object_memory(array: Array<any>): void {
        const objectMap: { [key: string]: MObject } = {};

        for (const element of array) {
            if (element.name in objectMap) {
                objectMap[element.name].count += 1;
            } else {
                const objectClass = this.object_class.find(obj => obj.classification === element.name);
                if (objectClass) {
                    const { relation, cadence, name } = objectClass;
                    objectMap[element.name] = new MObject(name, element.name, element.prob, relation, cadence);
                }
            }
        }

        const objectArr = Object.values(objectMap);
        this.update_object_memory(objectArr);
    }

    get_state(): Object {
        return {
            memory: {
                people: this.people_memory,
                objects: this.object_memory
            },
            view: {
                people: this.people_in_view,
                foreground: this.person_in_foreground
            }
        }
    }

    fill_memory_from_relations(): void {
        this.object_class = JSON.parse(fs.readFileSync(__dirname+"/objectEmotionalClassifications.json"));
    }

    fill_memory_from_profiles(): void {
        let profile_uri: string = __dirname + "/people/";
        fs.readdirSync(profile_uri).forEach((file: string) => {
            if(file.endsWith(".json"))
            {
                let array: any = JSON.parse(fs.readFileSync(profile_uri + file));
                let tmp: MPerson = new MPerson(array.name, array.id, 1);
                tmp.maeum_emotional_relation = array.maeum_emotional_relation;
                this.people_memory.push(tmp);
            }
        });
    }

    get_foreground_person() : String {
        console.log(this.person_in_foreground);
        return this.get_from_memory(this.person_in_foreground, MaeumObjectEnum.MPerson).name;
    }

    viewing_person(id: String): void {
        // Check if person exists
        let exists:boolean = this.is_in_memory(id, MaeumObjectEnum.MPerson);

        if(exists) {
            this.people_in_view.push(id);
            console.log(id);
        }

        if(!exists) {
            this.emit_state("person viewing");
            this.dispatcher.add(
                "MT", "MT", "matter/verbal/askname", "string::name", "matter/visual/person/add/:name", "MT"
            )
        }
    }

    update_emotions(id: String, emotion: string): void {
        this.person_in_foreground = id;
        console.log("[VISUAL] Updating " + id + " to emotion " + emotion);
        console.log(this.emotional.push_emotional_state_from_vm(emotion));
        this.people_memory.forEach((value: MPerson) => {
            if (value.id == id) {
                value.emotion_current = emotion;
            }
        });
    }

    add_to_memory(object:MaeumObject): String {
        let new_id:String = "N/A";
        

        if(object instanceof MPerson) {
            if (!this.is_in_memory(object.id, MaeumObjectEnum.MPerson)) {
                new_id = object.id;
                this.people_memory.push(object);
                fs.writeFileSync("./memory/people/" + object.id + ".json", JSON.stringify({
                    name: object.name,
                    id: object.id,
                    maeum_emotional_relation: object.maeum_emotional_relation
                }));
                console.log("Adding person " + object.name + " with ID " + object.id);
            }
            
        }
        if (object instanceof MObject) {
            new_id = object.id;
            this.object_memory.push(object);
        }
        return new_id;
    }

    remove_from_memory(id:String, type:MaeumObjectEnum): void{
        if (type == MaeumObjectEnum.MObject) {
            this.object_memory.forEach((value: MaeumObject, index:number) => {
                if (value.id == id) {
                    this.object_memory.splice(index, 1);
                }
            });
        }
        if (type == MaeumObjectEnum.MPerson) {
            this.people_memory.forEach((value: MaeumObject, index:number) => {
                if (value.id == id) {
                    this.people_memory.splice(index, 1);
                }
            });
        }
    }

    is_in_memory(id:String, type:MaeumObjectEnum): boolean {
        let is_it:boolean = false;
        if (type == MaeumObjectEnum.MObject) {
            this.object_memory.forEach((value: MaeumObject) => {
                if (value.id == id) {
                    is_it = true;
                }
            });
        }
        if (type == MaeumObjectEnum.MPerson) {
            this.people_memory.forEach((value: MaeumObject) => {
                if (value.id == id) {
                    is_it = true;
                }
            });
        }
        return is_it;
    }

    get_from_memory(id:String, type:MaeumObjectEnum): MaeumObject {
        let o: MaeumObject = new MaeumObject("Nikdo", "Nic", 0);
        let in_memory: Boolean = false;
        if(type == MaeumObjectEnum.MObject)
        {
            this.object_memory.forEach((value:MaeumObject) => {
                if(value.id == id)
                {
                    o = value;
                }
            });
        }
        if(type == MaeumObjectEnum.MPerson)
        {
            this.people_memory.forEach((value: MaeumObject) => {
                if (value.id == id) {
                    o = value;
                    in_memory = true;
                }
            });

            if(!in_memory) {
                this.dispatcher.add(
                    "MT", "MT", "/matter/verbal/askname", "string::name", "/matter/visual/person/add/:name", "MT"
                )
            }
        }
        return o;
    }
}

class MaeumObject {
    name: String;
    id: String;
    probability: Number;
    constructor(name:String, id:String, probability:Number)
    {
        this.name = name;
        this.id = id;
        this.probability = probability;
    }
}

enum MaeumObjectEnum {
    MObject,
    MPerson
}

class MPerson extends MaeumObject {
    emotion_current: String = "neutral";
    // can be: positive, neutral, negative
    maeum_emotional_relation: String = "neutral";
    
    super(name: String, id: String, probability: Number) {
        this.name = name;
        this.id = id;
        this.probability = probability;
    }
}

class MObject extends MaeumObject {
    relation: String;
    count: number = 1;
    cadence: number;
    constructor(name: String, id: String, probability: Number, relation: String = "neutral", cadence: number = 0) {
        super(name, id, probability);
        this.relation = relation;
        this.cadence = cadence;
    }
}


export { VisualMemory, MPerson, MObject };