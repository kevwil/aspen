package com.github.kevwil.aspen.input;

import com.github.kevwil.aspen.RackInput;
import com.github.kevwil.aspen.RackEnvironment;
import org.jruby.*;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.FilterInputStream;
import java.io.IOException;

/**
 * @author nicksieger
 * @author kevwil
 * @since Jan 27, 2011
 */
public class RackNonRewindableInput
extends RackBaseInput
{
    private static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
        public IRubyObject allocate( Ruby runtime, RubyClass klass ) {
            return new RackNonRewindableInput( runtime, klass );
        }
    };

    public static RubyClass getRackNonRewindableInputClass( Ruby runtime )
    {
        return RackBaseInput.getClass( runtime, "RackNonRewindableInput",
                RackBaseInput.getRackBaseInputClass( runtime ), ALLOCATOR,
                RackNonRewindableInput.class );
    }

    public RackNonRewindableInput( Ruby runtime, RubyClass klass )
    {
        super( runtime, klass );
    }

    public RackNonRewindableInput( Ruby runtime, RackEnvironment env )
    throws IOException
    {
        super( runtime, getRackNonRewindableInputClass( runtime ), env );
    }

    @Override
    protected RackInput getDelegateInput()
    {
        if( delegateInput == null )
        {
            // override close so we don't actually close the servlet input stream
            FilterInputStream filterStream = new FilterInputStream( inputStream )
            {
                @Override
                public void close()
                {
                }
            };
            delegateInput = new RubyIORackInput(
                    getRuntime(), new RubyIO( getRuntime(), filterStream ) );
        }
        return delegateInput;
    }
}
