package com.github.kevwil.aspen.rack;

import org.jruby.RubyArray;
import org.jruby.RubyHash;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public interface RackMiddleware
{
    public RubyArray call( RubyHash env );
}
