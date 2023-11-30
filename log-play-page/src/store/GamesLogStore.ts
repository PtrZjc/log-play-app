import { makeAutoObservable } from "mobx";

type CurrentGameRecord = Partial<GameRecord> & { numPlayersSelected: number };

export class GamesLogStore {
  currentGameRecord: CurrentGameRecord = INITIAL_GAME_RECORD;
  selectablePlayerList: string[] = INITIAL_PLAYER_LIST;
  selectableGameList: string[] = INITIAL_GAME_LIST;

  constructor() {
    makeAutoObservable(this);
  }
}

const INITIAL_GAME_RECORD: CurrentGameRecord = {
  numPlayersSelected: 2,
  playerResults: [
    {
      playerName: "Ja",
    },
    { playerName: "Ilonka" },
  ],
};
