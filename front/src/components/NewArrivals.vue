<template>
  <main class="home-container">
    <!-- 인트로 배너 -->
    <section class="hero">
      <div class="hero-text">
        <h1>유신사</h1>
        <p>멋쟁이 정장, 당신의 일상에 멋짐을.</p>
      </div>
      <img :src="banners[0].desktop" alt="메인 배너" class="hero-image"/>
    </section>

    <!-- 제품 리스트 -->
    <section class="product-list-section">
      <h2 class="section-title">Our Favorites</h2>
      <ul class="product-grid">
        <li v-for="product in products" :key="product.id" class="product-card">
          <div class="product-image-wrapper">
            <a :href="product.link">
              <img :src="product.image" :alt="product.name" class="product-image"/>
              <div class="product-info-overlay">
                <p class="name">{{ product.name }}</p>
                <!--                <p class="price">{{ product.price.toLocaleString() }}원</p>-->
              </div>
              <span v-if="product.badge" :class="['badge', product.badge.toLowerCase()]">
                {{ product.badge }}
              </span>
            </a>
          </div>
        </li>
      </ul>
    </section>

    <!-- 인스타그램 섹션 -->
    <section class="instagram-section">
      <h2 class="section-title">Follow Us on Instagram</h2>
      <p class="insta-desc">인스타에서 만나요!</p>
      <a href="https://instagram.com" target="_blank" class="insta-link"> @@@@ </a>
      <div class="insta-gallery">
        <img v-for="n in 4" :key="n" :src="`/g1.jpg`" alt="인스타 미리보기"/>
        <img
          v-for="n in 4"
          :key="n"
          src="/g1.jpg"
          alt="인스타 미리보기"
        />
      </div>
    </section>
  </main>
</template>

<script lang="ts" setup>
import {ref} from 'vue'

interface Product {
  id: number
  name: string
  image: string
  price: number
  link: string
  badge?: 'NEW' | 'BEST'
}

interface Banner {
  desktop: string
  mobile: string
}

const banners = ref<Banner[]>([
  {
    desktop: '/main.jpg',
    mobile: '/main.jpg',
  },
  {
    desktop: '/main.jpg',
    mobile: '/main.jpg',
  },
])

const products: Product[] = [
  {
    id: 1,
    name: 'Man',
    price: 6000,
    image: '/g1.jpg',
    link: '/product/유기농-달걀/18/category/1/display/2/',
    badge: 'NEW',
  },
  {
    id: 2,
    name: 'Woman',
    price: 12000,
    image: '/g1.jpg',
    link: '/product/유기농-아침-식빵/17/category/1/display/2/',
    badge: 'BEST',
  },
  {
    id: 3,
    name: 'Kids',
    price: 30000,
    image: '/g1.jpg',
    link: '/product/딸기-무스-케이크/16/category/1/display/2/',
  },
]
</script>

<style scoped>
.home-container {
  font-family: 'Pretendard', sans-serif;
}

.hero {
  position: relative;
  text-align: center;
  padding: 60px 20px 40px;
  background: linear-gradient(to bottom, #fff, #f9f9f9);
}

.hero-text h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 12px;
}

.hero-text p {
  font-size: 16px;
  color: #666;
}

.hero-image {
  max-width: 1100px;
  width: 100%;
  max-height: 320px;
  object-fit: cover;
  margin-top: 20px;
  border-radius: 12px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  text-align: center;
}

.product-list-section {
  max-width: 1080px;
  margin: 0 auto;
  padding: 60px 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.product-image-wrapper {
  position: relative;
}

.product-image {
  width: 100%;
  display: block;
  object-fit: cover;
  transition: 0.3s ease;
}

.product-info-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.8);
  text-align: center;
  opacity: 0;
  transform: translateY(100%);
  transition: 0.3s;
  pointer-events: none;
}

.product-card:hover .product-info-overlay {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

.name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.price {
  font-size: 15px;
  font-weight: bold;
  color: #111;
  margin-top: 4px;
}

.badge {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 12px;
  color: #fff;
  padding: 3px 8px;
  border-radius: 3px;
  font-weight: bold;
}

.badge.new {
  background-color: #26c6da;
}

.badge.best {
  background-color: #8bc34a;
}

.instagram-section {
  padding: 60px 20px;
  background: #f9f9f9;
  text-align: center;
}

.insta-desc {
  color: #666;
  margin-bottom: 10px;
}

.insta-link {
  display: inline-block;
  margin-bottom: 24px;
  font-weight: 500;
  color: #26c6da;
  text-decoration: none;
}

.insta-gallery {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.insta-gallery img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}
</style>
