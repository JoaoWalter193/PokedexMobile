const express = require('express');
const { Pool } = require('pg');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
const PORT = 3000;

// Configuração do PostgreSQL
const pool = new Pool({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'pokedex_user',
    password: process.env.DB_PASSWORD || 'pokedex_pass',
    database: process.env.DB_NAME || 'pokedex',
    port: process.env.DB_PORT || 5432
});

app.use(cors());
app.use(bodyParser.json());

// Health check
app.get('/api/health', async (req, res) => {
    try {
        await pool.query('SELECT 1');
        res.json({ status: 'OK', database: 'Conectado' });
    } catch (error) {
        res.status(500).json({ error: 'Erro na conexão com o banco' });
    }
});

app.post('/api/login', async (req, res) => {
    const { login, senha } = req.body;
    
    if (!login || !senha) {
        return res.status(400).json({ 
            success: false, 
            message: 'Login e senha são obrigatórios' 
        });
    }
    
    try {
        const result = await pool.query(
            'SELECT * FROM usuarios WHERE login = $1 AND senha = $2',
            [login, senha]
        );
        
        if (result.rows.length > 0) {
            res.json({ 
                success: true, 
                message: 'Login realizado com sucesso'
            });
        } else {
            res.status(401).json({ 
                success: false, 
                message: 'Login ou senha incorretos' 
            });
        }
    } catch (error) {
        res.status(500).json({ 
            success: false, 
            message: 'Erro no servidor' 
        });
    }
});

app.get('/api/dashboard/stats', async (req, res) => {
    try {
        const totalResult = await pool.query('SELECT COUNT(*) as total FROM pokemons');
        const tiposResult = await pool.query(`
            SELECT tipo, COUNT(*) as quantidade 
            FROM pokemons 
            GROUP BY tipo 
            ORDER BY quantidade DESC 
            LIMIT 3
        `);
        const habilidadesResult = await pool.query(`
            SELECT habilidade, COUNT(*) as quantidade
            FROM (
                SELECT unnest(habilidades) as habilidade
                FROM pokemons
            ) AS habilidades_unnested
            GROUP BY habilidade
            ORDER BY quantidade DESC
            LIMIT 3
        `);
        
        res.json({
            totalPokemons: parseInt(totalResult.rows[0].total),
            topTipos: tiposResult.rows,
            topHabilidades: habilidadesResult.rows
        });
    } catch (error) {
        res.status(500).json({ error: 'Erro ao buscar estatísticas' });
    }
});

app.get('/api/pokemons', async (req, res) => {
    try {
        const result = await pool.query('SELECT id, nome, tipo, habilidades, usuario_login FROM pokemons ORDER BY nome');
        res.json(result.rows);
    } catch (error) {
        res.status(500).json({ error: 'Erro ao buscar pokémons' });
    }
});

app.post('/api/pokemons', async (req, res) => {
    const { nome, tipo, habilidades, usuario_login } = req.body;
    
    if (!nome || !tipo || !habilidades || !usuario_login) {
        return res.status(400).json({ 
            success: false, 
            message: 'Todos os campos são obrigatórios' 
        });
    }
    
    if (!Array.isArray(habilidades) || habilidades.length === 0 || habilidades.length > 3) {
        return res.status(400).json({ 
            success: false, 
            message: 'São necessárias de 1 a 3 habilidades' 
        });
    }
    
    try {
        const existeResult = await pool.query('SELECT id FROM pokemons WHERE nome = $1', [nome]);
        
        if (existeResult.rows.length > 0) {
            return res.status(400).json({ 
                success: false, 
                message: 'Pokémon já cadastrado' 
            });
        }
        
        const result = await pool.query(
            'INSERT INTO pokemons (nome, tipo, habilidades, usuario_login) VALUES ($1, $2, $3, $4) RETURNING *',
            [nome, tipo, habilidades, usuario_login]
        );
        
        res.json({ 
            success: true, 
            message: 'Pokémon cadastrado com sucesso'
        });
    } catch (error) {
        res.status(500).json({ 
            success: false, 
            message: 'Erro ao cadastrar pokémon' 
        });
    }
});

app.get('/api/pokemons/:id', async (req, res) => {
    const { id } = req.params;
    
    try {
        const result = await pool.query(
            'SELECT id, nome, tipo, habilidades, usuario_login FROM pokemons WHERE id = $1',
            [id]
        );
        
        if (result.rows.length === 0) {
            return res.status(404).json({ error: 'Pokémon não encontrado' });
        }
        
        res.json(result.rows[0]);
    } catch (error) {
        res.status(500).json({ error: 'Erro ao buscar pokémon' });
    }
});

app.put('/api/pokemons/:id', async (req, res) => {
    const { id } = req.params;
    const { tipo, habilidades } = req.body;
    
    if (!tipo || !habilidades) {
        return res.status(400).json({ 
            success: false, 
            message: 'Tipo e habilidades são obrigatórios' 
        });
    }
    
    if (!Array.isArray(habilidades) || habilidades.length === 0 || habilidades.length > 3) {
        return res.status(400).json({ 
            success: false, 
            message: 'São necessárias de 1 a 3 habilidades' 
        });
    }
    
    try {
        const result = await pool.query(
            'UPDATE pokemons SET tipo = $1, habilidades = $2 WHERE id = $3 RETURNING *',
            [tipo, habilidades, id]
        );
        
        if (result.rows.length === 0) {
            return res.status(404).json({ 
                success: false, 
                message: 'Pokémon não encontrado' 
            });
        }
        
        res.json({ 
            success: true, 
            message: 'Pokémon atualizado com sucesso'
        });
    } catch (error) {
        res.status(500).json({ 
            success: false, 
            message: 'Erro ao atualizar pokémon' 
        });
    }
});

app.delete('/api/pokemons/:id', async (req, res) => {
    const { id } = req.params;
    
    try {
        const result = await pool.query('DELETE FROM pokemons WHERE id = $1 RETURNING *', [id]);
        
        if (result.rows.length === 0) {
            return res.status(404).json({ 
                success: false, 
                message: 'Pokémon não encontrado' 
            });
        }
        
        res.json({ 
            success: true, 
            message: 'Pokémon excluído com sucesso' 
        });
    } catch (error) {
        res.status(500).json({ 
            success: false, 
            message: 'Erro ao excluir pokémon' 
        });
    }
});

app.get('/api/pokemons/tipo/:tipo', async (req, res) => {
    const { tipo } = req.params;
    
    try {
        const result = await pool.query(
            'SELECT id,nome FROM pokemons WHERE tipo ILIKE $1 ORDER BY nome',
            [`%${tipo}%`]
        );
        
        res.json(result.rows.map(row => ({
				id: row.id,
				nome: row.nome
				})));
    } catch (error) {
        res.status(500).json({ error: 'Erro na pesquisa por tipo' });
    }
});

app.get('/api/pokemons/habilidade/:habilidade', async (req, res) => {
    const { habilidade } = req.params;
    
    try {
        const result = await pool.query(
            `SELECT DISTINCT p.id,p.nome 
             FROM pokemons p 
             WHERE EXISTS (
                 SELECT 1 
                 FROM unnest(p.habilidades) AS h 
                 WHERE h ILIKE $1
             )
             ORDER BY p.nome`,
            [`%${habilidade}%`]
        );
        
        res.json(result.rows.map(row => ({
				id: row.id,
				nome: row.nome
				})));

    } catch (error) {
        res.status(500).json({ error: 'Erro na pesquisa por habilidade' });
    }
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Servidor na porta ${PORT}`);
});
