package labs.lab3;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Properties;

public class EmailClient {
    public static void main(String[] args) {
        // Параметры SMTP-сервера
        String smtpHost = "smtp.yandex.com";
        String smtpPort = "465";
        String emailUsername = ""; // Ваш адрес электронной почты
        String emailPassword = ""; // Ваш пароль

        // Параметры письма
        String recipientAddress = ""; // Адрес получателя
        String subject = "Test subject";
        String bodyText = "Hello, this is a test email with an image";

        // Подготовка свойств для подключения
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.socketFactory.port", smtpPort);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //Установка SSL Соединения
        properties.put("mail.smtp.socketFactory.fallback", "false");

        // Создание аутентификатора
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        };

        // Создание сессии
        Session session = Session.getInstance(properties, authenticator);

        try {
            // Создание объекта сообщения
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientAddress));
            message.setSubject(subject);

            // Создание тела сообщения с текстом и изображением
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyText);

            // Путь к изображению
            String imagePath = "src\\main\\java\\labs\\lab3\\img.jpg";

            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.attachFile(imagePath);

            // Создание многочастного сообщения
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(imagePart);

            // Установка многочастного сообщения в качестве содержимого письма
            message.setContent(multipart);

            // Отправка письма
            Transport.send(message);

            System.out.println("Письмо успешно отправлено.");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}
