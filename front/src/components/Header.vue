<template>
  <header class="header-wrapper">
    <nav class="header-nav">

      <div class="nav-left">
        <router-link to="/" class="logo">
          <el-icon><Umbrella /></el-icon>
          <span class="brand">유신사</span>
        </router-link>

        <router-link to="/products" class="nav-link">Man</router-link>
        <router-link to="/products" class="nav-link">Woman</router-link>
        <router-link to="/products" class="nav-link">Kids</router-link>
      </div>

      <div class="nav-right">
        <template v-if="state.profile">
          <el-dropdown>
            <span class="user-menu">{{ state.profile.nickname }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/cart')"> 장바구니 </el-dropdown-item>
                <el-dropdown-item @click="router.push('/profile')">마이페이지</el-dropdown-item>
                <el-dropdown-item divided @click="logout">로그아웃</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button size="small" @click="router.push('/login')">로그인</el-button>
          <el-button size="small" @click="router.push('/signup')">회원가입</el-button>
        </template>
      </div>
    </nav>
  </header>
</template>

<script lang="ts" setup>
import { onBeforeMount, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { container } from 'tsyringe'
import type UserProfile from '@/entity/user/UserProfile'
import UserRepository from '@/repository/UserRepository'
import ProfileRepository from '@/repository/ProfileRepository'
import { Umbrella } from '@element-plus/icons-vue' // 사용 안 하는 아이콘 제거

const USER_REPOSITORY = container.resolve(UserRepository)
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)
const router = useRouter()

const state = reactive<{ profile: UserProfile | null }>({ profile: null })

onBeforeMount(() => {
  USER_REPOSITORY.getProfile().then((profile) => {
    PROFILE_REPOSITORY.setProfile(profile)
    state.profile = profile
  })
})

function logout() {
  state.profile = null
  router.push('/')
}
</script>

<style scoped>
.header-wrapper {
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 12px 16px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-nav {
  max-width: 1080px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  /* [핵심] 자식 요소(nav-left, nav-right)를 양 끝으로 밀어냅니다.
     이제 자식은 3개가 아니라 2개이므로, 왼쪽 덩어리와 오른쪽 덩어리로 나뉩니다.
  */
  justify-content: space-between;
  font-family: 'Pretendard', sans-serif;
}

/* [추가] 왼쪽 그룹 스타일 */
.nav-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  text-decoration: none;
}

.nav-link {
  /* 로고와 Shop 사이의 간격 */
  margin-left: 24px;
  font-size: 15px;
  color: #444;
  text-decoration: none;
  font-weight: 500; /* Shop 글씨가 너무 얇으면 살짝 두껍게 */
}

.nav-link:hover {
  color: #000; /* 호버 시 좀 더 진하게 */
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-menu {
  font-size: 14px;
  color: #333;
  cursor: pointer;
}
</style>
