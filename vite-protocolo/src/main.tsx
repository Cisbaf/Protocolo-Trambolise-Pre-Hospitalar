import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { ChakraProvider, defaultSystem } from '@chakra-ui/react'
import { createBrowserRouter, RouterProvider } from "react-router-dom"
import AvcFormPage from './pages/AvcFormPage.tsx'
import LayoutPage from './componentes/LayoutPage.tsx'

const router = createBrowserRouter([{
  element: <LayoutPage/>,
  children: [
    { path: "/", element: <AvcFormPage/>}
  ]
}]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ChakraProvider value={defaultSystem}>
      <RouterProvider router={router}/>
    </ChakraProvider>
  </StrictMode>,
)
