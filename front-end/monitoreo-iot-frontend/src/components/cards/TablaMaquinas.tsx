import React, { useEffect, useState } from 'react';
import { api } from '../../services/api';

interface Responsable {
  idUsuario: string;
  nombre: string;
  apellido: string;
  correo: string;
  estatus: string;
}

interface Maquina {
  idMaquina: number;
  nombre: string;
  mac: string;
  ip: string;
  fechaRegistro: string;
  estatus: 'ACTIVA' | 'INACTIVA' | 'MANTENIMIENTO';
  responsable: Responsable;
}

const ESTATUS = ['ACTIVA', 'INACTIVA', 'MANTENIMIENTO'] as const;
const ELEMENTOS_POR_PAGINA = 5;

const TablaMaquinas: React.FC = () => {
  const [maquinas, setMaquinas] = useState<Maquina[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState<number | null>(null);
  const [paginaActual, setPaginaActual] = useState(1);

  const fetchMaquinas = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.getMaquinas();
      setMaquinas(res.data);
    } catch (e) {
      setError('No se pudieron cargar las máquinas');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMaquinas();
  }, []);

  const totalPaginas = Math.ceil(maquinas.length / ELEMENTOS_POR_PAGINA);
  const inicio = (paginaActual - 1) * ELEMENTOS_POR_PAGINA;
  const fin = inicio + ELEMENTOS_POR_PAGINA;
  const maquinasPagina = maquinas.slice(inicio, fin);

  const handleEstatusChange = async (maquina: Maquina, nuevoEstatus: string) => {
    setSaving(maquina.idMaquina);
    try {
      await api.putMaquina(maquina.idMaquina, {
        nombre: maquina.nombre,
        mac: maquina.mac,
        ip: maquina.ip,
        estatus: nuevoEstatus,
      });
      setMaquinas((prev) =>
        prev.map((m) =>
          m.idMaquina === maquina.idMaquina ? { ...m, estatus: nuevoEstatus as Maquina['estatus'] } : m
        )
      );
    } catch (e) {
      alert('No se pudo actualizar el estatus');
    } finally {
      setSaving(null);
    }
  };

  const cambiarPagina = (nuevaPagina: number) => {
    setPaginaActual(nuevaPagina);
  };

  return (
    <div className="mt-8 flex flex-col items-center w-full">
      <h2 className="text-lg font-bold mb-4 text-center">Máquinas</h2>
      
      <div className="mb-4 text-sm text-gray-600">
        Mostrando {maquinasPagina.length} de {maquinas.length} máquinas
      </div>

      {loading ? (
        <div>Cargando...</div>
      ) : error ? (
        <div className="text-red-600">{error}</div>
      ) : (
        <>
          <div className="overflow-x-auto w-full flex justify-center">
            <table className="min-w-[900px] bg-white rounded shadow mx-auto">
              <thead>
                <tr>
                  <th className="px-4 py-2 border">Nombre</th>
                  <th className="px-4 py-2 border">IP</th>
                  <th className="px-4 py-2 border">MAC</th>
                  <th className="px-4 py-2 border">Fecha Registro</th>
                  <th className="px-4 py-2 border">Responsable</th>
                  <th className="px-4 py-2 border">Estatus</th>
                  <th className="px-4 py-2 border">Acción</th>
                </tr>
              </thead>
              <tbody>
                {maquinasPagina.map((m) => (
                  <tr key={m.idMaquina}>
                    <td className="px-4 py-2 border">{m.nombre}</td>
                    <td className="px-4 py-2 border">{m.ip}</td>
                    <td className="px-4 py-2 border">{m.mac}</td>
                    <td className="px-4 py-2 border">
                      {new Date(m.fechaRegistro).toLocaleString('es-MX', { hour12: false })}
                    </td>
                    <td className="px-4 py-2 border">{m.responsable?.correo || '-'}</td>
                    <td className="px-4 py-2 border">{m.estatus}</td>
                    <td className="px-4 py-2 border">
                      <select
                        className="border rounded px-2 py-1"
                        value={m.estatus}
                        disabled={saving === m.idMaquina}
                        onChange={e => handleEstatusChange(m, e.target.value)}
                      >
                        {ESTATUS.map((est) => (
                          <option key={est} value={est}>{est}</option>
                        ))}
                      </select>
                    </td>
                  </tr>
                ))}
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

export default TablaMaquinas;