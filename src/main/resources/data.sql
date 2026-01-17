-- =========================================
-- Users Table
-- =========================================
CREATE TABLE IF NOT EXISTS users (
                                     id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL
    );

-- =========================================
-- Chat Sessions Table-
-- =========================================
CREATE TABLE IF NOT EXISTS chat_sessions (
                                             id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    session_name VARCHAR(255) NOT NULL,
    favorite BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_chat_sessions_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

-- =========================================
-- Chat Messages Table
-- =========================================
CREATE TABLE IF NOT EXISTS chat_messages (
                                             id VARCHAR(50) PRIMARY KEY,
    session_id VARCHAR(50) NOT NULL,
    sender VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    context TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_chat_messages_session
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id)
    );

-- =========================================
-- Seed Users (Runs once safely)
-- =========================================
INSERT INTO users (id, username)
VALUES
    ('venkat', 'Alice'),
    ('yara', 'Bob')
    ON CONFLICT (id) DO NOTHING;

-- =========================================
-- Seed Chat Sessions (Runs once safely)
-- =========================================
INSERT INTO chat_sessions (id, user_id, session_name, favorite, created_at)
VALUES
    ('session1', 'venkat', 'AI Chat with Alice', false, NOW()),
    ('session2', 'yara', 'Favorite Chat', true, NOW())
    ON CONFLICT (id) DO NOTHING;

-- =========================================
-- Seed Chat Messages (Runs once safely)
-- =========================================
INSERT INTO chat_messages (id, session_id, sender, content, context, created_at)
VALUES
    ('msg1', 'session1', 'USER', 'Hello AI', NULL, NOW()),
    ('msg2', 'session1', 'AI', 'Hello Alice! How can I assist you today?', NULL, NOW()),
    ('msg3', 'session2', 'USER', 'Tell me a story', NULL, NOW()),
    ('msg4', 'session2', 'AI', 'Once upon a time...', NULL, NOW())
    ON CONFLICT (id) DO NOTHING;
