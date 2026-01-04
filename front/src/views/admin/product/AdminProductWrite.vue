<template>
  <div class="admin-product-write">
    <h1>상품 등록</h1>
    <el-form label-position="top" class="product-form">
      <el-form-item label="상품 제목" prop="title">
        <el-input v-model="state.productWrite.name" maxlength="30" show-word-limit
                  placeholder="상품명"/>
      </el-form-item>

      <el-form-item label="상품 이미지" required>
        <el-upload
          :show-file-list="true"
          list-type="picture-card"
          :file-list="fileList"
          :http-request="handleUpload"
          :on-remove="handleRemove"
        >
          <el-icon>
            <Plus/>
          </el-icon>
        </el-upload>
        <div v-if="fileList.length < 2" class="checkRequired">사진을 2장 이상 등록해주세요</div>
      </el-form-item>

      <el-form-item label="상품 가격" prop="title">
        <el-input v-model="state.productWrite.price" maxlength="10" show-word-limit
                  placeholder="가격"/>
      </el-form-item>

      <el-form-item label="카테고리" prop="category">
        <el-select v-model="state.productWrite.categoryId" placeholder="카테고리를 선택해주세요">
          <el-option v-for="item in state.categories" :key="item.id" :label="item.name"
                     :value="item.id"/>
        </el-select>
      </el-form-item>

      <el-form-item label="상품 옵션">
        <div class="option-container">
          <div v-for="(option, index) in options" :key="option.id" class="option-group">
            <div class="option-header">
              <span style="align-self: center; font-weight:bold; margin-right:10px;">옵션명:</span>
              <el-input v-model="option.optionName" placeholder="예: 색상" class="option-name-input"/>
              <el-button type="danger" plain @click="removeOption(index)">그룹 삭제</el-button>
            </div>
            <RecursiveOptions :option-values="option.optionValues"/>
          </div>

          <el-button type="primary" plain @click="addTopLevelOption"
                     style="width:100%; margin-top:10px;">
            + 최상위 옵션 추가
          </el-button>
        </div>
      </el-form-item>


      <div ref="editorRoot" class="editor-area"/>

      <div v-if="showPopup" class="image-popup"
           :style="{ top: popupY + 'px', left: popupX + 'px' }">
        <label>Width: <input v-model="inputWidth"/></label>
        <label>Height: <input v-model="inputHeight"/></label>
        <button @click="applyResize">적용</button>
      </div>

      <el-form-item prop="agree" style="margin-top: 20px;">
        <el-checkbox v-model="agree"> 상품 가격 및 정보를 확인하였습니다</el-checkbox>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="write()" :disabled="!agree">
          상품 등록
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import {ElMessage, type UploadUserFile} from 'element-plus'
import {onMounted, onBeforeUnmount, ref, reactive, nextTick} from 'vue'
import Editor from '@toast-ui/editor'
import '@toast-ui/editor/dist/toastui-editor.css'
import ProductWrite from '@/entity/product/ProductWrite.ts'
import {container} from 'tsyringe'
import {useRouter} from 'vue-router'
import type HttpError from '@/http/HttpError.ts'
import AdminProductRepository from '@/repository/admin/AdminProductRepository.ts'
import CategoryRepository from '@/repository/admin/AdminCategoryRepository.ts'
import type Category from '@/entity/product/Category.ts'
import RecursiveOptions from './RecursiveOptions.vue'
import type {Option, OptionValue} from './types'

const editorRoot = ref<HTMLDivElement | null>(null)
let editorInstance: Editor | null = null
const showPopup = ref(false)
const popupX = ref(0)
const popupY = ref(0)
const selectedImage = ref<HTMLImageElement | null>(null)
const inputWidth = ref('')
const inputHeight = ref('')
const loading = ref(false)
const agree = ref(false)

type StateType = {
  productWrite: ProductWrite
  categories: Category[]
}

const state = reactive<StateType>({
  productWrite: new ProductWrite(),
  categories: [],
})

const PRODUCT_REPOSITORY = container.resolve(AdminProductRepository)
const CATEGORY_REPOSITORY = container.resolve(CategoryRepository)
const router = useRouter()

// --- 옵션 로직 ---
let nextId = 0
const options = ref<Option[]>([])

// [Factory] 단일 옵션값 생성 (값 + 재고 입력 상태)
const createNewOptionValue = (): OptionValue => ({
  id: nextId++,
  valueName: '',
  stock: 0,
  subOptions: [],
})

// [Factory] 옵션 그룹 생성 (초기값 1개 포함)
const createNewOption = (): Option => ({
  id: nextId++,
  optionName: '',
  optionValues: [createNewOptionValue()], // 핵심: 빈 값 1개 미리 생성
})

const addTopLevelOption = () => {
  options.value.push(createNewOption())
}

const removeOption = (index: number) => {
  options.value.splice(index, 1)
}

// UI 데이터 -> 서버 전송 포맷 변환
const buildOptionsObject = (opts: Option[]): Record<string, any> => {
  const result: Record<string, any> = {}
  for (const opt of opts) {
    if (!opt.optionName) continue
    const valuesObject: Record<string, any> = {}
    for (const val of opt.optionValues) {
      if (!val.valueName) continue
      if (val.subOptions.length > 0) {
        valuesObject[val.valueName] = buildOptionsObject(val.subOptions)
      } else {
        valuesObject[val.valueName] = val.stock ?? 0
      }
    }
    result[opt.optionName] = valuesObject
  }
  return result
}

function write() {
  const requestBody = {...state.productWrite}

  if (options.value.length > 0) {
    requestBody.options = buildOptionsObject(options.value)
  } else {
    requestBody.price = Number(String(requestBody.price).replace(/,/g, ''))
    requestBody.options = {}
  }

  requestBody.productImages = getUploadedImageUrls()
  requestBody.description = editorInstance?.getHTML() || ''

  loading.value = true
  PRODUCT_REPOSITORY.write(requestBody)
    .then(() => {
      ElMessage({type: 'success', message: '글 등록이 완료되었습니다.'})
      router.replace('/admin')
    })
    .catch((e: HttpError) => {
      ElMessage({type: 'error', message: e.getMessage()})
    })
    .finally(() => {
      loading.value = false
    })
}

// --- 에디터 및 라이프사이클 ---
onMounted(async () => {
  // 카테고리 로드
  CATEGORY_REPOSITORY.getLeafCategories().then((responseList) => {
    state.categories = responseList
  })

  // 화면 렌더링 완료 대기 (화면 안바뀜 현상 해결)
  await nextTick()

  // 에디터 초기화
  if (editorRoot.value) {
    // 기존 인스턴스 정리 (혹시 남아있을 경우)
    if (editorInstance) {
      editorInstance.destroy()
      editorInstance = null
    }

    editorInstance = new Editor({
      el: editorRoot.value,
      height: '500px',
      initialEditType: 'wysiwyg',
      previewStyle: 'vertical',
      initialValue: '',
      hideModeSwitch: true,
      hooks: {
        addImageBlobHook: async (blob, callback) => {
          const formData = new FormData()
          formData.append('file', blob)
          try {
            const response = await fetch('http://localhost:8080/api/admin/images', {
              method: 'POST',
              body: formData,
              credentials: 'include',
            })
            const jsonString = await response.text();
            const data = JSON.parse(jsonString);
            const imageUrl = data.message;
            callback(imageUrl, blob.name)
          } catch (err) {
            console.error('업로드 실패', err)
            ElMessage.error('이미지 업로드에 실패했습니다.')
          }
        },
      },
    })

    // 이미지 클릭 리사이즈 이벤트 연결
    const contentEl = editorRoot.value.querySelector('.toastui-editor-main')
    if (contentEl) {
      contentEl.addEventListener('click', (event: MouseEvent) => {
        const target = event.target as HTMLElement
        if (target.tagName === 'IMG') {
          const img = target as HTMLImageElement
          const rect = img.getBoundingClientRect()

          selectedImage.value = img
          inputWidth.value = img.getAttribute('width') || ''
          inputHeight.value = img.getAttribute('height') || ''

          popupX.value = rect.left + window.scrollX
          popupY.value = rect.top + window.scrollY + rect.height + 8
          showPopup.value = true
        } else {
          showPopup.value = false
        }
      })
    }
  }
})

const applyResize = () => {
  if (!selectedImage.value) return
  const width = inputWidth.value.trim()
  const height = inputHeight.value.trim()

  width ? selectedImage.value.setAttribute('width', width) : selectedImage.value.removeAttribute('width')
  height ? selectedImage.value.setAttribute('height', height) : selectedImage.value.removeAttribute('height')

  showPopup.value = false
}

onBeforeUnmount(() => {
  // 메모리 누수 및 재진입 오류 방지를 위한 정리
  if (editorInstance) {
    editorInstance.destroy()
    editorInstance = null
  }
})

// --- 유틸리티 ---
const formatNumberInput = (field: 'price' | 'stock') => {
  const value = String(state.productWrite[field] || '').replace(/,/g, '')
  const number = Number(value)
  if (!isNaN(number)) {
    // @ts-ignore
    state.productWrite[field] = number.toLocaleString()
  }
}

const fileList = ref<UploadUserFile[]>([])
const imageMap = new Map<string, string>()

const handleUpload = async ({file, onSuccess, onError}: any) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('http://localhost:8080/api/admin/images', {
      method: 'POST',
      body: formData,
      credentials: 'include',
    })
    // const uploadedUrl = await res.text()
    const jsonString = await res.text();
    const data = JSON.parse(jsonString);
    const imageUrl = data.message;
    imageMap.set(file.uid, imageUrl)
    onSuccess()
  } catch (err) {
    ElMessage.error('업로드 실패')
    onError(err)
  }
}

const handleRemove = (file: any) => {
  imageMap.delete(file.uid)
}

const getUploadedImageUrls = () => {
  return Array.from(imageMap.values())
}
</script>

<style scoped>
.admin-product-write {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}

.product-form {
  max-width: 100%;
}

.checkRequired {
  color: #f56c6c;
  font-size: 13px;
  margin-top: 4px;
}

.image-popup {
  position: absolute;
  background: white;
  border: 1px solid #ddd;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 6px;
  z-index: 9999;
}

.option-container {
  width: 100%;
  border: 1px solid #dcdfe6;
  padding: 20px;
  border-radius: 4px;
  background-color: #fefefe;
}

.option-group {
  border: 1px solid #e4e7ed;
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 4px;
  background-color: #fff;
}

.option-header {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.option-name-input {
  width: auto;
  flex-grow: 2;
  font-weight: bold;
}

.editor-area {
  margin-top: 20px;
  margin-bottom: 20px;
}
</style>
