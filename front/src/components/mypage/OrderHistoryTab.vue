<template>
  <div class="order-history-container">
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

    <div v-if="orderList.length === 0" class="no-data">
      <el-empty description="조회된 주문 내역이 없습니다."/>
    </div>

    <div class="order-container">
      <div v-for="order in state.productPage.items" :key="order.orderId" class="order-card">
        <div class="order-header">
          <span class="order-id">주문번호: {{ order.orderId }}</span>
<!--          <span class="order-date">{{ formatDate(order.orderDate) }}</span>-->
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
import Product from "@/entity/product/Product.ts";

const ORDER_REPOSITORY = container.resolve(OrderRepository)

// 상태 관리
const orderList = ref<OrderProductResponse[]>([])
const activePreset = ref('week')
const currentPage = ref(1)
const pageSize = ref(10) // 기본 페이지 사이즈 (필요 시 수정)
const totalCount = ref(0) // 전체 데이터 개수 (페이지네이션 계산용)

// 날짜 초기화
const today = new Date()
const oneWeekAgo = new Date()
oneWeekAgo.setDate(today.getDate() - 7)
const selectedRange = ref<[Date, Date]>([oneWeekAgo, today])
const dateRanges = { week: '1주일', month1: '1개월', month3: '3개월' }

interface State {
  productPage: Paging<OrderProductResponse>
}

const state = reactive<State>({
  productPage: new Paging<OrderProductResponse>(),
})

onMounted(() => {
  // 초기 로딩 시 1페이지 요청
  fetchOrders(1, oneWeekAgo, today)
})

/**
 * 주문 내역 조회 함수
 * @param page 페이지 번호
 * @param start 시작일
 * @param end 종료일
 */
function fetchOrders(page: number, start: Date, end: Date) {
  ORDER_REPOSITORY.getOrders(page, start, end).then((responsePage) => {
    state.productPage = responsePage
    console.log(state.productPage)
  }).catch(err => {
    console.error("주문 내역 조회 실패", err)
    orderList.value = []
    totalCount.value = 0
  })
}

/**
 * 프리셋 버튼 클릭 (1주일, 1개월 등)
 * -> 검색 조건이 변경되므로 1페이지로 초기화
 */
function filterByPresetRange(type: string) {
  activePreset.value = type
  currentPage.value = 1 // 페이지 초기화

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
  selectedRange.value = [start, end]

  // 1페이지로 요청
  fetchOrders(1, start, end)
}

/**
 * 날짜 직접 선택 후 조회 또는 조회 버튼 클릭
 * -> 검색 조건 변경으로 간주하여 1페이지로 초기화
 */
function onSearchByRange() {
  if (selectedRange.value) {
    currentPage.value = 1 // 페이지 초기화
    fetchOrders(1, selectedRange.value[0], selectedRange.value[1])
  }
}

/**
 * 페이지네이션 페이지 변경 이벤트
 * -> 날짜 조건은 유지하고 페이지만 변경하여 요청
 */
function handlePageChange(newPage: number) {
  if (selectedRange.value) {
    fetchOrders(newPage, selectedRange.value[0], selectedRange.value[1])
  }
}

// ... 포맷팅 함수들 ...
function formatDate(dateStr: string | Date | undefined) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

function formatPrice(value: string | number) {
  if (!value) return '0'
  return value.toLocaleString()
}
</script>

<style scoped>
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

/* 페이지네이션 스타일 추가 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-bottom: 24px;
}
</style>
