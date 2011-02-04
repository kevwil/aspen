package com.github.kevwil.aspen.io;

import com.github.kevwil.aspen.RackEnvironment;
import com.github.kevwil.aspen.RackInput;
import org.jruby.*;
import org.jruby.anno.JRubyMethod;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author nicksieger
 * @author kevwil
 * @since Jan 27, 2011
 */
public abstract class RackBaseInput
extends RubyObject
implements RackInput
{
    public static RubyClass getClass(
            Ruby runtime,
            String name,
            RubyClass parent,
            ObjectAllocator allocator,
            Class annoClass)
    {
        RubyModule jrubyMod = runtime.getOrCreateModule( "JRuby" );
        RubyClass klass = jrubyMod.getClass( name );
        if( klass == null )
        {
            klass = jrubyMod.defineClassUnder( name, parent, allocator );
            klass.defineAnnotatedMethods( annoClass );
        }
        return klass;
    }

    public static RubyClass getRackBaseInputClass( Ruby runtime )
    {
        return getClass( runtime, "RackBaseInput", runtime.getObject(),
                ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR, RackBaseInput.class );
    }

    protected InputStream inputStream;
    protected RackInput delegateInput;
    protected int contentLength = -1;

    public RackBaseInput( Ruby runtime, RubyClass klass )
    {
        super( runtime, klass );
    }

    public RackBaseInput( Ruby runtime, RubyClass klass, RackEnvironment env )
            throws IOException
    {
        super( runtime, klass );
        inputStream = env.getInput();
        contentLength = env.getContentLength();
    }

    @JRubyMethod()
    public IRubyObject gets( ThreadContext context )
    {
        return getDelegateInput().gets( context );
    }

    @JRubyMethod( optional = 2 )
    public IRubyObject read( ThreadContext context, IRubyObject[] args )
    {
        return getDelegateInput().read( context, args );
    }

    @JRubyMethod()
    public IRubyObject each( ThreadContext context, Block block )
    {
        return getDelegateInput().each( context, block );
    }

    @JRubyMethod()
    public IRubyObject rewind( ThreadContext context )
    {
        return getDelegateInput().rewind( context );
    }

    public void close()
    {
        if( delegateInput != null )
        {
            delegateInput.close();
        }
        delegateInput = null;
    }

    protected abstract RackInput getDelegateInput();

    /*
     * For testing, to allow the input stream to be set from Ruby.
     */
    @JRubyMethod( name = "stream=", visibility = Visibility.PRIVATE )
    public IRubyObject set_stream( IRubyObject stream )
    {
        Object obj = JavaEmbedUtils.rubyToJava( stream );
        if( obj instanceof InputStream )
        {
            inputStream = (InputStream) obj;
        }
        return getRuntime().getNil();
    }

    /*
     * For testing, to allow the content length to be set from Ruby.
     */
    @JRubyMethod( name = "content_length=", visibility = Visibility.PRIVATE )
    public IRubyObject set_content_length( IRubyObject len )
    {
        this.contentLength = (int) len.convertToInteger().getLongValue();
        return getRuntime().getNil();
    }
}
