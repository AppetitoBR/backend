package appetito.apicardapio.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA que transforma o enum {@link PapelUsuario} em sua representação {@link String}
 * para armazenamento no banco de dados, e vice-versa.
 *
 * <p>Aplica automaticamente a conversão para todos os atributos do tipo {@link PapelUsuario}.</p>
 */
@Converter(autoApply = true)
public class PerfilUsuarioConverter implements AttributeConverter<PapelUsuario, String> {

    /**
     * Converte o valor do enum {@link PapelUsuario} para {@link String} a ser salvo no banco de dados.
     *
     * @param papelUsuario Enum a ser convertido.
     * @return Representação em texto do enum, em letras maiúsculas.
     */
    @Override
    public String convertToDatabaseColumn(PapelUsuario papelUsuario) {
        return papelUsuario.name().toUpperCase();
    }

    /**
     * Converte a representação textual do papel do usuário do banco de dados para o enum {@link PapelUsuario}.
     *
     * @param dbData String do banco de dados a ser convertida.
     * @return Enum correspondente ao valor fornecido.
     */
    @Override
    public PapelUsuario convertToEntityAttribute(String dbData) {
        return PapelUsuario.valueOf(dbData.toUpperCase());
    }
}