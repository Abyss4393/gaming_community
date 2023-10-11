package cn.abyss4393.webservice.states;

public enum WebSocketStates {
    WAITING("waiting"),
    ERROR("error");

    public final String states;
    WebSocketStates(String states){
        this.states = states;
    }
}
