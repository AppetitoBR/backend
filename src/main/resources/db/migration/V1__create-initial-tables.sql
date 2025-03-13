CREATE TABLE Usuario (
                         usuario_id INT AUTO_INCREMENT PRIMARY KEY,
                         nome_completo VARCHAR(255) NOT NULL,
                         apelido VARCHAR(50),
                         cpf VARCHAR(14) UNIQUE NOT NULL,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         senha VARCHAR(255) NOT NULL,
                         perfil VARCHAR(50) NOT NULL,
                         data_nascimento DATE,
                         idioma_padrao INT DEFAULT 0,
                         nacionalidade VARCHAR(100),
                         caminho_imagem_perfil VARCHAR(255),
                         situacao VARCHAR(20) DEFAULT 'ativo',
                         contatos TEXT,
                         endereco TEXT,
                         redes_sociais TEXT,
                         data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         data_atualizacao TIMESTAMP NULL
);

-- Tabela Estabelecimento
CREATE TABLE Estabelecimento (
                                 estabelecimento_id INT AUTO_INCREMENT PRIMARY KEY,
                                 razao_social VARCHAR(255) NOT NULL,
                                 nome_fantasia VARCHAR(255),
                                 cnpj VARCHAR(18) UNIQUE NOT NULL,
                                 contatos TEXT,
                                 endereco TEXT,
                                 tipo VARCHAR(20),
                                 ativo BOOLEAN DEFAULT TRUE,
                                 bloqueado BOOLEAN DEFAULT FALSE,
                                 data_cadastro DATE,
                                 segmento VARCHAR(100),
                                 usuario_cadastro_id INT,
                                 data_alteracao_cadastro DATE,
                                 usuario_alteracao_id INT,
                                 observacao TEXT,
                                 logomarca TEXT,
                                 url_cardapio_digital VARCHAR(255),
                                 subdominio_appetito VARCHAR(255),
                                 FOREIGN KEY (usuario_cadastro_id) REFERENCES Usuario(usuario_id),
                                 FOREIGN KEY (usuario_alteracao_id) REFERENCES Usuario(usuario_id)
);

CREATE TABLE Estabelecimento_Contrato (
                                          estabelecimento_id INT PRIMARY KEY,
                                          termos_contratuais TEXT,
                                          plano_financeiro TEXT,
                                          FOREIGN KEY (estabelecimento_id) REFERENCES Estabelecimento(estabelecimento_id) ON DELETE CASCADE
);

CREATE TABLE Estabelecimento_Proprietario (
                                              estabelecimento_id INT,
                                              usuario_id INT,
                                              PRIMARY KEY (estabelecimento_id, usuario_id),
                                              FOREIGN KEY (estabelecimento_id) REFERENCES Estabelecimento(estabelecimento_id) ON DELETE CASCADE,
                                              FOREIGN KEY (usuario_id) REFERENCES Usuario(usuario_id) ON DELETE CASCADE
);


-- Tabela Colaborador
CREATE TABLE Colaborador (
                             colaborador_id INT AUTO_INCREMENT PRIMARY KEY,
                             usuario_id INT,
                             estabelecimento_id INT,
                             cargo VARCHAR(100),
                             data_contratacao DATE,
                             calendario_trabalho TEXT,
                             inicio_turno TIME,
                             termino_turno TIME,
                             notificacoes TEXT,
                             FOREIGN KEY (usuario_id) REFERENCES Usuario(usuario_id) ON DELETE CASCADE,
                             FOREIGN KEY (estabelecimento_id) REFERENCES Estabelecimento(estabelecimento_id) ON DELETE CASCADE
);

CREATE TABLE Patrocinador (
                              patrocinador_id INT AUTO_INCREMENT PRIMARY KEY,
                              usuario_id INT,
                              contratos TEXT,
                              FOREIGN KEY (usuario_id) REFERENCES Usuario(usuario_id) ON DELETE CASCADE
);

CREATE TABLE Cardapio (
                          cardapio_id INT AUTO_INCREMENT PRIMARY KEY,
                          estabelecimento_id INT NOT NULL,  -- Não pode ser nulo
                          nome VARCHAR(255) NOT NULL,
                          secao VARCHAR(100),
                          descricao VARCHAR(255),
                          colaborador_id INT,
                          vigencia_inicio DATE,
                          vigencia_fim DATE,
                          ativo BOOLEAN DEFAULT TRUE,
                          FOREIGN KEY (estabelecimento_id) REFERENCES Estabelecimento(estabelecimento_id) ON DELETE CASCADE,
                          FOREIGN KEY (colaborador_id) REFERENCES Colaborador(colaborador_id)
);

CREATE TABLE Mesa (
                      mesa_id INT AUTO_INCREMENT PRIMARY KEY,
                      estabelecimento_id INT,
                      nome VARCHAR(50) NOT NULL,
                      capacidade INT,
                      status VARCHAR(20) DEFAULT 'livre',
                      ativo BOOLEAN DEFAULT TRUE,
                      colaborador_reserva_id INT,
                      data_hora_inicio_reserva TIMESTAMP,
                      nome_cliente_reserva VARCHAR(255),
                      contato_cliente_reserva VARCHAR(255),
                      observacao TEXT,
                      FOREIGN KEY (estabelecimento_id) REFERENCES Estabelecimento(estabelecimento_id) ON DELETE CASCADE,
                      FOREIGN KEY (colaborador_reserva_id) REFERENCES Colaborador(colaborador_id)
);

CREATE TABLE Produto (
                         produto_id INT AUTO_INCREMENT PRIMARY KEY,
                         cardapio_id INT,
                         nome_curto VARCHAR(100) NOT NULL,
                         nome_longo VARCHAR(255),
                         categoria VARCHAR(100),
                         tamanho VARCHAR(50),
                         preco_custo DECIMAL(10, 2),
                         preco_venda DECIMAL(10, 2),
                         estoque INT,
                         estoque_minimo INT,
                         ativo BOOLEAN DEFAULT TRUE,
                         unidade_medida VARCHAR(50),
                         imagens TEXT,
                         FOREIGN KEY (cardapio_id) REFERENCES Cardapio(cardapio_id) ON DELETE CASCADE
);

CREATE TABLE Complemento (
                             complemento_id INT AUTO_INCREMENT PRIMARY KEY,
                             produto_id INT,
                             nome_curto VARCHAR(100) NOT NULL,
                             quantidade_produto_vinculado VARCHAR(255),
                             preco_custo DECIMAL(10, 2),
                             preco_venda DECIMAL(10, 2),
                             imagens TEXT,
                             ativo BOOLEAN DEFAULT TRUE,
                             FOREIGN KEY (produto_id) REFERENCES Produto(produto_id) ON DELETE CASCADE
);

CREATE TABLE Pedido_Header (
                               pedido_id INT AUTO_INCREMENT PRIMARY KEY,
                               mesa_id INT,
                               data_hora_abertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               data_hora_fechamento TIMESTAMP NULL,
                               colaborador_id INT,
                               usuario_id INT,
                               id_dispositivo VARCHAR(255),
                               ip_conexao VARCHAR(15),
                               whatsapp_cliente VARCHAR(20),
                               nome_cliente VARCHAR(255),
                               numero_pessoas INT,
                               valor_total_pedido DECIMAL(10, 2),
                               valor_desconto DECIMAL(10, 2),
                               valor_taxas DECIMAL(10, 2),
                               valor_total_pago DECIMAL(10, 2),
                               cpf_pagador VARCHAR(14),
                               meio_pagamento INT,
                               status VARCHAR(50),
                               observacao TEXT,
                               FOREIGN KEY (mesa_id) REFERENCES Mesa(mesa_id) ON DELETE CASCADE,
                               FOREIGN KEY (colaborador_id) REFERENCES Colaborador(colaborador_id) ON DELETE SET NULL,
                               FOREIGN KEY (usuario_id) REFERENCES Usuario(usuario_id) ON DELETE SET NULL
);

CREATE TABLE Pedido_Body (
                             pedido_body_id INT AUTO_INCREMENT PRIMARY KEY,
                             pedido_id INT,
                             produto_id INT,
                             quantidade INT,
                             tipo_produto VARCHAR(20),
                             FOREIGN KEY (pedido_id) REFERENCES Pedido_Header(pedido_id) ON DELETE CASCADE,
                             FOREIGN KEY (produto_id) REFERENCES Produto(produto_id)
);

CREATE TABLE Chamado_Header (
                                chamado_id INT AUTO_INCREMENT PRIMARY KEY,
                                mesa_id INT,
                                data_hora_abertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                cliente_leu_qrcode BOOLEAN DEFAULT FALSE,
                                data_hora_fechamento TIMESTAMP NULL,
                                atendente_leu_qrcode BOOLEAN DEFAULT FALSE,
                                opcao INT,
                                mensagem_adicional TEXT,
                                status VARCHAR(50),
                                FOREIGN KEY (mesa_id) REFERENCES Mesa(mesa_id) ON DELETE CASCADE
);

CREATE TABLE Chamado_Body (
                              chamado_body_id INT AUTO_INCREMENT PRIMARY KEY,
                              chamado_id INT,
                              colaborador_id INT,
                              data_hora_abertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              cliente_leu_qrcode BOOLEAN DEFAULT FALSE,
                              data_hora_leitura_qrcode TIMESTAMP NULL,
                              data_hora_ida_mesa TIMESTAMP NULL,
                              status VARCHAR(50),
                              FOREIGN KEY (chamado_id) REFERENCES Chamado_Header(chamado_id) ON DELETE CASCADE,
                              FOREIGN KEY (colaborador_id) REFERENCES Colaborador(colaborador_id)
);
