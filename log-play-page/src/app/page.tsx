"use client";

import React, { useState } from "react";
import { Button, Form, Input, InputNumber } from "antd";
import styled from "styled-components";

type GameRecordFormInput = Partial<Omit<GameRecord, "playerResults">> & {
  numPlayers: number;
  playerResults: Partial<PlayerResult>[];
};

export default function GameEntryForm() {
  const [form] = Form.useForm();

  const initialValues = {
    gameName: "nope",
    numPlayers: 2,
    playerResults: initialPlayerResults,
  };

  console.log(initialValues);

  const onChange = (values: any) => {
    console.log(values);
  };

  return (
    /*
     * GRA
     * [wybrana gra
     * [search field
     * [kolowrotek do wybrania gier wybeirajacy
     * Inicjalnie w kolowrotku jest full, po kazdym wpisaniu zaweza sie liczba. Jesli zostanie 1 to jest koniec. Wartosc ze slidera. Jesli pasuje tylko jedna z searcha, to ona sie pojawi
     * */
    <Form
      form={form}
      labelCol={{ span: 10 }}
      wrapperCol={{ span: 14 }}
      layout={"horizontal"}
      onFinish={onChange}
      onValuesChange={onChange}
      initialValues={initialValues}
      style={{ maxWidth: 600 }}
    >
      {/*<div>Wybrana gra</div>*/}
      <Form.Item
        name="gameName"
        label="Game name"
        rules={[{ required: true, message: "Game name is required" }]}
      >
        <Input></Input>
      </Form.Item>
      <Form.Item name="numPlayers" label="Players number">
        <Input
          addonBefore={renderAddonButton("-")}
          addonAfter={renderAddonButton("+")}
          onChange={() => sanitizeFieldToDigits("numPlayers")}
          min={1}
          max={10}
        />
      </Form.Item>
      <Form.Item name="x" label="X">
        <Input />
      </Form.Item>
      <Button type="primary" htmlType="submit">
        Submit
      </Button>
    </Form>
  );

  function renderAddonButton(sign: "+" | "-") {
    return (
      <>
        <StyledButton
          onClick={() => {
            const newValue =
              Number(form.getFieldValue("numPlayers")) +
              1 * (sign === "+" ? 1 : -1);
            form.setFieldValue("numPlayers", newValue);
          }}
        >
          {sign}
        </StyledButton>
      </>
    );
  }

  function sanitizeFieldToDigits(field: string) {
    const sanitized = Number(form.getFieldValue(field).replace(/\\d+/g, ""));
    const s2 = !isNaN(sanitized) && sanitized < 1 : 1
    form.setFieldValue(field, !isNaN(sanitized) ? sanitized : 1);
  }
}

const initialPlayerResults: Partial<PlayerResult>[] = ["Ja", "Ilonka"].map(
  (name) => ({
    playerName: name,
  }),
);

const StyledButton = styled(Button)`
  border: none;
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
`;
