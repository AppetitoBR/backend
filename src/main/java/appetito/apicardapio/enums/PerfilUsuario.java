package appetito.apicardapio.enums;

public enum PerfilUsuario {
    //Nós developers
    ADMINISTRADOR,

    // funcoes padroes da empresa
    COZINHEIRO,
    ATENDENTE,
    GERENTE,

    //cliente é o default
    CLIENTE,

    // sao superusuarios -- tenho que mexer nessa parte para ver sobre o que fazer
    PATROCINADOR,
    COLABORADOR
}
