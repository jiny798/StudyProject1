<template>
  <li class="tree-item-li">
    <div class="tree-row" :class="{ 'is-expanded': isOpen }" @click="toggle">

      <div class="row-left">
        <span
          class="toggle-btn"
          :class="{ 'is-open': isOpen, 'invisible': !hasChildren }"
          @click.stop="toggle"
        >
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </span>

        <span class="icon-box" :class="hasChildren ? 'folder-color' : 'file-color'">
          <span v-if="hasChildren">{{ isOpen ? '📂' : '📁' }}</span>
          <span v-else>📄</span>
        </span>

        <span class="category-name">{{ category.name }}</span>
      </div>

      <div class="row-right">
        <button class="action-btn edit-btn" @click.stop="onModify(category)">
          수정
        </button>
        <button class="action-btn delete-btn" @click.stop="onRemove(category)">
          삭제
        </button>
      </div>

    </div>

    <ul v-if="hasChildren && isOpen" class="tree-children">
      <CategoryTreeItem
        v-for="child in category.children"
        :key="child.id"
        :category="child"
        :depth="depth + 1"
      />
    </ul>
  </li>
</template>

<script setup lang="ts">
import { computed, inject, ref } from 'vue'
import type Category from '@/entity/product/Category'

const props = defineProps<{
  category: Category
  depth: number
}>()

// 부모(CategoryPage)에서 provide한 함수를 주입받음
const onModify = inject<(category: Category) => void>('onModify')!
const onRemove = inject<(category: Category) => void>('onRemove')!

const isOpen = ref(true)

const hasChildren = computed(() => {
  return props.category.children && props.category.children.length > 0
})

const toggle = () => {
  if (hasChildren.value) {
    isOpen.value = !isOpen.value
  }
}
</script>

<style scoped>
.tree-item-li {
  list-style: none;
  position: relative;
  padding-left: 0;
}

.tree-children {
  padding-left: 24px;
  margin-left: 10px;
  border-left: 1px solid #e2e8f0;
  position: relative;
}

.tree-children .tree-item-li::before {
  content: '';
  position: absolute;
  top: 18px;
  left: -25px;
  width: 20px;
  height: 1px;
  background-color: #e2e8f0;
}

.tree-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 8px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  position: relative;
  height: 36px;
}

.row-left {
  display: flex;
  align-items: center;
  flex: 1;
  overflow: hidden;
}

.row-right {
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.tree-row:hover .row-right { opacity: 1; }
.tree-row:hover { background-color: #f1f5f9; border-color: #cbd5e1; }

.toggle-btn {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 4px;
  color: #94a3b8;
  border-radius: 4px;
}
.toggle-btn.is-open svg { transform: rotate(90deg); }
.toggle-btn.invisible { visibility: hidden; }

.icon-box { margin-right: 8px; font-size: 1.1rem; }
.category-name { font-size: 0.9rem; color: #334155; font-weight: 500; }

.action-btn {
  border: none;
  padding: 4px 8px;
  font-size: 0.75rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.edit-btn { background-color: #e0e7ff; color: #4338ca; }
.edit-btn:hover { background-color: #c7d2fe; }

.delete-btn { background-color: #fee2e2; color: #b91c1c; }
.delete-btn:hover { background-color: #fecaca; }
</style>
