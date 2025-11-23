<template>
  <div class="signup-container">
    <el-card class="signup-card">
      <template #header>
        <h2 class="signup-title">회원가입</h2>
      </template>

      <el-form
        ref="formRef"
        :model="state.signup"
        :rules="rules"
        label-position="top"
      >
        <el-form-item label="이름" prop="name">
          <el-input v-model="state.signup.name" placeholder="이름을 입력하세요" />
        </el-form-item>

        <el-form-item label="이메일" prop="email">
          <el-input v-model="state.signup.email" placeholder="example@email.com" />
        </el-form-item>

        <el-form-item label="비밀번호" prop="password">
          <el-input
            type="password"
            v-model="state.signup.password"
            placeholder="비밀번호"
            show-password
          />
        </el-form-item>

        <el-form-item label="휴대전화" prop="phoneNumber">
          <el-input v-model="state.signup.phoneNumber" placeholder="010-0000-0000" />
        </el-form-item>

        <el-form-item style="margin-top: 30px;">
          <el-button type="primary" class="full-width-btn" @click="doSignup()">
            가입하기
          </el-button>
          <el-button class="full-width-btn mt-2" @click="router.back()">
            취소
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { container } from 'tsyringe'
import Signup from '@/entity/user/Signup' // 새로 만든 클래스
import UserRepository from '@/repository/UserRepository'
import type HttpError from '@/http/HttpError'

const router = useRouter()
const USER_REPOSITORY = container.resolve(UserRepository)
const formRef = ref<FormInstance>()

const state = reactive({
  signup: new Signup(),
})

// 유효성 검사 규칙
const rules = reactive<FormRules>({
  name: [{ required: true, message: '이름을 입력해주세요.', trigger: 'blur' }],
  email: [
    { required: true, message: '이메일을 입력해주세요.', trigger: 'blur' },
    { type: 'email', message: '이메일 형식이 올바르지 않습니다.', trigger: 'blur' }
  ],
  password: [{ required: true, message: '비밀번호를 입력해주세요.', trigger: 'blur' }],
  phoneNumber: [{ required: true, message: '전화번호를 입력해주세요.', trigger: 'blur' }],
})

function doSignup() {
  if (!formRef.value) return

  formRef.value.validate((valid) => {
    if (valid) {
      USER_REPOSITORY.signup(state.signup)
        .then(() => {
          ElMessage.success('회원가입이 완료되었습니다! 로그인해주세요.')
          router.push('/login') // 로그인 페이지로 이동
        })
        .catch((e: HttpError) => {
          ElMessage.error(e.getMessage())
        })
    }
  })
}
</script>

<style scoped>
.signup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px; /* 모바일 등에서 여백 확보 */
}

.signup-card {
  width: 400px;
  border-radius: 8px;
}

.signup-title {
  text-align: center;
  margin: 0;
  color: #333;
}

.full-width-btn {
  width: 100%;
  font-weight: bold;
}

.mt-2 {
  margin-top: 10px;
  margin-left: 0 !important; /* Element Plus 버튼 간격 초기화 */
}
</style>
