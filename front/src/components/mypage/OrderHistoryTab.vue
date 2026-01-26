<template>
  <div class="order-history-container">

    <div class="header-section">
      <h2 class="page-title">주문현황내역</h2>
      <div class="guide-text">
        <p>• 기본적으로 최근 3개월간의 자료가 조회되며, 기간 검색 시 지난 주문내역을 조회하실 수 있습니다.</p>
        <p>• 주문번호를 클릭하시면 해당 주문에 대한 상세내역을 확인하실 수 있습니다.</p>
      </div>
    </div>

    <div class="filter-container">
      <div class="preset-group">
        <el-button-group>
          <el-button
            v-for="(label, key) in dateRanges"
            :key="key"
            :type="activePreset === key ? 'primary' : ''"
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
          @change="onSearchByRange"
        />
        <el-button type="primary" :icon="Search" @click="onSearchByRange">조회</el-button>
      </div>
    </div>

    <div v-if="state.productPage.items.length === 0" class="no-data">
      <el-empty description="조회된 주문 내역이 없습니다."/>
    </div>

    <div class="order-container">
      <div v-for="order in state.productPage.items" :key="order.orderId" class="order-card">
        <div class="order-header">
          <span class="order-id clickable">주문번호: {{ order.orderId }}</span>
        </div>

        <div class="product-list">
          <div
            v-for="product in order.products"
            :key="product.productId"
            class="product-item"
          >
            <div class="product-name">{{ product.productName }}</div>
            <div class="product-info">
              <span>{{ product.quantity }}개</span>
              <span class="price">{{ formatPrice(product.totalPrice) }}원</span>
            </div>
          </div>
        </div>

        <hr class="divider" />

        <div class="order-summary">
          <div class="row">
            <span>할인 금액</span>
            <span class="discount">-{{ formatPrice(order.discountPrice) }}원</span>
          </div>
          <div class="row total">
            <span>총 결제 금액</span>
            <span>{{ formatPrice(order.totalPrice) }}원</span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-container" v-if="orderList.length > 0 || totalCount > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalCount"
        layout="total, prev, pager, next"
        background
        @current-change="handlePageChange"
      />
    </div>

  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import { container } from 'tsyringe'
import { Search } from '@element-plus/icons-vue'
import OrderRepository from '@/repository/user/OrderRepository.ts'
import OrderProductResponse from "@/entity/order/user/OrderProductResponse.ts"
import Paging from "@/entity/data/Paging.ts";

const ORDER_REPOSITORY = container.resolve(OrderRepository)

// 상태 관리
const orderList = ref<OrderProductResponse[]>([])
const activePreset = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)

// 날짜 초기화
const today = new Date()
const threeMonthAgo = new Date()
threeMonthAgo.setDate(today.getDate() - 90)

// [수정] 타입 오류 방지를 위해 null 허용으로 변경 권장
const selectedRange = ref<[Date, Date] | [null, null]>([null, null])
const dateRanges = { week: '1주일', month1: '1개월', month3: '3개월' }

interface State {
  productPage: Paging<OrderProductResponse>
}

const state = reactive<State>({
  productPage: new Paging<OrderProductResponse>(),
})

onMounted(() => {
  // 초기 로딩 시 1페이지 요청 (안내 문구대로 최근 3개월 조회)
  fetchOrders(1, threeMonthAgo, today)
})

function fetchOrders(page: number, start: Date, end: Date) {
  ORDER_REPOSITORY.getOrders(page, start, end).then((responsePage) => {
    state.productPage = responsePage
  }).catch(err => {
    console.error("주문 내역 조회 실패", err)
    state.productPage.items = []
    totalCount.value = 0
  })
}

function filterByPresetRange(type: string) {
  activePreset.value = type
  currentPage.value = 1

  const end = new Date()
  const start = new Date()

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
  // @ts-ignore
  selectedRange.value = [start, end]

  fetchOrders(1, start, end)
}

function onSearchByRange() {
  if (selectedRange.value && selectedRange.value[0]) {
    currentPage.value = 1
    // @ts-ignore
    fetchOrders(1, selectedRange.value[0], selectedRange.value[1])
  }
}

function handlePageChange(newPage: number) {
  if (selectedRange.value && selectedRange.value[0]) {
    // @ts-ignore
    fetchOrders(newPage, selectedRange.value[0], selectedRange.value[1])
  } else {
    // 날짜 선택이 없는 초기 상태(3개월)에서의 페이징 처리 필요 시 로직 추가
    // 여기서는 간단히 초기값 재사용
    fetchOrders(newPage, threeMonthAgo, today)
  }
}

function formatPrice(value: string | number) {
  if (!value) return '0'
  return value.toLocaleString()
}
</script>

<style scoped>
/* [추가됨] 헤더 스타일 */
.header-section {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.guide-text {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.guide-text p {
  margin: 0;
}

/* 기존 스타일 유지 */
.filter-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}

.order-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
}

.order-id.clickable {
  cursor: pointer;
  color: #409EFF; /* Element Plus Primary Color 느낌 */
}
.order-id.clickable:hover {
  text-decoration: underline;
}

.product-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.95rem;
}

.product-info span {
  margin-left: 10px;
}

.divider {
  border: 0;
  border-top: 1px solid #eee;
  margin: 12px 0;
}

.order-summary .row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.discount {
  color: #e74c3c;
}

.total {
  font-weight: bold;
  font-size: 1.1rem;
  margin-top: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-bottom: 24px;
}
</style>
