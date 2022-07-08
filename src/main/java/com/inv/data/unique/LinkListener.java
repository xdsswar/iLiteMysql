package com.inv.data.unique;



interface LinkListener {

    /**
     * This method is called when an incoming message is received.
     *
     * @param link
     *            The source connection.
     * @param message
     *            The message received.
     * @return An optional response (may be null).
     */
    public String messageReceived(Link link, String message);

    /**
     * This method is called to notify that the connection with the remote side
     * has been closed.
     *
     * @param link
     *            The source connection.
     */
    public void linkClosed(Link link);

}
