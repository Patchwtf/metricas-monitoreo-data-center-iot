import axios from 'axios';
import type { LoginData, AuthResponse, User } from '../types';

const API_URL = 'https://localhost:8443/monitoreo/auth';

class AuthService {
  async login(loginData: LoginData): Promise<AuthResponse> {
    const response = await axios.post(`${API_URL}/login`, loginData);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  }


  async logout(): Promise<void> {
    const token = this.getToken();
    
    if (token) {
      try {
        await axios.post(`${API_URL}/logout`, {}, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        console.log('✅ Logout exitoso en backend');
      } catch (error) {
        console.warn('⚠️  Error en logout backend, pero continuando...', error);
      }
    }
    this.clearLocalStorage();
    window.location.href = '/login';
  }

  public clearLocalStorage(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUser(): User | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}

export default new AuthService();