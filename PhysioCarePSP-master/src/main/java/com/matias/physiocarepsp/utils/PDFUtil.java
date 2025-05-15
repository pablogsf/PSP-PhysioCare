package com.matias.physiocarepsp.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PDFUtil {
    private static final String CLINIC_NAME = "Clínica FisioCare";
    private static final String CLINIC_ADDRESS = "C/ Salud 123, Madrid";

    public static byte[] generateMedicalRecordPdf(String patientName, List<String> records) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph(CLINIC_NAME).setBold());
        doc.add(new Paragraph(CLINIC_ADDRESS));
        doc.add(new Paragraph("Ficha médica de: " + patientName));
        doc.add(new Paragraph(" "));

        for (String r : records) {
            doc.add(new Paragraph("- " + r));
        }

        doc.close();
        return baos.toByteArray();
    }

    // Similar: generateAppointmentsPdf(...) y generateSalaryPdf(...)
}
