type GamesLog = {
  gamesLog: GameRecord[];
};

type GameRecord = {
  timestamp: number;
  gameName: string;
  gameDate: string;
  playerResults: PlayerResult[];
};

type PlayerResult = {
  playerName: string;
  playerScore?: number;
  isWinner?: boolean;
  comment?: string;
};
