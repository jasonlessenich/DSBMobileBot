CREATE TABLE guild_notification_account (
    user_id BIGINT PRIMARY KEY,
    class INTEGER NOT NULL,
    subjects VARCHAR ARRAY NOT NULL
)