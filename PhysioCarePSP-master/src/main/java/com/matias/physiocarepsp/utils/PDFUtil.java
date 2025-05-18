package com.matias.physiocarepsp.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.matias.physiocarepsp.models.Appointment.Appointment;
import com.matias.physiocarepsp.models.Appointment.AppointmentDto;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for generating PDF documents for medical records, appointments, and salary reports.
 */
public class PDFUtil {
    private static final String CLINIC_NAME = "Clínica FisioCare";
    private static final String CLINIC_ADDRESS = "C/ Salud 123, Madrid";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Generates a PDF containing the medical record of a patient (without appointments).
     */
    public static byte[] generateMedicalRecordPdf(String patientName, List<String> records) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph(CLINIC_NAME).setBold());
        document.add(new Paragraph(CLINIC_ADDRESS));
        document.add(new Paragraph("Ficha médica de: " + patientName).setBold().setFontSize(14));
        document.add(new Paragraph(" "));

        for (String r : records) {
            document.add(new Paragraph("- " + r));
        }

        document.close();
        return baos.toByteArray();
    }

    /**
     * Generates a PDF listing all appointments of a patient, with a header and a "Fecha | Tratamiento" table.
     */
    public static byte[] generateAppointmentsPdf(String patientName, List<Appointment> appointments) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        document.add(new Paragraph(CLINIC_NAME).setBold());
        document.add(new Paragraph(CLINIC_ADDRESS));
        document.add(new Paragraph("Citas de: " + patientName).setBold().setFontSize(14));
        document.add(new Paragraph(" "));

        // Table with two columns: Fecha | Tratamiento
        float[] columnWidths = {3, 7};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Tratamiento");

        for (Appointment a : appointments) {
            // Use dateTime property instead of deprecated date
            table.addCell(a.getDateTime().format(DATE_FORMAT));
            table.addCell(a.getTreatment());
        }

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    /**
     * Generates a PDF with a salary report for a physiotherapist: "Fecha | Paciente | Tratamiento | Precio" table and total.
     */
    public static byte[] generateSalaryPdf(String physioName, List<Appointment> appointments) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        document.add(new Paragraph(CLINIC_NAME).setBold());
        document.add(new Paragraph(CLINIC_ADDRESS));
        document.add(new Paragraph("Nómina de: " + physioName).setBold().setFontSize(14));
        document.add(new Paragraph(" "));

        // Table with four columns: Fecha | Paciente | Tratamiento | Precio
        float[] columnWidths = {3, 5, 4, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Paciente");
        table.addHeaderCell("Tratamiento");
        table.addHeaderCell("Precio");

        double total = 0;
        for (Appointment a : appointments) {
            table.addCell(a.getDateTime().format(DATE_FORMAT));
            table.addCell(a.getPatientName());
            table.addCell(a.getTreatment());
            table.addCell(String.format("%.2f", a.getPrice()));
            total += a.getPrice();
        }

        document.add(table);
        document.add(new Paragraph("\nTotal a recibir: " + String.format("%.2f", total)).setBold().setFontSize(12));
        document.close();
        return baos.toByteArray();
    }

    public static PdfDocument createPdfDocument(List<AppointmentDto> appointments, String dest) {
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            float[] columnWidths = {2, 2, 3, 5, 5, 5, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addCell("ID");
            table.addCell("Physio");
            table.addCell("Fecha");
            table.addCell("Diagnóstico");
            table.addCell("Tratamiento");
            table.addCell("Observaciones");
            table.addCell("Precio (€)");

            for (AppointmentDto appt : appointments) {
                table.addCell(safeString(appt.getAppointmentId()));
                table.addCell(safeString(appt.getPhysioId()));
                table.addCell(safeString(appt.getDate()));
                table.addCell(safeString(appt.getDiagnosis()));
                table.addCell(safeString(appt.getTreatment()));
                table.addCell(safeString(appt.getObservations()));
                table.addCell(String.valueOf(appt.getPrice()));
            }

            document.add(table);
            document.close();
            System.out.println("Appointments PDF creado en: " + dest);
            return pdf;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String safeString(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
