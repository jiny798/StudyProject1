<template>
  <div class="flavor-category-page">
    <div class="content-wrapper">

      <div class="card form-card">
        <div class="card-header">
          <div class="header-icon">✨</div>
          <div>
            <h2>카테고리 관리</h2>
            <p>새로운 카테고리 분류를 등록하세요.</p>
          </div>
        </div>

        <div class="card-body">
          <div class="form-group">
            <label for="parent-category">상위 카테고리</label>
            <div class="select-wrapper">
              <select id="parent-category" v-model="state.categoryWrite.parentId">
                <option :value="null">🚫 최상위 (루트)</option>
                <option v-for="category in parentCategories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label for="category-name">카테고리 명</label>
            <input
              id="category-name"
              type="text"
              v-model="state.categoryWrite.name"
              placeholder="예: 베이커리, 과일 등"
              @keyup.enter="registerCategory"
            />
          </div>

          <button @click="registerCategory" class="submit-button">
            <span>저장하기</span>
          </button>
        </div>
      </div>

      <div class="card list-card">
        <div class="card-header">
          <div class="header-icon">📂</div>
          <div>
            <h2>카테고리 구조</h2>
            <p>등록된 분류 체계를 관리합니다.</p>
          </div>
        </div>

        <div class="card-body tree-wrapper">
          <ul v-if="categoryTree.length > 0" class="tree-root">
            <CategoryTreeItem
              v-for="category in categoryTree"
              :key="category.id"
              :category="category"
              :depth="0"
            />
          </ul>

          <div v-else class="empty-state">
            <div class="empty-img">📦</div>
            <p>등록된 카테고리가 없습니다.</p>
          </div>
        </div>
      </div>

    </div>

    <CategoryEditModal
      :show="isModalOpen"
      :category="editingCategory"
      :categories="parentCategories"
      @close="closeModal"
      @save="handleUpdate"
    />

  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, provide, reactive, ref } from 'vue'
import { container } from 'tsyringe'
import CategoryRepository from '@/repository/user/CategoryRepository.ts'
import CategoryWrite from '@/entity/product/CategoryWrite.ts'
import type Category from '@/entity/product/Category'
import CategoryTreeItem from '@/components/category/CategoryTreeItem.vue'
import CategoryEditModal from '@/components/category/CategoryEditModal.vue'

const categoryRepository = container.resolve(CategoryRepository)
const parentCategories = ref<Category[]>([])
const state = reactive({ categoryWrite: new CategoryWrite() })

// 모달 관련 상태
const isModalOpen = ref(false)
const editingCategory = ref<Category | null>(null)

const modifyCategory = (category: Category) => {
  editingCategory.value = category
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
  editingCategory.value = null
}

const handleUpdate = async (payload: { id: number, name: string, parentId: number | null }) => {
  try {
    console.log('수정 요청 Payload:', payload)

    await categoryRepository.modify(payload.id, {
      name: payload.name,
      parentId: payload.parentId
    })

    alert(`[Mock] 수정됨!\nID: ${payload.id}\n이름: ${payload.name}\n부모ID: ${payload.parentId}`)

    closeModal()
    await fetchCategories()
  } catch (e) {
    console.error(e)
    alert('수정 실패')
  }
}

const removeCategory = async (category: Category) => {
  if (confirm(`'${category.name}' 삭제하시겠습니까?`)) {
    try {
      // await categoryRepository.delete(category.id)
      alert('삭제되었습니다 (Mock).')
      await fetchCategories()
    } catch (e) {
      alert('삭제 실패')
    }
  }
}

provide('onModify', modifyCategory)
provide('onRemove', removeCategory)

// --- 트리 변환 로직 ---
const buildCategoryTree = (categories: Category[]): Category[] => {
  const map = new Map<number, Category>()
  const roots: Category[] = []
  // Deep Copy에 준하는 처리로 원본 오염 방지
  const clonedCategories = categories.map(c => ({ ...c, children: [] }))

  clonedCategories.forEach((category) => {
    map.set(category.id, category)
  })

  clonedCategories.forEach((category) => {
    if (category.parentId) {
      map.get(category.parentId)?.children?.push(category)
    } else {
      roots.push(category)
    }
  })
  return roots
}

const fetchCategories = async () => {
  try {
    parentCategories.value = await categoryRepository.getAll()
  } catch (error) {
    console.error(error)
  }
}

const categoryTree = computed(() => buildCategoryTree(parentCategories.value))

const registerCategory = async () => {
  if (!state.categoryWrite.name.trim()) {
    alert('이름을 입력해주세요.')
    return
  }
  try {
    await categoryRepository.write(state.categoryWrite)
    state.categoryWrite = new CategoryWrite()
    await fetchCategories()
  } catch (error) {
    console.error(error)
    alert('등록 중 오류가 발생했습니다.')
  }
}

onMounted(fetchCategories)
</script>

<style scoped>
/* 기존 CSS 유지 */
.flavor-category-page {
  background-color: #f0f2f5;
  min-height: 100vh;
  padding: 3rem 2rem;
  font-family: 'Pretendard', sans-serif;
}

.content-wrapper {
  display: flex;
  gap: 1.5rem;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  align-items: flex-start;
}

.card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #e5e7eb;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.form-card { flex: 1; max-width: 380px; position: sticky; top: 2rem; }
.list-card { flex: 1.5; min-height: 500px; }

.card-header {
  padding: 1.5rem;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  gap: 1rem;
  background-color: #fff;
}

.header-icon {
  width: 40px;
  height: 40px;
  background-color: #f0f9ff;
  color: #0ea5e9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.card-header h2 { margin: 0; font-size: 1.1rem; font-weight: 700; color: #111827; }
.card-header p { margin: 2px 0 0 0; font-size: 0.85rem; color: #6b7280; }

.card-body { padding: 1.5rem; }

.form-group { margin-bottom: 1.25rem; }
.form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.4rem;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.95rem;
  outline: none;
  transition: border-color 0.2s;
  background-color: #fff;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.submit-button {
  width: 100%;
  padding: 0.85rem;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 0.5rem;
  transition: background-color 0.2s;
}

.submit-button:hover { background-color: #2563eb; }

.tree-wrapper { background-color: #fff; overflow-x: auto; }
.tree-root { list-style: none; padding-left: 0; margin: 0; }

.empty-state { text-align: center; color: #9ca3af; margin-top: 4rem; }
.empty-img { font-size: 2.5rem; margin-bottom: 1rem; opacity: 0.5; }

@media (max-width: 768px) {
  .content-wrapper { flex-direction: column; }
  .form-card, .list-card { max-width: 100%; }
  .form-card { position: static; }
}
</style>
