package com.thunsaker.rapido.data;

import com.google.gson.Gson;
import com.thunsaker.rapido.data.api.model.Bitmark;
import com.thunsaker.rapido.services.UpdateService;

import java.util.ArrayList;
import java.util.List;

public class PendingUpdate {
    public String text;
    public List<UpdateService> services;
    public PickedLocation location;
    public List<Bitmark> links;
    private String originalText;

    public PendingUpdate() {
        services = new ArrayList<>();
        links = new ArrayList<>();
    }

    public PendingUpdate(String text, List<UpdateService> services) {
        super();
        this.text = text;
        this.originalText = text;
        this.services = services;
    }

    public String toString() {
        return toJson(this);
    }

    public static String toJson(PendingUpdate myPendingUpdate) {
        Gson gson = new Gson();
        return myPendingUpdate != null ? gson.toJson(myPendingUpdate, PendingUpdate.class) : "";
    }

    public static PendingUpdate GetPendingUpdateFromJson(String jsonString) {
        Gson gson = new Gson();
        return jsonString != null ? gson.fromJson(jsonString, PendingUpdate.class) : null;
    }
}
