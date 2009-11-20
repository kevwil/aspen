package com.github.kevwil.aspen;

import org.jruby.RubyString;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Java interface to expose env["rack.errors"] contract
 * @author kevwil
 * @since Nov 20, 2009
 */
public interface RackOutput
{
    /**
     * puts must be called with a single argument that responds to 'to_s'
     * @param context ruby context
     * @param data data to be written
     * @return nil
     */
    IRubyObject puts( ThreadContext context, IRubyObject data );

    /**
     * write must be called with a single argument that is a String.
     * @param context ruby context
     * @param data data to be written
     * @return nil
     */
    IRubyObject write( ThreadContext context, RubyString data );

    /**
     * flush must be called without arguments and must be called in order to make the error appear for sure.
     * @param context ruby context
     * @return vil
     */
    IRubyObject flush( ThreadContext context );

    /**
     * close must never be called on the error stream.
     */
    void close();
}
