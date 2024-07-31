package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import com.github.kevwil.aspen.domain.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jruby.*;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Jan 07, 2011
 */
@SuppressWarnings("rawtypes")
public class JRubyRackProxyTest
{
    private final Ruby runtime = Ruby.getGlobalRuntime();
    private JRubyRackProxy rack;
    private IRubyObject app;
    private Request r;

    @Before
    public void startUp()
    {
        app = createMock( IRubyObject.class );
        rack = new JRubyRackProxy(app);
        ChannelHandlerContext ctx = RackUtil.buildDummyChannelHandlerContext("localhost", "80");
        FullHttpRequest hr = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/");
        r = new Request(ctx, hr, runtime);
    }

    @After
    public void tearDown()
    {
        verify(app);
    }

    @Test
    public void shouldWriteBodyToResponse()
    {
        replay(app);
        Response response = new Response( r );
        String data = "line one\r\nline two\r\nline three";
        RubyString body = RubyString.newString(runtime, data );

        rack.writeBodyToResponse( body, response );

        assertTrue( response.hasBody() );
        assertFalse( response.hasException() );
        assertEquals( data, response.getBody().toString() );
    }

    @Test
    public void shouldCreateResponseFromRackArray()
    {
        replay(app);
        RubyArray array = RubyArray.newArray(runtime);
        array.add( 200 );
        RubyHash headers = RubyHash.newHash(runtime);
        headers.put( "X-Content", "foo" );
        RubyHash cookies = RubyHash.newHash(runtime);
        cookies.put( "foo_content", "foo" );
        cookies.put( "bar_content", "bar" );
        headers.put( "Cookie", cookies );
        array.add( headers );
        array.add( "Hello World!" );

        Response response = rack.createResponse( r, array );

        assertNotNull( response );
        assertTrue( response.hasBody() );
        assertFalse( response.hasException() );
        assertEquals( 200, response.getResponseStatus().code() );
        assertFalse( response.getHeaderNames().isEmpty() );
        assertEquals( "Hello World!", response.getBody().toString() );
    }

    @Test
    public void shouldCallRackApp()
    {
        RubyArray array = RubyArray.newArray(runtime);
        array.add( 200 );
        RubyHash headers = RubyHash.newHash(runtime);
        headers.put( "X-Content", "foo" );
        array.add( headers );
        array.add( "Hello World!" );

        expect( app.respondsTo( eq( "call" ) ) ).andReturn( true );
        expect( app.callMethod( anyObject( ThreadContext.class ),
                                 eq( "call" ),
                                 anyObject( IRubyObject[].class ),
                                 eq( Block.NULL_BLOCK ) ) ).andReturn( array );
        replay(app);

        Response response = rack.process( r );
        assertNotNull( response );
        assertTrue( response.hasBody() );
        assertFalse( response.hasException() );
        assertEquals( 200, response.getResponseStatus().code() );
        assertFalse( response.getHeaderNames().isEmpty() );
        assertEquals( "Hello World!", response.getBody().toString() );
    }
}
