package com.github.kevwil.aspen
/**
 * @author kevinw
 * @since Apr 2, 2009
 */

class AspenServer(conf:ServerConfig)
{
    private var r:Boolean = false
    
    def running = r
    def running_= (isRunning:Boolean) = r = isRunning
    
    def stop
    {
        r = false
    }
}