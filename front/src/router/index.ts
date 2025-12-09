import {createRouter, createWebHistory} from 'vue-router'

import LoginView from '@/views/user/LoginView.vue'
import SignUpView from '@/views/user/SignUpView.vue'
import CartView from '@/views/user/CartView.vue'
import HomeView from '@/views/HomeView.vue'
import ProductListView from '@/views/product/ProductListView.vue'
import ProductDetailsView from '@/views/product/ProductDetailsView.vue'
import ProfileView from '@/views/user/ProfileView.vue'
import AdminCategoryWriteView from '@/views/admin/category/AdminCategoryWriteView.vue'
import PaymentView from '@/views/pay/PaymentView.vue'
import AdminLayout from '@/views/admin/AdminLayout.vue'
import AdminProductWrite from '@/views/admin/product/AdminProductWrite.vue'
import AdminProductList from "@/views/admin/product/AdminProductList.vue";
import AdminUserListView from "@/views/admin/user/AdminUserListView.vue";
import AdminCouponCreateView from "@/views/admin/coupon/AdminCouponCreateView.vue";
import AdminCouponList from "@/views/admin/coupon/AdminCouponList.vue";


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignUpView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/cart',
      name: 'cart',
      component: CartView,
    },
    {
      path: '/profile',
      name: 'my page',
      component: ProfileView,
    },
    {
      path: '/products/:productId',
      name: 'product',
      component: ProductDetailsView,
      props: true,
    },
    {
      path: '/products',
      name: 'product list',
      component: ProductListView,
      props: true,
    },
    {
      path: '/payment',
      name: 'pay view',
      component: PaymentView,
      props: true,
    },
    {
      path: '/category',
      name: 'product category',
      component: AdminCategoryWriteView,
      props: true,
    },

    {
      path: '/admin',
      name: 'admin',
      component: AdminLayout,
      children: [
        {path: 'products/write', name: 'admin-product-write', component: AdminProductWrite},
        {path: 'products', name: 'admin-product-list', component: AdminProductList},
        {path: 'category/write', name: 'admin-category-write', component: AdminCategoryWriteView},
        {path: 'users', name: 'admin-user-list', component: AdminUserListView},
        {path: 'coupons/write', name: 'admin-coupon-write', component: AdminCouponCreateView},
        {path: 'coupons/list', name: 'admin-coupon-list', component: AdminCouponList},


      ],
    },
  ],
})

export default router
