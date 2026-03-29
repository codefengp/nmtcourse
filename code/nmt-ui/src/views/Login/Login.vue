<template>
  <div :class="prefixCls" class="login-page">
    <div class="login-shell">
      <div :class="`${prefixCls}__left login-aside`">
        <!-- 装饰圆环 -->
        <div class="deco-ring deco-ring--tr"></div>
        <div class="deco-ring deco-ring--bl"></div>

        <!-- 顶部品牌 -->
        <div class="brand-wrap">
          <img alt="系统 Logo" class="brand-logo" src="@/assets/svgs/logo-edu.svg" />
          <div class="brand-text">
            <p class="brand-label">工程教育认证平台</p>
            <h1 class="brand-title">{{ systemTitle }}</h1>
          </div>
        </div>

        <!-- 中部英雄区 -->
        <div class="aside-hero">
          <div class="hero-badge">OBE · 工程教育认证</div>
          <h2 class="hero-title">课程目标<br />达成度分析</h2>
          <p class="hero-sub">量化评价 · 科学诊断 · 持续改进</p>

          <div class="feature-grid">
            <div class="feature-card">
              <div class="feature-icon">◎</div>
              <div class="feature-name">指标关联</div>
              <div class="feature-desc">目标与毕业要求映射</div>
            </div>
            <div class="feature-card">
              <div class="feature-icon">◈</div>
              <div class="feature-name">权重计算</div>
              <div class="feature-desc">分项成绩量化配比</div>
            </div>
            <div class="feature-card">
              <div class="feature-icon">◉</div>
              <div class="feature-name">达成分析</div>
              <div class="feature-desc">课程目标量化评价</div>
            </div>
          </div>
        </div>

        <!-- 底部横排流程 -->
        <div class="aside-footer">
          <div class="step-item">
            <span class="step-num">01</span>
            <span class="step-text">关联指标点</span>
          </div>
          <div class="step-arrow">→</div>
          <div class="step-item">
            <span class="step-num">02</span>
            <span class="step-text">配置权重</span>
          </div>
          <div class="step-arrow">→</div>
          <div class="step-item">
            <span class="step-num">03</span>
            <span class="step-text">达成分析</span>
          </div>
          <div class="step-arrow">→</div>
          <div class="step-item">
            <span class="step-num">04</span>
            <span class="step-text">持续改进</span>
          </div>
        </div>
      </div>

      <div class="login-main">
        <div class="main-toolbar">
          <div class="mobile-brand">
            <img alt="系统 Logo" class="mobile-logo" src="@/assets/svgs/logo-edu.svg" />
            <span class="mobile-title">{{ systemTitle }}</span>
          </div>
          <div class="toolbar-actions">
            <ThemeSwitch />
            <LocaleDropdown />
          </div>
        </div>

        <Transition appear enter-active-class="animate__animated animate__bounceInRight">
          <div class="form-area">
            <div class="form-card">
              <div class="form-header">
                <h3 class="form-title">账号登录</h3>
                <p class="form-subtitle">{{ systemTitle }}</p>
              </div>

              <LoginForm class="form-panel" />
              <MobileForm class="form-panel" />
              <QrCodeForm class="form-panel" />
              <RegisterForm class="form-panel" />
              <SSOLoginVue class="form-panel" />
              <ForgetPasswordForm class="form-panel" />
            </div>
          </div>
        </Transition>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useDesign } from '@/hooks/web/useDesign'
import { ThemeSwitch } from '@/layout/components/ThemeSwitch'
import { LocaleDropdown } from '@/layout/components/LocaleDropdown'

import {
  LoginForm,
  MobileForm,
  QrCodeForm,
  RegisterForm,
  SSOLoginVue,
  ForgetPasswordForm
} from './components'

defineOptions({ name: 'Login' })

const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('login')
const systemTitle = '工程认证课程达成度计算与分析'
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-login;

.#{$prefix-cls} {
  min-height: 100%;
  overflow: auto;
  background:
    radial-gradient(circle at 12% 16%, rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.2), transparent 40%),
    radial-gradient(circle at 86% 84%, rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.14), transparent 34%),
    linear-gradient(140deg, #f3f7ff 0%, #edf4ff 45%, #f8fbff 100%);

  .login-shell {
    min-height: 100vh;
    display: flex;
  }

  // ─── 左侧 aside ───────────────────────────────────────────
  .login-aside {
    position: relative;
    width: 56%;
    color: #fff;
    padding: 48px 52px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    overflow: hidden;
    background: linear-gradient(150deg, #0d2140 0%, #0f2d5a 50%, #163572 100%);

    // 背景网格
    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background-image:
        linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255, 255, 255, 0.035) 1px, transparent 1px);
      background-size: 44px 44px;
      pointer-events: none;
    }

    // OBE 大字水印
    &::after {
      content: 'OBE';
      position: absolute;
      right: -16px;
      bottom: -32px;
      font-size: 200px;
      font-weight: 900;
      color: rgba(255, 255, 255, 0.04);
      letter-spacing: -8px;
      pointer-events: none;
      user-select: none;
      line-height: 1;
    }
  }

  // 装饰圆环
  .deco-ring {
    position: absolute;
    border-radius: 50%;
    pointer-events: none;
    user-select: none;
  }

  .deco-ring--tr {
    top: -80px;
    right: -80px;
    width: 320px;
    height: 320px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: inset 0 0 0 56px rgba(255, 255, 255, 0.03);
  }

  .deco-ring--bl {
    bottom: -50px;
    left: -50px;
    width: 180px;
    height: 180px;
    border: 1px solid rgba(232, 160, 32, 0.3);
  }

  // ─── 顶部品牌 ─────────────────────────────────────────────
  .brand-wrap {
    display: flex;
    align-items: center;
    gap: 14px;
    position: relative;
    z-index: 1;
  }

  .brand-logo {
    width: 52px;
    height: 52px;
    flex-shrink: 0;
  }

  .brand-text {
    min-width: 0;
  }

  .brand-label {
    font-size: 12px;
    letter-spacing: 2.5px;
    margin-bottom: 4px;
    opacity: 0.6;
    text-transform: uppercase;
  }

  .brand-title {
    margin: 0;
    font-size: 20px;
    line-height: 1.4;
    font-weight: 700;
    text-wrap: balance;
  }

  // ─── 中部英雄区 ───────────────────────────────────────────
  .aside-hero {
    position: relative;
    z-index: 1;
  }

  .hero-badge {
    display: inline-block;
    padding: 5px 14px;
    border-radius: 20px;
    background: rgba(232, 160, 32, 0.18);
    border: 1px solid rgba(232, 160, 32, 0.45);
    color: #f0b830;
    font-size: 12.5px;
    letter-spacing: 1.5px;
    font-weight: 600;
    margin-bottom: 20px;
  }

  .hero-title {
    margin: 0 0 16px;
    font-size: 48px;
    font-weight: 800;
    line-height: 1.2;
    letter-spacing: 1px;
    text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  }

  .hero-sub {
    margin: 0 0 32px;
    font-size: 14px;
    opacity: 0.7;
    letter-spacing: 2px;
  }

  .feature-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }

  .feature-card {
    padding: 18px 16px;
    border-radius: 10px;
    background: rgba(255, 255, 255, 0.07);
    border: 1px solid rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(4px);
    transition: background 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.11);
    }
  }

  .feature-icon {
    font-size: 22px;
    margin-bottom: 10px;
    opacity: 0.85;
    color: #f0b830;
  }

  .feature-name {
    font-size: 15px;
    font-weight: 700;
    margin-bottom: 5px;
  }

  .feature-desc {
    font-size: 12px;
    opacity: 0.6;
    line-height: 1.5;
  }

  // ─── 底部横排流程 ──────────────────────────────────────────
  .aside-footer {
    display: flex;
    align-items: center;
    gap: 10px;
    position: relative;
    z-index: 1;
    padding-top: 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.12);
  }

  .step-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    flex: 1;
  }

  .step-num {
    font-size: 18px;
    font-weight: 800;
    color: #e8a020;
    line-height: 1;
  }

  .step-text {
    font-size: 12px;
    opacity: 0.75;
    letter-spacing: 0.5px;
    white-space: nowrap;
  }

  .step-arrow {
    font-size: 16px;
    opacity: 0.35;
    flex-shrink: 0;
    margin-top: -4px;
  }

  // ─── 右侧登录区 ───────────────────────────────────────────
  .login-main {
    width: 44%;
    display: flex;
    flex-direction: column;
    padding: 22px 28px;
  }

  .main-toolbar {
    min-height: 58px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
  }

  .toolbar-actions {
    margin-left: auto;
    display: flex;
    align-items: center;
    gap: 10px;
    color: var(--el-text-color-primary);
  }

  .mobile-brand {
    display: none;
    align-items: center;
    gap: 10px;
    min-width: 0;
  }

  .mobile-logo {
    width: 34px;
    height: 34px;
    flex-shrink: 0;
  }

  .mobile-title {
    font-size: 16px;
    font-weight: 700;
    color: var(--el-text-color-primary);
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .form-area {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px 0 30px;
  }

  .form-card {
    width: 100%;
    max-width: 480px;
    border-radius: 12px;
    border: 1px solid #d8e4f0;
    border-top: 4px solid var(--el-color-primary);
    background: #fff;
    box-shadow: 0 2px 16px rgba(20, 50, 100, 0.07);
    padding: 32px 28px 20px;
  }

  .form-header {
    margin: 0 0 16px;
    text-align: center;
  }

  .form-title {
    margin: 0;
    font-size: 22px;
    font-weight: 700;
    line-height: 1.35;
    color: #0d2140;
  }

  .form-subtitle {
    margin-top: 6px;
    font-size: 12px;
    color: #8a96ae;
    letter-spacing: 0.3px;
  }

  .form-panel {
    width: 100%;
  }

  :deep(.login-form .el-divider__text) {
    background: #fff;
    color: #9aa3b5;
    font-size: 12px;
  }

  :deep(.login-form .el-card) {
    border-radius: 10px;
    border: 1px solid #e4eaf3;
    box-shadow: none;
  }

  :deep(.login-form .el-input__wrapper) {
    border-radius: 8px;
  }

  // ─── 响应式 ───────────────────────────────────────────────
  @media (max-width: 1200px) {
    .login-aside {
      display: none;
    }

    .login-main {
      width: 100%;
      padding: 16px 14px;
    }

    .mobile-brand {
      display: flex;
      flex: 1;
    }

    .form-card {
      max-width: 520px;
      padding-top: 28px;
    }
  }

  @media (max-width: 720px) {
    .main-toolbar {
      align-items: flex-start;
      gap: 10px;
      flex-direction: column;
    }

    .toolbar-actions {
      margin-left: 0;
    }

    .form-area {
      align-items: flex-start;
      padding-top: 8px;
    }

    .form-card {
      border-radius: 10px;
      padding: 22px 16px 14px;
    }

    .form-title {
      font-size: 20px;
    }
  }
}

// ─── Dark Mode ────────────────────────────────────────────
.dark .#{$prefix-cls} {
  background-color: #111827;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 32px 32px;

  .login-aside {
    background: linear-gradient(150deg, #060f1e 0%, #0a1e3d 50%, #0e234f 100%);
  }

  .form-card {
    background: #1e2939;
    border-color: rgba(148, 163, 184, 0.15);
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.3);
  }

  .form-title {
    color: #e2e8f4;
  }

  .form-subtitle {
    color: #6b7a99;
  }

  .mobile-title {
    color: var(--el-text-color-primary);
  }

  :deep(.login-form .el-divider__text) {
    background: #1e2939;
    color: #6b7a99;
  }

  :deep(.login-form .el-card) {
    border-color: rgba(148, 163, 184, 0.15);
    background: #1a2332;
  }
}
</style>
