-- Tabela de usuários
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL
);

-- Tabela de pokémons
CREATE TABLE pokemons (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    habilidades TEXT[] NOT NULL,
    usuario_login VARCHAR(50) NOT NULL
);

-- 3 usuários pré-cadastrados conforme requisito
INSERT INTO usuarios (login, senha) VALUES 
('ash', '123'),
('misty', '123'),
('brock', '123');

-- Pelo menos 10 pokémons pré-cadastrados
INSERT INTO pokemons (nome, tipo, habilidades, usuario_login) VALUES 
('Pikachu', 'Elétrico', '{"Choque do Trovão", "Cauda de Ferro", "Ataque Rápido"}', 'ash'),
('Charmander', 'Fogo', '{"Lança-chamas", "Arranhão", "Brasas"}', 'ash'),
('Bulbasaur', 'Grama', '{"Chicote de Vinha", "Semente Sanguessuga", "Folha Navalha"}', 'misty'),
('Squirtle', 'Água', '{"Jato de Água", "Casco Duro", "Redemoinho"}', 'misty'),
('Caterpie', 'Inseto', '{"Teia", "Picada"}', 'brock'),
('Pidgey', 'Normal', '{"Investida", "Redemoinho", "Ataque de Asa"}', 'ash'),
('Rattata', 'Normal', '{"Hiper Presa", "Perseguição", "Ataque Rápido"}', 'brock'),
('Spearow', 'Normal', '{"Bicada", "Fúria", "Ataque de Asa"}', 'misty'),
('Ekans', 'Veneno', '{"Picada", "Constrição", "Ácido"}', 'ash'),
('Sandshrew', 'Terrestre', '{"Arranhão", "Golpes Rápidos", "Terra Treme"}', 'brock');
