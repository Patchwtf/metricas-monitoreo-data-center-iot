import React, { useEffect, useState } from 'react';
import { api } from '../../services/api';

interface RegistroAcceso {
  idRegistro: number;
  time: string;
  idUsuario: number;
  acciones: 'ACCESO_CORRECTO' | 'ACCESO_DENEGADO';
}

const ACCIONES = {
  'ACCESO_CORRECTO': { texto: 'Acceso Correcto', color: 'text-green-600', bg: 'bg-green-50' },
  'ACCESO_DENEGADO': { texto: 'Acceso Denegado', color: 'text-red-600', bg: 'bg-red-50' },
};

const ELEMENTOS_POR_PAGINA = 5;

const TablaAccesos: React.FC = () => {
  const [accesos, setAccesos] = useState<RegistroAcceso[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [paginaActual, setPaginaActual] = useState(1);

  const fetchAccesos = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.getAccesos();

      const accesosOrdenados = res.data.sort((a: RegistroAcceso, b: RegistroAcceso) => 
        new Date(b.time).getTime() - new Date(a.time).getTime()
      );
      setAccesos(accesosOrdenados);
    } catch (e) {
      setError('No se pudieron cargar los registros de acceso');
      console.error('Error fetching accesos:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAccesos();
  }, []);

  const totalPaginas = Math.ceil(accesos.length / ELEMENTOS_POR_PAGINA);
  const inicio = (paginaActual - 1) * ELEMENTOS_POR_PAGINA;
  const fin = inicio + ELEMENTOS_POR_PAGINA;
  const accesosPagina = accesos.slice(inicio, fin);

  const cambiarPagina = (nuevaPagina: number) => {
    setPaginaActual(nuevaPagina);
  };

  const formatearFecha = (fecha: string) => {
    return new Date(fecha).toLocaleString('es-MX', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    });
  };

  const getEstiloAccion = (accion: string) => {
    const accionKey = accion as keyof typeof ACCIONES;
    return ACCIONES[accionKey] || ACCIONES.ACCESO_CORRECTO;
  };

  return (
    <div className="mt-8 flex flex-col items-center w-full">
      <h2 className="text-lg font-bold mb-4 text-center">Últimos Accesos al Cuarto de Máquinas</h2>
      
      <div className="mb-4 text-sm text-gray-600">
        Mostrando {accesosPagina.length} de {accesos.length} registros
      </div>

      {loading ? (
        <div>Cargando...</div>
      ) : error ? (
        <div className="text-red-600">{error}</div>
      ) : (
        <>
          <div className="overflow-x-auto w-full flex justify-center">
            <table className="min-w-[800px] bg-white rounded shadow mx-auto">
              <thead>
                <tr>
                  <th className="px-4 py-2 border">ID Registro</th>
                  <th className="px-4 py-2 border">Fecha y Hora</th>
                  <th className="px-4 py-2 border">ID Usuario</th>
                  <th className="px-4 py-2 border">Acción</th>
                  <th className="px-4 py-2 border">Estado</th>
                </tr>
              </thead>
              <tbody>
                {accesosPagina.map((acceso) => {
                  const estilo = getEstiloAccion(acceso.acciones);
                  return (
                    <tr key={acceso.idRegistro}>
                      <td className="px-4 py-2 border">#{acceso.idRegistro}</td>
                      <td className="px-4 py-2 border font-mono text-sm">
                        {formatearFecha(acceso.time)}
                      </td>
                      <td className="px-4 py-2 border">
                        <span className="bg-gray-100 px-2 py-1 rounded text-sm">
                          {acceso.idUsuario}
                        </span>
                      </td>
                      <td className="px-4 py-2 border">
                        <span className={`px-3 py-1 rounded-full text-xs font-medium ${estilo.bg} ${estilo.color}`}>
                          {estilo.texto}
                        </span>
                      </td>
                      <td className="px-4 py-2 border">
                        {acceso.acciones === 'ACCESO_CORRECTO' ? (
                        <span className="text-green-500">✅</span>
                        ) : acceso.acciones === 'ACCESO_DENEGADO' ? (
                        <span className="text-red-500">❌</span>
                        ) : (
                        <span className="text-gray-500">●</span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {totalPaginas > 1 && (
            <div className="flex justify-center items-center space-x-2 mt-4">
              <button
                onClick={() => cambiarPagina(paginaActual - 1)}
                disabled={paginaActual === 1}
                className="px-3 py-1 border rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
              >
                Anterior
              </button>
              
              <span className="text-sm">
                Página {paginaActual} de {totalPaginas}
              </span>
              
              <button
                onClick={() => cambiarPagina(paginaActual + 1)}
                disabled={paginaActual === totalPaginas}
                className="px-3 py-1 border rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
              >
                Siguiente
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default TablaAccesos;