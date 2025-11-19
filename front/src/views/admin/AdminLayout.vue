<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-sidebar">
      <h1 class="admin-title">Admin Page</h1>
      <el-menu :default-active="activeMenu" class="admin-menu" router>
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
          <!-- 추후 추가될 메뉴 -->
          <el-menu-item index="/admin/coupons/write">쿠폰 생성</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="categories">
          <template #title>
            <el-icon><Grid /></el-icon>
            <span>카테고리 관리</span>
          </template>
          <!-- 추후 추가될 메뉴 -->
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
import { ShoppingBag, Tickets, Grid } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-sidebar {
  border-right: 1px solid #e4e7ed;
}
.admin-title {
  font-size: 1.5rem;
  text-align: center;
  padding: 20px 0;
  margin: 0;
}
.admin-menu {
  border-right: none;
}
.admin-content {
  padding: 20px;
  background-color: #f9fafb;
}
</style>
