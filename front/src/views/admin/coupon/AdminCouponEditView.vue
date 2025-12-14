<template>
  <div class="coupon-edit-container">
    <el-card shadow="never" class="form-card">
      <template #header>
        <div class="card-header">
          <h2>쿠폰 수정</h2>
          <el-button link @click="cancel">뒤로가기</el-button>
        </div>
      </template>

      <el-skeleton :rows="10" animated v-if="initialLoading" />

      <el-form
        v-else
        label-position="top"
        label-width="120px"
        :model="state.couponWrite"
        class="coupon-form"
      >

        <div class="section-title">기본 정보</div>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="쿠폰명" required>
              <el-input
                v-model="state.couponWrite.name"
                placeholder="예) 연말 감사 15% 할인 쿠폰"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="쿠폰 설명">
              <el-input
                v-model="state.couponWrite.description"
                type="textarea"
                rows="3"
                placeholder="예) 5만원 이상 구매 시 최대 1만원 할인"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="총 발행 수량">
              <el-input-number
                v-model="state.couponWrite.totalQuantity"
                :min="1"
                :max="100000"
                controls-position="right"
                style="width: 100%"
              />
              <div class="form-tip">※ 이미 발급된 수량보다 적게 수정할 수 없습니다.</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider />

        <div class="section-title">할인 정책</div>
        <el-form-item label="할인 유형" required>
          <el-radio-group v-model="state.couponWrite.discountType">
            <el-radio-button label="PERCENTAGE">정률 할인 (%)</el-radio-button>
            <el-radio-button label="FIXED">정액 할인 (원)</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="state.couponWrite.discountType === 'PERCENTAGE' ? '할인율 (%)' : '할인 금액 (원)'" required>
              <el-input-number
                v-model="state.couponWrite.discountValue"
                :min="1"
                :max="state.couponWrite.discountType === 'PERCENTAGE' ? 100 : 10000000"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12" v-if="state.couponWrite.discountType === 'PERCENTAGE'">
            <el-form-item label="최대 할인 금액" required>
              <el-input-number
                v-model="state.couponWrite.maxDiscountAmount"
                :min="0"
                :step="1000"
                controls-position="right"
                style="width: 100%"
                placeholder="0원 입력 시 제한 없음"
              />
              <div class="form-tip">※ 0원 입력 시 한도 없음</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider />

        <div class="section-title">사용 조건</div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="최소 주문 금액" required>
              <el-input-number
                v-model="state.couponWrite.minOrderAmount"
                :min="0"
                :step="1000"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider />

        <div class="section-title">유효 기간</div>
        <el-form-item label="기간 설정 방식" required>
          <el-radio-group v-model="state.couponWrite.expirationType">
            <el-radio label="DATE_RANGE">날짜 지정 (기간)</el-radio>
            <el-radio label="VALID_DAYS_ON_ISSUE">발급 후 유효기간 (일)</el-radio>
          </el-radio-group>
        </el-form-item>

        <div v-if="state.couponWrite.expirationType === 'DATE_RANGE'">
          <el-form-item label="유효 기간 선택" required>
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              range-separator="~"
              start-placeholder="시작 일시"
              end-placeholder="종료 일시"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DDTHH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>
        </div>

        <div v-else>
          <el-form-item label="유효 일수" required>
            <el-input-number
              v-model="state.couponWrite.validDays"
              :min="1"
              controls-position="right"
            >
              <template #suffix>일</template>
            </el-input-number>
            <span class="ml-2">동안 사용 가능</span>
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button size="large" @click="cancel">취소</el-button>
          <el-button type="primary" size="large" @click="submitUpdate" :loading="loading">
            수정 완료
          </el-button>
        </div>

      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { container } from 'tsyringe'
import { ElMessage } from 'element-plus'
import { LocalDateTime } from '@js-joda/core'
import CouponRepository from '@/repository/user/CouponRepository.ts'
import CouponWrite from "@/entity/coupon/CouponWrite.ts";

const router = useRouter()
const route = useRoute()
const COUPON_REPOSITORY = container.resolve(CouponRepository)

const loading = ref(false)
const initialLoading = ref(true)
const couponId = Number(route.params.couponId)

const dateRange = ref<[string, string] | null>(null)

type StateType = {
  couponWrite: CouponWrite
}

const state = reactive<StateType>({
  couponWrite: new CouponWrite(),
})

onMounted(async () => {
  if (!couponId) {
    ElMessage.error('잘못된 접근입니다.')
    router.back()
    return
  }

  try {
    const data = await COUPON_REPOSITORY.get(couponId)
    state.couponWrite.name = data.name
    state.couponWrite.description = data.description
    state.couponWrite.discountType = data.discountType
    state.couponWrite.discountValue = data.discountValue
    state.couponWrite.expirationType = data.expirationType
    state.couponWrite.minOrderAmount = data.minOrderAmount
    state.couponWrite.maxDiscountAmount = data.maxDiscountAmount
    state.couponWrite.totalQuantity = data.totalQuantity
    state.couponWrite.validDays = data.validDays

    // 날짜 타입 처리 (DATE_RANGE인 경우 DatePicker에 바인딩)
    if (data.expirationType === 'DATE_RANGE' && data.startDate && data.endDate) {
      // 서버에서 "2024-12-25T10:00:00" 형태로 온다고 가정
      dateRange.value = [data.startDate.toString(), data.endDate.toString()]
    }

  } catch (e) {
    console.error(e)
    ElMessage.error('쿠폰 정보를 불러오는데 실패했습니다.')
    router.back()
  } finally {
    initialLoading.value = false
  }
})

// 2. 수정 요청 전송
const submitUpdate = async () => {
  if (!state.couponWrite.name) {
    ElMessage.warning('쿠폰명을 입력해주세요.')
    return
  }

  // 날짜 데이터 가공
  if (state.couponWrite.expirationType === 'DATE_RANGE') {
    if (!dateRange.value) {
      ElMessage.warning('유효 기간을 선택해주세요.')
      return
    }
    try {
      state.couponWrite.startDate = LocalDateTime.parse(dateRange.value[0])
      state.couponWrite.endDate = LocalDateTime.parse(dateRange.value[1])
      state.couponWrite.validDays = null
    } catch (e) {
      ElMessage.error('날짜 형식이 올바르지 않습니다.')
      return
    }
  } else {
    state.couponWrite.startDate = null
    state.couponWrite.endDate = null
  }

  if (state.couponWrite.discountType === 'FIXED') {
    state.couponWrite.maxDiscountAmount = state.couponWrite.discountValue
  }

  loading.value = true
  try {
    await COUPON_REPOSITORY.update(couponId, state.couponWrite)

    ElMessage.success('쿠폰이 성공적으로 수정되었습니다.')
    router.push('/admin/coupons/list') // 목록으로 이동
  } catch (e) {
    console.error(e)
    ElMessage.error('쿠폰 수정에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const cancel = () => {
  router.back()
}
</script>

<style scoped>
.coupon-edit-container {
  max-width: 800px;
  margin: 40px auto;
}

.form-card {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 20px;
  color: #303133;
  border-left: 4px solid #67c23a; /* 수정 화면이라 초록색으로 포인트 변경 */
  padding-left: 10px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.form-actions {
  margin-top: 40px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.ml-2 {
  margin-left: 8px;
}
</style>
