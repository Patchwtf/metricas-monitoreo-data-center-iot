import React, { useEffect, useState } from 'react';
import { api } from '../../services/api';

interface Rol {
  idRol: number;
  nombreRol: string;
  horarios: string;
  usuarios: null;
}

interface Usuario {
  idUsuario: string;
  nombre: string;
  apellido: string;
  correo: string;
  estatus: 'ACTIVO' | 'INACTIVO' | 'BLOQUEADO';
  numIntentos: number;
  rol: Rol;
  maquinas: null;
}

const ELEMENTOS_POR_PAGINA = 5;

const TablaUsuarios: React.FC = () => {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState<string | null>(null);
  const [paginaActual, setPaginaActual] = useState(1);

  const fetchUsuarios = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.getUsuarios();
      setUsuarios(res.data);
    } catch (e) {
      setError('No se pudieron cargar los usuarios');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsuarios();
  }, []);

  const totalPaginas = Math.ceil(usuarios.length / ELEMENTOS_POR_PAGINA);
  const inicio = (paginaActual - 1) * ELEMENTOS_POR_PAGINA;
  const fin = inicio + ELEMENTOS_POR_PAGINA;
  const usuariosPagina = usuarios.slice(inicio, fin);

  const handleDesbloquear = async (usuarioId: string) => {
    setSaving(usuarioId);
    try {
      await api.desbloquearUsuario(usuarioId);
      setUsuarios((prev) =>
        prev.map((u) =>
          u.idUsuario === usuarioId ? { ...u, estatus: 'ACTIVO', numIntentos: 0 } : u
        )
      );
      alert('Usuario desbloqueado exitosamente');
    } catch (e) {
      alert('No se pudo desbloquear el usuario');
    } finally {
      setSaving(null);
    }
  };

  const cambiarPagina = (nuevaPagina: number) => {
    setPaginaActual(nuevaPagina);
  };

  const getColorEstatus = (estatus: string) => {
    switch (estatus) {
      case 'ACTIVO':
        return 'text-green-600 bg-green-50';
      case 'INACTIVO':
        return 'text-yellow-600 bg-yellow-50';
      case 'BLOQUEADO':
        return 'text-red-600 bg-red-50';
      default:
        return 'text-gray-600 bg-gray-50';
    }
  };

  const getColorRol = (rol: string) => {
    switch (rol) {
      case 'ADMIN':
        return 'text-purple-600 bg-purple-50';
      case 'SERVICIO':
        return 'text-blue-600 bg-blue-50';
      case 'USUARIO':
        return 'text-green-600 bg-green-50';
      default:
        return 'text-gray-600 bg-gray-50';
    }
  };

  return (
    <div className="mt-8 flex flex-col items-center w-full">
      <h2 className="text-lg font-bold mb-4 text-center">Gestión de Usuarios</h2>
      

      <div className="mb-4 text-sm text-gray-600">
        Mostrando {usuariosPagina.length} de {usuarios.length} usuarios
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
                  <th className="px-4 py-2 border">Correo</th>
                  <th className="px-4 py-2 border">Rol</th>
                  <th className="px-4 py-2 border">Intentos Fallidos</th>
                  <th className="px-4 py-2 border">Estatus</th>
                  <th className="px-4 py-2 border">Acción</th>
                </tr>
              </thead>
              <tbody>
                {usuariosPagina.map((usuario) => (
                  <tr key={usuario.idUsuario}>
                    <td className="px-4 py-2 border">
                      {usuario.nombre} {usuario.apellido}
                    </td>
                    <td className="px-4 py-2 border">{usuario.correo}</td>
                    <td className="px-4 py-2 border">
                      <span className={`px-2 py-1 rounded text-xs font-medium ${getColorRol(usuario.rol.nombreRol)}`}>
                        {usuario.rol.nombreRol}
                      </span>
                    </td>
                    <td className="px-4 py-2 border text-center">
                      <span className={`px-2 py-1 rounded text-xs font-medium ${
                        usuario.numIntentos > 0 ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600'
                      }`}>
                        {usuario.numIntentos}
                      </span>
                    </td>
                    <td className="px-4 py-2 border">
                      <span className={`px-2 py-1 rounded text-xs font-medium ${getColorEstatus(usuario.estatus)}`}>
                        {usuario.estatus}
                      </span>
                    </td>
                    <td className="px-4 py-2 border">
                      {usuario.estatus === 'BLOQUEADO' ? (
                        <button
                          onClick={() => handleDesbloquear(usuario.idUsuario)}
                          disabled={saving === usuario.idUsuario}
                          className="px-3 py-1 bg-green-500 text-white rounded text-sm hover:bg-green-600 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          {saving === usuario.idUsuario ? 'Desbloqueando...' : 'Desbloquear'}
                        </button>
                      ) : (
                        <span className="text-gray-400 text-sm">-</span>
                      )}
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

export default TablaUsuarios;