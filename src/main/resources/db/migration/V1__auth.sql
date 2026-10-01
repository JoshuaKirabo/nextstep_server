-- People who can log in. Login is by email, unique regardless of case.
CREATE TABLE app_user (
    id            uuid        PRIMARY KEY,
    email         text        NOT NULL CHECK (length(email) BETWEEN 3 AND 320),
    password_hash text        NOT NULL,
    first_name    text        NOT NULL CHECK (length(first_name) BETWEEN 1 AND 120),
    last_name     text        NOT NULL CHECK (length(last_name)  BETWEEN 1 AND 120),
    tier          text        NOT NULL DEFAULT 'free' CHECK (tier IN ('free', 'pro')),
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX app_user_email_key ON app_user (lower(email));

-- 2FA secret, 0..1 per user. Row present = setup started; confirmed_at set = 2FA active.
CREATE TABLE user_totp (
    user_id      uuid        PRIMARY KEY REFERENCES app_user (id) ON DELETE CASCADE,
    secret       text        NOT NULL,          -- encrypted by the app before storing
    confirmed_at timestamptz,
    created_at   timestamptz NOT NULL DEFAULT now()
);

-- Append-only audit of login attempts. user_id is NULL when the email matched no account.
CREATE TABLE login_event (
    id              bigint      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         uuid        REFERENCES app_user (id) ON DELETE SET NULL,
    email_attempted text        NOT NULL,
    ip              inet        NOT NULL,
    user_agent      text,
    outcome         text        NOT NULL CHECK (outcome IN
                      ('success', 'bad_credentials', 'bad_totp', 'rate_limited')),
    occurred_at     timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX login_event_user_time ON login_event (user_id, occurred_at DESC);
CREATE INDEX login_event_ip_time   ON login_event (ip, occurred_at DESC);
