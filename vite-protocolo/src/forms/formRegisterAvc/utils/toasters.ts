import { toaster } from "../../../componentes/ui/toaster";


export function SuccessToaster(id: number) {
    toaster.create({
        title: "Sucesso 🥳🥳🥳!!!",
        description: `ID gerado ${id}`,
        type: "success",
        duration: 10000,
    });
}

export function ErrorToaster(message: string) {
    toaster.create({
        title: "Erro ao enviar formulário",
        description: message,
        type: "error",
        duration: 10000,
    });
}

export function FormErrorToaster() {
    toaster.create({
        title: "Erro ao enviar formulário",
        description: "Verifique todos os campos do formulário!",
        type: "error",
        duration: 10000,
    });
}