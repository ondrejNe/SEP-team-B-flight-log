package eu.profinit.education.flightlog.service;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.profinit.education.flightlog.domain.repositories.FlightRepository;
import eu.profinit.education.flightlog.to.FileExportTo;

@Service
public class CsvExportServiceImpl implements CsvExportService {

    // TODO 4.4 Vhodné vlastnosti CSV souboru definujte jako konstanty
    private final FlightRepository flightRepository;

    private final String fileName;

    public CsvExportServiceImpl(FlightRepository flightRepository, @Value("${csv.export.flight.fileName}") String fileName) {
        this.flightRepository = flightRepository;
        this.fileName = fileName;
    }

    @Override
    public FileExportTo getAllFlightsAsCsv() {
        // TODO 4.3: Naimplementujte vytváření CSV.
        // Tip: můžete použít Apache Commons CSV - https://commons.apache.org/proper/commons-csv/ v příslušných pom.xml naleznete další komentáře s postupem
        throw new NotImplementedException("Not implemented");
    }
}