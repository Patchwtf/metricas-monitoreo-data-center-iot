import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="bg-white shadow">
      <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
        <div>
          <Link to="/dashboard" className="text-2xl font-bold text-gray-900">
            IoT Monitor
          </Link>
        </div>
        <div className="flex items-center gap-4">
          {user ? (
            <>
              <span className="font-medium">{user.correo}</span>
              <button 
                onClick={logout}
                className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition"
              >
                Cerrar Sesión
              </button>
            </>
          ) : (
            <Link to="/login" className="text-blue-500 hover:underline">
              Iniciar Sesión
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;