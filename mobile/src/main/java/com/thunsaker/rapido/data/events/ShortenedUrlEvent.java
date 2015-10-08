package com.thunsaker.rapido.data.events;

import com.thunsaker.android.common.bus.BaseEvent;
import com.thunsaker.rapido.data.api.model.Bitmark;

public class ShortenedUrlEvent extends BaseEvent {
    public Bitmark bitmark;
    public String longUrl;

    /**
     *
     * @param bitmark       Result of link shortening
     */
    public ShortenedUrlEvent(Boolean result, String resultMessage, Bitmark bitmark, String longUrl) {
        super(result, resultMessage);
        this.bitmark = bitmark;
        this.longUrl = longUrl;
    }
}