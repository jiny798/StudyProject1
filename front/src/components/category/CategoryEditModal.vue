<template>
  <Transition name="modal">
    <div v-if="show" class="modal-backdrop" @click="$emit('close')">
      <div class="modal-container" @click.stop>

        <div class="modal-header">
          <h3>카테고리 수정</h3>
          <button class="close-btn" @click="$emit('close')">✕</button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label>상위 카테고리</label>
            <select v-model="localParentId">
              <option :value="null">🚫 최상위 (루트)</option>
              <option
                v-for="c in categories"
                :key="c.id"
                :value="c.id"
                :disabled="c.id === category?.id"
              >
                {{ c.name }} {{ c.id === category?.id ? '(현재 카테고리)' : '' }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>카테고리 명</label>
            <input
              ref="inputRef"
              type="text"
              v-model="localName"
              @keyup.enter="save"
              placeholder="카테고리 이름을 입력하세요"
            />
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn cancel" @click="$emit('close')">취소</button>
          <button class="btn save" @click="save">수정완료</button>
        </div>

      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import type Category from '@/entity/product/Category'

// 부모로부터 받는 데이터
const props = defineProps<{
  show: boolean
  category: Category | null
  categories: Category[] // [New] 전체 카테고리 목록 (드롭다운용)
}>()

// 부모에게 보낼 이벤트 정의
const emit = defineEmits(['close', 'save'])

const localName = ref('')
const localParentId = ref<number | null>(null) // [New] 부모 ID 상태
const inputRef = ref<HTMLInputElement | null>(null)

// 모달이 열리거나 category가 바뀌면 로컬 상태 동기화
watch(() => props.category, async (newCategory) => {
  if (newCategory) {
    localName.value = newCategory.name
    localParentId.value = newCategory.parentId // 현재 부모 ID 세팅

    // 모달이 뜰 때 자동으로 이름 입력창에 포커스 주기
    if (props.show) {
      await nextTick()
      inputRef.value?.focus()
    }
  }
})

const save = () => {
  if (!localName.value.trim()) {
    alert('이름을 입력해주세요.')
    return
  }

  // [Update] 이름뿐만 아니라 변경된 parentId도 함께 전송
  emit('save', {
    id: props.category?.id,
    name: localName.value,
    parentId: localParentId.value
  })
}
</script>

<style scoped>
/* 모달 배경 (Backdrop) */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(2px);
}

/* 모달 창 본체 */
.modal-container {
  background: white;
  width: 400px;
  max-width: 90%;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.2);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 헤더 */
.modal-header {
  padding: 1.2rem;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #1e293b;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #94a3b8;
}

/* 바디 */
.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.2rem; /* 그룹 간 간격 추가 */
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  font-size: 0.9rem;
  color: #475569;
}

/* input과 select 스타일 통일 */
.form-group input,
.form-group select {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.2s;
  background-color: #fff;
  box-sizing: border-box; /* 박스 깨짐 방지 */
}

.form-group input:focus,
.form-group select:focus {
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

/* 푸터 */
.modal-footer {
  padding: 1rem 1.2rem;
  background-color: #f8fafc;
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
}

.btn {
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  border: none;
}

.btn.cancel {
  background-color: #e2e8f0;
  color: #475569;
}
.btn.cancel:hover { background-color: #cbd5e1; }

.btn.save {
  background-color: #4f46e5;
  color: white;
}
.btn.save:hover { background-color: #4338ca; }

/* Vue Transition 효과 (fade) */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
