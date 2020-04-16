import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GUITest {
  @Test
  void browseTest() {
    try (final WebClient webClient = new WebClient()) {
      webClient.getOptions().setCssEnabled(false);
      final HtmlPage page = webClient.getPage("http://localhost:8080/browse_projects");
      assertElementExists(page, "title");
      assertElementExists(page, "status");
      assertElementExists(page, "endd");
      assertElementExists(page, "submit-btn");

      page.getElementById("endd").setTextContent("2021-01-01");
      page.getElementById("submit-btn").click();
      Thread.sleep(500);
      HtmlPage resultPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
      assertEquals(
          "http://localhost:8080/browse_projects?title=&status=Any&endd=2021-01-01",
          resultPage.getBaseURL().toString());
      assertElementExists(resultPage, "main");
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  void donateTest() {
    try (final WebClient webClient = new WebClient()) {
      webClient.getOptions().setCssEnabled(false);
      final HtmlPage page = webClient.getPage("http://localhost:8080/donate_project?id=1");
      assertElementExists(page, "email");
      assertElementExists(page, "amount");
      assertElementExists(page, "name");
      assertElementExists(page, "payinfo");
      assertElementExists(page, "submit-btn");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void fundTest() {
    try (final WebClient webClient = new WebClient()) {
      webClient.getOptions().setCssEnabled(false);
      final HtmlPage page = webClient.getPage("http://localhost:8080/create_project");
      assertElementExists(page, "title");
      assertElementExists(page, "flimit");
      assertElementExists(page, "description");
      assertElementExists(page, "endd");
      assertElementExists(page, "rewards");
      assertElementExists(page, "email");
      assertElementExists(page, "name");
      assertElementExists(page, "payinfo");
      assertElementExists(page, "submit-btn");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void assertElementExists(HtmlPage page, String id) {
    final DomElement elem = page.getElementById(id);
    assertNotEquals(null, elem);
  }
}
