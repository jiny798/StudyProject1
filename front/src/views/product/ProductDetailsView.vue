<template>
  <div class="product-detail-wrapper">
    <div class="product-detail">
      <div class="image-area">
        <img class="main-image" :src="imageUrl" alt="상품 이미지" />
        <ul class="thumb-list">
          <li v-for="(img, idx) in state.product.productImages" :key="idx">
            <img :src="img" class="thumb-image" alt="썸네일 이미지" />
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
          <button class="btn-primary">구매하기</button>
          <button class="btn-secondary" @click="addCart">장바구니</button>
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
import { ref, reactive, defineProps, computed } from 'vue'
import { container } from 'tsyringe'
import ProductRepository from '@/repository/ProductRepository'
import CategoryRepository from '@/repository/CategoryRepository'
import OrderRepository from '@/repository/OrderRepository'
import Product from '@/entity/product/Product'
import Category from '@/entity/product/Category'
import RequestProduct from '@/entity/order/RequestProduct'

// 의존성 주입
const PRODUCT_REPOSITORY = container.resolve(ProductRepository)
const CATEGORY_REPOSITORY = container.resolve(CategoryRepository)
const ORDER_REPOSITORY = container.resolve(OrderRepository)

const activeTab = ref('detail')
const imageUrl = ref('/g1.JPG') // 기본 이미지 (필요시 productImages[0]으로 교체 가능)

const props = defineProps<{ productId: number }>()

const state = reactive({
  product: new Product(),
  category: new Category(),
  requestProduct: new RequestProduct(),
})

// === [핵심 로직] 중첩 객체(Nested Object) 파싱 ===

// 1. 사용자가 선택한 값들을 저장 (예: ["블랙", "라지"])
const selectedValues = ref<string[]>([])

// 2. 현재 데이터 구조에 맞춰 라벨과 선택지를 계산
const optionSteps = computed(() => {
  const steps: { label: string; choices: string[] }[] = []

  // options가 없거나 비어있으면 종료
  if (!state.product.options || Object.keys(state.product.options).length === 0) {
    return []
  }

  // 탐색 시작점 (Root)
  let currentPointer: any = state.product.options

  // 무한 루프 방지용 제한 (옵션 깊이가 10단계를 넘진 않을 거라 가정)
  for (let i = 0; i < 10; i++) {
    // 1. 현재 Pointer의 키들을 가져옴 (이것이 옵션 이름. 예: "색상")
    const keys = Object.keys(currentPointer)
    if (keys.length === 0) break

    const labelName = keys[0] // "색상"
    const choicesMap = currentPointer[labelName] // {"블랙": {...}, "화이트": {...}}

    // 만약 choicesMap이 없거나 객체가 아니면 종료 (구조 이상)
    if (!choicesMap || typeof choicesMap !== 'object') break

    // 2. 선택지 목록 추출 (예: ["블랙", "화이트"])
    const choiceList = Object.keys(choicesMap)

    // 단계 추가
    steps.push({ label: labelName, choices: choiceList })

    // 3. 사용자가 현재 단계(i)에서 값을 선택했는지 확인
    if (selectedValues.value.length > i) {
      const selectedChoice = selectedValues.value[i]

      // 선택한 값으로 다음 깊이로 이동
      // 예: "블랙"을 선택했으면 -> {"사이즈": {"라지": 10}} 로 포인터 이동
      const nextNode = choicesMap[selectedChoice]

      if (typeof nextNode === 'number') {
        // 숫자(재고)가 나오면 끝! 더 이상 steps를 만들지 않음
        break
      } else if (typeof nextNode === 'object') {
        // 객체가 나오면 다음 루프에서 라벨(예: "사이즈")을 찾음
        currentPointer = nextNode
      } else {
        break
      }
    } else {
      // 아직 선택하지 않았다면 하위 옵션을 보여줄 수 없으므로 루프 종료
      break
    }
  }

  return steps
})

// 3. 최종 재고 확인 (선택이 끝났을 때 숫자가 나오는지 체크)
const finalStock = computed(() => {
  if (optionSteps.value.length === 0) return null

  // 현재 보이는 단계 수와 사용자 선택 수가 같은지 1차 확인
  if (selectedValues.value.length < optionSteps.value.length) return null

  // 직접 데이터 트리를 타고 내려가서 숫자가 나오는지 확인
  let ptr: any = state.product.options
  for (const val of selectedValues.value) {
    // 1. 옵션명(Label) 껍질 벗기기 (예: "색상")
    const keys = Object.keys(ptr)
    if (keys.length === 0) return null
    const label = keys[0]

    // 2. 선택지 맵으로 진입
    const choicesMap = ptr[label]
    if (!choicesMap) return null

    // 3. 선택한 값(val)으로 다음 노드 진입
    ptr = choicesMap[val]
    if (ptr === undefined) return null
  }

  return typeof ptr === 'number' ? ptr : null
})

// === [이벤트 핸들러] ===

function onOptionSelect(event: Event, index: number) {
  const target = event.target as HTMLSelectElement
  const value = target.value

  // 상위 옵션 변경 시 하위 옵션 초기화 (slice)
  const newSelection = selectedValues.value.slice(0, index)
  newSelection.push(value)
  selectedValues.value = newSelection
}

// 데이터 로드
PRODUCT_REPOSITORY.get(props.productId)
  .then((product) => {
    console.log('Server Response:', product)
    state.product = product

    // 이미지가 있다면 첫 번째 이미지를 대표 이미지로 설정
    if (product.productImages && product.productImages.length > 0) {
      imageUrl.value = product.productImages[0]
    }
  })
  .catch((err) => {
    console.error(err)
  })

function addCart() {
  if (finalStock.value === null) {
    alert('모든 옵션을 선택해주세요.')
    return
  }

  if (finalStock.value <= 0) {
    alert('품절된 상품입니다.')
    return
  }

  state.requestProduct.productId = state.product.id
  state.requestProduct.count = 1
  // DB 저장을 위해 "블랙 / 라지" 형태로 변환
  state.requestProduct.option = selectedValues.value.join(' / ')

  console.log('Cart Request:', state.requestProduct)

  ORDER_REPOSITORY.addCart(state.requestProduct)
    .then(() => alert('장바구니에 담겼습니다.'))
    .catch((e) => {
      console.error(e)
      alert('장바구니 담기 실패')
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
