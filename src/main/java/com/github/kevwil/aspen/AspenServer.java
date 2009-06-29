package com.github.kevwil.aspen;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jruby.*;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author kevinw
 * @since Jun 25, 2009
 */
public class AspenServer
extends RubyObject
{
    // an object responsible for creating instances of a defined class type
    private static ObjectAllocator ALLOCATOR = new ObjectAllocator()
    {
        public IRubyObject allocate( final Ruby runtime, final RubyClass klass )
        {
            return new AspenServer( runtime, klass );
        }
    };

    public static void createAspenServer( final Ruby runtime, final RubyModule mAspen )
    {
        try
        {
            RubyClass aspenServer = mAspen.defineClassUnder( "AspenServer", runtime.getObject(), ALLOCATOR );
            aspenServer.defineAnnotatedMethods( AspenServer.class );
        }
        catch( Exception e )
        {
            e.printStackTrace( System.err );
        }
    }

    private String _host;
    private int _port;
    private boolean _verbose;
    private boolean _running;
    private ServerBootstrap _bootstrap;
    private ChannelGroup _allChannels;
    private ChannelFactory _channelFactory;
    private IRubyObject _rackAdapter;

    public AspenServer( Ruby runtime, RubyClass klass )
    {
        super( runtime, klass );
        _host = "0.0.0.0";
        _port = 80;
        _verbose = false;
        _running = false;
        _rackAdapter = runtime.getNil();
        _allChannels = new DefaultChannelGroup( "aspen-server" );
        _channelFactory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool() );
        _bootstrap = new ServerBootstrap( _channelFactory );
    }

    @JRubyMethod( required = 0, optional = 2 )
    public IRubyObject initialize( final IRubyObject[] args )
    {
        if( args.length > 0 )
            _host = args[0].isNil() ? "0.0.0.0" : args[0].toString();
        if( args.length > 1 )
            _port = args[1].isNil() ? 80 : RubyNumeric.num2int( args[1] );

        _bootstrap.setPipelineFactory( new RackHttpServerPipelineFactory( this ) );

        return this;
    }

    @JRubyMethod( name = "host" )
    public IRubyObject getHost()
    {
        return getRuntime().newString( _host );
    }

    @JRubyMethod( name = "port" )
    public IRubyObject getPort()
    {
        return RubyNumeric.int2fix( getRuntime(), _port );
    }

    @JRubyMethod( name = "verbose=", required = 1 )
    public IRubyObject setVerbose( final IRubyObject verbose )
    {
        _verbose = verbose.isTrue();
        return getRuntime().getNil();
    }

    @JRubyMethod( name = "adapter=", required = 1 )
    public IRubyObject setRackAdapter( final IRubyObject appAdapter )
    {
        _rackAdapter = appAdapter;
        // TODO: pass rack adapter down to ... RackDispatcher???
        return getRuntime().getNil();
    }

    @JRubyMethod
    public IRubyObject start()
    {
        try
        {
            Channel channel = _bootstrap.bind( new InetSocketAddress( _host, _port ) );
            _allChannels.add( channel );
            _running = true;
        }
        catch( Exception e )
        {
            // TODO: throw a proper Ruby error for the app
            e.printStackTrace();
        }
        return getRuntime().getNil();
    }

    @JRubyMethod
    public IRubyObject stop()
    {
        if( _running )
        {
            try
            {
                ChannelGroupFuture future = _allChannels.close();
                future.awaitUninterruptibly();
                _channelFactory.releaseExternalResources();
                _bootstrap.releaseExternalResources();
            }
            catch( Exception e )
            {
                // TODO: throw a proper Ruby error for the app
                e.printStackTrace();
            }
        }
        else
        {
            // TODO: send message | exception ??
        }
        return getRuntime().getNil();
    }

    boolean isVerbose()
    {
        return _verbose;
    }

    IRubyObject getRackAdapter()
    {
        return _rackAdapter;
    }
}
