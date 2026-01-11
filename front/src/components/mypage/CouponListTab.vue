<template>
  <div class="coupon-tab-container">
    <div v-if="couponList.length === 0" class="no-data">
      <el-empty description="보유하신 쿠폰이 없습니다." />
    </div>

    <div v-else class="coupon-grid">
      <div v-for="(coupon, index) in couponList" :key="index" class="coupon-ticket">
        <div class="ticket-left">
          <div class="coupon-title">{{ coupon.couponName }}</div>
          <div class="coupon-date">
            {{ formatDate(coupon.validUntil) }} 까지
          </div>
          <div class="coupon-condition">
            {{ coupon.minOrderPrice ? `${coupon.minOrderPrice.toLocaleString()}원 이상 구매 시` : '조건 없음' }}
          </div>
        </div>
        <div class="ticket-right">
          <div class="amount">
            {{ formatDiscount(coupon) }}
          </div>
          <el-button type="primary" size="small" plain round class="use-btn">사용가능</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { container } from 'tsyringe'
import CouponRepository from "@/repository/user/CartRepository.ts" // 경로 확인 필요
import MyCoupon from "@/entity/user/coupon/MyCoupon.ts"

const COUPON_REPOSITORY = container.resolve(CouponRepository)
const couponList = ref<MyCoupon[]>([])

onMounted(async () => {
  // 쿠폰 목록 로드
  couponList.value = await COUPON_REPOSITORY.getMyCouponList()
})

// 날짜 포맷
function formatDate(dateStr: string | Date | undefined) {
  if (!dateStr) return '기간 제한 없음'
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

// 할인 금액/율 포맷
function formatDiscount(coupon: MyCoupon) {
  // 예: 정액 할인이면
  if (coupon.discountPrice) return `${coupon.discountPrice.toLocaleString()}원`
  // 예: 정률 할인이면 (엔티티 필드에 따라 조정 필요)
  // if (coupon.discountRate) return `${coupon.discountRate}%`
  return '할인'
}
</script>

<style scoped>
.coupon-tab-container {
  padding: 16px 0;
}

.coupon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); /* 반응형 그리드 */
  gap: 20px;
}

/* 티켓 스타일 */
.coupon-ticket {
  display: flex;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: transform 0.2s;
}
.coupon-ticket:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
}

.ticket-left {
  flex: 1;
  padding: 20px;
  border-right: 2px dashed #eee;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-title { font-size: 16px; font-weight: 700; color: #333; margin-bottom: 8px; }
.coupon-date { font-size: 13px; color: #888; margin-bottom: 4px; }
.coupon-condition { font-size: 12px; color: #aaa; }

.ticket-right {
  width: 100px;
  background-color: #fafafa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
}

.amount { font-size: 18px; font-weight: 800; color: #409eff; }
.use-btn { font-size: 11px; }

.no-data { padding: 40px 0; }
</style>
