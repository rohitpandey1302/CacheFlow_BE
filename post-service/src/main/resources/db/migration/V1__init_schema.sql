-- V1__init_schema.sql
-- Initial schema for CacheFlow backend

CREATE TABLE IF NOT EXISTS posts (
    id          BIGINT          PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    title       VARCHAR(512)    NOT NULL,
    body        TEXT            NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS comments (
    id          BIGINT          PRIMARY KEY,
    post_id     BIGINT          NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    name        VARCHAR(256)    NOT NULL,
    email       VARCHAR(256)    NOT NULL,
    body        TEXT            NOT NULL
);

-- Indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts(user_id);
CREATE INDEX IF NOT EXISTS idx_posts_id_asc ON posts(id ASC);   -- for paginated ORDER BY id ASC
CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments(post_id);