package com.matias.physiocarepsp.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.matias.physiocarepsp.models.Appointment.Appointment;
import com.matias.physiocarepsp.models.Appointment.AppointmentDto;
import com.matias.physiocarepsp.models.Record.Record;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
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
    public static PdfDocument generateSalaryPdf(String physioName, List<AppointmentDto> appointments,  String destino) {
        try{
            PdfWriter writer = new PdfWriter(destino);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

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
            for (AppointmentDto a : appointments) {
                table.addCell(a.getDate());
                table.addCell(a.getPatientName());
                table.addCell(a.getTreatment());
                table.addCell(String.format("%.2f", a.getPrice()));
                total += a.getPrice();
            }

            document.add(table);
            document.add(new Paragraph("\nTotal a recibir: " + String.format("%.2f", total)).setBold().setFontSize(12));
            document.close();
            return pdf;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static PdfDocument createPdfDocument(List<AppointmentDto> appointments, String dest) {
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            float[] columnWidths = {1.5f, 1.5f, 2.5f, 5f, 5f, 5f, 1.5f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addCell(createHeaderCell( "ID"));
            table.addCell(createHeaderCell( "Physio"));
            table.addCell(createHeaderCell("Fecha"));
            table.addCell(createHeaderCell("Diagnóstico"));
            table.addCell(createHeaderCell("Tratamiento"));
            table.addCell(createHeaderCell("Observaciones"));
            table.addCell(createHeaderCell("Precio (€)"));

            for (AppointmentDto appt : appointments) {
                table.addCell(createWrappedCell(truncateId(safeString(appt.getAppointmentId()))));
                table.addCell(createWrappedCell(truncateId(safeString(appt.getPhysioId()))));
                table.addCell(createWrappedCell(safeString(formatIsoDate(appt.getDate()))));
                table.addCell(createWrappedCell(safeString(appt.getDiagnosis())));
                table.addCell(createWrappedCell(safeString(appt.getTreatment())));
                table.addCell(createWrappedCell(safeString(appt.getObservations())));
                table.addCell(createWrappedCell(String.valueOf(appt.getPrice())));
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

    private static Cell createHeaderCell(String content) {
        return new Cell()
                .add(new Paragraph(content).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    private static Cell createWrappedCell(String content) {
        return new Cell()
                .add(new Paragraph(content != null ? content : "")
                .setFontSize(9)
                .setMultipliedLeading(1.2f))
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setPadding(4)
                .setKeepTogether(true);
    }

    private static String truncateId(String id) {
        return (id != null && id.length() > 8) ? id.substring(0, 8) + "..." : id;
    }

    private static String formatIsoDate(String isoDateString) {
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(isoDateString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
            return zdt.format(formatter);
        } catch (Exception e) {
            return isoDateString;
        }
    }

    public static PdfDocument createRecordPdf(List<Record> records, String dest){
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            float[] columnWidths = {5f, 5f, 5f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addCell(createHeaderCell( "ID Record"));
            table.addCell(createHeaderCell( "ID Paciente"));
            table.addCell(createHeaderCell( "Medical Record"));

            for (Record appt : records) {
                table.addCell(createWrappedCell(safeString(appt.getId())));
                table.addCell(createWrappedCell(safeString(appt.getPatient())));
                table.addCell(createWrappedCell(safeString(formatIsoDate(appt.getMedicalRecord()))));
            }

            document.add(table);
            document.close();
            System.out.println("Records PDF creado en: " + dest);
            return pdf;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
