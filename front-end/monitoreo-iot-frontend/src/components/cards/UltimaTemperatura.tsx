import React, { useState, useEffect, useRef } from 'react';
import { api } from '../../services/api';

interface UltimaTemperaturaProps {
  onTemperaturaChange?: (temperatura: number) => void;
}

const UltimaTemperatura: React.FC<UltimaTemperaturaProps> = ({ onTemperaturaChange }) => {
  const [temperatura, setTemperatura] = useState<number | null>(null);
  const [fecha, setFecha] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [ajustando, setAjustando] = useState(false);
  const [showAjusteForm, setShowAjusteForm] = useState(false);
  const [temperaturaAjuste, setTemperaturaAjuste] = useState('21.76');

  const ultimoCorregido = useRef<number | null>(null);

  useEffect(() => {
    const fetchTemperatura = async () => {
      try {
        setLoading(true);
        setError('');
        const response = await api.getTemperaturasUltima();
        const temp = response.data.temperatura;
        const registro = response.data.registro;
        const idRegistro = response.data.idRegistro;
        setTemperatura(temp);
        setFecha(registro);

        if (onTemperaturaChange) {
          onTemperaturaChange(temp);
        }

        if ((temp > 70 || temp < 10) && idRegistro !== ultimoCorregido.current) {
          try {
            const now = new Date().toISOString();
            await api.postCorregirTemperatura({
              registro: now,
              temperatura: 21.76,
              estatus: 'TEMPERATURA_NORMAL',
            });
            ultimoCorregido.current = idRegistro;
          } catch (e) {
            console.error('Error al corregir temperatura:', e);
          }
        }
      } catch (err: any) {
        setError('No se pudo obtener la temperatura');
      } finally {
        setLoading(false);
      }
    };
    
    fetchTemperatura();
    const interval = setInterval(fetchTemperatura, 10000);
    return () => clearInterval(interval);
  }, [onTemperaturaChange]);

  const handleAjusteTemperatura = async () => {
    if (!temperaturaAjuste) return;
    
    setAjustando(true);
    try {
      const now = new Date().toISOString();
      await api.postCorregirTemperatura({
        registro: now,
        temperatura: parseFloat(temperaturaAjuste),
        estatus: 'TEMPERATURA_NORMAL',
      });
      
      // Actualizar la temperatura mostrada inmediatamente
      setTemperatura(parseFloat(temperaturaAjuste));
      if (onTemperaturaChange) {
        onTemperaturaChange(parseFloat(temperaturaAjuste));
      }
      
      setShowAjusteForm(false);
      alert('✅ Temperatura ajustada correctamente');
    } catch (e) {
      console.error('Error al ajustar temperatura:', e);
      alert('❌ Error al ajustar la temperatura');
    } finally {
      setAjustando(false);
    }
  };

  const getColorTemperatura = () => {
    if (temperatura === null) return 'text-gray-600';
    if (temperatura > 70) return 'text-red-600';
    if (temperatura > 50) return 'text-yellow-600';
    return 'text-green-600';
  };

  const getBordeTemperatura = () => {
    if (temperatura === null) return 'border-gray-200';
    if (temperatura > 70) return 'border-red-500';
    if (temperatura > 50) return 'border-yellow-500';
    return 'border-green-500';
  };

  const getEstadoTemperatura = () => {
    if (temperatura === null) return null;
    if (temperatura > 70) return '🚨 CRÍTICA';
    if (temperatura > 50) return '⚠️ ELEVADA';
    return '✅ NORMAL';
  };

  return (
    <div className={`bg-white rounded-2xl shadow-lg p-6 flex flex-col justify-between h-full w-full border-2 ${getBordeTemperatura()} transition-all duration-300`}>
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
        </h3>
        <button
          onClick={() => setShowAjusteForm(!showAjusteForm)}
          className="px-3 py-1 bg-blue-500 text-white rounded-lg text-sm hover:bg-blue-600 transition-colors"
        >
          ⚙️ Ajustar
        </button>
      </div>

      {/* Formulario de ajuste */}
      {showAjusteForm && (
        <div className="mb-4 p-3 bg-blue-50 rounded-lg border border-blue-200">
          <div className="flex items-center gap-2 mb-2">
            <input
              type="number"
              step="0.01"
              value={temperaturaAjuste}
              onChange={(e) => setTemperaturaAjuste(e.target.value)}
              className="flex-1 px-2 py-1 border border-gray-300 rounded text-sm"
              placeholder="Temperatura"
            />
            <span className="text-sm text-gray-600">°C</span>
          </div>
          <div className="flex gap-2">
            <button
              onClick={handleAjusteTemperatura}
              disabled={ajustando}
              className="flex-1 px-2 py-1 bg-green-500 text-white rounded text-sm hover:bg-green-600 disabled:opacity-50"
            >
              {ajustando ? 'Enviando...' : '✅ Aplicar'}
            </button>
            <button
              onClick={() => setShowAjusteForm(false)}
              className="px-2 py-1 bg-gray-500 text-white rounded text-sm hover:bg-gray-600"
            >
              ✖️ Cancelar
            </button>
          </div>
        </div>
      )}

      {loading ? (
        <div className="flex flex-col items-center justify-center py-4">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mb-2"></div>
          <div className="text-gray-500 text-sm">Cargando...</div>
        </div>
      ) : error ? (
        <div className="text-red-500 text-center py-4">{error}</div>
      ) : (
        <div className="text-center">
          <div className={`text-5xl font-bold mb-3 ${getColorTemperatura()}`}>
            {temperatura !== null ? `${temperatura}°C` : '--'}
          </div>
          
          {getEstadoTemperatura() && (
            <div className={`text-sm font-semibold mb-3 px-3 py-1 rounded-full ${
              temperatura !== null && temperatura > 70 
                ? 'bg-red-100 text-red-700' 
                : temperatura !== null && temperatura > 50
                ? 'bg-yellow-100 text-yellow-700'
                : 'bg-green-100 text-green-700'
            }`}>
              {getEstadoTemperatura()}
            </div>
          )}
          
          <div className="text-xs text-gray-500 mb-4">
            Actualizado: {fecha ? new Date(fecha).toLocaleString('es-MX', { hour12: false }) : '--'}
          </div>
          
          {/* Información de umbrales */}
          <div className="text-xs text-gray-600 bg-gray-50 rounded-lg p-2">
            <div className="grid grid-cols-3 gap-1">
              <div className="text-green-600">✅ &lt;50°C</div>
              <div className="text-yellow-600">⚠️ 50-70°C</div>
              <div className="text-red-600">🚨 &gt;70°C</div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default UltimaTemperatura;