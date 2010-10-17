package com.github.kevwil.aspen;

/**
 * thread-safe Netty callback handler
 *
 * By using this base class, you assert that member variables
 * are designed to be OK to share among multiple channels (or
 * there's nothing to share).
 *
 * You need to override the UpstreamHandler method you want to handle.
 *
 * @author kevwil
 * @since Dec. 21, 2009
 */
public class SingleInstanceHandler
extends AspenUpstreamHandlerBase
{
}
