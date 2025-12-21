<template>
  <div class="admin-user-list">
    <div class="header">
      <h1 class="page-title">회원 목록</h1>
    </div>

    <el-table :data="state.userPage.items" border style="width: 100%" v-loading="loading">
      <el-table-column label="No" width="80" align="center">
        <template #default="{ row }">
          {{ row.id }}
        </template>
      </el-table-column>

      <el-table-column prop="email" label="이메일" min-width="180" />
      <el-table-column prop="name" label="이름" width="120" align="center" />

      <el-table-column label="관리" width="220" align="center">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="openCouponModal(row)">
            쿠폰 지급
          </el-button>

          <el-button size="small" @click="moveToDetail(row.id)">상세</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="couponDialogVisible"
      title="쿠폰 지급"
      width="400px"
    >
      <p v-if="targetUser" class="dialog-desc">
        <strong>{{ targetUser.name }}</strong> ({{ targetUser.email }}) 님에게<br/>
        지급할 쿠폰을 선택해주세요.
      </p>

      <el-form label-position="top">
        <el-form-item label="지급할 쿠폰 선택">
          <el-select
            v-model="selectedCouponId"
            placeholder="쿠폰을 선택하세요"
            style="width: 100%"
          >
            <el-option
              v-for="coupon in couponList"
              :key="coupon.id"
              :label="coupon.name"
              :value="coupon.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="couponDialogVisible = false">취소</el-button>
          <el-button type="primary" @click="giveCoupon" :loading="sending">
            지급하기
          </el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {container} from 'tsyringe'
import {ElMessage} from 'element-plus'

import User from '@/entity/user/UserProfile.ts'
import CouponResponse from '@/entity/coupon/Coupon.ts'
import UserRepository from '@/repository/user/UserRepository.ts'
import CouponRepository from '@/repository/user/CouponRepository.ts' // Coupon 리포지토리 필요
import Paging from "@/entity/data/Paging.ts";
import IssueCouponRequest from "@/entity/coupon/IssueCouponRequest.ts";

// --- 기존 로직 ---
const state = reactive({
  userPage: new Paging<User>(),
  issueCouponRequest: new IssueCouponRequest()
})
const loading = ref(false)
const USER_REPOSITORY = container.resolve(UserRepository)
const COUPON_REPOSITORY = container.resolve(CouponRepository)

// 쿠폰 모달 관련 상태 ---
const couponDialogVisible = ref(false)
const sending = ref(false)
const targetUser = ref<User | null>(null) // 쿠폰 받을 유저
const selectedCouponId = ref<number | null>(null) // 선택된 쿠폰 ID
const couponList = ref<CouponResponse[]>([]) // 선택 가능한 쿠폰 목록

// 회원 목록 조회
const getList = async (page = 1) => {
  loading.value = true
  try {
    state.userPage = await USER_REPOSITORY.getList(page)
  } catch (e) {
    ElMessage.error('회원 목록 로드 실패')
  } finally {
    loading.value = false
  }
}

const loadCoupons = async () => {
  try {
    // 발급 가능한 쿠폰 전체 목록을 가져오는 API 호출 (Paging 없이 전체 리스트)
    // 예: GET /api/admin/coupons/active
    couponList.value = (await COUPON_REPOSITORY.getList(1)).items
  } catch (e) {
    console.error(e)
    ElMessage.error('쿠폰 목록을 불러오지 못했습니다.')
  }
}

const openCouponModal = async (user: User) => {
  targetUser.value = user
  selectedCouponId.value = null // 초기화
  couponDialogVisible.value = true

  // 쿠폰 목록이 비어있으면 로드
  if (couponList.value.length === 0) {
    await loadCoupons()
  }
}

// 실제 지급 API 호출
const giveCoupon = async () => {
  if (!targetUser.value || !selectedCouponId.value) {
    ElMessage.warning('쿠폰을 선택해주세요.')
    return
  }

  sending.value = true
  try {
    state.issueCouponRequest.userId =  targetUser.value.id
    state.issueCouponRequest.couponId =  selectedCouponId.value

    await COUPON_REPOSITORY.issue(state.issueCouponRequest)

    ElMessage.success(`${targetUser.value.name}님에게 쿠폰을 지급했습니다.`)
    couponDialogVisible.value = false
  } catch (e) {
    console.error(e)
    ElMessage.error('쿠폰 지급에 실패했습니다.')
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.dialog-desc {
  margin-bottom: 20px;
  font-size: 14px;
  color: #555;
  line-height: 1.5;
}
/* 기존 스타일 유지 */
.admin-user-list { padding: 20px; background-color: #fff; }
.header { margin-bottom: 20px; }
</style>
