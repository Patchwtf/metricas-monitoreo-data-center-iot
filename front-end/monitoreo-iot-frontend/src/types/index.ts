export interface User {
  correo: string;
  roles: string[];
  estatus: string;
  token: string;
}

export interface LoginData {
  correo: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  correo: string;
  roles: string[];
  estatus: string;
  expiresAt: string;
}

export interface Metrica {
  id: number;
  nombreMaquina: string;
  usoCpu: number;
  usoMemoria: number;
  temperatura: number;
  timestamp: string;
}