package com.github.chain5j.protocol.rx;

import com.github.chain5j.protocol.websocket.events.NewHeadsNotification;
import io.reactivex.Flowable;

/**
 * The Flowables JSON-RPC client event API.
 */
public interface Chain5jRx {
    // ============chain5j=========
    /**
     * Creates a {@link Flowable} instance that emits a notification when a new header is appended
     * to a chain, including chain reorganizations.
     *
     * @return a {@link Flowable} instance that emits a notification for every new header
     */
    Flowable<NewHeadsNotification> newHeadsNotifications();

}
