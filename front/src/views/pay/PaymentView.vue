<template>
  <div class="payment-container">
    <el-card shadow="never" class="payment-card">
      <template #header>
        <div class="card-header">
          <h2>주문 / 결제</h2>
        </div>
      </template>

      <div class="content-wrapper">
        <div class="left-section">
          <section class="order-items-section">
            <h3>주문 상품</h3>
            <el-table :data="cartItems" style="width: 100%">
              <el-table-column label="상품 정보">
                <template #default="{ row }">
                  <div class="product-info">
                    <el-image src="https://via.placeholder.com/60" fit="cover" class="product-image" />
                    <div class="product-details">
                      <p class="product-name">{{ row.productName }}</p>
                      <p class="product-options">{{ row.optionDescription }}</p>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="수량" width="80" align="center" />
              <el-table-column label="상품 금액" width="120" align="right">
                <template #default="{ row }">
                  <span>{{ (row.price * row.quantity).toLocaleString() }}원</span>
                </template>
              </el-table-column>
            </el-table>
          </section>

          <el-divider />

          <section class="shipping-info-section">
            <h3>배송지 정보</h3>
            <el-form label-position="top">
              <el-form-item label="받는 사람">
                <el-input v-model="shippingInfo.recipient" />
              </el-form-item>
              <el-form-item label="연락처">
                <el-input v-model="shippingInfo.phone" />
              </el-form-item>
              <el-form-item label="주소">
                <el-input v-model="shippingInfo.address" type="textarea" :rows="2" />
              </el-form-item>
            </el-form>
          </section>
        </div>

        <div class="right-section">
          <el-card shadow="never" class="summary-card">
            <template #header>
              <h4>결제 금액</h4>
            </template>
            <div class="summary-row">
              <span>총 상품 금액</span>
              <span>{{ totalProductPrice.toLocaleString() }}원</span>
            </div>
            <div class="summary-row">
              <span>배송비</span>
              <span>{{ shippingFee.toLocaleString() }}원</span>
            </div>
            <div class="summary-row">
              <span>할인 금액</span>
              <span class="discount">-{{ totalDiscount.toLocaleString() }}원</span>
            </div>
            <el-divider />
            <div class="summary-row total">
              <span>최종 결제 금액</span>
              <span class="total-price">{{ finalPaymentPrice.toLocaleString() }}원</span>
            </div>
            <el-button type="primary" class="payment-button" @click="onPayment">
              {{ finalPaymentPrice.toLocaleString() }}원 결제하기
            </el-button>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { CartItem } from "@/entity/cart/CartResponse.ts"; // 타입 import

const router = useRouter()

// 넘어온 데이터를 담을 변수
const cartItems = ref<CartItem[]>([])

const shippingInfo = ref({
  recipient: '홍길동',
  phone: '010-1234-5678',
  address: '서울시 강남구 테헤란로'
})

const totalDiscount = ref(0) // 지금은 0원 처리

// ★★★ [핵심] 페이지 로드 시 데이터 수신 ★★★
onMounted(() => {
  // history.state에서 장바구니에서 넘긴 데이터를 꺼냅니다.
  const receivedData = history.state.selectedItems

  // 데이터가 없으면(URL 직접 입력해서 들어온 경우) 장바구니로 돌려보냄
  if (!receivedData || receivedData.length === 0) {
    ElMessage.error('주문할 상품 정보가 없습니다.')
    router.replace('/cart') // 뒤로가기 방지를 위해 replace 사용
    return
  }

  cartItems.value = receivedData
})

// 계산 로직 (받아온 cartItems 기반으로 자동 계산)
const totalProductPrice = computed(() =>
  cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
)

const shippingFee = computed(() => {
  if (totalProductPrice.value === 0) return 0
  return totalProductPrice.value >= 50000 ? 0 : 3000
})

const finalPaymentPrice = computed(
  () => totalProductPrice.value + shippingFee.value - totalDiscount.value
)

const onPayment = () => {
  if (cartItems.value.length === 0) return

  // 결제 요청 데이터 준비
  const paymentData = {
    items: cartItems.value.map(item => ({
      id: item.cartItemId, // 혹은 productId
      quantity: item.quantity
    })),
    amount: finalPaymentPrice.value,
    shipping: shippingInfo.value
  }

  console.log("결제 요청 데이터:", paymentData)
  ElMessage.success('결제 API 연동 예정입니다.')
}
</script>

<style scoped>
/* 스타일은 기존 PaymentView 스타일 그대로 유지 */
.payment-container {
  padding: 2rem;
  background-color: #f4f6f9;
  min-height: calc(100vh - 60px);
}
.payment-card {
  max-width: 1200px;
  margin: 0 auto;
}
.card-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}
.content-wrapper {
  display: flex;
  gap: 2rem;
}
.left-section {
  flex: 3;
}
.right-section {
  flex: 1;
}
h3 {
  font-size: 1.2rem;
  font-weight: 500;
  margin-bottom: 1rem;
  border-left: 3px solid #4a6fa5;
  padding-left: 0.5rem;
}
.product-info {
  display: flex;
  align-items: center;
}
.product-image {
  width: 60px;
  height: 60px;
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
}
.summary-card {
  position: sticky;
  top: 2rem;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  font-size: 0.95rem;
}
.summary-row.total {
  font-size: 1.1rem;
  font-weight: bold;
}
.total-price {
  color: #d32f2f;
}
.discount {
  color: #4a6fa5;
}
.payment-button {
  width: 100%;
  margin-top: 1rem;
  padding: 1.2rem;
  font-size: 1rem;
}
@media (max-width: 992px) {
  .content-wrapper {
    flex-direction: column;
  }
  .right-section {
    position: static;
  }
}
</style>
