import org.junit.*;
import play.twirl.api.Content;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.contentAsString;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertTrue(a==2);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertTrue(html.contentType().equals("text/html"));
        assertTrue(contentAsString(html).contains("Your new application is ready."));
    }


}
