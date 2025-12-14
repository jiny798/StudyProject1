<template>
  <div class="admin-product-list">
    <div class="header">
      <h1 class="page-title">상품 목록</h1>
      <el-button type="primary" @click="moveToWrite">상품 등록</el-button>
    </div>

    <el-table :data="state.productPage.items" border style="width: 100%" v-loading="loading">

      <el-table-column label="No" width="80" align="center">
        <template #default="{ row }">
          {{ row.id }}
        </template>
      </el-table-column>

      <el-table-column label="이미지" width="100" align="center">
        <template #default="{ row }">
          <el-image
            style="width: 50px; height: 50px; border-radius: 4px;"
            :src="getMainImage(row)"
            :preview-src-list="getAllImages(row)"
            fit="cover"
            preview-teleported
          >
            <template #error>
              <div class="image-slot"><el-icon><Picture /></el-icon></div>
            </template>
          </el-image>
        </template>
      </el-table-column>

      <el-table-column prop="name" label="상품명" min-width="200" />

      <el-table-column label="가격" width="150" align="right">
        <template #default="{ row }">
          {{ row.price?.toLocaleString() ?? 0 }}원
        </template>
      </el-table-column>

      <el-table-column label="등록일" width="150" align="center">
        <template #default="{ row }">
          {{ row.getDisplaySimpleRegDate() }}
        </template>
      </el-table-column>

      <el-table-column label="관리" width="150" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="moveToEdit(row.id)">수정</el-button>
          <el-button size="small" type="danger" @click="deleteProduct(row.id)">삭제</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-area">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="state.productPage.totalCount"
        :page-size="state.productPage.size"
        :current-page="state.productPage.page"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { container } from 'tsyringe'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'

import Product from '@/entity/product/Product.ts'
import ProductRepository from '@/repository/user/ProductRepository.ts'
import Paging from "@/entity/data/Paging.ts";

interface State {
  productPage: Paging<Product>
}

const state = reactive<State>({
  productPage: new Paging<Product>(),
})

const loading = ref(false)
const router = useRouter()
const PRODUCT_REPOSITORY = container.resolve(ProductRepository)

// --- 데이터 조회 및 변환 로직 (핵심) ---
const getList = async (page = 1) => {
  loading.value = true
  try {
    const response = await PRODUCT_REPOSITORY.getList(page)
      .then((responsePage) => {
        state.productPage = responsePage
      })
  } catch (e: any) {
    console.error(e)
    ElMessage.error('상품 목록을 불러오지 못했습니다.')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getList()
})

const handlePageChange = (page: number) => {
  getList(page)
}

const moveToWrite = () => {
  router.push('/admin/products/write')
}

const moveToEdit = (id: number) => {
  // id가 Product.ts에 없더라도 DB PK는 보통 존재하므로 인자로 받아서 처리
  router.push(`/admin/products/edit/${id}`)
}

const deleteProduct = (id: number) => {
  ElMessageBox.confirm('정말 삭제하시겠습니까?', '경고', {
    confirmButtonText: '삭제',
    cancelButtonText: '취소',
    type: 'warning',
  }).then(async () => {
    try {
      await PRODUCT_REPOSITORY.delete(id)
      ElMessage.success('삭제되었습니다.')
      getList()
    } catch (e) {
      ElMessage.error('삭제 실패')
    }
  })
}

// 뷰 헬퍼 함수
const getMainImage = (product: Product) => {
  // Product 클래스 초기값에 의해 productImages는 항상 배열임이 보장됨 ([] 초기화)
  if (product.productImages.length > 0) {
    return product.productImages[0]
  }
  return ''
}

const getAllImages = (product: Product) => {
  return product.productImages
}
</script>

<style scoped>
.admin-product-list {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-title {
  font-size: 1.5rem;
  margin: 0;
}
.pagination-area {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 20px;
}
</style>
