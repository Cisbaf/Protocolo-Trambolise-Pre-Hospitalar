
// src/context/AuthContext.tsx
import { createContext, useContext, useEffect, useState } from "react";
import { BaseURL } from "../settings";

interface User {
  username: string;
}

interface LoginResult {
  success: boolean;
  message?: string;
}

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (username: string, password: string) => Promise<LoginResult>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // 🔍 busca usuário autenticado
  async function refreshUser() {
    try {
      const res = await fetch(`${BaseURL}/auth/me`, {
        credentials: "include",
      });

      if (!res.ok) {
        setUser(null);
        return;
      }

      const data = await res.json();
      setUser(data);
    } catch {
      setUser(null);
    }
  }

  // 🔐 login
  async function login(username: string, password: string): Promise<LoginResult> {
    try {
      const res = await fetch(`${BaseURL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ username, password }),
      });

      const text = await res.text();

      if (!res.ok) {
        return {
          success: false,
          message: text || "Usuário ou senha inválidos",
        };
      }

      await refreshUser();

      return { success: true };
    } catch {
      return {
        success: false,
        message: "Erro ao conectar com o servidor",
      };
    }
  }

  // 🔓 logout
  async function logout() {
    try {
      await fetch(`${BaseURL}/auth/logout`, {
        method: "POST",
        credentials: "include",
      });
    } finally {
      setUser(null);
    }
  }

  // 🚀 ao iniciar app
  useEffect(() => {
    (async () => {
      await refreshUser();
      setLoading(false);
    })();
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

// hook
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth deve estar dentro do AuthProvider");
  }
  return context;
}
