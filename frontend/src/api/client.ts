import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import type { AxiosRequestConfig } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

class ApiClient {
  private client: AxiosInstance;
  private token: string | null = null;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: 10000,
    });

    this.client.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        if (this.token) {
          config.headers.Authorization = `Bearer ${this.token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    this.client.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        if (error.response?.status === 401) {
          this.clearToken();
          if (typeof window !== 'undefined') {
            window.location.href = '/login';
          }
        }
        return Promise.reject(this.normalizeError(error));
      }
    );

    if (typeof window !== 'undefined') {
      const storedToken = localStorage.getItem('auth_token');
      if (storedToken) {
        this.token = storedToken;
      }
    }
  }

  setToken(token: string) {
    this.token = token;
    if (typeof window !== 'undefined') {
      localStorage.setItem('auth_token', token);
    }
  }

  clearToken() {
    this.token = null;
    if (typeof window !== 'undefined') {
      localStorage.removeItem('auth_token');
    }
  }

  getToken(): string | null {
    return this.token;
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }

  private normalizeError(error: AxiosError): Error & { response?: AxiosError['response']; status?: number } {
    const normalized = new Error(error.message) as Error & { response?: AxiosError['response']; status?: number };
    normalized.name = 'ApiError';
    normalized.response = error.response;
    normalized.status = error.response?.status;
    return normalized;
  }

  async register(data: { name: string; email: string; password: string }) {
    const response = await this.client.post('/api/auth/register', data);
    return response.data;
  }

  async login(data: { email: string; password: string }) {
    const response = await this.client.post('/api/auth/login', data);
    return response.data;
  }

  async getCurrentUser() {
    const response = await this.client.get('/api/auth/me');
    return response.data;
  }

  async getTasks() {
    const response = await this.client.get('/api/tasks');
    return response.data;
  }

  async getTask(id: number) {
    const response = await this.client.get(`/api/tasks/${id}`);
    return response.data;
  }

  async createTask(data: { title: string; description?: string; status: string; priority: string; dueDate?: string }) {
    const response = await this.client.post('/api/tasks', data);
    return response.data;
  }

  async updateTask(id: number, data: { title: string; description?: string; status: string; priority: string; dueDate?: string }) {
    const response = await this.client.put(`/api/tasks/${id}`, data);
    return response.data;
  }

  async deleteTask(id: number) {
    await this.client.delete(`/api/tasks/${id}`);
  }

  async getDashboard() {
    const response = await this.client.get('/api/tasks/dashboard');
    return response.data;
  }
}

export const api = new ApiClient();