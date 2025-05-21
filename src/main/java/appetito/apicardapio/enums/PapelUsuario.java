package appetito.apicardapio.enums;
/**
 * Enumeração que representa os diferentes papéis ou funções que um usuário pode ter dentro do sistema.
 *
 * <p>Define os níveis de acesso e responsabilidades para controle de permissões.</p>
 */
public enum PapelUsuario {

    /**
     * Papel com permissões administrativas completas, geralmente para desenvolvedores ou administradores do sistema.
     */
    ADMINISTRADOR,

    /**
     * Função padrão da empresa responsável pela cozinha e preparo dos pedidos.
     */
    COZINHEIRO,

    /**
     * Função padrão da empresa responsável pelo atendimento direto aos clientes.
     */
    ATENDENTE,

    /**
     * Função padrão da empresa responsável pela gestão e supervisão das operações.
     */
    GERENTE,
}