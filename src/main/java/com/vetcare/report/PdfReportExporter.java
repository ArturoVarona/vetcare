package com.vetcare.report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class PdfReportExporter implements ReportExporter {
    @Override
    public void export(List<String[]> rows, Path destination) throws IOException {
        // TODO: implementar con la librería de PDF que apruebe el docente
        throw new UnsupportedOperationException("Exportación a PDF pendiente");
    }
}   