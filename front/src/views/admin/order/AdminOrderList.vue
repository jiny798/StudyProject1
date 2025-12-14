<template>
  <div class="admin-order-list">
    <div class="header">
      <h1 class="page-title">주문 목록</h1>
    </div>

    <el-table
      :data="state.orderPage.items"
      v-loading="loading"
      style="width: 100%; margin-top: 20px;"
      border
    >
      <el-table-column prop="orderId" label="주문번호" width="100" align="center" />

      <el-table-column label="주문 상품 / 옵션" min-width="250">
        <template #default="{ row }">
          <div v-for="(item, index) in row.orderProducts" :key="index" class="product-item">
            <span class="product-name">
               {{ item.productName }}
            </span>

            <el-tag v-if="item.optionName" size="small" type="info" effect="plain" class="option-tag">
              {{ item.optionName }}
            </el-tag>

            <span class="quantity">x {{ item.quantity }}개</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="주문자" width="150" align="center">
        <template #default="{ row }">
          <div class="user-name">{{ row.userName }}</div>
          <div class="user-email">{{ row.userEmail }}</div>
        </template>
      </el-table-column>

      <el-table-column label="결제금액" width="120" align="right">
        <template #default="{ row }">
          <span class="price-text">{{ formatPrice(row.totalPrice) }}원</span>
        </template>
      </el-table-column>

      <el-table-column label="상태" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ formatStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="주문일시" width="160" align="center">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>

      <el-table-column label="관리" width="90" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="openOrderDetail(row.orderId)">
            상세
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-area">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="state.orderPage.totalCount"
        :page-size="state.orderPage.size"
        :current-page="state.orderPage.page"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted, ref } from 'vue'
import { container } from 'tsyringe'
import { ElMessage } from 'element-plus'

import AdminOrderRepository from '@/repository/admin/AdminOrderRepository'
import AdminOrderSummary from '@/entity/order/admin/AdminOrderSummary.ts'
import Paging from "@/entity/data/Paging"

interface State {
  orderPage: Paging<AdminOrderSummary>
}

const state = reactive<State>({
  orderPage: new Paging<AdminOrderSummary>(),
})
const loading = ref(false)
const ORDER_REPOSITORY = container.resolve(AdminOrderRepository)

const getList = async (page = 1) => {
  loading.value = true
  try {
    // Repository의 getOrders 호출 (페이지 번호 전달)
    const responsePage = await ORDER_REPOSITORY.getOrders(page)
    state.orderPage = responsePage
  } catch (e: any) {
    console.error(e)
    ElMessage.error('주문 목록을 불러오지 못했습니다.')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getList()
})

const handlePageChange = (page: number) => {
  getList(page)
}

const openOrderDetail = (orderId: number) => {
  // todo 상세 페이지 이동 구현 필요
  console.log('상세 페이지 이동 구현 필요:', orderId)
  // router.push(`/admin/orders/${orderId}`)
}

const formatPrice = (price: number) => {
  return price?.toLocaleString() ?? 0
}

const formatDate = (dateValue: any) => {
  if (!dateValue) return '-'

  // Java LocalDateTime이 String("2025-12-14T17:00:00")으로 넘어온다고 가정
  // 만약 배열 [2025, 12, 14...] 로 넘어온다면 처리가 달라져야 합니다.
  const date = new Date(dateValue)

  // 유효하지 않은 날짜 처리
  if (isNaN(date.getTime())) return dateValue

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')

  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const formatStatus = (status: string) => {
  switch (status) {
    case 'ORDERED': return '주문완료'
    case 'PAID': return '결제완료'
    case 'PREPARING': return '준비중'
    case 'SHIPPING': return '배송중'
    case 'DELIVERED': return '배송완료'
    case 'CANCELED': return '취소'
    default: return status
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'PAID': return 'success'    // 초록색
    case 'PREPARING': return 'warning' // 주황색
    case 'CANCELED': return 'danger'   // 빨간색
    case 'DELIVERED': return 'info'    // 회색
    default: return ''                 // 기본 파란색
  }
}
</script>

<style scoped>
.admin-order-list {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}
.header {
  margin-bottom: 20px;
}
.page-title {
  font-size: 1.5rem;
  margin: 0;
}
.pagination-area {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  font-size: 14px;
}
.product-item:last-child {
  margin-bottom: 0;
}
.product-name {
  font-weight: 500;
  color: #333;
}
.option-tag {
  color: #666;
  border-color: #eee;
}
.quantity {
  color: #888;
  font-size: 13px;
}
.user-name {
  font-weight: bold;
}
.user-email {
  font-size: 12px;
  color: #999;
}
.price-text {
  font-weight: 600;
  color: #f56c6c;
}
</style>
