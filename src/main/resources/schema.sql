
-- 建立玩家資料表
CREATE TABLE IF NOT EXISTS players (
    uid INTEGER PRIMARY KEY AUTOINCREMENT,
    player_name TEXT NOT NULL
);

-- 建立遊戲歷史紀錄資料表
CREATE TABLE IF NOT EXISTS game_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- 對應玩家 UID
    -- NULL 代表訪客或未綁定玩家的舊紀錄
    uid INTEGER,

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
        DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (uid) REFERENCES players(uid)
);

-- 玩家姓名索引
CREATE INDEX IF NOT EXISTS idx_players_name
ON players(player_name);

-- 遊戲紀錄 UID 索引
CREATE INDEX IF NOT EXISTS idx_history_uid
ON game_history(uid);
