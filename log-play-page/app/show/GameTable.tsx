"use client"

import React from 'react';
import type {TableColumnsType} from 'antd';
import {Table} from 'antd';
import {MOCK_RESPONSE} from "@/app/MockResponse";

type GameRecordRow = { key: React.Key, players: number } & GameRecord;
type PlayerResultRow = { key: React.Key } & PlayerResult;

const items = [
  {key: '1', label: 'Action 1'},
  {key: '2', label: 'Action 2'},
];

export default function GameTable() {
  const expandedRowRender = (record: GameRecord) => {
    const columns: TableColumnsType<PlayerResult> = [
      {title: 'Player Name', dataIndex: 'playerName', key: 'playerName'},
      {title: 'Player Score', dataIndex: 'playerScore', key: 'playerScore'},
      {title: 'Comment', dataIndex: 'comment', key: 'comment'},
    ];
    
    return <Table columns={columns} dataSource={record.playerResults} pagination={false}/>;
  };
  
  const columns: TableColumnsType<GameRecord> = [
    {title: 'Game Name', dataIndex: 'gameName', key: 'gameName'},
    {title: 'Players', dataIndex: 'players', key: 'players'},
    {title: 'Game Date', dataIndex: 'gameDate', key: 'gameDate'},
    {title: 'Game Description', dataIndex: 'gameDescription', key: 'gameDescription'},
    {title: 'Solo', dataIndex: 'solo', key: 'solo'},
    {title: 'Duration', dataIndex: 'duration', key: 'duration'},
  ];
  
  
  const data = MOCK_RESPONSE.games.map((game, index) => ({
    ...game,
    key: game.timestamp,
    players: game.playerResults.length,
    playerResults: game.playerResults.map((player, playerIndex) => ({
      ...player,
      playerName: player.isWinner ? player.playerName + ' 👑' : player.playerName,
    })),
  }));
  
  return (
    <Table
      columns={columns}
      expandable={{expandedRowRender}}
      dataSource={data}
    />
  );
}