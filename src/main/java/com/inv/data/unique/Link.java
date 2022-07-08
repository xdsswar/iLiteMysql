package com.inv.data.unique;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("all")
class Link implements Runnable {

    /**
     * A plain internal synchronization object.
     */
    private Object synchLock = new Object();

    /**
     * The lock ID.
     */
    private String id;

    /**
     * The Link listener.
     */
    private LinkListener listener;

    /**
     * The underlying socket Link.
     */
    private Socket socket;

    /**
     * The stream for reading what client sent.
     */
    private InputStream inputStream;

    /**
     * The stream for sending bytes to the client.
     */
    private OutputStream outputStream;

    /**
     * A running flag. When true the Link handling routine is started.
     */
    private boolean running = false;

    /**
     * Secondary thread executing the reading routine.
     */
    private Thread thread;

    /**
     * It builds the Link.
     * @param id The associated lock id.
     * @param socket The underlying socket Link.
     * @param listener A Link listener (required).
     */
    public Link(String id, Socket socket, LinkListener listener) {
        this.id = id;
        this.socket = socket;
        this.listener = listener;
    }

    /**
     * It starts the Link handling routine.
     *
     * @throws IllegalStateException
     *             If the Link routine has already been started.
     */
    public void start() throws IllegalStateException {
        synchronized (synchLock) {
            // Status check.
            if (running) {
                throw new IllegalStateException("Single/Server/" + id
                        + "/Link already started");
            }
            // Running flag update.
            running = true;
            // Starts the secondary thread.
            thread = new Thread(this, "Single/Server/" + id + "/Link");
            thread.setDaemon(true);
            thread.start();
            // Waits for start signal.
            do {
                try {
                    synchLock.wait();
                    break;
                } catch (InterruptedException e) {
                    ;
                }
            } while (true);
        }
    }

    /**
     * It stops the Link handling routine.
     *
     * @throws IllegalStateException
     *             If the Link routine is not started.
     */
    public void stop() throws IllegalStateException {
        synchronized (synchLock) {
            // Status check.
            if (!running) {
                throw new IllegalStateException("Single/Server/" + id
                        + "/Link not started");
            }
            // Running flag update.
            running = false;
            // Issues an interrupt signal to the secondary thread.
            thread.interrupt();
            // Closes any underlying stream.
            try {
                inputStream.close();
            } catch (IOException e) {
                ;
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                ;
            }
            try {
                socket.close();
            } catch (IOException e) {
                ;
            }
            // Waiting for server exiting.
            if (Thread.currentThread() != thread) {
                do {
                    try {
                        thread.join();
                        break;
                    } catch (InterruptedException e) {
                        ;
                    }
                } while (true);
            }
            // Discards references.
            socket = null;
            inputStream = null;
            outputStream = null;
        }
    }

    /**
     * The Link handling routine.
     */
    public void run() {
        // Sends start signal.
        synchronized (synchLock) {
            synchLock.notify();
        }
        // Streams retrieval.
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            stop();
        }
        // Loop.
        while (!Thread.interrupted()) {
            try {
                String message = Notify.read(inputStream);
                String response = listener.messageReceived(this, message);
                if (response == null) {
                    response = "";
                }
                Notify.write(response, outputStream);
            } catch (IOException e) {
                stop();
            }
        }
        // Listener notification.
        listener.linkClosed(this);
    }

}
