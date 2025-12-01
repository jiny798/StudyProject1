<template>
  <div class="cart-container">
    <el-card shadow="never" class="cart-card">
      <template #header>
        <div class ="card-header">
          <h2>장바구니</h2>
        </div>
      </template>

      <div v-if="cartItems.length > 0">
        <el-table
          ref="multipleTableRef"
          :data="cartItems"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="상품 정보">
            <template #default="{ row }">
              <div class="product-info">
                <el-image :src="row.image" fit="cover" class="product-image" />
                <div class="product-details">
                  <p class="product-name">{{ row.name }}</p>
                  <p class="product-options">{{ row.options }}</p>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElTable } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const router = useRouter()

// Mock Data - 실제로는 API를 통해 받아와야 합니다.
const cartItems = ref([
  {
    id: 1,
    name: '쫀쫀한 그레이 니트',
    options: '색상: 그레이 / 사이즈: L',
    price: 120000,
    quantity: 1,
    image: '/img2.png'
  },
  {
    id: 2,
    name: '상남자 아크릴 니트',
    options: '색상: 블랙 / 사이즈: M',
    price: 300,
    quantity: 2,
    image: '/img3.png'
  },
  {
    id: 3,
    name: '미친커플 니트',
    options: '색상: 아이보리 / 사이즈: S',
    price: 60000,
    quantity: 1,
    image: '/img.png'
  }
])

const multipleTableRef = ref<InstanceType<typeof ElTable>>()
const multipleSelection = ref<typeof cartItems.value>([])

const handleSelectionChange = (val: typeof cartItems.value) => {
  multipleSelection.value = val
}

const removeItem = (index: number) => {
  cartItems.value.splice(index, 1)
  ElMessage.success('상품이 삭제되었습니다.')
}

const removeSelectedItems = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('삭제할 상품을 선택해주세요.')
    return
  }
  cartItems.value = cartItems.value.filter(item => !multipleSelection.value.includes(item))
  ElMessage.success('선택된 상품이 삭제되었습니다.')
}

const totalSelectedPrice = computed(() =>
  multipleSelection.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
)

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
  // TODO: 선택된 상품 정보를 결제 페이지로 넘기는 로직 구현
  router.push('/payment') // 임시로 결제 페이지로 이동
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
