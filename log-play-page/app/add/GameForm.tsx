import React, {useState} from 'react';
import {AutoComplete, Button, Checkbox, Col, DatePicker, Form, Input, Row, Select, Switch, TimePicker,} from 'antd';
import {GAMES, PLAYERS} from "@/app/add/constants";
import dayjs from "dayjs";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import '../globals.css';
import duration from 'dayjs/plugin/duration';

dayjs.extend(duration);

export const GameForm: React.FC = () => {
  const [form] = Form.useForm();
  const [fixedTime, setFixedTime] = useState(false);
  
  const onFinish = (values: any) => {
    values.duration = (values["fixedTime"] ?? dayjs.duration(values.rangeTime[1].diff(values.rangeTime[0]))).format("HH:mm");
    delete values.rangeTime;
    delete values.fixedTime;
    console.log(JSON.stringify(values, null, 2));
  }
  
  const insensitiveCaseIncludes = (input: string, option: any) => {
    const candidate = option.value.toLowerCase();
    const search = input.toLowerCase();
    return candidate.includes(search);
  };
  
  
  return (
    <Row justify={"center"}>
      <Col span={12}>
        <Form
          initialValues={
            {
              gameDate: dayjs(),
              rangeTime: [dayjs().subtract(1, "hour"), dayjs()],
              // 1 hour
              fixedTime: dayjs().startOf("day").add(1, "hour"),
              playerResults: [
                {playerName: PLAYERS[0]},
                {playerName: PLAYERS[1]},
              ]
            }}
          form={form}
          onFinish={onFinish}
        >
          <Form.Item name="gameName" label="Game" rules={[{required: true}]}>
            <AutoComplete
              options={GAMES.map(game => ({value: game}))}
              filterOption={insensitiveCaseIncludes}
            />
          </Form.Item>
          <Form.Item name="gameDate" label="Game Date">
            <DatePicker
              disabledDate={date => date.isAfter(dayjs())}
            />
          </Form.Item>
          <div className={"flex justify-between items-center"}>
            <Switch
              className={"-mt-6 mr-2"}
              checkedChildren="Fixed Time"
              unCheckedChildren="Time Range"
              onChange={setFixedTime}
            />
            <div className={"flex flex-grow justify-center"}>
              
              {!fixedTime ?
                <Form.Item name="rangeTime">
                  <TimePicker.RangePicker
                    format="HH:mm"
                    minuteStep={5}
                  />
                </Form.Item> :
                <Form.Item name="fixedTime">
                  <TimePicker
                    format="HH:mm"
                    showNow={false}
                    minuteStep={5}
                  />
                </Form.Item>
              }
            </div>
          </div>
          <Form.List name="playerResults">
            {(fields, {add, remove}) => (
              <>
                {fields.map(({key, name, ...restField}) => (
                  <div key={key} className={"flex flex-row justify-between items-center space-x-2"}>
                    <Form.Item
                      className={"w-2/3"}
                      {...restField}
                      name={[name, 'playerName']}
                      rules={[{required: true, message: 'Missing player name'}]}
                    >
                      <AutoComplete
                        placeholder={"Player Name"}
                        options={PLAYERS.map(player => ({value: player}))}
                        filterOption={insensitiveCaseIncludes}
                      />
                    </Form.Item>
                    <Form.Item
                      className={"w-1/5"}
                      {...restField}
                      name={[name, 'playerScore']}
                    >
                      <Input type="number" placeholder="Score"/>
                    </Form.Item>
                    <Form.Item
                      {...restField}
                      name={[name, 'isWinner']}
                    >
                      
                      <Checkbox
                      >👑</Checkbox>
                    </Form.Item>
                    <Form.Item>
                      <MinusCircleOutlined onClick={() => remove(name)}/>
                    </Form.Item>
                  </div>
                ))}
                <Form.Item>
                  <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                    Add field
                  </Button>
                </Form.Item>
              </>
            )}
          </Form.List>
          
          <Form.Item>
            <Button type="primary" htmlType="submit">
              Add
            </Button>
          </Form.Item>
        </Form>
      </Col>
    </Row>
  );
};
