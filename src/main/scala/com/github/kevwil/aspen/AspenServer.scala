package com.github.kevwil.aspen
/**
 * @author kevinw
 * @since Apr 2, 2009
 */

class AspenServer(conf:ServerConfig)
{
    private var r:Boolean = false
    private var i:Boolean = false
    
    def running = r

    def init()
    {
        i = true
    }
    
    def stop
    {
        if(!i) throw new IllegalStateException("server not initialized")
        r = false
    }

    def start
    {
        if(!i) throw new IllegalStateException("server not initialized")
        r = true
    }

    def kill
    {
        stop
    }
}