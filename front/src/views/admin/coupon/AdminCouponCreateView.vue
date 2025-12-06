<template>
  <div class="coupon-create-container">
    <el-card shadow="never" class="form-card">
      <template #header>
        <div class="card-header">
          <h2>쿠폰 등록</h2>
        </div>
      </template>

      <el-form label-position="top" label-width="120px" :model="form" class="coupon-form">

        <div class="section-title">기본 정보</div>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="쿠폰명" required>
              <el-input
                v-model="form.name"
                placeholder="예) 연말 감사 15% 할인 쿠폰"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="쿠폰 설명">
              <el-input
                v-model="form.description"
                type="textarea"
                rows="3"
                placeholder="예) 5만원 이상 구매 시 최대 1만원 할인"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="총 발행 수량" required>
              <el-input-number
                v-model="form.totalQuantity"
                :min="1"
                :max="100000"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider />

        <div class="section-title">할인 정책</div>
        <el-form-item label="할인 유형" required>
          <el-radio-group v-model="form.discountType">
            <el-radio-button label="PERCENTAGE">정률 할인 (%)</el-radio-button>
            <el-radio-button label="FIXED_AMOUNT">정액 할인 (원)</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="form.discountType === 'PERCENTAGE' ? '할인율 (%)' : '할인 금액 (원)'" required>
              <el-input-number
                v-model="form.discountValue"
                :min="1"
                :max="form.discountType === 'PERCENTAGE' ? 100 : 10000000"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12" v-if="form.discountType === 'PERCENTAGE'">
            <el-form-item label="최대 할인 금액" required>
              <el-input-number
                v-model="form.maxDiscountAmount"
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
                v-model="form.minOrderAmount"
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
          <el-radio-group v-model="form.expirationType">
            <el-radio label="DATE_RANGE">날짜 지정 (기간)</el-radio>
            <el-radio label="FIXED_PERIOD">발급 후 유효기간 (일)</el-radio>
          </el-radio-group>
        </el-form-item>

        <div v-if="form.expirationType === 'DATE_RANGE'">
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
              v-model="form.validDays"
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
          <el-button type="primary" size="large" @click="submitCoupon" :loading="loading">
            쿠폰 등록
          </el-button>
        </div>

      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { container } from 'tsyringe'
import { ElMessage } from 'element-plus'

// CouponRepository 가정 (실제 파일 경로에 맞게 수정)
import CouponRepository from '@/repository/CouponRepository.ts'

const router = useRouter()
const COUPON_REPOSITORY = container.resolve(CouponRepository)
const loading = ref(false)

// 날짜 범위 선택용 (Element Plus DatePicker는 배열로 다룸)
const dateRange = ref<[string, string] | null>(null)

// 폼 데이터 초기값 (JSON 구조에 맞춤)
const form = reactive({
  name: '',
  description: '',
  discountType: 'PERCENTAGE', // 'PERCENTAGE' | 'FIXED_AMOUNT'
  discountValue: 0,
  expirationType: 'DATE_RANGE', // 'DATE_RANGE' | 'FIXED_PERIOD'
  validDays: null as number | null,
  startDate: null as string | null,
  endDate: null as string | null,
  minOrderAmount: 0,
  maxDiscountAmount: 0,
  totalQuantity: 100
})

// 유효성 검사 및 데이터 전송
const submitCoupon = async () => {
  // 1. 간단한 유효성 검사
  if (!form.name) {
    ElMessage.warning('쿠폰명을 입력해주세요.')
    return
  }
  if (form.discountValue <= 0) {
    ElMessage.warning('할인 금액(율)은 0보다 커야 합니다.')
    return
  }

  // 2. 만료 타입에 따른 데이터 정리
  if (form.expirationType === 'DATE_RANGE') {
    if (!dateRange.value) {
      ElMessage.warning('유효 기간을 선택해주세요.')
      return
    }
    form.startDate = dateRange.value[0]
    form.endDate = dateRange.value[1]
    form.validDays = null // 날짜 지정이므로 일수는 null
  } else {
    // FIXED_PERIOD
    if (!form.validDays || form.validDays < 1) {
      ElMessage.warning('유효 일수를 입력해주세요.')
      return
    }
    form.startDate = null
    form.endDate = null
  }

  // 3. 정액 할인일 경우 최대 할인 금액 처리 (보통 정액 할인은 그 금액 자체가 최대치이므로 같게 하거나 0)
  if (form.discountType === 'FIXED_AMOUNT') {
    form.maxDiscountAmount = form.discountValue
  }

  // 4. API 전송
  loading.value = true
  try {
    // JSON 페이로드 확인용 로그
    console.log('Sending Payload:', form)

    await COUPON_REPOSITORY.create(form)

    ElMessage.success('쿠폰이 성공적으로 생성되었습니다.')
    router.push('/admin/coupons') // 목록 페이지로 이동
  } catch (e) {
    console.error(e)
    ElMessage.error('쿠폰 생성에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const cancel = () => {
  router.back()
}
</script>

<style scoped>
.coupon-create-container {
  max-width: 800px;
  margin: 40px auto;
}

.form-card {
  padding: 20px;
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
  border-left: 4px solid #409eff;
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
