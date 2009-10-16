package com.github.kevwil.aspen;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.*;

import java.util.*;

/**
 *
 * @author kevwil
 */
public class RackResponseTranslator
{
    static HttpResponse translate(RubyArray rackOutput) throws RackException
    {
        if( rackOutput.size() != 3 )
        {
            throw new RackException("received output not in the form of a 3-element array");
        }

        int status = RubyInteger.num2int(rackOutput.entry(0).convertToInteger());
        Map<String,String> headers = new HashMap<String,String>();
        RubyHash rackHash = rackOutput.entry(1).convertToHash();
        for( Object key : rackHash.keySet() )
        {
            headers.put( key.toString(), rackHash.get( key ).toString() );
        }
        String body = rackOutput.entry(2).asJavaString();

        HttpResponse response = new DefaultHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(status));
        for( String key : headers.keySet() )
        {
            response.addHeader(key, headers.get(key));
        }
        response.setContent(ChannelBuffers.copiedBuffer(body, "UTF-8")); // need correct content-type
        return response;
    }
}
