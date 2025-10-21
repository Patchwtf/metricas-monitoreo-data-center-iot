import axios from 'axios';
import authService from './auth';

const API_BASE = 'https://localhost:8443/monitoreo/api';

axios.interceptors.request.use((config) => {
  const token = authService.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      alert('Tu sesión ha expirado o no tienes permisos. Inicia sesión de nuevo.');
      authService.logout();
    }
    return Promise.reject(error);
  }
);

export const api = {
  getDashboard: () => axios.get(`${API_BASE}/dashboard`),
  getMaquinas: () => axios.get(`${API_BASE}/maquinas`),
  getMetricas: () => axios.get(`${API_BASE}/metricas`),
  getUsuarios: () => axios.get(`${API_BASE}/usuarios`),
  getMetricasHoy: () => axios.get(`${API_BASE}/dashboard/metricas-tiempo-real`),
  getTemperaturas: () => axios.get(`${API_BASE}/temperaturas`),
  getTemperaturasUltima: () => axios.get(`${API_BASE}/temperaturas/actual`),
  getAccesos: () => axios.get(`${API_BASE}/accesos`),
  postCorregirTemperatura: (data: { registro: string; temperatura: number; estatus: string }) =>
    axios.post(`${API_BASE}/temperaturas`, data),
  putMaquina: (idMaquina: number, data: { nombre: string; mac: string; ip: string; estatus: string }) =>
    axios.put(`${API_BASE}/maquinas/${idMaquina}`, data),
  putUsuario: (idUsuario: string, data: { nombre: string; apellido: string; correo: string; estatus: string }) =>
    axios.put(`${API_BASE}/usuarios/${idUsuario}/desbloquear`, data),
  desbloquearUsuario: (idUsuario: string) => 
  axios.patch(`${API_BASE}/usuarios/${idUsuario}/desbloquear`),
};