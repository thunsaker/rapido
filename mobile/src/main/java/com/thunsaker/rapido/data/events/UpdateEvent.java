package com.thunsaker.rapido.data.events;

import com.thunsaker.android.common.bus.BaseEvent;
import com.thunsaker.rapido.services.UpdateService;
import com.thunsaker.rapido.services.UpdateServiceResult;

import java.util.List;

public class UpdateEvent extends BaseEvent {

    public String updateText;
    public List<UpdateService> updateServices;
    public UpdateServiceResult updateServiceResult;

    public UpdateEvent() {
        this(false, "", UpdateServiceResult.RESULT_OTHER, "", null);
    }

    public UpdateEvent(Boolean result, String resultMessage, UpdateServiceResult resultType, String updateText, List<UpdateService> updateServices) {
        super(result, resultMessage);
        this.updateServiceResult = resultType;
        this.updateText = updateText;
        this.updateServices = updateServices;
    }
}