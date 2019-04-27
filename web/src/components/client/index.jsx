import React from 'react'
import { Layout } from 'antd'
import { Route } from 'react-router-dom'
import Header from '@/components/client/Header'
import GoodList from '@/components/client/GoodList'
import Home from '@/components/client/Home'
import Good from '@/components/client/GoodDetail'
import Profile from '@/components/client/Profile'

const { Content, Footer } = Layout

const Client = () => {
  return (
    <Layout>
      <Header />
      <Content style={{ padding: '0 3%' }}>
        <div
          style={{
            background: '#fff',
            margin: '3% 0',
            padding: '3%',
            minHeight: 280,
          }}
        >
          <Route exact path="/" component={Home} />
          <Route exact path="/profile" component={Profile} />
          <Route exact path="/goods" component={GoodList} />
          <Route exact path="/goods/:id" component={Good} />
        </div>
      </Content>
      <Footer style={{ textAlign: 'center' }}>Flea ©2018 Created by Aries Tam</Footer>
    </Layout>
  )
}

export default Client
