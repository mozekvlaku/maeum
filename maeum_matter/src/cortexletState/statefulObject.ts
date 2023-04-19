abstract class StatefulObject {
    public IDENTIFIER: string = "so";
    protected stateChangeCallback:any = null;

    get_state(): Object {
        return { "message": "Not instantiated." }
    }

    emit_state(message: String = "") : String {
        if(this.stateChangeCallback != null)
            this.stateChangeCallback(message);

        return "dsfuhg"
    }

    subscribe_state_change(onChange: () => void)
    {
        this.stateChangeCallback = onChange;
    }
}

export default StatefulObject