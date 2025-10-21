import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';

interface MetricaHoy {
  id: number;
  nombreMaquina: string;
  usoCpu: number;
  usoMemoria: number;
  temperatura: number;
  timestamp: string;
}

const MetricasHoy: React.FC = () => {
  const [metricasHoy, setMetricasHoy] = useState<MetricaHoy[]>([]);
  const [resumen, setResumen] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchMetricasHoy = async () => {
      try {
        setLoading(true);
        setError('');
        const response = await api.getMetricasHoy();
        const data = response.data;

        if (Array.isArray(data)) {
          setMetricasHoy(data);
          setResumen(null);
        } else if (data && Array.isArray(data.metricas)) {
          setMetricasHoy(data.metricas);
          setResumen(null);
        } else if (data && typeof data === 'object') {
          setResumen(data);
          setMetricasHoy([]);
        } else {
          setMetricasHoy([]);
          setResumen(null);
        }
      } catch (err: any) {
        setError('No se pudieron obtener las métricas de hoy');
      } finally {
        setLoading(false);
      }
    };
    fetchMetricasHoy();
    const interval = setInterval(fetchMetricasHoy, 10000);
    return () => clearInterval(interval);
  }, []);
  
  return (
    <div className="bg-white rounded-lg shadow p-6 h-full w-full flex flex-col justify-between items-end">
      <h3 className="text-xl font-semibold mb-4"></h3>
      {loading ? (
        <div className="text-gray-500">Cargando...</div>
      ) : error ? (
        <div className="text-red-500">{error}</div>
      ) : metricasHoy.length === 0 ? (
        resumen ? (
          <div className="space-y-4">
            <div className="flex justify-between border-b pb-1 gap-3">
              <span className="font-medium min-w-[120px]">Temperatura Promedio</span>
              <span className="font-bold text-blue-600 text-right">{resumen.temperaturaPromedio ?? '--'}°C</span>
            </div>
            <div className="flex justify-between border-b pb-1 gap-2">
              <span className="font-medium min-w-[120px]">Total Máquinas</span>
              <span className="font-bold text-blue-600 text-right">{resumen.totalMaquinas ?? '--'}</span>
            </div>
            <div className="flex justify-between border-b pb-1 gap-2">
              <span className="font-medium min-w-[120px]">Máquinas Monitoreadas</span>
              <span className="font-bold text-blue-600 text-right">{resumen.maquinasMonitoreadas ?? '--'}</span>
            </div>
            <div className="flex justify-between border-b pb-1 gap-2">
              <span className="font-medium min-w-[120px]">CPU Promedio</span>
              <span className="font-bold text-blue-600 text-right">{resumen.cpuPromedio ?? '--'}%</span>
            </div>
            <div className="flex justify-between border-b pb-1 gap-2">
              <span className="font-medium min-w-[120px]">RAM Promedio</span>
              <span className="font-bold text-blue-600 text-right">{resumen.ramPromedio ?? '--'}%</span>
            </div>
          </div>
        ) : (
          <div className="text-gray-500">No hay métricas registradas hoy.</div>
        )
      ) : (
        <div className="metricas-list space-y-2">
          {metricasHoy.map(metrica => (
            <div key={metrica.id} className="flex justify-between items-center border-b pb-1">
              <span className="font-medium">{metrica.nombreMaquina}</span>
              <span className={`font-bold ${metrica.usoCpu > 80 ? 'text-red-600' : 'text-blue-600'}`}>{metrica.usoCpu}% CPU</span>
              <span className="text-xs text-gray-500">{new Date(metrica.timestamp).toLocaleTimeString('es-MX', { hour12: false })}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MetricasHoy;
