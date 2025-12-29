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

      <el-table-column label="관리" width="180" align="center">
        <template #default="{ row }">
          <div class="action-buttons">

            <el-button
              v-if="row.status === 'DELIVERY'"
              size="small"
              type="success"
              @click.stop="completeDelivery(row.orderId)"
            >
              배송 완료
            </el-button>

            <el-button
              v-if="row.status === 'DELIVERY'"
              size="small"
              type="danger"
              @click.stop="cancelDelivery(row.orderId)"
            >
              배송 취소
            </el-button>

          </div>
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
    state.orderPage = await ORDER_REPOSITORY.getOrders(page, 'COMPLETED')

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

const completeDelivery = async (orderId: number) => {
  if (!confirm('해당 주문의 배송을 완료하시겠습니까?')) return
  try {
    loading.value = true
    await ORDER_REPOSITORY.completeDelivery(orderId)

    ElMessage.success('배송이 시작되었습니다.')
    await getList(state.orderPage.page)

  } catch (e: any) {
    console.error(e)
    ElMessage.error('배송 상태 변경 중 오류가 발생했습니다.')
  } finally {
    loading.value = false // 로딩바 해제
  }
}

const formatStatus = (status: string) => {
  switch (status) {
    case 'PENDING': return '대기'
    case 'DELIVERY': return '배송중'
    case 'COMPLETED': return '완료'
    case 'CANCELED': return '취소됨'
    default: return status
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'PENDING': return 'success'    // 초록색
    case 'DELIVERY': return 'warning' // 주황색
    case 'COMPLETED': return 'danger'   // 빨간색
    case 'CANCELED': return 'info'    // 회색
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

.action-buttons {
  display: flex;            /* 가로 배치 */
  justify-content: center;  /* 중앙 정렬 */
  align-items: center;      /* 수직 중앙 정렬 */
  gap: 8px;                 /* 버튼 사이 간격 8px */
  width: 100%;              /* 부모 영역 꽉 채우기 */
}

.action-buttons .el-button + .el-button {
  margin-left: 0 !important;
}
</style>
