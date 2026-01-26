<template>
  <div class="edit-container">
    <header class="page-header">
      <el-page-header @back="goBack" content="회원정보 수정"/>
    </header>

    <div class="content-wrapper">

      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>기본 정보</span>
            <el-button type="primary" :loading="loading.profile" @click="saveProfile">
              저장
            </el-button>
          </div>
        </template>

        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-position="top"
        >
          <el-form-item label="이메일">
            <el-input v-model="state.user.email" disabled/>
          </el-form-item>

          <el-form-item label="이름" prop="name">
            <el-input v-model="state.user.name"/>
          </el-form-item>

          <el-form-item label="휴대폰 번호" prop="phone">
            <el-input v-model="state.user.phone" :formatter="formatPhone"/>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>비밀번호 변경</span>
            <el-button type="primary" :loading="loading.password" @click="savePassword">
              변경
            </el-button>
          </div>
        </template>

        <el-form
          ref="pwdFormRef"
          :model="state.passwordUpdate"
          :rules="pwdRules"
          label-position="top"
        >
          <el-form-item label="현재 비밀번호" prop="current">
            <el-input v-model="state.passwordUpdate.beforePassword" type="password" show-password/>
          </el-form-item>
          <el-form-item label="새 비밀번호" prop="new">
            <el-input v-model="state.passwordUpdate.password" type="password" show-password/>
          </el-form-item>
          <el-form-item label="새 비밀번호 확인" prop="confirm">
            <el-input v-model="state.passwordUpdate.passwordConfirm" type="password" show-password/>
            <span v-if="pwdError" class="error-text">{{ pwdError }}</span>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>배송지 관리</span>
            <el-button type="primary" plain @click="openAddressModal">+ 새 배송지</el-button>
          </div>
        </template>

        <div v-if="addressList.length === 0" class="no-address">
          등록된 배송지가 없습니다.
        </div>

        <div v-else class="address-list">
          <div
            v-for="(addr, index) in addressList"
            :key="index"
            class="address-item"
            :class="{ 'active-card': addr.isDefault }"
          >
            <div class="addr-info">
              <div class="addr-top-row">
                <span class="addr-name">{{ addr.recipient }}</span>
                <el-tag v-if="addr.isDefault" type="success" size="small" effect="dark" round>기본
                </el-tag>
              </div>
              <p class="addr-text">[{{ addr.zipCode }}] {{ addr.roadAddress }} {{
                  addr.detailAddress
                }}</p>
              <p class="addr-phone">{{ addr.phone }}</p>
            </div>

            <div class="addr-actions">
              <el-button
                v-if="!addr.isDefault"
                size="small"
                @click="setAsDefault(index)"
              >
                기본으로
              </el-button>
              <el-button link type="danger" icon="Delete" @click="removeAddress(index)"/>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" title="새 배송지 추가" width="500px">
      <el-form :model="newAddr" label-position="top">
        <el-form-item label="수령인">
          <el-input v-model="newAddr.recipient"/>
        </el-form-item>
        <el-form-item label="주소">
          <div style="display:flex; gap:8px; margin-bottom:8px;">
            <el-input v-model="newAddr.zipCode" placeholder="우편번호" style="width:100px" readonly/>
            <el-button @click="mockAddressSearch">검색</el-button>
          </div>
          <el-input v-model="newAddr.roadAddress" readonly style="margin-bottom:8px;"/>
          <el-input v-model="newAddr.detailAddress" placeholder="상세주소"/>
        </el-form-item>
        <el-form-item label="연락처">
          <el-input v-model="newAddr.phone"/>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="newAddr.isDefault">기본 배송지로 설정</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">취소</el-button>
        <el-button type="primary" :loading="loading.address" @click="saveNewAddress">저장</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {reactive, ref, computed, onBeforeMount} from 'vue'
import {Delete} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {container} from 'tsyringe'
import {useRouter} from 'vue-router'
import ProfileRepository from '@/repository/user/ProfileRepository.ts'
import UserRepository from "@/repository/user/UserRepository.ts";
import UserProfile from "@/entity/user/account/UserProfile.ts";
import PasswordUpdate from "@/entity/user/account/PasswordUpdate.ts";

const router = useRouter()

// 의존성 주입
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)
const USER_REPOSITORY = container.resolve(UserRepository)
const state = reactive({
  user: new UserProfile(),
  passwordUpdate: new PasswordUpdate()
})

onBeforeMount(() => {
  state.user = PROFILE_REPOSITORY.getProfile() || new UserProfile()

})

// --- State (각 폼 별로 데이터 분리) ---
const loading = reactive({
  profile: false,
  password: false,
  address: false
})

// 1. 프로필 정보
const profileFormRef = ref()
const profileForm = reactive({
  email: 'dev_java@company.com',
  name: '김자바',
  phone: '010-1234-5678'
})

const profileRules = {
  name: [{required: true, message: '이름을 입력하세요', trigger: 'blur'}],
  phone: [{required: true, message: '연락처를 입력하세요', trigger: 'blur'}]
}

const pwdRules = {
  beforePassword: [{required: true, message: '현재 비밀번호를 입력하세요', trigger: 'blur'}],
  password: [{required: true, message: '새 비밀번호를 입력하세요', trigger: 'blur'}],
  passwordConfirm: [{required: true, message: '비밀번호 확인을 입력하세요', trigger: 'blur'}]
}

// 3. 배송지 정보
const addressList = ref([
  {
    recipient: '김자바',
    zipCode: '13529',
    roadAddress: '판교역로 166',
    detailAddress: 'A동',
    phone: '010-1234-5678',
    isDefault: true
  },
  {
    recipient: '회사',
    zipCode: '04524',
    roadAddress: '세종대로 110',
    detailAddress: '8층',
    phone: '010-9876-5432',
    isDefault: false
  }
])
const dialogVisible = ref(false)
const newAddr = reactive({
  recipient: '',
  zipCode: '',
  roadAddress: '',
  detailAddress: '',
  phone: '',
  isDefault: false
})


// 기본 정보 저장
async function saveProfile() {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate((valid: boolean) => {
    if (valid) {
      loading.profile = true
      // API Call Simulation
      setTimeout(() => {
        loading.profile = false
        ElMessage.success('기본 정보가 저장되었습니다.')
      }, 800)
    }
  })
}

//  비밀번호 변경
function savePassword() {
  if (!state.passwordUpdate.password) return
  if (state.passwordUpdate.password !== state.passwordUpdate.passwordConfirm) {
    ElMessage.error('비밀번호가 일치하지 않습니다.')
    return
  }

  USER_REPOSITORY.updatePassword(state.passwordUpdate)
    .then(value => {
      console.log('value')
      console.log(value.result)
      ElMessage.success('비밀번호가 변경되었습니다.')
      state.passwordUpdate.password = '';
      state.passwordUpdate.passwordConfirm = '';
      state.passwordUpdate.beforePassword = '';
    })
    .catch(reason => {
      console.log("reason") //SIGNUP_PASSWORD_CONFIRM_FAIL
      console.log(reason)
      if (reason.errorCode == 'SIGNUP_PASSWORD_CONFIRM_FAIL') {
        ElMessage.success('비밀번호를 확인해주세요.')
      }
    })

}

// [로직 3-A] 배송지 "기본으로 설정" (즉시 반영)
function setAsDefault(index: number) {
  // API Call: PATCH /address/{id}/default
  addressList.value.forEach(a => a.isDefault = false)
  addressList.value[index].isDefault = true

  // UI 정렬 (선택된 것을 위로)
  const selected = addressList.value.splice(index, 1)[0]
  addressList.value.unshift(selected)

  ElMessage.success('기본 배송지가 변경되었습니다.')
}

// [로직 3-B] 배송지 "삭제" (즉시 반영)
function removeAddress(index: number) {
  ElMessageBox.confirm('삭제하시겠습니까?', '경고', {type: 'warning'})
    .then(() => {
      // API Call: DELETE /address/{id}
      addressList.value.splice(index, 1)
      ElMessage.success('삭제되었습니다.')
    })
}

// [로직 3-C] 배송지 "추가" (모달에서 저장 시)
function saveNewAddress() {
  if (!newAddr.zipCode) return ElMessage.warning('주소를 입력하세요')

  loading.address = true
  setTimeout(() => {
    // API Call: POST /address
    const item = {...newAddr}
    if (item.isDefault) {
      addressList.value.forEach(a => a.isDefault = false)
      addressList.value.unshift(item)
    } else {
      addressList.value.push(item)
    }

    loading.address = false
    dialogVisible.value = false
    ElMessage.success('배송지가 추가되었습니다.')
  }, 500)
}

// Utils
function formatPhone(v: string) {
  return v.replace(/[^0-9]/g, '').replace(/^(\d{2,3})(\d{3,4})(\d{4})$/, `$1-$2-$3`)
}

function goBack() {
  router.push('/profile')
}

function openAddressModal() {
  Object.assign(newAddr, {
    recipient: '',
    zipCode: '',
    roadAddress: '',
    detailAddress: '',
    phone: '',
    isDefault: false
  });
  dialogVisible.value = true
}

function mockAddressSearch() {
  newAddr.zipCode = '12345';
  newAddr.roadAddress = '테스트 로드 123';
}

const pwdError = computed(() => (state.passwordUpdate.beforePassword
  && state.passwordUpdate.password !== state.passwordUpdate.passwordConfirm) ? '새로운 비밀번호를 동일하게 입력해주세요' : '')

</script>

<style scoped>
.edit-container {
  max-width: 720px;
  margin: 0 auto;
  padding-bottom: 40px;
  color: #333;
}

.page-header {
  padding: 20px 0;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 카드 스타일 */
.section-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 700;
  font-size: 16px;
}

/* 배송지 아이템 스타일 */
.address-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.address-item.active-card {
  border-color: #67c23a;
  background-color: #f0f9eb;
}

.addr-top-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.addr-name {
  font-weight: 700;
}

.addr-text {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.addr-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.error-text {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  display: block;
}
</style>
