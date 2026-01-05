# 前端配置指南

## 概述

前端应用通过 NGINX 统一网关访问后端服务，所有 API 请求路由到 `http://localhost/api`。

---

## 环境配置

### Vue 3 + Vite 项目

#### 1. 环境变量配置

**.env.development**（本地开发）：

```bash
# API 基础路径（通过 NGINX）
VITE_API_BASE_URL=http://localhost/api

# 或者直连 BFF（调试用）
# VITE_API_BASE_URL=http://localhost:8082/api
```

**.env.production**（生产环境）：

```bash
VITE_API_BASE_URL=https://your-domain.com/api
```

#### 2. Axios 配置

**src/utils/request.js**：

```javascript
import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { code, data, message } = response.data;
    
    // 业务成功
    if (code === '200' || code === 200) {
      return data;
    }
    
    // 业务失败
    ElMessage.error(message || '请求失败');
    return Promise.reject(new Error(message || '请求失败'));
  },
  (error) => {
    console.error('Response error:', error);
    
    // HTTP 错误处理
    if (error.response) {
      const { status, data } = error.response;
      
      switch (status) {
        case 401:
          // 未授权，清除 token 并跳转登录
          localStorage.removeItem('access_token');
          localStorage.removeItem('user_info');
          window.location.href = '/login';
          ElMessage.error('登录已过期，请重新登录');
          break;
        case 403:
          ElMessage.error('没有权限访问');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error('服务器错误');
          break;
        default:
          ElMessage.error(data?.message || '请求失败');
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查网络连接');
    } else {
      ElMessage.error('请求配置错误');
    }
    
    return Promise.reject(error);
  }
);

export default request;
```

#### 3. API 模块封装

**src/api/auth.js**：

```javascript
import request from '@/utils/request';

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/v1/auth/captcha',
    method: 'get',
  });
}

// 登录
export function login(data) {
  return request({
    url: '/v1/auth/login',
    method: 'post',
    data,
  });
}

// 登出
export function logout() {
  return request({
    url: '/v1/auth/logout',
    method: 'post',
  });
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: '/v1/auth/reset-password',
    method: 'post',
    data,
  });
}
```

**src/api/device.js**：

```javascript
import request from '@/utils/request';

// 获取设备列表
export function getDeviceList(params) {
  return request({
    url: '/v1/devices',
    method: 'get',
    params,
  });
}

// 创建设备
export function createDevice(data) {
  return request({
    url: '/v1/devices',
    method: 'post',
    data,
  });
}

// 更新设备
export function updateDevice(id, data) {
  return request({
    url: `/v1/devices/${id}`,
    method: 'put',
    data,
  });
}

// 删除设备
export function deleteDevice(id) {
  return request({
    url: `/v1/devices/${id}`,
    method: 'delete',
  });
}
```

#### 4. 登录页面示例

**src/views/Login.vue**：

```vue
<template>
  <div class="login-container">
    <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules">
      <h2>用户登录</h2>
      
      <el-form-item prop="identifier">
        <el-input
          v-model="loginForm.identifier"
          placeholder="用户名/邮箱/手机号"
          prefix-icon="User"
        />
      </el-form-item>
      
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="密码"
          prefix-icon="Lock"
          show-password
        />
      </el-form-item>
      
      <el-form-item prop="captcha">
        <div class="captcha-wrapper">
          <el-input
            v-model="loginForm.captcha"
            placeholder="验证码"
            prefix-icon="Key"
          />
          <img
            :src="captchaImage"
            alt="验证码"
            class="captcha-image"
            @click="refreshCaptcha"
          />
        </div>
      </el-form-item>
      
      <el-form-item>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleLogin"
          style="width: 100%"
        >
          登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getCaptcha, login } from '@/api/auth';

const router = useRouter();
const loginFormRef = ref(null);
const loading = ref(false);

const loginForm = ref({
  identifier: '',
  password: '',
  captcha: '',
  captchaKey: '',
});

const captchaImage = ref('');

const loginRules = {
  identifier: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' },
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
  ],
};

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const data = await getCaptcha();
    loginForm.value.captchaKey = data.captchaId;
    captchaImage.value = `data:image/png;base64,${data.imageBase64}`;
  } catch (error) {
    ElMessage.error('获取验证码失败');
  }
};

// 登录
const handleLogin = async () => {
  const valid = await loginFormRef.value.validate();
  if (!valid) return;
  
  loading.value = true;
  try {
    const data = await login(loginForm.value);
    
    // 保存 token 和用户信息
    localStorage.setItem('access_token', data.token);
    localStorage.setItem('user_info', JSON.stringify({
      userId: data.userId,
      username: data.username,
    }));
    
    // 检查是否需要重置密码
    if (data.requirePasswordReset) {
      ElMessage.warning('检测到您使用了初始密码，请先修改密码');
      router.push('/reset-password');
      return;
    }
    
    ElMessage.success('登录成功');
    router.push('/');
  } catch (error) {
    refreshCaptcha(); // 刷新验证码
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  refreshCaptcha();
});
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.el-form {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.captcha-wrapper {
  display: flex;
  gap: 10px;
}

.captcha-image {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style>
```

---

## React 项目配置

### 1. 环境变量

**.env.development**：

```bash
REACT_APP_API_BASE_URL=http://localhost/api
```

**.env.production**：

```bash
REACT_APP_API_BASE_URL=https://your-domain.com/api
```

### 2. Axios 配置

**src/utils/request.js**：

```javascript
import axios from 'axios';
import { message } from 'antd';

const request = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL,
  timeout: 30000,
});

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

request.interceptors.response.use(
  (response) => {
    const { code, data, message: msg } = response.data;
    if (code === '200' || code === 200) {
      return data;
    }
    message.error(msg || '请求失败');
    return Promise.reject(new Error(msg));
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
      message.error('登录已过期');
    }
    return Promise.reject(error);
  }
);

export default request;
```

---

## 调试技巧

### 1. 查看网络请求

在浏览器开发者工具（F12）→ Network 标签页：

- 查看请求 URL 是否正确
- 查看请求头是否包含 Authorization
- 查看响应状态码和内容

### 2. 直连 BFF（跳过 NGINX）

临时修改 `.env.development`：

```bash
VITE_API_BASE_URL=http://localhost:8082/api
```

### 3. 查看 CORS 问题

如果出现跨域错误，检查：

1. NGINX 配置中的 CORS 头是否正确
2. BFF 服务是否配置了 CORS
3. 请求方法是否为 OPTIONS（预检请求）

### 4. Token 调试

在浏览器控制台：

```javascript
// 查看当前 token
localStorage.getItem('access_token')

// 手动设置 token
localStorage.setItem('access_token', 'your_token_here')

// 清除 token
localStorage.removeItem('access_token')
```

---

## 常见问题

### Q1: 请求返回 404

**原因**：API 路径不正确或 NGINX 配置错误

**解决**：

1. 检查前端请求路径是否以 `/api/v1/` 开头
2. 检查 NGINX 配置中的 `proxy_pass` 是否正确
3. 检查 BFF 服务是否正常运行

### Q2: 请求返回 401

**原因**：Token 无效或已过期

**解决**：

1. 重新登录获取新 token
2. 检查 token 是否正确保存在 localStorage
3. 检查请求头是否包含 `Authorization: Bearer {token}`

### Q3: 请求超时

**原因**：后端服务未启动或响应慢

**解决**：

1. 检查所有服务是否正常运行
2. 增加 axios timeout 配置
3. 检查网络连接

### Q4: CORS 错误

**原因**：跨域配置不正确

**解决**：

1. 使用 NGINX 代理（推荐）
2. 在 BFF 配置 CORS
3. 开发环境使用 Vite/Webpack 代理

---

## 生产环境部署

### 1. 构建前端

```bash
npm run build
# 或
yarn build
```

### 2. 部署到 NGINX

将构建产物（`dist/` 目录）复制到 NGINX 配置的 root 路径：

```bash
cp -r dist/* /usr/local/var/www/html/
```

### 3. 更新 NGINX 配置

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /usr/local/var/www/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://kronos_bff_cluster;
    }
}
```

### 4. HTTPS 配置

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    # 其他配置同上...
}

server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

---

## 性能优化

### 1. 请求缓存

```javascript
// 使用 axios-cache-adapter
import { setupCache } from 'axios-cache-adapter';

const cache = setupCache({
  maxAge: 15 * 60 * 1000, // 15分钟
});

const request = axios.create({
  adapter: cache.adapter,
});
```

### 2. 请求取消

```javascript
import { ref } from 'vue';

const controller = ref(null);

const fetchData = () => {
  // 取消上一次请求
  if (controller.value) {
    controller.value.abort();
  }
  
  controller.value = new AbortController();
  
  return request({
    url: '/v1/devices',
    signal: controller.value.signal,
  });
};
```

### 3. 请求重试

```javascript
import axios from 'axios';
import axiosRetry from 'axios-retry';

axiosRetry(request, {
  retries: 3,
  retryDelay: axiosRetry.exponentialDelay,
  retryCondition: (error) => {
    return axiosRetry.isNetworkOrIdempotentRequestError(error)
      || error.response?.status === 503;
  },
});
```

---

## 监控与日志

### 1. 请求日志

```javascript
request.interceptors.request.use((config) => {
  console.log(`[API Request] ${config.method.toUpperCase()} ${config.url}`, config.data);
  return config;
});

request.interceptors.response.use(
  (response) => {
    console.log(`[API Response] ${response.config.url}`, response.data);
    return response;
  },
  (error) => {
    console.error(`[API Error] ${error.config?.url}`, error);
    return Promise.reject(error);
  }
);
```

### 2. 性能监控

```javascript
request.interceptors.request.use((config) => {
  config.metadata = { startTime: Date.now() };
  return config;
});

request.interceptors.response.use((response) => {
  const duration = Date.now() - response.config.metadata.startTime;
  console.log(`[API Performance] ${response.config.url} took ${duration}ms`);
  return response;
});
```
