import React, {useEffect, useState} from 'react';
import {
  AutoComplete,
  Button,
  Checkbox,
  Col,
  DatePicker,
  Form,
  Input,
  Row,
  Select,
  Space,
  Switch, TimePicker,
} from 'antd';
import {GAMES, PLAYERS} from "@/app/constants";
import dayjs from "dayjs";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import './globals.css';

const {Option} = Select;

const layout = {
  labelCol: {span: 8},
  wrapperCol: {span: 16},
};

const tailLayout = {
  wrapperCol: {offset: 8, span: 16},
};

type GameFormParams = Record<string, string>

export const GameForm: React.FC = () => {
  
  const [form] = Form.useForm();
  
  const [fixedTime, setFixedTime] = useState(false);
  
  const onFinish = (values: any) => console.log(JSON.stringify(values, null, 2));
  
  const insensitiveCaseIncludes = (input: string, option: any) => {
    const candidate = option.value.toLowerCase();
    const search = input.toLowerCase();
    return candidate.includes(search);
  };
  
  
  return (
    <Row justify={"center"}>
      <Col span={9}>
        <Form
          initialValues={
            {
              gameDate: dayjs(),
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
              className={"-mt-6"}
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
                  <Space key={key} align="baseline">
                    <Form.Item
                      {...restField}
                      name={[name, 'playerName']}
                      rules={[{required: true, message: 'Missing player name'}]}
                    >
                      <AutoComplete
                        style={{width: 200}}
                        placeholder={"Player Name"}
                        options={PLAYERS.map(player => ({value: player}))}
                        filterOption={insensitiveCaseIncludes}
                      />
                    </Form.Item>
                    <Form.Item
                      {...restField}
                      name={[name, 'playerScore']}
                      rules={[{required: true, message: 'Missing last name'}]}
                    >
                      <Input type="number" placeholder="Score"/>
                    </Form.Item>
                    <Form.Item
                      {...restField}
                      name={[name, 'isWinner']}
                    >
                      <Checkbox>Won</Checkbox>
                    </Form.Item>
                    <MinusCircleOutlined onClick={() => remove(name)}/>
                  </Space>
                ))}
                <Form.Item>
                  <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                    Add field
                  </Button>
                </Form.Item>
              </>
            )}
          </Form.List>
          
          <Form.Item {...tailLayout}>
            <Button type="primary" htmlType="submit">
              Add
            </Button>
          </Form.Item>
        </Form>
      </Col>
    </Row>
  );
};
