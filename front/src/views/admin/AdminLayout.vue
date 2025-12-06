<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-sidebar">
      <h1 class="admin-title">Admin Page</h1>
      <el-menu :default-active="activeMenu" class="admin-menu" router>

        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>대시보드</span>
        </el-menu-item>

        <el-sub-menu index="orders">
          <template #title>
            <el-icon><List /></el-icon>
            <span>주문 관리</span>
          </template>
          <el-menu-item index="/admin/orders">주문 목록</el-menu-item>
          <el-menu-item index="/admin/orders/delivery">배송 관리</el-menu-item>
          <el-menu-item index="/admin/orders/claims">취소/반품 관리</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="members">
          <template #title>
            <el-icon><User /></el-icon>
            <span>회원 관리</span>
          </template>
          <el-menu-item index="/admin/users">회원 목록</el-menu-item>
<!--          <el-menu-item index="/admin/users/points">적립금 관리</el-menu-item>-->
        </el-sub-menu>

        <el-sub-menu index="products">
          <template #title>
            <el-icon><ShoppingBag /></el-icon>
            <span>상품 관리</span>
          </template>
          <el-menu-item index="/admin/products/write">상품 등록</el-menu-item>
          <el-menu-item index="/admin/products">상품 목록</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="coupons">
          <template #title>
            <el-icon><Tickets /></el-icon>
            <span>쿠폰 관리</span>
          </template>
          <el-menu-item index="/admin/coupons/write">쿠폰 생성</el-menu-item>
<!--          <el-menu-item index="/admin/coupons/issue">쿠폰 발급</el-menu-item>-->
        </el-sub-menu>

        <el-sub-menu index="categories">
          <template #title>
            <el-icon><Grid /></el-icon>
            <span>카테고리 관리</span>
          </template>
          <el-menu-item index="/admin/category/write">카테고리 생성</el-menu-item>
        </el-sub-menu>

      </el-menu>
    </el-aside>

    <el-main class="admin-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
// 아이콘들을 추가로 import 했습니다 (Odometer, List, User)
import {
  ShoppingBag,
  Tickets,
  Grid,
  Odometer,
  List,
  User
} from '@element-plus/icons-vue'

const route = useRoute()

// 현재 URL 경로와 메뉴의 index를 일치시켜 하이라이팅 처리
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-sidebar {
  border-right: 1px solid #e4e7ed;
  background-color: #fff;
}
.admin-title {
  font-size: 1.5rem;
  text-align: center;
  padding: 20px 0;
  margin: 0;
  color: #303133; /* 텍스트 가독성을 위해 색상 지정 */
}
.admin-menu {
  border-right: none;
}
.admin-content {
  padding: 20px;
  background-color: #f9fafb;
}
</style>
