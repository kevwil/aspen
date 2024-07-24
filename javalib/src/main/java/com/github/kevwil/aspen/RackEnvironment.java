package com.github.kevwil.aspen;

import org.jruby.RubyHash;

import java.io.InputStream;

/**
 * @author kevwil
 * @since Jan 27, 2011
 */
public interface RackEnvironment
{
    InputStream getInput();

    int getContentLength();

    RackInput getRackInput();

    void setRackInput( RackInput input );

    RubyHash toRuby();
}
