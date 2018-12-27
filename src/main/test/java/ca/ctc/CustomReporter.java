package ca.ctc;

import org.apache.commons.io.FileUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;

public class CustomReporter implements IReporter {
    public void copyFromJar(String source, final Path target) throws URISyntaxException, IOException {
        URI resource = getClass().getResource(source).toURI();
//        FileSystem fileSystem = FileSystems.newFileSystem(
//                resource,
//                Collections.<String, String>emptyMap()
//        );


        final Path jarPath = Paths.get(resource);

        Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {

            private Path currentTarget;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                currentTarget = target.resolve(jarPath.relativize(dir).toString());
                Files.createDirectories(currentTarget);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(jarPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
                               String outputDirectory) {

        //Iterating over each suite included in the test
        for (ISuite suite : suites) {
            copyFiles(outputDirectory);

            //Following code gets the suite name
            String suiteName = suite.getName();

            //Getting the results for the said suite
            Map<String, ISuiteResult> suiteResults = suite.getResults();
            for (ISuiteResult sr : suiteResults.values()) {
                ITestContext tc = sr.getTestContext();
                for (ITestResult result: tc.getFailedTests().getAllResults()) {
                    String id = result.getName();
                    String json1 = ((MyTest)result.getInstance()).getJson(id, "00001");
                    String json2 = ((MyTest)result.getInstance()).getJson(id, "00002");
                    renderReport(String.format("%s/%s.html", outputDirectory, id), json1, json2);
                }
                System.out.println("Passed tests for suite '" + suiteName +
                        "' is:" + tc.getPassedTests().getAllResults().size());
                System.out.println("Failed tests for suite '" + suiteName +
                        "' is:" + tc.getFailedTests().getAllResults().size());
                System.out.println("Skipped tests for suite '" + suiteName +
                        "' is:" + tc.getSkippedTests().getAllResults().size());
            }


        }
    }

    private void copyFiles(String outputDirectory) {
        File directory = new File(outputDirectory);
        directory.mkdirs();

        try {
            FileUtils.cleanDirectory(directory);
            copyFromJar("/scripts", directory.toPath());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void renderReport(String fileName, String json1, String json2) {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/example.twig");
            JtwigModel model = JtwigModel.newModel()
                    .with("json1", json1)
                    .with("json2", json2);

            template.render(model, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
