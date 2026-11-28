-- SQLite
SELECT h.*, p.player_name
FROM game_history AS h
LEFT JOIN players AS p ON p.uid = h.uid
ORDER BY h.id DESC;