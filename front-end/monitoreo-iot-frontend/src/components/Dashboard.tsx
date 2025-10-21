import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import UltimaTemperatura from './cards/UltimaTemperatura';
import MetricasHoy from './cards/MetricasHoy';
import TablaMaquinas from './cards/TablaMaquinas';
import TablaAccesos from './cards/Accesos';
import DesbloqueoUsuarios from './cards/DesbloqueoUsuarios';

const Dashboard: React.FC = () => {
  const { user, logout, loading: authLoading } = useAuth();
  const [estadoSistema, setEstadoSistema] = useState<'operativo' | 'advertencia' | 'critico'>('operativo');
  const [temperaturaActual, setTemperaturaActual] = useState<number>(0);
  const [logoutLoading, setLogoutLoading] = useState(false);
  const [showLogoutModal, setShowLogoutModal] = useState(false);

  const handleLogoutClick = () => {
    console.log('🔴 Abriendo modal de logout');
    setShowLogoutModal(true);
  };

  const handleLogoutConfirm = async () => {
    console.log('✅ Confirmación de logout');
    setShowLogoutModal(false);
    setLogoutLoading(true);
    
    try {
      await logout();
      window.location.href = '/login';
    } catch (error) {
      console.error('Error:', error);
      window.location.href = '/login';
    }
  };

  const handleLogoutCancel = () => {
    console.log('Cancelación de logout');
    setShowLogoutModal(false);
  };

  const actualizarEstadoSistema = (temperatura: number) => {
    setTemperaturaActual(temperatura);
    
    if (temperatura >= 70) {
      setEstadoSistema('critico');
    } else if (temperatura >= 50) {
      setEstadoSistema('advertencia');
    } else {
      setEstadoSistema('operativo');
    }
  };

  const getEstadoConfig = () => {
    switch (estadoSistema) {
      case 'operativo':
        return {
          texto: '✅ Todo Operativo',
          color: 'text-green-600',
          bg: 'bg-green-50',
          border: 'border-green-200',
          icono: '🟢'
        };
      case 'advertencia':
        return {
          texto: '⚠️ Advertencia - Temperatura Elevada',
          color: 'text-yellow-600',
          bg: 'bg-yellow-50',
          border: 'border-yellow-200',
          icono: '🟡'
        };
      case 'critico':
        return {
          texto: '🚨 Crítico - Temperatura Peligrosa',
          color: 'text-red-600',
          bg: 'bg-red-50',
          border: 'border-red-200',
          icono: '🔴'
        };
      default:
        return {
          texto: '✅ Todo Operativo',
          color: 'text-green-600',
          bg: 'bg-green-50',
          border: 'border-green-200',
          icono: '🟢'
        };
    }
  };

  const estadoConfig = getEstadoConfig();

  if (logoutLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-gray-100 flex items-center justify-center">
        <div className="flex flex-col items-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mb-4"></div>
          <div className="text-xl text-gray-600">Cerrando sesión...</div>
        </div>
      </div>
    );
  }

   return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-gray-100 w-full">
      {/* Modal de Confirmación de Logout */}
      {showLogoutModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 transform transition-all">
            <div className="text-center">
              <div className="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
                <span className="text-2xl">🚪</span>
              </div>
              <h3 className="text-lg font-bold text-gray-900 mb-2">
                Cerrar Sesión
              </h3>
              <p className="text-sm text-gray-600 mb-6">
                ¿Estás seguro de que quieres salir del sistema?
              </p>
              
              <div className="flex gap-3">
                <button
                  onClick={handleLogoutCancel}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  onClick={handleLogoutConfirm}
                  disabled={logoutLoading}
                  className="flex-1 px-4 py-2 bg-red-500 text-white rounded-xl hover:bg-red-600 transition-colors disabled:opacity-50"
                >
                  {logoutLoading ? 'Cerrando...' : 'Sí, Cerrar'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      <header className="bg-white shadow-lg border-b border-gray-200 w-full">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <div className="flex items-center space-x-4">
            <div className="bg-blue-500 p-3 rounded-xl">
              <span className="text-2xl text-white">🖥️</span>
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900 bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                IoT Monitor - Centro de Datos
              </h1>
              <p className="text-sm text-gray-600">
                Monitoreo en tiempo real de servidores y métricas
              </p>
            </div>
          </div>
          <div className="flex items-center gap-6">
            <div className="text-right">
              <p className="font-medium text-gray-900">{user?.correo}</p>
              <p className="text-sm text-gray-600 flex items-center gap-1">
                <span className="w-2 h-2 bg-green-500 rounded-full"></span>
                Rol: {user?.roles?.join(', ')}
              </p>
            </div>
            <button 
              onClick={handleLogoutClick}
              disabled={logoutLoading || authLoading}
              className="bg-gradient-to-r from-red-500 to-red-600 text-white px-6 py-2 rounded-xl hover:from-red-600 hover:to-red-700 transition-all duration-200 disabled:opacity-50 shadow-md hover:shadow-lg transform hover:-translate-y-0.5"
            >
              {logoutLoading ? 'Cerrando...' : 'Cerrar Sesión'}
            </button>
          </div>
        </div>
      </header>

      <main className="w-full py-6 px-4 flex flex-col">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 mb-6">
          <div className={`col-span-1 lg:col-span-4 rounded-2xl shadow-lg p-6 border-2 ${estadoConfig.border} ${estadoConfig.bg} transition-all duration-300`}>
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <span className="text-3xl">{estadoConfig.icono}</span>
                <div>
                  <h3 className="text-xl font-bold text-gray-900">Estado del Sistema</h3>
                  <p className={`text-lg font-semibold ${estadoConfig.color}`}>
                    {estadoConfig.texto}
                  </p>
                  {temperaturaActual > 0 && (
                    <p className="text-sm text-gray-600 mt-1">
                      Temperatura actual: <span className="font-medium">{temperaturaActual}°C</span>
                    </p>
                  )}
                </div>
              </div>
              <div className="text-right">
                <p className="text-sm text-gray-600">Umbrales:</p>
                <p className="text-xs text-gray-500">Normal: &lt;50°C | Advertencia: 50-69°C | Crítico: ≥70°C</p>
              </div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6 w-full">
          <div className="h-full flex">
            <div className="bg-white rounded-2xl shadow-lg p-6 w-full border border-gray-100 hover:shadow-xl transition-all duration-300">
              <h3 className="text-lg font-semibold mb-4 text-gray-900 flex items-center gap-2">
                <span className="text-xl">🌡️</span>
                Temperatura en Tiempo Real
              </h3>
              <UltimaTemperatura onTemperaturaChange={actualizarEstadoSistema} />
            </div>
          </div>
          <div className="h-full flex">
            <div className="bg-white rounded-2xl shadow-lg p-6 w-full border border-gray-100 hover:shadow-xl transition-all duration-300">
              <h3 className="text-lg font-semibold mb-4 text-gray-900 flex items-center gap-2">
                <span className="text-xl">📊</span>
                Métricas del Día
              </h3>
              <MetricasHoy />
            </div>
          </div>
        </div>

        {/* Tablas de Gestión */}
        <div className="space-y-8">
          <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-100 hover:shadow-xl transition-all duration-300">
            <h3 className="text-lg font-semibold mb-4 text-gray-900 flex items-center gap-2">
              <span className="text-xl">💻</span>
              Gestión de Máquinas
            </h3>
            <TablaMaquinas />
          </div>
          
          <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-100 hover:shadow-xl transition-all duration-300">
            <h3 className="text-lg font-semibold mb-4 text-gray-900 flex items-center gap-2">
              <span className="text-xl">🔐</span>
              Registro de Accesos
            </h3>
            <TablaAccesos />
          </div>
          
          <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-100 hover:shadow-xl transition-all duration-300">
            <h3 className="text-lg font-semibold mb-4 text-gray-900 flex items-center gap-2">
              <span className="text-xl">👥</span>
              Gestión de Usuarios
            </h3>
            <DesbloqueoUsuarios />
          </div>
        </div>
      </main>

      <footer className="bg-white border-t border-gray-200 mt-12">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="flex justify-between items-center">
            <p className="text-sm text-gray-600">
              IoT Monitor - Sistema de Monitoreo en Tiempo Real
            </p>
            <div className="flex space-x-4">
              <span className="text-sm text-gray-500">v1.0.0</span>
              <span className="text-sm text-gray-500">•</span>
              <span className="text-sm text-green-500 flex items-center gap-1">
                <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
                Jhosbyn Daniel Guillen Ortiz
              </span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Dashboard;