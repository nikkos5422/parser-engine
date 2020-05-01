package cz.notix.outputengine.Services;

import cz.notix.outputengine.ExceptionHandling.CustomException;
import cz.notix.outputengine.ExceptionHandling.ResponseError;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;

@Service
public class PdfMailService {
    private JavaMailSender javaMailSender;

    private static final Logger logger = LoggerFactory.getLogger(JavaMailSender.class);


    private final String MESSAGE_SUBJECT = "Vytvořené PDF z PowerFLOW";
    private final String EMAIL = "flow@notix.cz";

    @Autowired
    public PdfMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendPdf(JasperPrint jasperPrint, String targetEmail) throws CustomException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true, "UTF-8");


        helper.setFrom(EMAIL);
        helper.setTo(targetEmail);
        helper.setSubject(MESSAGE_SUBJECT);
        helper.setText(getMessageText(), true);
        } catch (MessagingException e) {
            throw  new CustomException(ResponseError.ErrorType.MESSAGING_EXCEPTION,e.getClass().toString(),"there is some problem with creating and setting mime message.");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
        } catch (JRException e) {
            throw  new CustomException(ResponseError.ErrorType.EXPORT_REPORT_TO_PDF_STREAM,e.getClass().toString(),"there is some problem with expoting  report to pdf stream.");
        }

        DataSource pdfAttachment = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");

        try {
            helper.addAttachment("PowerFLOW.pdf", pdfAttachment);
        } catch (MessagingException e) {
            throw  new CustomException(ResponseError.ErrorType.MESSAGING_EXCEPTION,e.getClass().toString(),"there is some problem with attaching pdf file to message");

        }
        try {
            javaMailSender.send(message);
        } catch (org.springframework.mail.MailException exception) {
            logger.debug("The first attempt to send the letter failed, message will be send one more time.");
            javaMailSender.send(message);
        }
    }

    private String getMessageText() {
        String result;

        String metaLang = "<meta http-equiv=\"Content-Language\" content=\"cs\" />";
        String encoding = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=Windows-1250\" />";

        result = "<!DOCTYPE html><html><head>" + metaLang + "" + encoding + "</head> <body><div>Dobrý den,<br/><br/>" +
                "zasíláme vám vytvořené PDF k dalšímu zpracování.<br/><br/>" +
                "S přáním hezkého dne<br/>" +
                "<b>Tým PowerFLOW</b><br/>" +
                "<a href=\"https://www.powerflow.cz/\">www.powerflow.cz</a></div> </body></html>";

        return result;
    }
}
