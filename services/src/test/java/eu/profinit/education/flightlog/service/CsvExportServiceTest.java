package eu.profinit.education.flightlog.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import eu.profinit.education.flightlog.util.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.profinit.education.flightlog.AbstractIntegrationTest;
import eu.profinit.education.flightlog.to.FileExportTo;

import static org.junit.jupiter.api.Assertions.*;

public class CsvExportServiceTest extends AbstractIntegrationTest {

    @Autowired
    private CsvExportService testSubject;

    @Test
    public void testCSVExport() throws IOException, URISyntaxException {
        FileExportTo fileExportTo = testSubject.getAllFlightsAsCsv();
        String result = new String(fileExportTo.getContent(), fileExportTo.getEncoding());
        assertEquals(FileUtils.normalizeLineEndingsToCrLf(readFileToString("expectedExport.csv")),FileUtils.normalizeLineEndingsToCrLf(result));
    }

    private String readFileToString(String fileName)
        throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(
            Paths.get(getClass().getClassLoader()
                .getResource(fileName).toURI())));
    }


}