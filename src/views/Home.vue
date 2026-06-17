<template>
  <div class="home-page">
    <!-- Header -->
    <header class="header" :class="{ scrolled: isScrolled }">
      <div class="container">
        <div class="header-inner">
          <div class="header-left">
            <div class="logo">
              <div class="logo-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
                  <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
                </svg>
              </div>
              <span>AI 云学智训</span>
            </div>
          </div>
          <div class="header-right">
            <nav class="nav-links">
              <router-link to="/videos">视频资源</router-link>
              <router-link to="/agent-chat">工作台</router-link>
            </nav>
            <router-link to="/agent-chat" class="btn-primary">立即体验</router-link>
          </div>
        </div>
      </div>
    </header>

    <main>
      <!-- Hero Section -->
      <section class="hero">
        <div class="container">
          <div class="hero-inner">
            <div class="hero-content">
              <div class="hero-badge">AI 驱动学习平台</div>
              <h1 class="hero-title">
                智能学习
                <span>因你而变</span>
              </h1>
              <p class="hero-desc">
                基于多智能体与大模型技术，通过对话了解你的学习水平，
                自动规划个性化学习路径，生成专属学习资料。
                无论你是学生还是自学者，AI 云学智训都是你的全能学习助手。
              </p>
              <div class="hero-actions">
                <router-link to="/agent-chat" class="hero-cta">
                  免费开始使用
                  <el-icon><ArrowRight /></el-icon>
                </router-link>
                <router-link to="/videos" class="hero-secondary">
                  浏览视频资源
                </router-link>
              </div>
              <div class="hero-stats">
                <div class="stat-item">
                  <span class="stat-number">3</span>
                  <span class="stat-label">智能 Agent</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">24/7</span>
                  <span class="stat-label">在线服务</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">100%</span>
                  <span class="stat-label">免费使用</span>
                </div>
              </div>
            </div>
            <div class="hero-illustration">
              <div class="illustration-container">
                <div class="illustration-main">
                  <div class="illustration-screen">
                    <div class="screen-header">
                      <span class="screen-dot red"></span>
                      <span class="screen-dot yellow"></span>
                      <span class="screen-dot green"></span>
                    </div>
                    <div class="screen-content">
                      <div class="screen-card">
                        <div class="screen-card-icon icon-green">
                          <el-icon :size="20"><ChatDotRound /></el-icon>
                        </div>
                        <div class="screen-card-text">
                          <h4>画像构建</h4>
                          <p>智能分析学习水平</p>
                        </div>
                      </div>
                      <div class="screen-card">
                        <div class="screen-card-icon icon-green-light">
                          <el-icon :size="20"><Guide /></el-icon>
                        </div>
                        <div class="screen-card-text">
                          <h4>路径规划</h4>
                          <p>个性化学习路径</p>
                        </div>
                      </div>
                      <div class="screen-card">
                        <div class="screen-card-icon icon-green-dark">
                          <el-icon :size="20"><Document /></el-icon>
                        </div>
                        <div class="screen-card-text">
                          <h4>资源生成</h4>
                          <p>自动生成学习资料</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="floating-badge">
                  <div class="badge-icon">
                    <el-icon :size="20"><Cpu /></el-icon>
                  </div>
                  <span>AI 助手在线</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="features" id="features">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">核心功能</h2>
            <p class="section-subtitle">三大智能 Agent 协同工作，为你提供全方位学习支持</p>
          </div>
          <div class="feature-grid">
            <div class="feature-card" v-for="feature in features" :key="feature.id">
              <div class="feature-card-icon">
                <el-icon :size="32"><component :is="iconMap[feature.icon]" /></el-icon>
              </div>
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.desc }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Video Section -->
      <section class="videos-section">
        <div class="container">
          <div class="section-header">
            <div>
              <h2 class="section-title">精选视频</h2>
              <p class="section-subtitle">优质学习视频资源，助力你的学习之旅</p>
            </div>
            <router-link to="/videos" class="view-all-link">
              查看更多
              <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div class="video-grid">
            <div
              v-for="video in latestVideos"
              :key="video.id"
              class="video-card"
              @click="$router.push(`/videos/${video.id}`)"
            >
              <div class="video-cover">
                <img :src="video.coverUrl" :alt="video.title" />
                <div class="video-play">
                  <el-icon :size="36"><VideoPlay /></el-icon>
                </div>
                <span class="video-duration">{{ video.durationText }}</span>
              </div>
              <div class="video-info">
                <h3>{{ video.title }}</h3>
                <p>{{ video.description?.substring(0, 50) || '' }}...</p>
                <div class="video-meta">
                  <span class="video-category">{{ video.categoryName }}</span>
                  <span class="video-views">{{ formatNumber(video.viewCount) }} 次观看</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="cta-section">
        <div class="container">
          <h2>准备好开始智能学习了吗？</h2>
          <p>与 AI 助手对话，获取个性化学习路径和专属资源</p>
          <router-link to="/agent-chat" class="btn-cta">
            免费开始使用
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
      </section>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-grid">
          <div class="footer-brand">
            <div class="logo">
              <div class="logo-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
                  <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
                </svg>
              </div>
              <span style="color: white;">AI 云学智训</span>
            </div>
            <p>基于多智能体与大模型技术的一站式 AI 学习平台，让学习更高效。</p>
          </div>
          <div class="footer-links">
            <h4>主要功能</h4>
            <router-link to="/agent-chat">AI 学习助手</router-link>
            <router-link to="/learning-paths">学习路径</router-link>
            <router-link to="/resources">学习资源</router-link>
            <router-link to="/videos">视频学习</router-link>
          </div>
          <div class="footer-links">
            <h4>帮助支持</h4>
            <a href="#">使用指南</a>
            <a href="#">常见问题</a>
            <a href="#">联系我们</a>
          </div>
          <div class="footer-links">
            <h4>关于我们</h4>
            <a href="#">项目简介</a>
            <a href="#">隐私政策</a>
            <a href="#">服务条款</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2026 AI 云学智训平台 - 基于多智能体与大模型的个性化学习平台</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Search, ArrowRight, Cpu, ChatDotRound, Guide, Document, 
  VideoPlay, Edit, Upload, User, DataLine, Refresh 
} from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const latestVideos = ref([])
const isScrolled = ref(false)

const iconMap = {
  ChatDotRound,
  Guide,
  Document,
  Edit,
  DataLine,
  VideoPlay,
  Cpu,
  User,
  Refresh
}

const features = [
  { id: 1, icon: 'ChatDotRound', title: 'AI 画像构建', desc: '通过对话智能分析你的学习水平和知识掌握情况，构建精准的学习画像' },
  { id: 2, icon: 'Guide', title: 'AI 路径规划', desc: '基于学习画像，为你量身定制个性化学习路径，高效提升学习效果' },
  { id: 3, icon: 'Document', title: 'AI 资源生成', desc: '自动生成学习资料、练习题和思维导图，让学习更加高效' }
]

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

const getLatestVideos = async () => {
  try {
    const res = await request.get('/api/videos/latest')
    latestVideos.value = (res.data || []).slice(0, 4)
  } catch (error) {
    console.error('获取最新视频失败：', error)
  }
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  getLatestVideos()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* ===== Container ===== */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* ===== Header ===== */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(240, 253, 244, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #dcfce7;
  transition: box-shadow 0.3s ease;
}

.header.scrolled {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 72px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo span {
  font-size: 20px;
  font-weight: 700;
  color: #166534;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 28px;
}

.nav-links a {
  text-decoration: none;
  color: #4b5563;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s ease;
}

.nav-links a:hover {
  color: #16a34a;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 42px;
  padding: 0 24px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: white;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  border-radius: 9999px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
}

/* ===== Hero Section ===== */
.hero {
  padding: 140px 0 100px;
  background: linear-gradient(180deg, #f0fdf4 0%, #ffffff 100%);
  overflow: hidden;
}

.hero-inner {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.hero-content {
  max-width: 560px;
}

.hero-badge {
  display: inline-block;
  padding: 8px 16px;
  background: #dcfce7;
  color: #15803d;
  font-size: 14px;
  font-weight: 600;
  border-radius: 9999px;
  margin-bottom: 24px;
}

.hero-title {
  font-size: 56px;
  font-weight: 800;
  line-height: 1.15;
  color: #1a1a2e;
  margin-bottom: 24px;
  letter-spacing: -1px;
}

.hero-title span {
  display: block;
  background: linear-gradient(135deg, #22c55e 0%, #15803d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 17px;
  color: #4b5563;
  line-height: 1.8;
  margin-bottom: 36px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 48px;
}

.hero-cta {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  height: 54px;
  padding: 0 36px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  border-radius: 9999px;
  transition: all 0.2s ease;
}

.hero-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(34, 197, 94, 0.4);
}

.hero-secondary {
  display: inline-flex;
  align-items: center;
  height: 54px;
  padding: 0 32px;
  background: #ffffff;
  color: #15803d;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  border-radius: 9999px;
  border: 2px solid #bbf7d0;
  transition: all 0.2s ease;
}

.hero-secondary:hover {
  border-color: #4ade80;
  background: #f0fdf4;
}

.hero-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: #16a34a;
}

.stat-label {
  font-size: 14px;
  color: #9ca3af;
}

/* Hero Illustration */
.hero-illustration {
  display: flex;
  justify-content: center;
}

.illustration-container {
  position: relative;
  width: 100%;
  max-width: 480px;
}

.illustration-main {
  background: #ffffff;
  border-radius: 24px;
  padding: 32px;
  border: 1px solid #dcfce7;
  box-shadow: 0 20px 40px rgba(34, 197, 94, 0.1);
}

.illustration-screen {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #e5e7eb;
}

.screen-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
}

.screen-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.screen-dot.red { background: #ef4444; }
.screen-dot.yellow { background: #f59e0b; }
.screen-dot.green { background: #22c55e; }

.screen-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.screen-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: #f0fdf4;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.screen-card:hover {
  background: #dcfce7;
}

.screen-card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.screen-card-icon.icon-green { background: linear-gradient(135deg, #22c55e, #16a34a); }
.screen-card-icon.icon-green-light { background: linear-gradient(135deg, #4ade80, #22c55e); }
.screen-card-icon.icon-green-dark { background: linear-gradient(135deg, #16a34a, #15803d); }

.screen-card-text h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 2px;
  color: #1a1a2e;
}

.screen-card-text p {
  font-size: 13px;
  color: #9ca3af;
}

.floating-badge {
  position: absolute;
  bottom: -16px;
  right: -16px;
  background: white;
  border-radius: 16px;
  padding: 14px 18px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 10px;
  animation: float 3s ease-in-out infinite;
}

.badge-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.floating-badge span:last-child {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a2e;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* ===== Features Section ===== */
.features {
  padding: 100px 0;
  background: #ffffff;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.section-subtitle {
  font-size: 18px;
  color: #4b5563;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.feature-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 40px 32px;
  text-align: center;
  border: 1px solid #dcfce7;
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(34, 197, 94, 0.15);
  border-color: #86efac;
}

.feature-card-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.feature-card p {
  font-size: 15px;
  color: #4b5563;
  line-height: 1.6;
}

/* ===== Video Section ===== */
.videos-section {
  padding: 100px 0;
  background: #f0fdf4;
}

.videos-section .section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 48px;
  text-align: left;
}

.view-all-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #16a34a;
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s ease;
}

.view-all-link:hover {
  color: #15803d;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.video-card {
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #dcfce7;
  transition: all 0.3s ease;
  cursor: pointer;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(34, 197, 94, 0.15);
}

.video-cover {
  position: relative;
  height: 160px;
  background: #dcfce7;
  overflow: hidden;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-play {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #16a34a;
  opacity: 0;
  transition: all 0.3s ease;
}

.video-card:hover .video-play {
  opacity: 1;
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 4px 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  font-size: 12px;
  font-weight: 500;
  border-radius: 8px;
}

.video-info {
  padding: 20px;
}

.video-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-info p {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 12px;
  line-height: 1.5;
}

.video-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
}

.video-category {
  padding: 4px 10px;
  background: #dcfce7;
  color: #15803d;
  border-radius: 9999px;
  font-weight: 500;
}

.video-views {
  color: #9ca3af;
}

/* ===== CTA Section ===== */
.cta-section {
  padding: 100px 0;
  background: linear-gradient(135deg, #16a34a 0%, #14532d 100%);
  text-align: center;
}

.cta-section h2 {
  font-size: 40px;
  font-weight: 700;
  color: white;
  margin-bottom: 20px;
}

.cta-section p {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.btn-cta {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  height: 56px;
  padding: 0 40px;
  background: white;
  color: #15803d;
  font-size: 17px;
  font-weight: 600;
  text-decoration: none;
  border-radius: 9999px;
  transition: all 0.2s ease;
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 30px rgba(0, 0, 0, 0.2);
}

/* ===== Footer ===== */
.footer {
  background: #14532d;
  color: white;
  padding: 60px 0 40px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 60px;
  margin-bottom: 48px;
}

.footer-brand p {
  font-size: 14px;
  color: #86efac;
  line-height: 1.7;
  margin-top: 16px;
}

.footer-links h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #bbf7d0;
}

.footer-links a {
  display: block;
  font-size: 14px;
  color: #86efac;
  text-decoration: none;
  margin-bottom: 12px;
  transition: color 0.2s ease;
}

.footer-links a:hover {
  color: white;
}

.footer-bottom {
  border-top: 1px solid #166534;
  padding-top: 32px;
  text-align: center;
}

.footer-bottom p {
  font-size: 13px;
  color: #4ade80;
}

/* ===== Responsive ===== */
@media (max-width: 1024px) {
  .hero-inner {
    grid-template-columns: 1fr;
    gap: 48px;
  }
  
  .hero-content {
    text-align: center;
    max-width: 100%;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .hero-stats {
    justify-content: center;
  }
  
  .hero-title {
    font-size: 44px;
  }
  
  .feature-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .footer-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 40px;
  }
}

@media (max-width: 768px) {
  .header-inner {
    height: 64px;
  }
  
  .nav-links {
    display: none;
  }
  
  .hero {
    padding: 100px 0 60px;
  }
  
  .hero-title {
    font-size: 36px;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .hero-stats {
    gap: 24px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .feature-grid {
    grid-template-columns: 1fr;
  }
  
  .video-grid {
    grid-template-columns: 1fr;
  }
  
  .cta-section h2 {
    font-size: 28px;
  }
  
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }
  
  .floating-badge {
    display: none;
  }
  
  .videos-section .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
