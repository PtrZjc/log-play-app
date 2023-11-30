"use client";

import React, { useState } from "react";
import {
  Button,
  Cascader,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Radio,
  Select,
  Switch,
  TreeSelect,
} from "antd";
import styled from "styled-components";

type SizeType = Parameters<typeof Form>[0]["size"];

const StyledFormItem = styled(Form.Item)`
  margin-bottom: 2px; /* You can set this to whatever value you want */
`;

const App: React.FC = () => {
  const [componentSize, setComponentSize] = useState<SizeType | "default">(
    "default",
  );

  const onFormLayoutChange = ({ size }: { size: SizeType }) => {
    setComponentSize(size);
  };

  return (
    <Form
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 14 }}
      initialValues={{ size: componentSize }}
      onValuesChange={onFormLayoutChange}
      size={componentSize as SizeType}
      style={{ maxWidth: 600 }}
    >
      <StyledFormItem label="Form Size" name="ssize">
        <Radio.Group>
          <Radio.Button value="small">Small</Radio.Button>
          <Radio.Button value="default">Default</Radio.Button>
          <Radio.Button value="large">Large</Radio.Button>
        </Radio.Group>
      </StyledFormItem>
      <StyledFormItem label="Input">
        <Input />
      </StyledFormItem>
      <StyledFormItem label="Select">
        <Select>
          <Select.Option value="demo">Demo</Select.Option>
        </Select>
      </StyledFormItem>
      <StyledFormItem label="TreeSelect">
        <TreeSelect
          treeData={[
            {
              title: "Light",
              value: "light",
              children: [{ title: "Bamboo", value: "bamboo" }],
            },
          ]}
        />
      </StyledFormItem>
      <StyledFormItem label="Cascader">
        <Cascader
          options={[
            {
              value: "zhejiang",
              label: "Zhejiang",
              children: [{ value: "hangzhou", label: "Hangzhou" }],
            },
          ]}
        />
      </StyledFormItem>
      <StyledFormItem label="DatePicker">
        <DatePicker />
      </StyledFormItem>
      <StyledFormItem label="InputNumber">
        <InputNumber />
      </StyledFormItem>
      <StyledFormItem label="Switch" valuePropName="checked">
        <Switch />
      </StyledFormItem>
      <StyledFormItem label="Button">
        <Button>Button</Button>
      </StyledFormItem>
    </Form>
  );
};

export default App;
