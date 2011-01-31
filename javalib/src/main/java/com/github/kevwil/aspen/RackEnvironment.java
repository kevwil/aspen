package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import org.jruby.RubyHash;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

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
