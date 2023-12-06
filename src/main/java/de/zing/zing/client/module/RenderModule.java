package de.zing.zing.client.module;

import de.zing.zing.client.util.Pos2f;

//TODO: implement
public abstract class RenderModule extends Module {
    protected Pos2f pos;

    public RenderModule(String name) {
        super(name);
        this.loadSaved();
    }

    abstract void setDefaultPos();

    void loadSaved() {
        //TODO: load from file
        setDefaultPos();
    }
}
