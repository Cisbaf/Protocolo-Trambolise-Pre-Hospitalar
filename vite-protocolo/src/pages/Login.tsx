import { useState } from "react";
import {
  Box,
  Button,
  Input,
  Heading,
  VStack,
  Text,
  Field,
  Center,
} from "@chakra-ui/react";

import { FiEye, FiEyeOff } from "react-icons/fi";
import { useAuth } from "../context/AuthContext";
import { LoadingOverlay } from "../componentes/ui/loading";
import { useNavigate } from "react-router-dom";
import { toaster } from "../componentes/ui/toaster";

export default function Login() {
  const navigate = useNavigate();
  const { login, loading } = useAuth();

  const [username, setusername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  async function handleLogin() {
    if (!username || !password) {
      toaster.error({
        title: "Campos obrigatórios",
        description: "Preencha username e senha",
        duration: 3000,
      });
      return;
    }

    const response = await login(username, password);

    if(!response.success) {
        toaster.error({
        title: "Erro",
        description: response.message || "Erro inesperado",
        duration: 4000,
      });
      return;
    }

    toaster.success({
        title: "Sucesso",
        description: "Login realizado com sucesso 🚀",
        duration: 3000,
      });

    navigate("/painel");

  }

  return (
    <Center>
        <Box
      w="100%"
      maxW="400px"
      bg="white"
      p={8}
      borderRadius="2xl"
      boxShadow="lg"
    >
      <LoadingOverlay isOpen={loading}/>
      <VStack gap={5} align="stretch">
        <Heading size="lg" textAlign="center">
          Entrar
        </Heading>

        <Text fontSize="sm" color="gray.500" textAlign="center">
          Acesse sua conta
        </Text>

        {/* username */}
        <Field.Root>
          <Field.Label>Usuário</Field.Label>
          <Input
            placeholder=""
            value={username}
            onChange={(e) => setusername(e.target.value)}
            _focusVisible={{ borderColor: "blue.500" }}
          />
        </Field.Root>

        {/* SENHA */}
        <Field.Root>
          <Field.Label>Senha</Field.Label>

          <Box position="relative">
            <Input
              type={showPassword ? "text" : "password"}
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              pr="40px"
              _focusVisible={{ borderColor: "blue.500" }}
            />

            <Box
              position="absolute"
              right="10px"
              top="50%"
              transform="translateY(-50%)"
              cursor="pointer"
              color="gray.500"
              onClick={() => setShowPassword((prev) => !prev)}
            >
              {showPassword ? <FiEyeOff /> : <FiEye />}
            </Box>
          </Box>
        </Field.Root>

        <Button
          colorPalette="blue"
          size="lg"
          w="100%"
          onClick={handleLogin}
          loading={loading}
        >
          Entrar
        </Button>
      </VStack>
    </Box>
    </Center>
  );
}