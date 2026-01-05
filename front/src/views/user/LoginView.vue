<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">로그인</h2>
      </template>

      <el-form
        ref="formRef"
        :model="state.login"
        :rules="rules"
        label-position="top"
        @submit.prevent
      >
        <el-form-item label="이메일" prop="email">
          <el-input
            v-model="state.login.email"
            placeholder="example@email.com"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="비밀번호" prop="password">
          <el-input
            type="password"
            v-model="state.login.password"
            placeholder="비밀번호 입력"
            prefix-icon="Lock"
            show-password
            @keyup.enter="doLogin()"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="full-width-btn" @click="doLogin()">
            로그인
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <el-button link type="primary" @click="moveToSignup">회원가입 하러가기</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import Login from '@/entity/user/account/Login.ts'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type HttpError from '@/http/HttpError'
import UserRepository from '@/repository/user/UserRepository.ts'
import { container } from 'tsyringe'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const USER_REPOSITORY = container.resolve(UserRepository)

const state = reactive({
  login: new Login(),
})

const formRef = ref<FormInstance>()

const rules = reactive<FormRules>({
  email: [
    { required: true, message: '이메일을 입력해주세요.', trigger: 'blur' },
    { type: 'email', message: '올바른 이메일 형식이 아닙니다.', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '비밀번호를 입력해주세요.', trigger: 'blur' }
  ]
})

function doLogin() {
  if (!formRef.value) return

  // 유효성 검사 실행
  formRef.value.validate((valid) => {
    if (valid) {
      USER_REPOSITORY.login(state.login)
        .then(() => {
          ElMessage.success('환영합니다 :)')
          // SPA(Single Page App) 경험을 위해 router 사용 권장.
          // (토큰 처리를 위해 새로고침이 반드시 필요하다면 location.href 유지)
          location.href = '/'
        })
        .catch((e: HttpError) => {
          ElMessage.error(e.getMessage())
        })
    }
  })
}

function moveToSignup() {
  router.push('/signup')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 화면 전체 높이 사용 */
  background-color: #f5f7fa; /* 은은한 배경색 */
}

.login-card {
  width: 400px;
  border-radius: 8px;
}

.login-title {
  text-align: center;
  margin: 0;
  color: #333;
}

.full-width-btn {
  width: 100%;
  font-weight: bold;
  padding: 12px 0;
}

.login-footer {
  text-align: center;
  margin-top: 10px;
}
</style>
