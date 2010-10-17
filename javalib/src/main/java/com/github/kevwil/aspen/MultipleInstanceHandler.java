package com.github.kevwil.aspen;

import org.jboss.netty.channel.ChannelHandler;

/**
 * Non-thread-safe Netty callback handler
 *
 * "one" means you must create a new instance of
 * this handler type for each new channel. It means
 * the member variables of the handler instance can not be
 * shared at all, and violating this contract will
 * lead the handler to a race condition. 
 *
 * You need to override the UpstreamHandler method you want to handle.
 *
 * @author kevwil
 * @since Dec. 21, 2009
 */
@ChannelHandler.Sharable
public class MultipleInstanceHandler
extends AspenUpstreamHandlerBase
{
}
