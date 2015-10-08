package com.thunsaker.rapido.data.events;

import com.thunsaker.android.common.bus.BaseEvent;

public class BitlyAuthEvent extends BaseEvent {
    public BitlyAuthEvent(Boolean result, String resultMessage) {
        super(result, resultMessage);
    }
}
