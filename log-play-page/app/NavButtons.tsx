'use client'

import {Button, Col, Row} from "antd";
import {useRouter} from "next/navigation";

export default function NavButtons() {
  const router = useRouter();
  
  const routeToAdd = () => router.push('/add');
  const routeToShowAll = () => router.push('/show');
  
  return <Row justify="center" gutter={16}>
    <Col span={6} >
      <Button className={"w-full"} onClick={routeToAdd}>Form</Button>
    </Col>
    <Col span={6}>
      <Button className={"w-full"} onClick={routeToShowAll}>Show Games</Button>
    </Col>
  </Row>
}