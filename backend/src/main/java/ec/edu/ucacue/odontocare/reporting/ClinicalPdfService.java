package ec.edu.ucacue.odontocare.reporting;

import java.awt.image.BufferedImage;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import ec.edu.ucacue.odontocare.auth.domain.UserAccount;
import ec.edu.ucacue.odontocare.auth.infrastructure.UserAccountRepository;
import ec.edu.ucacue.odontocare.clinicalrecord.application.*;
import ec.edu.ucacue.odontocare.clinicalrecord.domain.*;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.*;
import ec.edu.ucacue.odontocare.odontogram.application.OdontogramService;
import ec.edu.ucacue.odontocare.patient.domain.*;
import ec.edu.ucacue.odontocare.patient.infrastructure.ClinicalBackgroundRepository;
import ec.edu.ucacue.odontocare.odontogram.infrastructure.DentalPieceRepository;
import ec.edu.ucacue.odontocare.treatment.application.TreatmentService;

@Service public class ClinicalPdfService{
 private static final long MAX_IMAGE=2*1024*1024;
 private static final DateTimeFormatter DT=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
 private static final Font H1=new Font(Font.HELVETICA,16,Font.BOLD,new java.awt.Color(16,42,67));
 private static final Font H2=new Font(Font.HELVETICA,12,Font.BOLD,new java.awt.Color(11,107,97));
 private static final Font BODY=new Font(Font.HELVETICA,9);
 private static final Font SMALL=new Font(Font.HELVETICA,8,Font.NORMAL,new java.awt.Color(80,95,100));

 private final ClinicalRecordRepository records;
 private final ClinicalBackgroundRepository backgrounds;
 private final DentalConsultationRepository consultationRepo;
 private final ConsultationService consultations;
 private final OdontogramService odontogram;
 private final TreatmentService treatment;
 private final UserAccountRepository users;
 private final DentalPieceRepository dentalPieces;
 private final String institution;
 private final String systemName;

 public ClinicalPdfService(ClinicalRecordRepository r,ClinicalBackgroundRepository b,DentalConsultationRepository cr,ConsultationService c,OdontogramService o,TreatmentService t,UserAccountRepository u, DentalPieceRepository dp, @Value("${app.reporting.institution-name:Institución académica demostrativa}")String institution,@Value("${app.reporting.system-name:OdontoUcacue}")String systemName){
     records=r;backgrounds=b;consultationRepo=cr;consultations=c;odontogram=o;treatment=t;users=u;dentalPieces=dp;this.institution=institution;this.systemName=systemName;
 }

 @Transactional(readOnly=true)
 public byte[] generate(Long recordId,MultipartFile imageFile){
     byte[] image=validateImage(imageFile);
     ClinicalRecord record=records.findById(recordId).orElseThrow(()->new ClinicalRecordNotFoundException(recordId));
     Patient p=record.getPatient();
     var consultationEntities=consultationRepo.findByClinicalRecordIdOrderByConsultationDateDescIdDesc(recordId).stream().sorted(Comparator.comparing(DentalConsultation::getConsultationDate)).toList();
     var details=consultationEntities.stream().map(c->consultations.detail(recordId,c.getId())).toList();
     var current=odontogram.current(recordId);
     var procedures=treatment.procedures(recordId);
     var plans=treatment.plans(recordId);
     var evolutions=treatment.evolutions(recordId);
     try{
         ByteArrayOutputStream out=new ByteArrayOutputStream();
         Document doc=new Document(PageSize.A4,36,36,52,42);
         PdfWriter writer=PdfWriter.getInstance(doc,out);
         writer.setPageEvent(new HeaderFooter(institution,systemName));
         doc.open();
         doc.add(new Paragraph(systemName+" - Historia Clínica Odontológica",H1));
         doc.add(new Paragraph("Fecha de generación: "+DT.format(LocalDateTime.now())+" | HCE: "+p.getClinicalRecordNumber(),SMALL));
         
         section(doc,"Datos personales y contacto");
         doc.add(keyValue(new String[][]{
             {"Paciente",p.getFirstNames()+" "+p.getLastNames()},
             {"Identificación",p.getIdentification()},
             {"Nacimiento",p.getBirthDate().toString()},
             {"Sexo",p.getSex()},
             {"Teléfono",p.getPhone()},
             {"Correo",p.getEmail()},
             {"Dirección",p.getAddress()}
         }));
         
         section(doc,"Antecedentes, alergias y alertas");
         for(ClinicalBackground bg:backgrounds.findByPatientIdAndActiveTrue(p.getId()))
             doc.add(new Paragraph(translateBackground(bg.getType().name())+": "+bg.getDescription(),BODY));
             
         section(doc,"Odontograma actual");
         if(image!=null){
             Image img=Image.getInstance(image);
             img.scaleToFit(500,240);
             img.setAlignment(Image.ALIGN_CENTER);
             doc.add(img);
         } else {
             doc.add(new Paragraph("Captura del odontograma no proporcionada.",BODY));
         }
         
         section(doc,"Hallazgos por pieza");
         PdfPTable findings=table("Pieza","Estado","Superficies","Observaciones");
         for(var piece:current){
             if(piece.currentEvent()!=null){
                 addRow(findings,
                     formatPiece(piece.pieceCode()),
                     piece.currentEvent().status().name().replace("_", " "),
                     String.join(", ",piece.currentEvent().surfaces().stream().map(s->s.name().replace("_", " ")).toList()),
                     safe(piece.currentEvent().observations())
                 );
             }
         }
         doc.add(findings);
         
         section(doc,"Historial cronológico de consultas");
         for(int i=0;i<details.size();i++){
             var d=details.get(i);
             var entity=consultationEntities.get(i);
             doc.add(new Paragraph(DT.format(d.consultationDate())+" - "+d.consultationType()+" - "+d.reason(),H2));
             doc.add(new Paragraph("Profesional: "+professional(entity.getCreatedBy()),SMALL));
             if(d.vitalSigns()!=null)
                 doc.add(new Paragraph("Signos vitales: PA "+safe(d.vitalSigns().systolicBp())+"/"+safe(d.vitalSigns().diastolicBp())+", FC "+safe(d.vitalSigns().heartRate())+", FR "+safe(d.vitalSigns().respiratoryRate())+", T "+safe(d.vitalSigns().temperatureC())+", SpO2 "+safe(d.vitalSigns().oxygenSaturation()),BODY));
             
             // Aquí añadimos los hallazgos de esta consulta
             var events = odontogram.byConsultation(recordId, entity.getId());
             if (!events.isEmpty()) {
                 String hallazgos = String.join(" | ", events.stream().map(e -> formatPiece(e.pieceCode()) + ": " + e.status().name().replace("_", " ") + " (" + String.join(", ", e.surfaces().stream().map(s -> s.name().replace("_", " ")).toList()) + ")").toList());
                 doc.add(new Paragraph("Hallazgos odontograma: " + hallazgos, BODY));
             }
             
             for(var diagnosis:d.diagnoses())
                 doc.add(new Paragraph("Diagnóstico: "+diagnosis.description()+" | CIE-10: "+safe(diagnosis.icd10Code())+" | Piezas afectadas: "+formatPiece(diagnosis.affectedTeeth()),BODY));
             if(d.closing() != null)
                 doc.add(new Paragraph("Tratamiento: "+safe(d.closing().treatment())+" | Recomendaciones: "+safe(d.closing().recommendations())+" | Próximo control: "+safe(d.closing().nextControlDate()),BODY));
         }
         
         section(doc,"Procedimientos");
         PdfPTable pro=table("Fecha","Procedimiento","Piezas","Estado","Profesional");
         for(var x:procedures)
             addRow(pro,
                 DT.format(x.date()),
                 x.typeName(),
                 String.join(", ",x.pieceCodes().stream().map(c->formatPiece(c)).toList()),
                 translateProcedureStatus(x.status()),
                 professional(x.professionalId())
             );
         doc.add(pro);
         
         section(doc,"Plan de tratamiento");
         PdfPTable planTable=table("Plan","Ítem","Pieza","Prioridad","Estado");
         for(var plan:plans)
             for(var item:plan.items())
                 addRow(planTable,
                     plan.title(),
                     item.description(),
                     formatPiece(item.pieceCode()),
                     String.valueOf(item.priority()),
                     translateItemStatus(item.status())
                 );
         doc.add(planTable);
         
         section(doc,"Evolución clínica y próximos controles");
         PdfPTable evo=table("Fecha","Resultado","Pieza","Descripción","Próximo control");
         for(var x:evolutions)
             addRow(evo,
                 DT.format(x.date()),
                 translateEvolutionResult(x.result()),
                 formatPiece(x.pieceCode()),
                 x.description(),
                 safe(x.nextControl())
             );
         doc.add(evo);
         
         doc.close();
         return out.toByteArray();
     }catch(Exception e){
         throw new IllegalStateException("No fue posible generar el PDF clínico.",e);
     }
 }

 private byte[] validateImage(MultipartFile file){
     if(file==null||file.isEmpty())return null;
     if(file.getSize()>MAX_IMAGE)throw new PdfValidationException("La captura supera el máximo de 2 MB.");
     if(!"image/png".equalsIgnoreCase(file.getContentType()))throw new PdfValidationException("Solo se acepta una captura PNG.");
     try{
         byte[] bytes=file.getBytes();
         if(bytes.length<8||bytes[0]!=(byte)0x89||bytes[1]!=0x50||bytes[2]!=0x4E||bytes[3]!=0x47)throw new PdfValidationException("El contenido no corresponde a un PNG válido.");
         BufferedImage image=ImageIO.read(new ByteArrayInputStream(bytes));
         if(image==null||image.getWidth()>4096||image.getHeight()>4096)throw new PdfValidationException("Dimensiones de imagen inválidas.");
         return bytes;
     }catch(IOException e){
         throw new PdfValidationException("No fue posible leer la captura.");
     }
 }

 public byte[] generatePatientListReport(java.util.List<ec.edu.ucacue.odontocare.patient.presentation.PatientResponse> patients) {
     try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         Document doc = new Document(PageSize.A4.rotate(), 36, 36, 52, 42);
         PdfWriter writer = PdfWriter.getInstance(doc, out);
         writer.setPageEvent(new HeaderFooter(institution, systemName));
         doc.open();
         doc.add(new Paragraph(systemName + " - Listado de Pacientes", H1));
         doc.add(new Paragraph("Fecha de generación: " + DT.format(LocalDateTime.now()), SMALL));
         section(doc, "Pacientes Registrados");
         
         PdfPTable table = table("HC", "Identificación", "Paciente", "Nacimiento", "Teléfono", "Responsable");
         for (var p : patients) {
             addRow(table, p.clinicalRecordNumber(), p.identification(), 
                 p.firstNames() + " " + p.lastNames(), 
                 p.birthDate().toString(), p.phone(), safe(p.responsibleName()));
         }
         doc.add(table);
         doc.close();
         return out.toByteArray();
     } catch (Exception e) {
         throw new IllegalStateException("No fue posible generar el listado de pacientes en PDF.", e);
     }
 }

 private String professional(Long id){return users.findById(id).map(u->u.getNombres()).orElse("Profesional #"+id);}
 private static String safe(Object v){return v==null||v.toString().isBlank()?"No registrado":v.toString();}
 private static void section(Document d,String title)throws DocumentException{d.add(Chunk.NEWLINE);d.add(new Paragraph(title,H2));}
 private static PdfPTable keyValue(String[][] rows){
     PdfPTable t=new PdfPTable(2);
     t.setWidthPercentage(100);
     t.setSpacingBefore(8f);
     t.setSpacingAfter(12f);
     for(String[] r:rows){
         PdfPCell c1=new PdfPCell(new Phrase(14f,safe(r[0]),new Font(Font.HELVETICA,9,Font.BOLD)));
         c1.setBackgroundColor(new java.awt.Color(245,247,250));
         c1.setPadding(6f);
         c1.setBorderColor(new java.awt.Color(220,225,230));
         t.addCell(c1);
         PdfPCell c2=new PdfPCell(new Phrase(14f,safe(r[1]),BODY));
         c2.setPadding(6f);
         c2.setBorderColor(new java.awt.Color(220,225,230));
         t.addCell(c2);
     }
     return t;
 }
 private static PdfPTable table(String... headers){
     PdfPTable t=new PdfPTable(headers.length);
     t.setWidthPercentage(100);
     t.setSpacingBefore(8f);
     t.setSpacingAfter(12f);
     for(String h:headers){
         PdfPCell c=new PdfPCell(new Phrase(14f,h,new Font(Font.HELVETICA,9,Font.BOLD,java.awt.Color.WHITE)));
         c.setBackgroundColor(new java.awt.Color(11,107,97));
         c.setPadding(7f);
         c.setBorderColor(new java.awt.Color(11,107,97));
         t.addCell(c);
     }
     t.setHeaderRows(1);
     t.setSplitRows(true);
     return t;
 }
 private static void addRow(PdfPTable t,String... values){
     for(String v:values){
         PdfPCell c=new PdfPCell(new Phrase(14f,safe(v),BODY));
         c.setPadding(6f);
         c.setBorderColor(new java.awt.Color(220,225,230));
         t.addCell(c);
     }
 }
 
 private String formatPiece(String pieceCode) {
     if (pieceCode != null && pieceCode.startsWith("TOOTH_")) {
         return dentalPieces.findByInternalCodeAndActiveTrue(pieceCode)
                 .map(p -> p.getFdiCode() != null ? "Pieza " + p.getFdiCode() : "Pieza " + p.getVisualOrder())
                 .orElseGet(() -> {
                     try {
                         return "Pieza " + Integer.parseInt(pieceCode.substring(6));
                     } catch (NumberFormatException e) {
                         return pieceCode;
                     }
                 });
     }
     return safe(pieceCode);
 }

 private static String translateBackground(String type) {
     if (type == null) return "No registrado";
     return switch (type) {
         case "ALLERGY" -> "Alergias";
         case "BRUSHING_FREQUENCY" -> "Frecuencia de cepillado";
         case "BRUXISM" -> "Bruxismo";
         case "CHRONIC_DISEASES" -> "Enfermedades crónicas";
         case "CURRENT_MEDICATION" -> "Medicación actual";
         case "DENTAL_FLOSS_USE" -> "Uso de hilo dental";
         case "DENTAL_OBSERVATIONS" -> "Observaciones dentales";
         case "FAMILY" -> "Antecedentes familiares";
         case "LAST_DENTAL_VISIT" -> "Última visita al dentista";
         case "MOUTHWASH_USE" -> "Uso de enjuague bucal";
         case "ORAL_HABITS" -> "Hábitos orales";
         case "ORAL_HYGIENE" -> "Higiene oral";
         case "ORTHODONTICS_USE" -> "Uso de ortodoncia";
         case "PATHOLOGICAL" -> "Antecedentes patológicos";
         case "PREVIOUS_DENTAL_TREATMENTS" -> "Tratamientos dentales previos";
         case "PROSTHESIS_USE" -> "Uso de prótesis";
         case "SURGICAL" -> "Antecedentes quirúrgicos";
         default -> type;
     };
 }

 private static String translateProcedureStatus(ec.edu.ucacue.odontocare.treatment.domain.ProcedureStatus status) {
     if (status == null) return "No registrado";
     return switch (status) {
         case PLANNED -> "Planificado";
         case IN_PROGRESS -> "En progreso";
         case COMPLETED -> "Completado";
         case CANCELLED -> "Cancelado";
     };
 }

 private static String translateItemStatus(ec.edu.ucacue.odontocare.treatment.domain.TreatmentItemStatus status) {
     if (status == null) return "No registrado";
     return switch (status) {
         case PENDING -> "Pendiente";
         case IN_PROGRESS -> "En progreso";
         case COMPLETED -> "Completado";
         case CANCELLED -> "Cancelado";
     };
 }

 private static String translateEvolutionResult(ec.edu.ucacue.odontocare.treatment.domain.EvolutionResult result) {
     if (result == null) return "No registrado";
     return switch (result) {
         case FAVORABLE -> "Favorable";
         case STABLE -> "Estable";
         case PENDING -> "Pendiente";
         case REQUIRES_FOLLOW_UP -> "Requiere seguimiento";
     };
 }

 private static class HeaderFooter extends PdfPageEventHelper{
     private final String institution,system;
     HeaderFooter(String i,String s){institution=i;system=s;}
     public void onEndPage(PdfWriter w,Document d){
         ColumnText.showTextAligned(w.getDirectContent(),Element.ALIGN_LEFT,new Phrase(institution+" | "+system,SMALL),36,PageSize.A4.getHeight()-28,0);
         ColumnText.showTextAligned(w.getDirectContent(),Element.ALIGN_RIGHT,new Phrase("Página "+w.getPageNumber(),SMALL),PageSize.A4.getWidth()-36,24,0);
     }
 }
}
