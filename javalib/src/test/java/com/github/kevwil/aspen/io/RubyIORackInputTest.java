package com.github.kevwil.aspen.io;

import org.jruby.*;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.*;

import static org.easymock.EasyMock.*;
// import static org.easymock.classextension.EasyMock.expect;
// import static org.easymock.classextension.EasyMock.createMock;

/**
 * @author kevwil
 * @since Feb 03, 2011
 */
public class RubyIORackInputTest
{
    private RubyIO _mockIO;
    private static final Ruby _runtime = Ruby.getGlobalRuntime();
    private RubyIORackInput _input;

    @Before
    public void setUp()
    {
        _mockIO = createMock( RubyIO.class );
    }

    @After
    public void tearDown()
    {
        verify( _mockIO );
    }

    @Test
    public void shouldSetBinmode()
    {
        expect( _mockIO.binmode() ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
    }

    @Test
    public void shouldDelegateOnGets()
    {
        expect( _mockIO.binmode() ).andReturn( null );
        expect( _mockIO.gets( anyObject( ThreadContext.class ) ) ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
        _input.gets( _runtime.getCurrentContext() );
    }

    @Test
    public void shouldDelegateOnReadOneArg()
    {
        expect( _mockIO.binmode() ).andReturn( null );
        expect( _mockIO.read( anyObject( ThreadContext.class ), anyObject( IRubyObject.class ) ) ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
        _input.read( _runtime.getCurrentContext(), createArgs( 1 ) );
    }

    @Test
    public void shouldDelegateOnReadTwoArg()
    {
        expect( _mockIO.binmode() ).andReturn( null );
        expect( _mockIO.read( anyObject( ThreadContext.class ), anyObject( IRubyObject.class ), anyObject( IRubyObject.class ) ) ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
        _input.read( _runtime.getCurrentContext(), createArgs( 2 ) );
    }

    @Test
    public void shouldDelegateOnReadThreeArg()
    {
        expect( _mockIO.binmode() ).andReturn( null );
        expect( _mockIO.read( anyObject( ThreadContext.class ) ) ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
        _input.read( _runtime.getCurrentContext(), createArgs( 3 ) );
    }

    @Test
    public void shouldDelegateOnReadNoArg()
    {
        expect( _mockIO.binmode() ).andReturn( null );
        expect( _mockIO.read( anyObject( ThreadContext.class ) ) ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
        _input.read( _runtime.getCurrentContext(), createArgs( 0 ) );
    }

    @Test
    public void shouldDelegateOnEach()
    {

        expect( _mockIO.binmode() ).andReturn( null );
        expect( _mockIO.each_line( anyObject( ThreadContext.class ), anyObject( IRubyObject[].class ), anyObject( Block.class ) ) ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
        _input.each( _runtime.getCurrentContext(), Block.NULL_BLOCK );
    }

    @Test
    public void shouldDelegateOnRewind()
    {
        expect( _mockIO.binmode() ).andReturn( null );
        expect( _mockIO.rewind( anyObject( ThreadContext.class ) ) ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
        _input.rewind( _runtime.getCurrentContext() );
    }

    @Test
    public void shouldDoNothingOnClose()
    {
        expect( _mockIO.binmode() ).andReturn( null );
        replay( _mockIO );

        _input = new RubyIORackInput( _runtime, _mockIO );
        _input.close();
    }

    private IRubyObject[] createArgs( int num )
    {
        IRubyObject[] list = new IRubyObject[num];
        for( int i = 0; i < num; i++ )
        {
            list[i] = RubyString.newEmptyString( _runtime );
        }
        return list;
    }
}
