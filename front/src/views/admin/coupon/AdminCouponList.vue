<template>
  <div class="coupon-list-container">
    <el-card shadow="never" class="list-card">
      <template #header>
        <div class="card-header">
          <h2>쿠폰 관리</h2>
          <el-button type="primary" @click="goToCreate">
            + 쿠폰 등록
          </el-button>
        </div>
      </template>

      <div class="filter-section"></div>

      <el-table
        v-loading="loading"
        :data="coupons"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <el-table-column prop="name" label="쿠폰명" min-width="200" />

        <el-table-column label="할인 혜택" width="180">
          <template #default="scope">
            <span v-if="scope.row.discountType === 'PERCENTAGE'">
              {{ scope.row.discountValue }}% 할인
              <small v-if="scope.row.maxDiscountAmount > 0" class="sub-text">
                (최대 {{ formatCurrency(scope.row.maxDiscountAmount) }})
              </small>
            </span>
            <span v-else>
              {{ formatCurrency(scope.row.discountValue) }} 할인
            </span>
          </template>
        </el-table-column>

        <el-table-column label="유효 기간" width="250">
          <template #default="scope">
            <div v-if="scope.row.expirationType === 'DATE_RANGE'">
              {{ formatDate(scope.row.startDate) }} <br/> ~ {{ formatDate(scope.row.endDate) }}
            </div>
            <div v-else>
              발급 후 {{ scope.row.validDays }}일간
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="totalQuantity" label="발행 수량" width="120" align="right">
          <template #default="scope">
            {{ scope.row.totalQuantity?.toLocaleString() }}장
          </template>
        </el-table-column>

        <el-table-column prop="issuedQuantity" label="발급됨" width="100" align="right">
          <template #default="scope">
            {{ scope.row.issuedQuantity?.toLocaleString() || 0 }}장
          </template>
        </el-table-column>

        <el-table-column label="관리" width="150" align="center">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row.id)">수정</el-button>
            <el-button
              size="small"
              type="danger"
              plain
              @click="handleDelete(scope.row.id)"
            >삭제</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="fetchCoupons"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { container } from 'tsyringe'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminCouponRepository from '@/repository/admin/AdminCouponRepository.ts'
import CouponResponse from "@/entity/coupon/Coupon.ts"

const router = useRouter()
const COUPON_REPOSITORY = container.resolve(AdminCouponRepository)

const coupons = ref<CouponResponse[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const fetchCoupons = async () => {
  loading.value = true
  try {
    const requestPage = currentPage.value - 1;
    const response = await COUPON_REPOSITORY.getList(requestPage, pageSize.value)
    coupons.value = response.items
    total.value = response.totalCount

  } catch (e) {
    console.error(e)
    ElMessage.error('목록을 불러오는데 실패했습니다.')
  } finally {
    loading.value = false
  }
}

// 2. 화면 이동
const goToCreate = () => {
  router.push('/admin/coupon/create')
}

const handleEdit = (id: number) => {
  router.push(`/admin/coupon/edit/${id}`)
}

// 3. 삭제 처리
const handleDelete = (id: number) => {
  ElMessageBox.confirm(
    '정말 이 쿠폰을 삭제하시겠습니까?',
    '경고',
    {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await COUPON_REPOSITORY.delete(id)
      ElMessage.success('삭제되었습니다.')
      fetchCoupons()
    } catch (e) {
      console.error(e)
      ElMessage.error('삭제 실패')
    }
  })
}

const formatCurrency = (value?: number) => {
  if (value === undefined || value === null) return '0원'
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value)
}

const formatDate = (dateStr?: string | null) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

// --- [Lifecycle Hooks] ---
onMounted(() => {
  fetchCoupons()
})
</script>

<style scoped>
.coupon-list-container {
  max-width: 1200px;
  margin: 40px auto;
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

.sub-text {
  display: block;
  font-size: 11px;
  color: #909399;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
