/**
 * 格式化数字（千/万）
 * @param {number} num
 * @returns {string}
 */
export function formatNumber(num) {
  if (!num) return '0'
  if (num < 1000) return num.toString()
  if (num < 10000) return (num / 1000).toFixed(1) + 'k'
  return (num / 10000).toFixed(1) + 'w'
}

/**
 * 格式化时间为相对时间
 * @param {string|Date} timeString
 * @returns {string}
 */
export function formatTime(timeString) {
  if (!timeString) return ''
  const date = new Date(timeString)
  const now = new Date()
  const diff = now - date

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const month = 30 * day

  if (diff < minute) return '刚刚'
  if (diff < hour) return Math.floor(diff / minute) + '分钟前'
  if (diff < day) return Math.floor(diff / hour) + '小时前'
  if (diff < month) return Math.floor(diff / day) + '天前'
  return date.toLocaleDateString('zh-CN')
}

/**
 * 格式化日期
 * @param {string|Date} date
 * @returns {string}
 */
export function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

/**
 * 解析逗号分隔的标签字符串为数组
 * @param {string} tagsString
 * @returns {string[]}
 */
export function getVideoTags(tagsString) {
  if (!tagsString) return []
  return tagsString.split(',').filter(tag => tag.trim())
}
