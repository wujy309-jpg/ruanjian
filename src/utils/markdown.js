import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const md = new MarkdownIt({
  html: false,
  breaks: true,
  linkify: true,
  highlight(str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (_) {}
    }
    return md.utils.escapeHtml(str)
  }
})

export function renderMarkdown(text) {
  if (!text) return ''
  return md.render(text)
}

export function extractMarkdownText(rawContent) {
  if (!rawContent) return ''
  try {
    const parsed = JSON.parse(rawContent)
    return parsed.markdown || parsed.text || rawContent
  } catch {
    return rawContent
  }
}

export function injectHeadingIds(text) {
  return text.replace(/^(#{1,3})\s+(.+)$/gm, (match, hashes, title) => {
    const id = 'heading-' + title.replace(/\s+/g, '-').replace(/[^\w\u4e00-\u9fff-]/g, '')
    return `${hashes} <span id="${id}">${title}</span>`
  })
}

export function extractToc(text) {
  const headings = []
  const regex = /^(#{1,3})\s+(.+)$/gm
  let match
  while ((match = regex.exec(text)) !== null) {
    headings.push({
      level: match[1].length,
      text: match[2],
      id: 'heading-' + match[2].replace(/\s+/g, '-').replace(/[^\w\u4e00-\u9fff-]/g, '')
    })
  }
  return headings
}
