package study;

import org.testng.annotations.Test;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import static org.assertj.core.api.Assertions.assertThat;

public class S3FindingProjectDirByClasspathTest {

    @Test
    public void getLocationWhereThisClassIsFound() {

        // THE MAGIC
        ProtectionDomain pd = this.getClass().getProtectionDomain();
        CodeSource codeSource = pd.getCodeSource();
        URL url = codeSource.getLocation();

        System.out.println("codeSource url=" + url.toString());
        // e.g, "url=file:/Users/kazurayam/github/unittest-helper/preliminary-study/build/classes/java/test/"
        assertThat(url.toString()).contains("unittest-helper/preliminary-study/build/classes/java/test");
        String codeSourcePathElementsUnderProjectDirectory = "build/classes/java/test/";
        String projectDir =
                url.toString().replace(codeSourcePathElementsUnderProjectDirectory,"");
        System.out.println("project directory=" + TestUtils.shortenPath(projectDir));
    }
}
