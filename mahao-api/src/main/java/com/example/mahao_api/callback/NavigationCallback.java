package com.example.mahao_api.callback;

import com.example.mahao_api.PostCard;

public interface NavigationCallback {

    /**
     * when find
     *
     * @param postCard
     */
    void onFound(PostCard postCard);

    /**
     * on lose
     */
    void onLost(PostCard postCard);

    /**
     * on arrival
     *
     * @param postCard
     */
    void onArrival(PostCard postCard);

    /**
     * on Interrupt
     *
     * @param postCard
     */
    void onInterrupt(PostCard postCard);
}
