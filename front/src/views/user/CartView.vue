<template>
  <div class="cart-container">
    <el-card shadow="never" class="cart-card">
      <template #header>
        <div class="card-header">
          <h2>장바구니</h2>
        </div>
      </template>

      <div v-if="state.cartResponse.cartItems.length > 0">
        <el-table
          ref="multipleTableRef"
          :data="state.cartResponse.cartItems"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column label="상품 정보">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                  src="https://via.placeholder.com/80"
                  fit="cover"
                  class="product-image"
                />
                <div class="product-details">
                  <p class="product-name">{{ row.productName }}</p>
                  <p class="product-options">{{ row.optionDescription }}</p>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="수량" width="150" align="center">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" size="small" />
            </template>
          </el-table-column>

          <el-table-column label="상품 금액" width="150" align="right">
            <template #default="{ row }">
              <span>{{ (row.price * row.quantity).toLocaleString() }}원</span>
            </template>
          </el-table-column>

          <el-table-column label="삭제" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" :icon="Delete" circle @click="removeItem($index)" />
            </template>
          </el-table-column>
        </el-table>

        <div class="cart-actions">
          <el-button @click="removeSelectedItems">선택 삭제</el-button>
        </div>

        <el-divider />

        <div class="cart-summary">
          <div class="summary-item">
            <span>총 선택된 상품 금액</span>
            <span class="price">{{ totalSelectedPrice.toLocaleString() }}원</span>
          </div>
          <div class="summary-item">
            <span>배송비</span>
            <span class="price">{{ shippingFee.toLocaleString() }}원</span>
          </div>
          <div class="summary-item total">
            <span>결제 예상 금액</span>
            <span class="total-price">{{ finalPaymentPrice.toLocaleString() }}원</span>
          </div>
        </div>

        <div class="order-button-wrapper">
          <el-button type="primary" size="large" @click="goToPayment">
            선택 상품 주문하기
          </el-button>
        </div>
      </div>

      <el-empty v-else description="장바구니에 담긴 상품이 없습니다." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElTable } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

import CartRepository from "@/repository/user/CartRepository.ts";
import { container } from "tsyringe";
import CartResponse, { CartItem } from "@/entity/cart/CartResponse.ts"; // CartItem 타입 import 필요

const CART_REPOSITORY = container.resolve(CartRepository)
const router = useRouter()

// 상태 관리: API 응답을 담을 객체
const state = reactive({
  cartResponse: new CartResponse()
})

CART_REPOSITORY.get()
  .then((response) => {
    console.log('Server Response:', response)
    // 받아온 데이터를 state에 할당
    state.cartResponse = response
  })
  .catch((error) => {
    console.error("장바구니 조회 실패", error);
    ElMessage.error("장바구니 정보를 불러오는데 실패했습니다.");
  });

// 테이블 참조용
const multipleTableRef = ref<InstanceType<typeof ElTable>>()
// 선택된 아이템들
const multipleSelection = ref<CartItem[]>([])

// 체크박스 선택 변경 시 호출
const handleSelectionChange = (val: CartItem[]) => {
  multipleSelection.value = val
}

// 개별 삭제 (인덱스 기반으로 state 배열에서 제거)
const removeItem = (index: number) => {
  // 실제로는 여기서 API 호출로 서버 삭제 요청을 먼저 해야 합니다.
  state.cartResponse.cartItems.splice(index, 1)
  ElMessage.success('상품이 삭제되었습니다.')
}

const removeSelectedItems = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('삭제할 상품을 선택해주세요.')
    return
  }

  // 선택되지 않은 항목들만 남기기 (filter)
  state.cartResponse.cartItems = state.cartResponse.cartItems.filter(
    item => !multipleSelection.value.includes(item)
  )

  // 선택 목록 초기화
  multipleSelection.value = []
  ElMessage.success('선택된 상품이 삭제되었습니다.')
}

// 총 상품 금액 계산 (선택된 항목 기준)
const totalSelectedPrice = computed(() =>
  multipleSelection.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
)

// 배송비 계산
const shippingFee = computed(() => {
  if (multipleSelection.value.length === 0) return 0
  return totalSelectedPrice.value >= 50000 ? 0 : 3000
})

const finalPaymentPrice = computed(() => totalSelectedPrice.value + shippingFee.value)

const goToPayment = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('주문할 상품을 선택해주세요.')
    return
  }

  router.push({
    path: '/payment',
    state: {
      selectedItems: JSON.parse(JSON.stringify(multipleSelection.value))
    }
  })
}
</script>

<style scoped>
.cart-container {
  padding: 2rem;
  background-color: #f4f6f9;
  min-height: calc(100vh - 60px);
}

.cart-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-image {
  width: 80px;
  height: 80px;
  margin-right: 1rem;
  border-radius: 4px;
}

.product-details {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-weight: 500;
}

.product-options {
  font-size: 0.85rem;
  color: #888;
  /* 옵션이 길어질 경우 말줄임표 처리 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 300px;
}

.cart-actions {
  margin-top: 1rem;
}

.cart-summary {
  margin-top: 2rem;
  padding: 1.5rem;
  background-color: #fafafa;
  border-radius: 4px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-item.total {
  font-size: 1.2rem;
  font-weight: bold;
}

.price {
  color: #555;
}

.total-price {
  color: #d32f2f;
}

.order-button-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 2rem;
}
</style>
