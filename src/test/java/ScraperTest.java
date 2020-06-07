import com.google.common.io.Resources;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertTrue;

public class ScraperTest  {

    @Test
    public void testRead() throws IOException {
        String pwd = Resources.toString(Resources.getResource("password"), Charset.defaultCharset());
        assertTrue(!pwd.isEmpty());
    }
}