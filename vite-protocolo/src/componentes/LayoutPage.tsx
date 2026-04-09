
import {
  Box
} from "@chakra-ui/react"
import { Outlet } from "react-router-dom"
import AppBar from "./AppBar"
import { Toaster } from "./ui/toaster"

export default function LayoutPage() {

    return (
         <Box
            bg="gray.50"
            minH="100vh"
            w="100%"
    
            >
            <Toaster/>
            {/* APP BAR */}
            <AppBar/>
            {/* Container responsivo */}
            <Box
                w="100%"
                maxW={{ base: "100%", md: "720px", lg: "900px", xl: "1100px" }}
                mx="auto"
                px={{ base: 3, sm: 4, md: 6 }}
                py={{ base: 4, md: 10 }}
            >
                <Outlet />
            </Box>
        </Box>
    )
}