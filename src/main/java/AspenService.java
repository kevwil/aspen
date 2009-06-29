import com.github.kevwil.aspen.AspenLoader;
import org.jruby.Ruby;
import org.jruby.runtime.load.BasicLibraryService;

import java.io.IOException;

/**
 * @author kevinw
 * @since Jun 25, 2009
 */
public class AspenService
implements BasicLibraryService
{
    public boolean basicLoad( final Ruby runtime ) throws IOException
    {
        try
        {
            AspenLoader.registerAspen( runtime );
            return true;
        }
        catch( Exception e )
        {
            e.printStackTrace( System.err );
            return false;
        }
    }
}
