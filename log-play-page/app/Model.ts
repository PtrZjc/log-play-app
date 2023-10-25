type GamesLog = {
  games: GameRecord[];
};

type GameRecord = {
  userName?: string;
  timestamp: number;
  gameName: string;
  gameDate: string;
  gameDescription?: string;
  solo?: boolean;
  duration?: string;
  playerResults: PlayerResult[];
};

type PlayerResult = {
  playerName: string;
  playerScore: number;
  isWinner?: boolean;
  comment?: string;
};
