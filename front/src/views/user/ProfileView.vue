<template>
  <div class="mypage-container">
    <section class="profile-section">
      <div class="profile-left">
        <el-avatar :size="72" icon="UserFilled" class="avatar" />
        <div class="info-text">
          <p class="email">{{ state.user.email }}</p>
        </div>
      </div>
      <el-button class="edit-btn" size="small" @click="goToProfileEdit">프로필 수정</el-button>
    </section>

    <section class="summary-section">
      <div
        class="summary-item"
        v-for="(value, key) in summary"
        :key="key"
        :class="{ 'is-active': currentView === key }"
        @click="changeView(key)"
      >
        <p class="label">{{ keyLabel(key) }}</p>
        <p class="value">{{ value }}</p>
        <div class="active-bar" v-if="currentView === key"></div>
      </div>
    </section>

    <section class="content-section">
      <Transition name="fade" mode="out-in">
        <component
          :is="getViewComponent(currentView)"
          v-if="getViewComponent(currentView)"
        />

        <div v-else class="empty-placeholder">
          <el-empty :description="`${keyLabel(currentView)} 내역 준비 중입니다.`" />
        </div>
      </Transition>
    </section>

  </div>
</template>

<script setup lang="ts">
import { onBeforeMount, reactive, ref, markRaw } from 'vue'
import { container } from 'tsyringe'
import { UserFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

// 자식 컴포넌트 Import
import OrderHistoryTab from '@/components/mypage/OrderHistoryTab.vue'
import CouponListTab from '@/components/mypage/CouponListTab.vue'

// Repositories & Entities
import ProfileRepository from '@/repository/user/ProfileRepository.ts'
import OrderRepository from '@/repository/user/OrderRepository.ts'
import CouponRepository from "@/repository/user/CartRepository.ts"
import UserProfile from '@/entity/user/account/UserProfile.ts'

const router = useRouter()
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)
const ORDER_REPOSITORY = container.resolve(OrderRepository)
const COUPON_REPOSITORY = container.resolve(CouponRepository)

// 현재 보고 있는 화면 Key ('orders' | 'coupons' | 'reviews')
const currentView = ref('orders')

const state = reactive({
  user: new UserProfile()
})

const summary = reactive({
  orders: 0,
  coupons: 0,
  reviews: 0, // 리뷰 항목 추가
})

// 화면 전환 함수
function changeView(key: string) {
  currentView.value = key
}

// Key에 따른 컴포넌트 리턴
function getViewComponent(key: string) {
  const components: Record<string, any> = {
    orders: OrderHistoryTab,
    coupons: CouponListTab,
    // reviews: ReviewListTab (나중에 만드시면 여기에 추가)
  }
  return components[key]
}

function keyLabel(key: string) {
  const map: Record<string, string> = { orders: '주문', coupons: '쿠폰', reviews: '리뷰' }
  return map[key] || key
}

onBeforeMount(() => {
  state.user = PROFILE_REPOSITORY.getProfile() || new UserProfile()

  Promise.all([
    ORDER_REPOSITORY.getOrderCount(),
    COUPON_REPOSITORY.getMyCouponList()
  ]).then(([orderRes, couponRes]) => {
    summary.orders = orderRes.orderCount
    summary.coupons = couponRes.length
    // summary.reviews = ... (리뷰 개수 API 호출)
  })
})

function goToProfileEdit() {
  router.push('/profile/edit')
}
</script>

<style scoped>
.mypage-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 32px 16px;
  font-family: 'Pretendard', sans-serif;
  color: #222;
}

/* 프로필 섹션 */
.profile-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}
.profile-left { display: flex; align-items: center; }
.avatar { background-color: #f5f5f5; margin-right: 16px; }
.info-text .email { margin-top: 4px; font-size: 14px; color: #666; }

/* 요약(네비게이션) 섹션 스타일 변경 */
.summary-section {
  display: flex;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee; /* 하단 라인으로 구분 */
  margin-bottom: 32px;
}

.summary-item {
  flex: 1;
  text-align: center;
  padding: 16px 0;
  cursor: pointer;
  position: relative; /* active-bar 위치 잡기 위함 */
  transition: all 0.2s;
}

.summary-item:hover .label,
.summary-item:hover .value {
  color: #409eff;
}

.summary-item .label {
  font-size: 13px;
  color: #888;
  margin-bottom: 6px;
}
.summary-item .value {
  font-size: 18px;
  font-weight: 600;
  color: #222;
}

/* 활성화(선택됨) 상태 스타일 */
.summary-item.is-active .label { color: #409eff; font-weight: 500; }
.summary-item.is-active .value { color: #409eff; }

/* 선택 시 하단 파란색 바 애니메이션 */
.active-bar {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background-color: #409eff;
  border-radius: 3px 3px 0 0;
}

/* 콘텐츠 영역 */
.content-section {
  min-height: 300px;
}

.empty-placeholder {
  padding: 60px 0;
  text-align: center;
}

/* 페이드 애니메이션 (선택 사항) */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
