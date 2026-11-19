
CREATE TABLE IF NOT EXISTS game_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    bet_amount INTEGER NOT NULL
        CHECK (bet_amount > 0),

    player_score INTEGER NOT NULL
        CHECK (player_score >= 0),

    dealer_score INTEGER NOT NULL
        CHECK (dealer_score >= 0),

    result TEXT NOT NULL
        CHECK (
            result IN (
                'PLAYER_WIN',
                'DEALER_WIN',
                'PUSH'
            )
        ),

    chip_change INTEGER NOT NULL,

    played_at TEXT NOT NULL
        DEFAULT CURRENT_TIMESTAMP
);

