import StatefulObject from "../cortexletState/statefulObject";
import fs from 'fs';
class LongTermManager extends StatefulObject {
    public IDENTIFIER: string = "lt";
    private memory: Object;

    constructor() {
        super();
        this.load_memory();
    }

    load_memory() : void {
        this.memory = JSON.parse(fs.readFileSync(__dirname+"/longTermMemory.json").toString());
    }

    get_from_memory(key:string) : any
    {
        return this.memory[key];
    }

    set_value(change: (memToChange:Object) => void){
        change(this.memory);
        
        fs.writeFileSync(__dirname + "/longTermMemory.json", JSON.stringify(this.memory));

        this.emit_state("memchange")
    }

    get_state(): Object {
        return this.memory
    }
}

export default LongTermManager