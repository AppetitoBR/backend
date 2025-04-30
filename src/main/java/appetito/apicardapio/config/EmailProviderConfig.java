package appetito.apicardapio.config;

public class EmailProviderConfig {

    public static String getSmtpHost(String email) {

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        return switch (domain) {
            case "gmail.com" -> "smtp.gmail.com";
            case "outlook.com", "hotmail.com", "live.com" -> "smtp.office365.com";
            case "yahoo.com" -> "smtp.mail.yahoo.com";
            case "zoho.com" -> "smtp.zoho.com";
            default -> throw new IllegalArgumentException("Provedor de e-mail não suportado: " + domain);
        };
    }
}