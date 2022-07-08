package com.inv.data.unique;

import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @author XDSSWAR
 */
class Lock {

    /**
     * The lock id.
     */
    private String id;

    /**
     * The lock file.
     */
    private File lockFile;

    /**
     * The port file.
     */
    private File portFile;

    /**
     * The channel associated to the lock file.
     */
    private FileChannel lockFileChannel;

    /**
     * The file lock taken on the lock file.
     */
    private FileLock lockFileLock;

    /**
     * The server handling message reception for this lock.
     */
    private Server server;

    /**
     * It builds the lock representation.
     *
     * @param id
     *            The lock id.
     * @param lockFile
     *            The lock file.
     * @param lockFileChannel
     *            The channel associated to the lock file.
     * @param lockFileLock
     *            The file lock taken on the lock file.
     * @param server
     *            The server handling message reception for this lock.
     */
    Lock(String id, File lockFile, File portFile, FileChannel lockFileChannel,
         FileLock lockFileLock, Server server) {
        super();
        this.id = id;
        this.lockFile = lockFile;
        this.portFile = portFile;
        this.lockFileChannel = lockFileChannel;
        this.lockFileLock = lockFileLock;
        this.server = server;
    }

    /**
     * It returns the lock id.
     *
     * @return The lock id.
     */
    public String getId() {
        return id;
    }

    /**
     * It returns the lock file.
     *
     * @return The lock file.
     */
    public File getLockFile() {
        return lockFile;
    }

    /**
     * It returns the port file.
     *
     * @return The port file.
     */
    public File getPortFile() {
        return portFile;
    }

    /**
     * It returns the channel associated to the lock file.
     *
     * @return The channel associated to the lock file.
     */
    public FileChannel getLockFileChannel() {
        return lockFileChannel;
    }

    /**
     * It returns the file lock taken on the lock file.
     *
     * @return The file lock taken on the lock file.
     */
    public FileLock getLockFileLock() {
        return lockFileLock;
    }

    /**
     * It returns the server handling message reception for this lock.
     *
     * @return The server handling message reception for this lock.
     */
    public Server getServer() {
        return server;
    }

}