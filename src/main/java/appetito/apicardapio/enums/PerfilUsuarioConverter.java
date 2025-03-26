package appetito.apicardapio.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PerfilUsuarioConverter implements AttributeConverter<PapelUsuario, String> {

    @Override
    public String convertToDatabaseColumn(PapelUsuario papelUsuario) {
        return papelUsuario.name().toUpperCase();
    }

    @Override
    public PapelUsuario convertToEntityAttribute(String dbData) {
        return PapelUsuario.valueOf(dbData.toUpperCase());
    }
}