package com.github.kevwil.aspen;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

public class ServerBootstrapFactory {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public ServerBootstrap newServerBootstrap(int ioThreadCount) {
        if (Epoll.isAvailable()) {
            return newEpollServerBootstrap(ioThreadCount);
        }
        return newNioServerBootstrap(ioThreadCount);
    }

    public void shutdownGracefully(boolean shouldWait) {
        Future<?> workerFuture = workerGroup.shutdownGracefully();
        Future<?> bossFuture = bossGroup.shutdownGracefully();
        if (shouldWait) {
            workerFuture.awaitUninterruptibly();
            bossFuture.awaitUninterruptibly();
        }
    }

    private ServerBootstrap newEpollServerBootstrap(int ioThreadCount) {
        if (ioThreadCount > 0) {
            bossGroup = new MultiThreadIoEventLoopGroup(ioThreadCount, EpollIoHandler.newFactory());
            workerGroup = new MultiThreadIoEventLoopGroup(ioThreadCount, EpollIoHandler.newFactory());
        } else {
            bossGroup = new MultiThreadIoEventLoopGroup(EpollIoHandler.newFactory());
            workerGroup = new MultiThreadIoEventLoopGroup(EpollIoHandler.newFactory());
        }
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(EpollServerSocketChannel.class);
    }

    private ServerBootstrap newNioServerBootstrap(int ioThreadCount) {
        if (ioThreadCount > 0) {
            bossGroup = new MultiThreadIoEventLoopGroup(ioThreadCount, EpollIoHandler.newFactory());
            workerGroup = new MultiThreadIoEventLoopGroup(ioThreadCount, EpollIoHandler.newFactory());
        } else {
            bossGroup = new MultiThreadIoEventLoopGroup(EpollIoHandler.newFactory());
            workerGroup = new MultiThreadIoEventLoopGroup(EpollIoHandler.newFactory());
        }
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class);
    }

}
