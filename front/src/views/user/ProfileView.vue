<template>
  <div class="mypage-container">
    <section class="profile-section">
      <div class="profile-left">
        <el-avatar :size="72" icon="UserFilled" class="avatar" />
        <div class="info-text">
          <p class="email">{{ state.user.email }}</p>
        </div>
      </div>
      <el-button class="edit-btn" size="small" @click="goToProfileEdit">프로필 수정</el-button>
    </section>

    <section class="summary-section">
      <div class="summary-item" v-for="(value, key) in summary" :key="key">
        <p class="label">{{ keyLabel(key) }}</p>
        <p class="value">{{ value }}</p>
      </div>
    </section>

    <el-tabs v-model="state.activeTab" class="tabs" stretch>

      <el-tab-pane label="구매내역" name="orders">

        <div class="filter-container">
          <div class="preset-group">
            <el-button-group>
              <el-button
                v-for="(label, key) in dateRanges"
                :key="key"
                :type="activePreset === key ? 'primary' : ''"
                size="default"
                @click="filterByPresetRange(key)"
              >
                {{ label }}
              </el-button>
            </el-button-group>
          </div>

          <div class="picker-group">
            <el-date-picker
              v-model="selectedRange"
              type="daterange"
              unlink-panels
              range-separator="~"
              start-placeholder="시작일"
              end-placeholder="종료일"
              size="large"
              class="custom-date-picker"
              style="flex: 1;"
            />
            <el-button type="primary" size="large" :icon="Search" @click="onSearchByRange">
              조회
            </el-button>
          </div>
        </div>

        <div v-if="filteredProductList.length === 0" class="no-data">
          <el-empty description="조회된 주문 내역이 없습니다." />
        </div>

        <div v-for="(order, index) in filteredProductList" :key="index" class="order-card">

          <div class="order-header">
            <div class="header-left">
              <span class="order-date-text">{{ formatDate(order.createdAt) }}</span>
              <span class="divider">|</span>
              <span class="order-id-label">주문번호 {{ order.orderId }}</span>
            </div>
            <div class="header-right">
              <el-tag :type="getStatusType(order.deliveryStatus)" effect="light" round>
                {{ getStatusLabel(order.deliveryStatus) }}
              </el-tag>
            </div>
          </div>

          <div class="order-body" @click="goToOrderDetail(order.orderId)">
            <div class="product-info-section">
              <div class="product-name">
                {{ getOrderName(order) }}
              </div>
              <div class="product-desc">
                {{ order.products[0]?.optionName || '기본 옵션' }}
              </div>
            </div>

            <div class="price-info-section">
              <div class="price-row">
                <span class="value price">{{ order.totalPrice?.toLocaleString() }}원</span>
              </div>
              <div class="price-row" v-if="order.discountPrice > 0">
                <span class="value discount">(-{{ order.discountPrice?.toLocaleString() }}원 할인)</span>
              </div>
            </div>
          </div>

          <div class="order-footer">
            <el-button class="detail-btn" size="default" @click="goToOrderDetail(order.orderId)">
              상세보기
            </el-button>
          </div>
        </div>

      </el-tab-pane>

      <el-tab-pane label="문의내역" name="inquiries">
        <div class="no-data">준비 중입니다.</div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount, reactive, ref } from 'vue'
import { container } from 'tsyringe'
import { Search, UserFilled } from '@element-plus/icons-vue' // 아이콘 Import

// Repository & Entity Imports (사용자 환경에 맞게 경로 유지)
import OrderRepository from '@/repository/user/OrderRepository.ts'
import UserProfile from '@/entity/user/UserProfile'
import ProfileRepository from '@/repository/user/ProfileRepository.ts'
import OrderProductResponse from "@/entity/order/user/OrderProductResponse.ts"
import router from "@/router";

// --- DI Container Resolve ---
const ORDER_REPOSITORY = container.resolve(OrderRepository)
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)

// --- State & Variables ---
const today = new Date()
const oneWeekAgo = new Date()
oneWeekAgo.setDate(today.getDate() - 7)

const selectedRange = ref<[Date, Date] | null>([oneWeekAgo, today])
const activePreset = ref<string>('week') // 현재 활성화된 필터 버튼
const filteredProductList = ref<OrderProductResponse[]>([])

// 기간 프리셋 정의
const dateRanges = {
  week: '1주일',
  month1: '1개월',
  month3: '3개월',
  month6: '6개월',
}

// 요약 정보 (API 연동 시 reactive 객체 업데이트 필요)
const summary = reactive({
  orders: 0,
  coupons: 0,
  reviews: 0,
})

const state = reactive({
  user: new UserProfile(),
  orderList: [] as OrderProductResponse[],
  activeTab: 'orders',
})

onBeforeMount(() => {
  // 프로필 정보 로드
  state.user = PROFILE_REPOSITORY.getProfile() || new UserProfile()
  // 초기 데이터 로드 (최근 1주일)
  getOrderList(oneWeekAgo, today)
})

function getOrderList(start: Date, end: Date) {
  // 주문 개수
  ORDER_REPOSITORY.getOrderCount().then((response) => {
    summary.orders = response.orderCount
    console.log('앙' + summary.orders)
  })

  // 주문목록
  ORDER_REPOSITORY.getOrders(start, end).then((orderList) => {
    state.orderList = orderList.map((item) => Object.assign(new OrderProductResponse(), item))
    filteredProductList.value = [...state.orderList]
  })
}

// 프리셋 버튼 클릭 핸들러
function filterByPresetRange(type: keyof typeof dateRanges) {
  activePreset.value = type

  const end = new Date()
  let start = new Date()

  switch (type) {
    case 'week':
      start.setDate(end.getDate() - 7)
      break
    case 'month1':
      start.setMonth(end.getMonth() - 1)
      break
    case 'month3':
      start.setMonth(end.getMonth() - 3)
      break
    case 'month6':
      start.setMonth(end.getMonth() - 6)
      break
  }

  selectedRange.value = [start, end]
  getOrderList(start, end)
}

// 수동 조회 버튼 핸들러
function onSearchByRange() {
  activePreset.value = '' // 프리셋 하이라이트 해제
  if (selectedRange.value) {
    const [start, end] = selectedRange.value
    getOrderList(start, end)
  }
}

// 상품명 포맷팅 (XX 외 N개)
function getOrderName(order: any) {
  if (!order.products || order.products.length === 0) {
    return '상품 정보 없음'
  }
  const firstProductName = order.products[0].productName
  const count = order.products.length

  if (count > 1) {
    return `${firstProductName} 외 ${count - 1}개`
  } else {
    return firstProductName
  }
}

// 날짜 포맷팅 (YYYY.MM.DD)
function formatDate(dateStr: string | Date | undefined) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

// 상태값 한글 변환
function getStatusLabel(status: string) {
  const map: Record<string, string> = {
    'READY': '주문접수',
    'SHIPPING': '배송중',
    'COMPLETE': '배송완료',
    'CANCELED': '취소완료'
  }
  return map[status] || status
}

// 상태별 태그 컬러
function getStatusType(status: string) {
  const map: Record<string, string> = {
    'READY': 'info',
    'SHIPPING': 'primary',
    'COMPLETE': 'success',
    'CANCELED': 'danger'
  }
  return map[status] || 'info'
}

// Navigation
function goToProfileEdit() {
  // alert('프로필 수정 페이지 진입')
  router.push('/profile/edit')
}

function goToOrderDetail(orderId: number) {
  alert(`주문 상세 페이지로 이동 (ID: ${orderId})`)
  // router.push(`/mypage/orders/${orderId}`)
}

function keyLabel(key: string) {
  const map = { orders: '주문', coupons: '쿠폰', points: '포인트', reviews: '리뷰' }
  return map[key as keyof typeof map] || key
}
</script>

<style scoped>
.mypage-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 32px 16px;
  font-family: 'Pretendard', sans-serif;
  color: #222;
}

/* 프로필 섹션 */
.profile-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.profile-left { display: flex; align-items: center; }
.avatar { background-color: #f5f5f5; margin-right: 16px; }
.info-text .email { margin-top: 4px; font-size: 14px; color: #666; }

/* 요약 섹션 */
.summary-section {
  display: flex;
  justify-content: space-between;
  background: #fafafa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 32px;
  text-align: center;
}
.summary-item { flex: 1; }
.label { font-size: 13px; color: #999; margin-bottom: 6px; }
.value { font-size: 16px; font-weight: 600; }

/* 탭 & 필터 영역 */
.tabs { background-color: #fff; }

.filter-container {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.preset-group { display: flex; justify-content: flex-start; }
.picker-group { display: flex; gap: 8px; align-items: center; }

/* 날짜 선택기 모바일 대응 */
:deep(.el-date-editor.custom-date-picker) {
  width: 100% !important;
  box-sizing: border-box;
}

/* 주문 목록 카드 스타일 */
.order-card {
  border: 1px solid #eee;
  border-radius: 12px;
  margin-bottom: 24px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  overflow: hidden;
}

/* 카드 헤더 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-bottom: 1px solid #f5f5f5;
  background-color: #fff;
}
.header-left { font-size: 14px; color: #333; font-weight: 500; }
.order-date-text { font-weight: 600; color: #222; }
.divider { margin: 0 8px; color: #ddd; font-size: 12px; }
.order-id-label { color: #888; font-weight: 400; }

/* 카드 바디 */
.order-body {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.2s;
}
.order-body:hover { background-color: #fcfcfc; }

.product-info-section { flex: 1; padding-right: 16px; }
.product-name { font-size: 17px; font-weight: 600; color: #333; margin-bottom: 6px; line-height: 1.4; }
.product-desc { font-size: 13px; color: #999; }

.price-info-section { text-align: right; min-width: 110px; }
.price-row .price { font-size: 18px; font-weight: 700; color: #222; }
.price-row .discount { color: #ff4d4f; font-weight: 500; font-size: 13px; margin-top: 4px; display: block; }

/* 카드 푸터 */
.order-footer {
  padding: 12px 20px;
  background-color: #fcfcfc;
  border-top: 1px solid #f5f5f5;
  display: flex;
  justify-content: center;
}
.detail-btn { width: 100%; font-weight: 500; background-color: #fff; color: #555; border-color: #ddd; }
.detail-btn:hover { color: #409eff; border-color: #c6e2ff; background-color: #ecf5ff; }

/* 기타 */
.no-data { text-align: center; padding: 40px 0; color: #999; }
</style>
