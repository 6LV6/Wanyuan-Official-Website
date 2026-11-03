/**
 * Access Token 只放在内存里：
 * - 刷新页面就没了，需要靠 httpOnly 的 Refresh Token 静默续期
 * - XSS 即使能执行脚本，也读不到 Refresh Token（JS 无权访问 httpOnly Cookie）
 */
let accessToken: string | null = null

export function getAccessToken(): string | null {
  return accessToken
}

export function setAccessToken(token: string | null): void {
  accessToken = token
}
