import { createBrowserRouter } from "react-router-dom";

import AvcFormPage from "./pages/AvcFormPage.tsx";
import LayoutPage from "./componentes/LayoutPage.tsx";
import { AuthProvider } from "./context/AuthContext.tsx";
import Login from "./pages/Login.tsx";
import { PrivateRoute } from "./componentes/PrivateRoute.tsx";
import LogoutPage from "./pages/Logout.tsx";
import PainelPage from "./pages/Painel.tsx";

export const router = createBrowserRouter([
  {
    element: (
      <AuthProvider>
        <LayoutPage />
      </AuthProvider>
    ),
    children: [
      {
        path: "/",
        element: <AvcFormPage />,
      },
      {
        path: "/login",
        element: <Login />,
      },
      {
        path: "/logout",
        element: <LogoutPage />,
      },
      {
        path: "/painel",
        element: (
          <PrivateRoute>
            <PainelPage />
          </PrivateRoute>
        ),
      },
    ],
  },
]);