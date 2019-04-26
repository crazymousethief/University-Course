import React, { useEffect, useState } from 'react'
import { Card, Avatar, Button, Icon, Tabs } from 'antd'
import Lightbox from 'react-images'
import { StickyContainer, Sticky } from 'react-sticky'
import GoodPrice from '@/components/client/Price'
import Item from '@/models/Item'
import Comments from './Comments'
import './index.css'

const TabPane = Tabs.TabPane

const Good = ({
  match: {
    params: { id },
  },
}) => {
  const [loading, setLoading] = useState(true)
  const [data, setData] = useState({ images: [{ url: '' }], seller: { username: '' }, createdDate: 0 })
  const [open, setOpen] = useState(false)
  const [current, setCurrent] = useState(0)

  const fetchData = async () => {
    setLoading(true)
    const { data } = await Item.getById(id)
    setData(data)
    setLoading(false)
  }

  useEffect(() => {
    fetchData()
  }, [id])

  return (
    <div>
      <Card
        title={data.name}
        bordered={false}
        loading={loading}
        extra={
          <>
            <Icon type="heart" theme="twoTone" twoToneColor="#eb2f96" />
            {data.collectByCount}人已收藏
          </>
        }
      >
        <div className="good-container">
          <div className="good-images">
            <Lightbox
              images={data.images.map(i => ({ src: i.url }))}
              isOpen={open}
              currentImage={current % data.images.length}
              onClickPrev={() => setCurrent(current - 1 + data.images.length)}
              onClickNext={() => setCurrent(current + 1 + data.images.length)}
              onClose={() => setOpen(false)}
            />
            <img src={data.images[0].url} alt="good" onClick={() => setOpen(true)} />
          </div>
          <div className="good-details">
            <div className="good-info">
              <div>
                <span>售价</span>
                <GoodPrice className="price" value={data.price} />
              </div>
              <div>
                <span>原价</span>
                <GoodPrice className="original-price" value={data.originalPrice} />
              </div>
              <div>
                <span>发布地</span>
                {data.location}
              </div>
            </div>
            <Card style={{ marginTop: 16 }} loading={loading}>
              <Card.Meta
                avatar={<Avatar src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png" />}
                title={data.seller.username}
                description={`${new Date(data.createdDate).toLocaleDateString()}发布`}
              />
            </Card>
            <Button type="primary" size="large" className="buy-btn">
              立即购买
            </Button>
          </div>
        </div>
      </Card>
      <StickyContainer>
        <Tabs
          defaultActiveKey="1"
          renderTabBar={(props, DefaultTabBar) => (
            <Sticky bottomOffset={80}>
              {({ style }) => <DefaultTabBar {...props} style={{ ...style, zIndex: 1, background: '#fff' }} />}
            </Sticky>
          )}
        >
          <TabPane tab="详情" key="1">
            Content of Tab Pane 1
          </TabPane>
          <TabPane tab="留言" key="2">
            <Comments id={id} />
          </TabPane>
        </Tabs>
      </StickyContainer>
    </div>
  )
}

export default Good
