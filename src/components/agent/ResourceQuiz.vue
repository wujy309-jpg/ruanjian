<template>
  <div class="resource-quiz">
    <div v-if="questions.length === 0" class="quiz-empty">
      <el-empty description="暂无题目" :image-size="80" />
    </div>
    <div v-else>
      <div class="quiz-progress">
        <span>第 {{ currentIndex + 1 }} / {{ questions.length }} 题</span>
        <el-progress
          :percentage="Math.round(((currentIndex + 1) / questions.length) * 100)"
          :stroke-width="6"
          :show-text="false"
        />
      </div>
      <div class="quiz-card" v-if="currentQuestion">
        <div class="question-header">
          <el-tag size="small" :type="diffColor(currentQuestion.difficulty)">
            {{ currentQuestion.difficulty || 'MEDIUM' }}
          </el-tag>
          <span class="question-type">{{ typeLabel(currentQuestion.type) }}</span>
        </div>
        <div class="question-content">{{ currentQuestion.content || currentQuestion.title }}</div>
        <div class="question-options" v-if="currentQuestion.options && currentQuestion.options.length">
          <div
            v-for="(opt, idx) in currentQuestion.options"
            :key="idx"
            class="option-item"
            :class="{
              selected: selectedAnswer === idx,
              correct: showResult && opt.isCorrect,
              wrong: showResult && selectedAnswer === idx && !opt.isCorrect
            }"
            @click="!showResult && selectOption(idx)"
          >
            <span class="option-label">{{ String.fromCharCode(65 + idx) }}</span>
            <span class="option-text">{{ opt.content }}</span>
            <el-icon v-if="showResult && opt.isCorrect" class="option-icon correct"><Check /></el-icon>
            <el-icon v-if="showResult && selectedAnswer === idx && !opt.isCorrect" class="option-icon wrong"><Close /></el-icon>
          </div>
        </div>
        <div class="question-judge" v-else-if="isJudgeQuestion">
          <div class="judge-options">
            <div
              class="judge-option"
              :class="{
                selected: selectedAnswer === 'true',
                correct: showResult && currentQuestion.answer?.isCorrect === true,
                wrong: showResult && selectedAnswer === 'true' && currentQuestion.answer?.isCorrect !== true
              }"
              @click="!showResult && selectJudgeOption('true')"
            >
              <el-icon :size="24"><Check /></el-icon>
              <span>正确</span>
            </div>
            <div
              class="judge-option"
              :class="{
                selected: selectedAnswer === 'false',
                correct: showResult && currentQuestion.answer?.isCorrect === false,
                wrong: showResult && selectedAnswer === 'false' && currentQuestion.answer?.isCorrect !== false
              }"
              @click="!showResult && selectJudgeOption('false')"
            >
              <el-icon :size="24"><Close /></el-icon>
              <span>错误</span>
            </div>
          </div>
        </div>
        <div class="question-short" v-else-if="isShortAnswer">
          <el-input
            v-model="shortAnswerText"
            type="textarea"
            :rows="3"
            placeholder="请输入你的答案..."
            :disabled="showResult"
          />
        </div>
        <div class="question-analysis" v-if="showResult && currentQuestion.analysis">
          <div class="analysis-label">解析：</div>
          <div class="analysis-text">{{ currentQuestion.analysis }}</div>
        </div>
        <div class="question-answer" v-if="showResult && (isJudgeQuestion || isShortAnswer) && currentQuestion.answer">
          <div class="answer-label">参考答案：</div>
          <div class="answer-text">{{ currentQuestion.answer.answer || currentQuestion.answer }}</div>
        </div>
        <div class="quiz-actions">
          <el-button v-if="!showResult" type="primary" @click="submitAnswer" :disabled="!canSubmit">
            提交答案
          </el-button>
          <el-button v-else @click="nextQuestion">
            {{ currentIndex < questions.length - 1 ? '下一题' : '完成' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Check, Close } from '@element-plus/icons-vue'

const props = defineProps({
  content: { type: String, default: '' }
})

const currentIndex = ref(0)
const selectedAnswer = ref(null)
const shortAnswerText = ref('')
const showResult = ref(false)

const questions = computed(() => {
  if (!props.content) return []
  try {
    const parsed = JSON.parse(props.content)
    if (Array.isArray(parsed)) return parsed
    if (parsed.questions) return parsed.questions
    return []
  } catch {
    return []
  }
})

const currentQuestion = computed(() => questions.value[currentIndex.value] || null)

const isChoiceQuestion = computed(() => {
  const t = currentQuestion.value?.type
  return t === 'CHOICE' || (!t && currentQuestion.value?.options?.length)
})

const isJudgeQuestion = computed(() => {
  return currentQuestion.value?.type === 'JUDGE'
})

const isShortAnswer = computed(() => {
  const t = currentQuestion.value?.type
  return t === 'SHORT_ANSWER' || t === 'TEXT' || (!isChoiceQuestion.value && !isJudgeQuestion.value)
})

const canSubmit = computed(() => {
  if (isChoiceQuestion.value) return selectedAnswer.value !== null
  if (isJudgeQuestion.value) return selectedAnswer.value !== null
  return shortAnswerText.value.trim().length > 0
})

function selectOption(idx) {
  if (showResult.value) return
  selectedAnswer.value = idx
}

function selectJudgeOption(val) {
  if (showResult.value) return
  selectedAnswer.value = val
}

function submitAnswer() {
  showResult.value = true
}

function nextQuestion() {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    selectedAnswer.value = null
    shortAnswerText.value = ''
    showResult.value = false
  }
}

function typeLabel(type) {
  const map = { CHOICE: '选择题', JUDGE: '判断题', SHORT_ANSWER: '简答题', TEXT: '文本题' }
  return map[type] || type || '选择题'
}

function diffColor(diff) {
  const map = { EASY: 'success', MEDIUM: 'warning', HARD: 'danger' }
  return map[diff] || 'info'
}
</script>

<style scoped>
.resource-quiz {
  padding: 8px 0;
}
.quiz-progress {
  margin-bottom: 16px;
}
.quiz-progress span {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.quiz-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
}
.question-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.question-type {
  font-size: 12px;
  color: #909399;
}
.question-content {
  font-size: 15px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 16px;
}
.question-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}
.option-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.option-item:hover:not(.correct):not(.wrong) {
  border-color: #409EFF;
  background: #ecf5ff;
}
.option-item.selected {
  border-color: #409EFF;
  background: #ecf5ff;
}
.option-item.correct {
  border-color: #67C23A;
  background: #f0f9eb;
}
.option-item.wrong {
  border-color: #F56C6C;
  background: #fef0f0;
}
.option-label {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #f0f2f5;
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  flex-shrink: 0;
}
.option-item.correct .option-label {
  background: #67C23A;
  color: #fff;
}
.option-item.wrong .option-label {
  background: #F56C6C;
  color: #fff;
}
.option-text {
  flex: 1;
  font-size: 14px;
  color: #303133;
}
.option-icon {
  font-size: 18px;
}
.option-icon.correct { color: #67C23A; }
.option-icon.wrong { color: #F56C6C; }
.question-judge {
  margin-bottom: 16px;
}
.judge-options {
  display: flex;
  gap: 16px;
}
.judge-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 15px;
  color: #606266;
}
.judge-option:hover:not(.correct):not(.wrong) {
  border-color: #409EFF;
  background: #ecf5ff;
}
.judge-option.selected {
  border-color: #409EFF;
  background: #ecf5ff;
}
.judge-option.correct {
  border-color: #67C23A;
  background: #f0f9eb;
  color: #67C23A;
}
.judge-option.wrong {
  border-color: #F56C6C;
  background: #fef0f0;
  color: #F56C6C;
}
.question-short {
  margin-bottom: 16px;
}
.question-answer {
  margin-bottom: 16px;
  padding: 12px;
  background: #f0f9eb;
  border-radius: 8px;
}
.answer-label {
  font-size: 13px;
  color: #67C23A;
  font-weight: 600;
  margin-bottom: 4px;
}
.answer-text {
  font-size: 14px;
  color: #303133;
}
.question-analysis {
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}
.analysis-label {
  font-size: 13px;
  color: #909399;
  font-weight: 600;
  margin-bottom: 4px;
}
.analysis-text {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}
.quiz-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>
