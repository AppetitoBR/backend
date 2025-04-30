CREATE TABLE cliente (
                         cliente_id INT AUTO_INCREMENT PRIMARY KEY,
                         nome_completo VARCHAR(255) NOT NULL,
                         apelido VARCHAR(50),
                         cpf VARCHAR(14) UNIQUE,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         senha VARCHAR(255) NOT NULL,
                         data_nascimento DATE,
                         idioma_padrao VARCHAR(50),
                         nacionalidade VARCHAR(100),
                         imagem_perfil LONGBLOB,
                         situacao ENUM('ATIVO', 'INATIVO', 'BLOQUEADO') DEFAULT 'ATIVO',
                         contatos TEXT,
                         endereco TEXT,
                         redes_sociais TEXT,
                         data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         data_atualizacao TIMESTAMP NULL
);

CREATE TABLE usuario_dashboard (
                                   usuario_dashboard_id INT AUTO_INCREMENT PRIMARY KEY,
                                   nome_completo VARCHAR(255) NOT NULL,
                                   email VARCHAR(255) UNIQUE NOT NULL,
                                   senha VARCHAR(255) NOT NULL,
                                   telefone VARCHAR(20),
                                   situacao ENUM('ATIVO', 'INATIVO', 'BLOQUEADO') DEFAULT 'ATIVO',
                                   data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   data_atualizacao TIMESTAMP NULL
);

CREATE TABLE estabelecimento (
                                 estabelecimento_id INT AUTO_INCREMENT PRIMARY KEY,
                                 razao_social VARCHAR(255) NOT NULL,
                                 nome_fantasia VARCHAR(255) UNIQUE NOT NULL,
                                 cnpj VARCHAR(18) UNIQUE NOT NULL,
                                 contatos TEXT,
                                 endereco TEXT,
                                 tipo VARCHAR(20),
                                 data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 segmento VARCHAR(100),
                                 usuario_cadastro_id INT,
                                 data_alteracao_cadastro DATETIME NULL,
                                 usuario_alteracao_id INT,
                                 observacao TEXT,
                                 logomarca TEXT,
                                 url_cardapio_digital VARCHAR(255),
                                 subdominio_appetito VARCHAR(255),
                                 FOREIGN KEY (usuario_cadastro_id) REFERENCES usuario_dashboard(usuario_dashboard_id) ON DELETE SET NULL,
                                 FOREIGN KEY (usuario_alteracao_id) REFERENCES usuario_dashboard(usuario_dashboard_id) ON DELETE SET NULL
);

CREATE TABLE usuario_estabelecimento (
                                         usuario_estabelecimento_id INT AUTO_INCREMENT PRIMARY KEY,
                                         usuario_dashboard_id INT NOT NULL,
                                         estabelecimento_id INT NOT NULL,
                                         papel ENUM('ADMINISTRADOR', 'GERENTE', 'ATENDENTE', 'COZINHEIRO', 'COLABORADOR') NOT NULL,
                                         data_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
                                         data_fim DATE NULL,
                                         FOREIGN KEY (usuario_dashboard_id) REFERENCES usuario_dashboard(usuario_dashboard_id) ON DELETE CASCADE,
                                         FOREIGN KEY (estabelecimento_id) REFERENCES estabelecimento(estabelecimento_id) ON DELETE CASCADE,
                                         UNIQUE (usuario_dashboard_id, estabelecimento_id, papel)
);

CREATE TABLE estabelecimento_contrato (
                                          estabelecimento_id INT PRIMARY KEY,
                                          termos_contratuais TEXT,
                                          plano_financeiro TEXT,
                                          FOREIGN KEY (estabelecimento_id) REFERENCES estabelecimento(estabelecimento_id) ON DELETE CASCADE
);

CREATE TABLE cardapio (
                          cardapio_id INT AUTO_INCREMENT PRIMARY KEY,
                          estabelecimento_id INT NOT NULL,
                          nome VARCHAR(255) NOT NULL,
                          secao VARCHAR(100),
                          descricao VARCHAR(255),
                          vigencia_inicio DATE,
                          vigencia_fim DATE,
                          ativo BOOLEAN DEFAULT TRUE,
                          FOREIGN KEY (estabelecimento_id) REFERENCES estabelecimento(estabelecimento_id) ON DELETE CASCADE
);

CREATE TABLE mesa (
                      mesa_id INT AUTO_INCREMENT PRIMARY KEY,
                      estabelecimento_id INT NOT NULL,
                      nome VARCHAR(50) NOT NULL,
                      capacidade INT,
                      status ENUM('LIVRE', 'OCUPADO', 'RESERVADO', 'MANUTENCAO') DEFAULT 'LIVRE',
                      ativo BOOLEAN DEFAULT TRUE,
                      usuario_reserva_id INT,
                      data_hora_inicio_reserva TIMESTAMP NULL,
                      nome_cliente_reserva VARCHAR(255),
                      contato_cliente_reserva VARCHAR(255),
                      observacao TEXT,
                      qrcode longblob NOT NULL,
                      FOREIGN KEY (estabelecimento_id) REFERENCES estabelecimento(estabelecimento_id) ON DELETE CASCADE
);
CREATE TABLE produto (
                         produto_id INT AUTO_INCREMENT PRIMARY KEY,
                         cardapio_id INT NOT NULL,
                         nome_curto VARCHAR(100) NOT NULL,
                         nome_longo VARCHAR(255),
                         categoria VARCHAR(100),
                         tamanho VARCHAR(50),
                         preco_custo DECIMAL(10,2),
                         preco_venda DECIMAL(10,2),
                         estoque INT DEFAULT 0,
                         estoque_minimo INT DEFAULT 0,
                         ativo BOOLEAN DEFAULT TRUE,
                         unidade_medida VARCHAR(50),
                         imagens longblob,
                         FOREIGN KEY (cardapio_id) REFERENCES cardapio(cardapio_id) ON DELETE CASCADE
);

CREATE TABLE complemento (
                             complemento_id INT AUTO_INCREMENT PRIMARY KEY,
                             produto_id INT NOT NULL,
                             nome_curto VARCHAR(100) NOT NULL,
                             quantidade_produto_vinculado VARCHAR(255),
                             preco_custo DECIMAL(10,2),
                             preco_venda DECIMAL(10,2),
                             imagens TEXT,
                             ativo BOOLEAN DEFAULT TRUE,
                             FOREIGN KEY (produto_id) REFERENCES produto(produto_id) ON DELETE CASCADE
);

CREATE TABLE pedido (
                        pedido_id INT AUTO_INCREMENT PRIMARY KEY,
                        cliente_id INT NOT NULL,
                        mesa_id INT NOT NULL,
                        total DECIMAL(10,2) NOT NULL,
                        status ENUM('ABERTO', 'FINALIZADO', 'CANCELADO') DEFAULT 'ABERTO',
                        data_hora_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (cliente_id) REFERENCES cliente(cliente_id) ON DELETE CASCADE,
                        FOREIGN KEY (mesa_id) REFERENCES mesa(mesa_id) ON DELETE CASCADE
);

CREATE TABLE pedido_item (
                             pedido_item_id INT AUTO_INCREMENT PRIMARY KEY,
                             pedido_id INT NOT NULL,
                             produto_id INT NOT NULL,
                             quantidade INT NOT NULL,
                             preco_unitario DECIMAL(10,2) NOT NULL,
                             observacao TEXT,
                             FOREIGN KEY (pedido_id) REFERENCES pedido(pedido_id) ON DELETE CASCADE,
                             FOREIGN KEY (produto_id) REFERENCES produto(produto_id) ON DELETE CASCADE
);

CREATE TABLE chamado (
                         chamado_id INT AUTO_INCREMENT PRIMARY KEY,
                         mesa_id INT NOT NULL,
                         cliente_id INT NOT NULL,
                         data_hora_abertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         cliente_leu_qrcode BOOLEAN DEFAULT FALSE,
                         atendente_leu_qrcode BOOLEAN DEFAULT FALSE,
                         data_hora_fechamento TIMESTAMP NULL,
                         data_hora_atendimento TIMESTAMP NULL,
                         mensagem_adicional TEXT,
                         status ENUM('CHAMADO', 'ATENDIDO', 'CANCELADO') DEFAULT 'CHAMADO',
                         FOREIGN KEY (mesa_id) REFERENCES mesa(mesa_id) ON DELETE CASCADE,
                         FOREIGN KEY (cliente_id) REFERENCES cliente(cliente_id) ON DELETE CASCADE
);
