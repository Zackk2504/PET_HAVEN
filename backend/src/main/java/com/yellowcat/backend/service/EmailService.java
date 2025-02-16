package com.yellowcat.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class EmailService {


    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("taskExecutor")
    public void sendEmail(String to, String subject, String body) {
        try {
            // Tạo MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // Sử dụng MimeMessageHelper để cấu hình email
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // 'true' để cho phép HTML

            // Gửi email
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            // Log lỗi hoặc xử lý
            System.err.println("Error while sending email: " + e.getMessage());
        }
    }

    @Async("taskExecutor")
    public void sendEmailWithHoaDon(String to, String subject, String body, byte[] pdfBytes, String pdfFileName) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            // Thêm file PDF đính kèm
            helper.addAttachment(pdfFileName, new ByteArrayResource(pdfBytes));

            // Gửi email
            mailSender.send(message);
            System.out.println("Email đã được gửi thành công!");
        } catch (MessagingException e) {
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
