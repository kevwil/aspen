package com.github.kevwil.aspen;

import java.io.InputStream;

/**
 * @author kevwil
 * @since Jan 27, 2011
 */
public interface RackEnvironment
{
    InputStream getInput();

    int getContentLength();
}
