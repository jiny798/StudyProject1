<template>
  <div class="option-values-container">
    <div v-for="(value, index) in props.optionValues" :key="value.id" class="option-value-card">

      <div class="option-value-header">
        <div class="input-label">옵션값</div>
        <el-input
          v-model="value.valueName"
          placeholder="값 입력 (예: 블랙)"
          class="option-value-input"
        />
        <el-button type="danger" icon="Delete" circle plain @click="removeOptionValue(index)" />
      </div>

      <div class="option-value-body">
        <template v-if="value.subOptions.length > 0">
          <div class="sub-option-wrapper">
            <div class="branch-guide">↳ <b>{{ value.valueName || '...' }}</b>의 상세 옵션 설정</div>

            <div
              v-for="(subOption, subIndex) in value.subOptions"
              :key="subOption.id"
              class="sub-option-group"
            >
              <div class="sub-option-header">
                <span class="label-text">하위 옵션명:</span>
                <el-input
                  v-model="subOption.optionName"
                  placeholder="예: 사이즈"
                  class="sub-option-name-input"
                />
                <el-button link type="danger" @click="removeSubOption(value, subIndex)">그룹 삭제</el-button>
              </div>

              <RecursiveOptions :option-values="subOption.optionValues" />
            </div>
          </div>
        </template>

        <template v-else>
          <div class="leaf-node-control">
            <div class="stock-input-wrapper">
              <span class="label-text">재고:</span>
              <el-input
                v-model.number="value.stock"
                placeholder="수량 입력"
                type="number"
                class="stock-input"
              />
              <span class="unit">개</span>
            </div>

            <div class="action-divider">또는</div>
            <el-button type="primary" plain size="small" @click="switchToSubOptionMode(value)">
              + 하위 옵션(상세) 추가하기
            </el-button>
          </div>
        </template>
      </div>
    </div>

    <div class="add-value-btn-wrapper">
      <el-button class="add-btn" @click="addOptionValue" icon="Plus">옵션값 추가</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { Option, OptionValue } from './types'

const props = defineProps<{
  optionValues: OptionValue[];
}>()

// ID 생성을 위한 임시 카운터 (실무에선 UUID 등을 권장)
let nextId = Math.floor(Math.random() * 10000)

// 새로운 옵션 그룹(Key) 생성 팩토리 메서드
const createNewOptionGroup = (): Option => ({
  id: nextId++,
  optionName: '',
  optionValues: [], // 초기엔 비어있는 리스트
})

// 1. 현재 레벨에 새로운 값(Value) 추가 (예: 블랙 추가)
const addOptionValue = () => {
  props.optionValues.push({
    id: nextId++,
    valueName: '',
    stock: 0,       // 기본값은 재고 입력 모드 (Leaf)
    subOptions: [], // 하위 옵션 없음
  })
}

const removeOptionValue = (index: number) => {
  props.optionValues.splice(index, 1)
}

// 2. [중요] 재고 입력 모드 -> 하위 옵션 모드로 전환
// 객체의 타입을 Integer(Stock)에서 Map(SubOptions)으로 바꾸는 과정입니다.
const switchToSubOptionMode = (optionValue: OptionValue) => {
  // 사용자가 실수로 눌렀을 때 데이터 손실 방지 (선택사항)
  if (optionValue.stock && optionValue.stock > 0) {
    if (!confirm('하위 옵션을 추가하면 현재 입력된 재고 정보는 삭제됩니다. 진행하시겠습니까?')) return;
  }

  optionValue.stock = null; // 재고 정보 제거
  // 하위 옵션 그룹을 담을 그릇을 생성합니다.
  optionValue.subOptions.push(createNewOptionGroup());
}

const removeSubOption = (optionValue: OptionValue, index: number) => {
  optionValue.subOptions.splice(index, 1)
  // 만약 모든 하위 옵션 그룹을 삭제했다면, 다시 재고 입력 모드로 돌아갈지 결정할 수 있습니다.
  // 여기서는 subOptions가 비면 템플릿의 v-else에 의해 자동으로 재고 입력창이 뜹니다.
}
</script>

<script lang="ts">
// 재귀 컴포넌트 명시
export default {
  name: 'RecursiveOptions',
}
</script>

<style scoped>
.option-values-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.option-value-card {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 15px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.option-value-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
}

.input-label {
  font-weight: bold;
  font-size: 14px;
  color: #606266;
  min-width: 60px;
}

.option-value-input {
  width: 200px;
}

.option-value-body {
  padding-left: 20px; /* 들여쓰기로 계층 구조 표현 */
  border-left: 3px solid #409eff; /* 시각적 가이드 라인 */
  margin-left: 10px;
}

/* Leaf Node Style (재고 입력) */
.leaf-node-control {
  display: flex;
  align-items: center;
  gap: 15px;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.stock-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stock-input {
  width: 100px;
}

.action-divider {
  color: #909399;
  font-size: 12px;
}

/* Branch Node Style (하위 옵션) */
.sub-option-wrapper {
  background-color: #fcfcfc;
}

.branch-guide {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.sub-option-group {
  border: 1px dashed #c0c4cc;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.sub-option-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.sub-option-name-input {
  width: 150px;
}

.add-value-btn-wrapper {
  margin-top: 10px;
  text-align: center;
}
.add-btn {
  width: 100%;
  border-style: dashed;
}
</style>
