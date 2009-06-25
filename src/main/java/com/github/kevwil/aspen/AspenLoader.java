package com.github.kevwil.aspen;

import org.jruby.*;

/**
 * @author kevinw
 * @since Jun 25, 2009
 */
public class AspenLoader
{
    public static void registerAspen( final Ruby runtime )
    {
        RubyModule mAspen = runtime.getOrCreateModule( "Aspen" );
        AspenServer.createAspenServer( runtime, mAspen );
    }
}
