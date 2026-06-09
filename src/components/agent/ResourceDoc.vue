<template>
  <div class="resource-doc">
    <div class="doc-toc" v-if="toc.length > 1">
      <h4>目录</h4>
      <ul>
        <li
          v-for="(item, idx) in toc"
          :key="idx"
          :class="'toc-' + item.level"
          @click="scrollTo(item.id)"
        >
          {{ item.text }}
        </li>
      </ul>
    </div>
    <div class="doc-content" v-html="renderedMarkdown" ref="contentRef"></div>
  </div>
</template>

<script setup>
import { computed, ref, nextTick } from 'vue'
import { renderMarkdown, extractMarkdownText, injectHeadingIds, extractToc } from '@/utils/markdown'

const props = defineProps({
  content: { type: String, default: '' }
})

const contentRef = ref(null)

const markdownText = computed(() => extractMarkdownText(props.content))

const renderedMarkdown = computed(() => {
  const text = injectHeadingIds(markdownText.value)
  return renderMarkdown(text)
})

const toc = computed(() => extractToc(markdownText.value))

function scrollTo(id) {
  nextTick(() => {
    const el = contentRef.value?.querySelector(`#${id}`)
    if (el) el.scrollIntoView({ behavior: 'smooth' })
  })
}
</script>

<style scoped>
.resource-doc {
  display: flex;
  gap: 20px;
}
.doc-toc {
  width: 180px;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}
.doc-toc h4 {
  font-size: 13px;
  color: #303133;
  margin-bottom: 8px;
}
.doc-toc ul {
  list-style: none;
  padding: 0;
}
.doc-toc li {
  font-size: 12px;
  color: #606266;
  padding: 3px 0;
  cursor: pointer;
  transition: color 0.2s;
}
.doc-toc li:hover {
  color: #409EFF;
}
.doc-toc .toc-2 { padding-left: 12px; }
.doc-toc .toc-3 { padding-left: 24px; }
.doc-content {
  flex: 1;
  min-width: 0;
  line-height: 1.8;
  font-size: 14px;
  color: #303133;
}
.doc-content :deep(h1) { font-size: 22px; margin: 20px 0 12px; }
.doc-content :deep(h2) { font-size: 18px; margin: 16px 0 10px; padding-bottom: 6px; border-bottom: 1px solid #e4e7ed; }
.doc-content :deep(h3) { font-size: 15px; margin: 14px 0 8px; }
.doc-content :deep(p) { margin: 8px 0; }
.doc-content :deep(pre) {
  background: #282c34;
  color: #abb2bf;
  border-radius: 8px;
  padding: 14px 18px;
  overflow-x: auto;
  margin: 10px 0;
}
.doc-content :deep(code) {
  font-family: 'Fira Code', monospace;
  font-size: 13px;
}
.doc-content :deep(table) {
  border-collapse: collapse;
  margin: 10px 0;
  width: 100%;
}
.doc-content :deep(th),
.doc-content :deep(td) {
  border: 1px solid #dcdfe6;
  padding: 8px 12px;
  text-align: left;
}
.doc-content :deep(th) {
  background: #f5f7fa;
  font-weight: 600;
}
.doc-content :deep(blockquote) {
  border-left: 3px solid #409EFF;
  padding: 4px 12px;
  margin: 8px 0;
  background: #ecf5ff;
  color: #606266;
}
</style>
