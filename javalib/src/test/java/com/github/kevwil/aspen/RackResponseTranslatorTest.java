package com.github.kevwil.aspen;

import org.jboss.netty.handler.codec.http.*;
import org.jruby.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

/**
 * @author kevwil
 * @since Oct 16, 2009
 */
public class RackResponseTranslatorTest
{
    private Ruby runtime;

    @Before
    public void setUp()
    {
        runtime = Ruby.getGlobalRuntime();
    }

    @Test
    public void shouldTranslateHelloWorldMessage() throws Exception
    {
        String[] message = {"Hello World!"};
        RubyArray rack_response = createRackResponse( 200, new HashMap<String,String>(), message );

        HttpResponse response = RackResponseTranslator.translate( rack_response );

        assertEquals( HttpResponseStatus.OK, response.getStatus() );
        assertTrue( response.getHeaderNames().isEmpty() );
        assertEquals( message[0], response.getContent().toString( "UTF-8" ) );
    }

    @Test
    public void shouldHandleMultiLintBodies() throws Exception
    {
        String[] message = {"Hello ","World!"};
        RubyArray rack_response = createRackResponse( 200, new HashMap<String,String>(), message );

        HttpResponse response = RackResponseTranslator.translate( rack_response );

        assertEquals( HttpResponseStatus.OK, response.getStatus() );
        assertTrue( response.getHeaderNames().isEmpty() );
        assertEquals( message[0]+message[1], response.getContent().toString( "UTF-8" ) );
    }

    @Test
    public void shouldPassContentTypeHeader() throws Exception
    {
        String[] message = {"Hello World!"};
        Map<String,String> headers = new HashMap<String,String>();
        headers.put( "CONTENT_TYPE", "text/css" );
        RubyArray rack_response = createRackResponse( 200, headers, message );

        HttpResponse response = RackResponseTranslator.translate( rack_response );

        assertEquals( HttpResponseStatus.OK, response.getStatus() );
        assertTrue( response.getHeaderNames().contains( "CONTENT_TYPE" ) );
        assertEquals( message[0], response.getContent().toString( "UTF-8" ) );
    }

    private RubyArray createRackResponse( int status, Map<String,String> headers, String[] body )
    {
        RubyHash hash = RubyHash.newHash( runtime );
        for( String key : headers.keySet() )
        {
            hash.put(
                    RubyString.newString( runtime, key ),
                    RubyString.newString( runtime, headers.get( key ) ) );
        }
        RubyArray bodies = RubyArray.newArray( runtime );
        for( String s : body )
        {
            bodies.append( RubyString.newString( runtime, s ) );
        }
        return RubyArray.newArray( runtime )
                .append( runtime.newFixnum( status ) )
                .append( hash )
                .append( bodies );
    }
}
