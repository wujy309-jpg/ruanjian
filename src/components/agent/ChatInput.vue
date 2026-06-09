<template>
  <div class="chat-input">
    <el-input
      v-model="inputText"
      type="textarea"
      :rows="2"
      placeholder="输入你的学习需求，例如：我想学Java..."
      resize="none"
      @keydown.enter.exact.prevent="handleSend"
      :disabled="disabled"
    />
    <el-button
      type="primary"
      :icon="Promotion"
      @click="handleSend"
      :disabled="disabled || !inputText.trim()"
      :loading="loading"
    >
      发送
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Promotion } from '@element-plus/icons-vue'

const props = defineProps({
  disabled: { type: Boolean, default: false },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['send'])
const inputText = ref('')

function handleSend() {
  const text = inputText.value.trim()
  if (!text || props.disabled) return
  emit('send', text)
  inputText.value = ''
}
</script>

<style scoped>
.chat-input {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  padding: 16px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
}
.chat-input :deep(.el-textarea__inner) {
  border-radius: 8px;
}
</style>
