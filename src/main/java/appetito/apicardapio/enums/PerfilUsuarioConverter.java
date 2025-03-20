package appetito.apicardapio.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PerfilUsuarioConverter implements AttributeConverter<PerfilUsuario, String> {

    @Override
    public String convertToDatabaseColumn(PerfilUsuario perfilUsuario) {
        return perfilUsuario.name().toUpperCase();
    }

    @Override
    public PerfilUsuario convertToEntityAttribute(String dbData) {
        return PerfilUsuario.valueOf(dbData.toUpperCase());
    }
}