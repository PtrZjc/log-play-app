import {ReactNode} from "react";
import NavButtons from "@/app/NavButtons";
import {Col, Row} from "antd";
import '@/app/globals.css';

export const metadata = {
  title: 'GamesLog',
}

export type ChildProps = {
  children: ReactNode
}

export default function RootLayout({children}: ChildProps) {
  return (
    <html lang="en">
    <body>
    <Row justify="center">
      <Col xs={24} sm={24} md={18} lg={12} xl={10} xxl={8}>
        <div className={"m-6"}>
        <NavButtons/>
        </div>
        {children}
      </Col>
    </Row>
    </body>
    </html>
  )
}
