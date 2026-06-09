<template>
  <div class="home-page">
    <div class="navbar">
      <div class="logo">
        <img src="../assets/logo.svg" alt="logo" class="logo-img" />
        <span class="title">AI云学智训平台</span>
      </div>
      <div class="nav-actions">
        <el-button @click="goToVideos" icon="VideoPlay">视频学习</el-button>
      </div>
    </div>

    <div class="main-container">
      <div class="hero-section">
        <h1>基于多智能体与大模型的高校个性化学习资源生成智能体</h1>
        <p class="subtitle">通过对话了解你的学习水平，自动规划学习路径，生成个性化学习资料</p>
      </div>

      <div class="feature-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="feature-card ai-entry" @click="goToAgentChat">
              <el-icon :size="48" color="#409EFF"><Cpu /></el-icon>
              <h3>AI 学习助手</h3>
              <p>个性化学习画像 · 智能路径规划 · 多模态资源</p>
              <el-tag type="danger" size="small" effect="dark">NEW</el-tag>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="feature-card" @click="goToAgentChat">
              <el-icon :size="48" color="#409EFF"><ChatDotRound /></el-icon>
              <h3>智能对话</h3>
              <p>通过自然对话了解你的知识水平和学习偏好</p>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="feature-card" @click="goToLearningPaths">
              <el-icon :size="48" color="#67C23A"><Guide /></el-icon>
              <h3>学习路径</h3>
              <p>多智能体协同规划个性化学习路径</p>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="feature-card" @click="goToResources">
              <el-icon :size="48" color="#E6A23C"><Document /></el-icon>
              <h3>资源生成</h3>
              <p>自动生成文档、思维导图、练习题等学习资源</p>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="video-section" v-if="latestVideos.length > 0">
        <div class="section-header">
          <h2>最新视频</h2>
          <el-button text @click="goToVideos">查看更多 →</el-button>
        </div>
        <el-row :gutter="20">
          <el-col :span="6" v-for="video in latestVideos" :key="video.id">
            <div class="video-card" @click="goToVideoDetail(video.id)">
              <img :src="video.coverUrl || ''" class="video-cover" />
              <h4>{{ video.title }}</h4>
              <p>{{ video.description?.substring(0, 50) || '' }}</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Cpu } from "@element-plus/icons-vue";
import request from "../utils/request";

const router = useRouter();
const latestVideos = ref([]);

const getLatestVideos = async () => {
  try {
    const res = await request.get("/api/videos/latest");
    latestVideos.value = (res.data || []).slice(0, 4);
  } catch (error) {
    console.error("获取最新视频失败：", error);
  }
};

const goToVideos = () => router.push("/videos");
const goToVideoDetail = (id) => router.push(`/videos/${id}`);
const goToAgentChat = () => router.push("/agent-chat");
const goToLearningPaths = () => router.push("/learning-paths");
const goToResources = () => router.push("/resources");

onMounted(() => {
  getLatestVideos();
});
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 64px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}
.logo-img {
  width: 36px;
  height: 36px;
}
.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}
.hero-section {
  text-align: center;
  padding: 60px 0 40px;
}
.hero-section h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 12px;
}
.subtitle {
  font-size: 16px;
  color: #909399;
}
.feature-section {
  margin: 40px 0;
}
.feature-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: transform 0.2s;
  position: relative;
  cursor: pointer;
}
.feature-card.ai-entry {
  cursor: pointer;
  border: 2px solid #d9ecff;
  background: linear-gradient(135deg, #ecf5ff 0%, #fff 100%);
}
.feature-card.ai-entry:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.15);
}
.feature-card.ai-entry .el-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}
.feature-card:hover {
  transform: translateY(-4px);
}
.feature-card h3 {
  margin: 16px 0 8px;
  color: #303133;
}
.feature-card p {
  color: #909399;
  font-size: 14px;
}
.video-section {
  margin-top: 40px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.section-header h2 {
  font-size: 20px;
  color: #303133;
}
.video-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: transform 0.2s;
}
.video-card:hover {
  transform: translateY(-2px);
}
.video-cover {
  width: 100%;
  height: 150px;
  object-fit: cover;
  background: #e4e7ed;
}
.video-card h4 {
  padding: 12px 12px 4px;
  font-size: 14px;
  color: #303133;
}
.video-card p {
  padding: 0 12px 12px;
  font-size: 12px;
  color: #909399;
}
</style>
