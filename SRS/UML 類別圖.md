# UML 類別圖

# 1. 核心資料類別

### Card

```
+----------------------------------+
|              Card                |
+----------------------------------+
| - suit: Suit                     |
| - rank: Rank                     |
+----------------------------------+
| + Card(suit: Suit, rank: Rank)   |
| + getSuit(): Suit                |
| + getRank(): Rank                |
| + getValue(): int                |
+----------------------------------+
```

### Deck

```
+----------------------------------+
|              Deck                |
+----------------------------------+
| - cards: List<Card>              |
| - currentIndex: int              |
+----------------------------------+
| + Deck()                         |
| - initialize(): void             |
| + shuffle(): void                |
| + drawCard(): Card               |
| + isEmpty(): boolean             |
+----------------------------------+
```

### Hand

```
+----------------------------------+
|              Hand                |
+----------------------------------+
| - cards: List<Card>              |
+----------------------------------+
| + Hand()                         |
| + addCard(card: Card): void      |
| + calculateScore(): int          |
| + isBust(): boolean              |
| + isBlackjack(): boolean         |
| + clear(): void                  |
| + getCards(): List<Card>         |
+----------------------------------+
```

# 2. 遊戲角色類別

### Player

```
+----------------------------------+
|             Player               |
+----------------------------------+
| - hand: Hand                     |
| - chips: int                     |
+----------------------------------+
| + Player(initialChips: int)      |
| + getHand(): Hand                |
| + getChips(): int                |
| + placeBet(amount: int): boolean |
| + receivePayout(amount: int): void|
| + isBankrupt(): boolean          |
+----------------------------------+
```

### Dealer

```
+----------------------------------+
|             Dealer               |
+----------------------------------+
| - hand: Hand                     |
| - holeCardRevealed: boolean      |
+----------------------------------+
| + Dealer()                       |
| + getHand(): Hand                |
| + play(deck: Deck): void         |
| + revealHoleCard(): void         |
| + isCardVisible(index: int): boolean |
| + reset(): void                  |
+----------------------------------+
```

# 3. 遊戲管理類別

### BlackjackGame

```
+------------------------------------------+
|               BlackjackGame             |
+------------------------------------------+
| - deck: Deck                             |
| - player: Player                         |
| - dealer: Dealer                         |
| - currentBet: int                        |
| - state: GameState                       |
| - result: GameResult                     |
+------------------------------------------+
| + BlackjackGame()                        |
| + startRound(bet: int): boolean          |
| + hit(): void                            |
| + stand(): void                          |
| - dealerPlay(): void                     |
| - checkBlackjack(): boolean              |
| - determineWinner(): void                |
| - settleBet(): void                      |
| - resetRound(): void                     |
| + getState(): GameState                  |
| + getResult(): GameResult                |
+------------------------------------------+
```

# 4. 核心控制 & 可視化 類別

### GameController

```
+------------------------------------------+
|              GameController              |
+------------------------------------------+
| - game: BlackjackGame                    |
| - view: GameView                         |
+------------------------------------------+
| + GameController(game, view)             |
| + handleStartRound(): void               |
| + handleHit(): void                      |
| + handleStand(): void                    |
| + updateView(): void                     |
+------------------------------------------+
```

### GameView

```
+------------------------------------------+
|                 GameView                 |
+------------------------------------------+
| - controller: GameController             |
+------------------------------------------+
| + GameView(controller)                   |
| + showPlayerHand(hand: Hand): void       |
| + showDealerHand(hand: Hand): void       |
| + showChips(chips: int): void            |
| + showResult(result: GameResult): void   |
| + setButtonsEnabled(state: GameState): void|
+------------------------------------------+
```

# 5. Enumeration 介面

### Suit

```
+----------------------+
|     <<enumeration>>  |
|         Suit         |
+----------------------+
| HEARTS               |
| DIAMONDS             |
| CLUBS                |
| SPADES               |
+----------------------+
```

### Rank

```
+----------------------+
|     <<enumeration>>  |
|         Rank         |
+----------------------+
| ACE                  |
| TWO                  |
| THREE                |
| FOUR                 |
| FIVE                 |
| SIX                  |
| SEVEN                |
| EIGHT                |
| NINE                 |
| TEN                  |
| JACK                 |
| QUEEN                |
| KING                 |
+----------------------+
```

### GameState

```
+----------------------+
|     <<enumeration>>  |
|      GameState       |
+----------------------+
| READY                |
| PLAYER_TURN          |
| DEALER_TURN          |
| ROUND_OVER           |
| GAME_OVER            |
+----------------------+
```

### GameResult

```
+----------------------+
|     <<enumeration>>  |
|      GameResult      |
+----------------------+
| PLAYER_WIN           |
| DEALER_WIN           |
| PUSH                 |
+----------------------+
```

這裡先採用三種結果，Blackjack 屬於特殊的玩家勝利，另外透過 `Hand.isBlackjack()` 判斷是否適用 3:2 賠付。

# 6. 類別關係圖

最後把類別之間的關係整理出來。

這次使用 UML 常見符號：

| 符號 | 意義 |

|---|---|

| `-->` | 一般關聯 |

| `..>` | 依賴 |

| `*--` | 組合 Composition |

| `o--` | 聚合 Aggregation |

| `--|>` | 繼承 Generalization |

| `..|>` | 實作 Realization |

```
                     +----------------+
                     |      Card      |
                     +----------------+
                       ^            ^
                       |            |
                    uses          uses
                       |            |
              +--------+            +--------+
              |                              |
       +-------------+                +-------------+
       |     Deck    |                |     Hand    |
       +-------------+                +-------------+
              ^                              ^
              |                              |
              | uses                         |
              |                    +---------+---------+
              |                    |                   |
              |              +-----------+       +-----------+
              |              |  Player   |       |  Dealer   |
              |              +-----------+       +-----------+
              |                    ^                   ^
              |                    |                   |
              +--------------------+-------------------+
                                   |
                            +------------------+
                            |  BlackjackGame   |
                            +------------------+
                                   ^
                                   |
                                   |
                            +------------------+
                            |  GameController  |
                            +------------------+
                                   |
                                   v
                            +------------------+
                            |     GameView     |
                            +------------------+
```

這個版本沒有繼承關係，因此不需要建立 `extends`。`Player` 和 `Dealer` 目前是各自持有 `Hand`，而不是繼承同一個父類別。

# 7. UML 存取修飾符對照

之後我們統一使用這套表示法：

| UML | Java | 意義 |
| --- | --- | --- |
| `+` | public | 公開 |
| `-` | private | 私有 |
| `#` | protected | 子類別及同 package 可存取 |
| `~` | package-private | 同 package 可存取 |