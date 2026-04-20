import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { ChakraProvider, defaultSystem } from '@chakra-ui/react'
import { RouterProvider } from "react-router-dom"
import { router } from './routes'


createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ChakraProvider value={defaultSystem}>
      <RouterProvider router={router}/>
    </ChakraProvider>
  </StrictMode>,
)
