<template>
  <div class="product-detail-wrapper">
    <div class="product-detail">
      <div class="image-area">
        <img class="main-image" :src="imageUrl" alt="상품 이미지"/>
        <ul class="thumb-list">
          <li v-for="(img, idx) in state.product.productImages" :key="idx">
            <img :src="img" class="thumb-image" alt="썸네일 이미지"/>
          </li>
        </ul>
      </div>

      <div class="info-area">
        <h1 class="product-title">{{ state.product.name }}</h1>
        <p class="product-price">{{ state.product.price?.toLocaleString() ?? 0 }}원</p>

        <dl class="product-meta">
          <div>
            <dt>배송</dt>
            <dd>국내배송 / 2,500원 (5만원 이상 무료)</dd>
          </div>
        </dl>

        <div class="option-area" v-if="state.product.options">

          <div
            v-for="(step, index) in optionSteps"
            :key="index"
            class="flavor-select"
          >
            <label>{{ step.label }}</label>

            <select
              :value="selectedValues[index]"
              @change="onOptionSelect($event, index)"
            >
              <option disabled value="">선택해주세요</option>

              <option
                v-for="choice in step.choices"
                :key="choice"
                :value="choice"
              >
                {{ choice }}
              </option>
            </select>
          </div>

          <div v-if="finalStock !== null" class="stock-message">
            <span v-if="finalStock > 0"> </span>
            <span v-else class="sold-out">품절되었습니다.</span>
          </div>

        </div>
        <div class="button-group">
          <button class="btn-primary" @click="buyNow">구매하기</button>
          <button class="btn-secondary" @click="addToCart">장바구니</button>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="detail-tab">
      <el-tab-pane label="상세정보" name="detail">
        <div class="tab-content" v-html="state.product.description"></div>
      </el-tab-pane>
      <el-tab-pane label="배송정보" name="shipping">
        <div class="tab-content">
          <p>등록된 문의가 없습니다.</p>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts" setup>
import {ref, reactive, defineProps, computed} from 'vue'
import {container} from 'tsyringe'
import { useRouter } from 'vue-router'
import ProductRepository from '@/repository/user/ProductRepository.ts'
import CategoryRepository from '@/repository/admin/AdminCategoryRepository.ts'
import OrderRepository from '@/repository/user/OrderRepository.ts'
import CartRepository from "@/repository/user/CartRepository.ts";
import Product from '@/entity/product/Product'
import Category from '@/entity/product/Category'
import {ElMessage, ElMessageBox} from "element-plus";
import AddCart from "@/entity/cart/AddCart.ts";
const router = useRouter()

// 의존성 주입
const PRODUCT_REPOSITORY = container.resolve(ProductRepository)
const CATEGORY_REPOSITORY = container.resolve(CategoryRepository)
const ORDER_REPOSITORY = container.resolve(OrderRepository)
const CART_REPOSITORY = container.resolve(CartRepository)

const activeTab = ref('detail')
const imageUrl = ref('/g1.JPG')

const props = defineProps<{ productId: number }>()

const state = reactive({
  product: new Product(),
  category: new Category(),
  addCart: new AddCart()
})

const selectedValues = ref<string[]>([])

const optionSteps = computed(() => {
  const steps: { label: string; choices: string[] }[] = []
  if (!state.product.options || Object.keys(state.product.options).length === 0) {
    return []
  }

  let currentPointer: any = state.product.options
  for (let i = 0; i < 10; i++) { // 무한 루프 방지 todo: 옵션 최대개수 정해지면 수정 필요
    const keys = Object.keys(currentPointer)
    if (keys.length === 0) break

    const labelName = keys[0]
    const choicesMap = currentPointer[labelName]
    if (!choicesMap || typeof choicesMap !== 'object') break

    const choiceList = Object.keys(choicesMap)
    steps.push({ label: labelName, choices: choiceList })

    if (selectedValues.value.length > i) {
      const selectedChoice = selectedValues.value[i]
      const nextNode = choicesMap[selectedChoice]

      // id,stock 을 직접 가지고 있다면 중단
      if (typeof nextNode === 'object' && nextNode !== null && !('id' in nextNode && 'stock' in nextNode)) {
        currentPointer = nextNode
      } else {
        break // 최종 노드(id, stock 객체) 또는 더 이상 진행할 수 없으면 중단
      }
    } else {
      break
    }
  }
  return steps
})

const finalSelectedOption = computed(() => {
  if (!state.product.options || selectedValues.value.length === 0) return null

  let ptr: any = state.product.options
  for (const val of selectedValues.value) {
    const keys = Object.keys(ptr)
    if (keys.length === 0) return null
    const label = keys[0]
    const choicesMap = ptr[label]
    if (!choicesMap) return null
    ptr = choicesMap[val]
    if (ptr === undefined) return null
  }

  // 최종 ptr이 id와 stock을 가진 마지막 객체인지 확인
  if (typeof ptr === 'object' && ptr !== null && 'id' in ptr && 'stock' in ptr) {
    console.log('id,stock : ' + ptr.id + ' ' + ptr.stock)
    return ptr as { id: number; stock: number }
  }

  return null
})

const finalStock = computed(() => finalSelectedOption.value?.stock ?? null)
const finalOptionId = computed(() => finalSelectedOption.value?.id ?? null)

function onOptionSelect(event: Event, index: number) {
  const target = event.target as HTMLSelectElement
  const value = target.value

  // 상위 옵션 변경 시 하위 옵션 초기화 (slice)
  const newSelection = selectedValues.value.slice(0, index)
  newSelection.push(value)
  selectedValues.value = newSelection
}

PRODUCT_REPOSITORY.get(props.productId)
  .then((product) => {
    console.log('Server Response:', product)
    state.product = product

    if (product.productImages && product.productImages.length > 0) {
      imageUrl.value = product.productImages[0]
    }
  })
  .catch((err) => {
    console.error(err)
  })

const addToCart = () => {
  const request = new AddCart()
  state.addCart.optionId = finalOptionId.value
  state.addCart.productId = props.productId
  state.addCart.quantity = 1

  CART_REPOSITORY.write(state.addCart)


  ElMessageBox.confirm('장바구니에 상품을 담았습니다. 장바구니로 이동하시겠습니까?', '알림', {
    confirmButtonText: '이동',
    cancelButtonText: '계속 쇼핑하기',
    type: 'success'
  })
    .then(() => {
      router.push('/cart')
    })
    .catch(() => {

    })
}

const buyNow = () => {
  if (optionSteps.value.length > 0 && !finalOptionId.value) {
    ElMessage.warning('모든 옵션을 선택해주세요.')
    return
  }

  // 2. 품절 체크
  if (finalStock.value !== null && finalStock.value <= 0) {
    ElMessage.error('선택하신 상품은 품절되었습니다.')
    return
  }

  // 옵션 설명 문자열 생성 (예: "색상: 블랙 / 사이즈: L")
  const optionDesc = optionSteps.value.map((step, index) => {
    return `${step.label}: ${selectedValues.value[index]}`
  }).join(' / ')

  const directBuyItem = {
    productId: state.product.id,
    productName: state.product.name,
    price: state.product.price,
    quantity: 1,
    optionId: finalOptionId.value,
    optionDescription: optionDesc,
    image: imageUrl.value
  }

  router.push({
    path: '/payment',
    state: {
      selectedItems: [directBuyItem]
    }
  })
}



</script>

<style scoped>
.product-detail-wrapper {
  max-width: 1080px;
  margin: 0 auto;
  padding: 40px 16px;
  font-family: 'Pretendard', sans-serif;
}

.product-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
}

.image-area {
  flex: 1;
  min-width: 300px;
}

.main-image {
  width: 100%;
  border-radius: 8px;
  border: 1px solid #eee;
  margin-bottom: 10px;
}

.thumb-list {
  display: flex;
  gap: 8px;
  list-style: none;
  padding: 0;
}

.thumb-image {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #ddd;
  cursor: pointer;
}

.info-area {
  flex: 1;
  min-width: 300px;
}

.product-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 12px;
}

.product-price {
  font-size: 20px;
  font-weight: 700;
  color: #111;
  margin-bottom: 16px;
}

.product-meta {
  font-size: 14px;
  color: #666;
  margin-bottom: 24px;
}

.product-meta dt {
  font-weight: 500;
  margin-right: 8px;
}

.product-meta div {
  display: flex;
  margin-bottom: 6px;
}

/* 옵션 영역 스타일 */
.option-area {
  padding: 20px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  margin-bottom: 24px;
}

.flavor-select {
  margin-bottom: 16px;
}

.flavor-select label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

select {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  outline: none;
  background-color: white;
}

.stock-message {
  margin-top: 10px;
  font-size: 14px;
  font-weight: bold;
  color: #2c3e50;
}

.sold-out {
  color: #e74c3c;
}

.button-group {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.btn-primary,
.btn-secondary {
  flex: 1;
  padding: 14px;
  font-size: 15px;
  font-weight: 600;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-primary {
  background-color: #111;
  color: #fff;
}

.btn-primary:hover {
  background-color: #333;
}

.btn-secondary {
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.detail-tab {
  margin-top: 60px;
}

.tab-content {
  padding: 24px;
}
</style>
