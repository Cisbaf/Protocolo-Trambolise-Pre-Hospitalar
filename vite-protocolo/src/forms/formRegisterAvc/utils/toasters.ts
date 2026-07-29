import { toaster } from "../../../componentes/ui/toaster";


export function SuccessToaster(id: number) {
    toaster.create({
        title: "Sucesso 🥳🥳🥳!!!",
        description: `ID gerado ${id}`,
        type: "success",
        duration: 10000,
    });
}

export function UpdateSuccessToaster(id: string) {
    toaster.create({
        title: "Registro atualizado ✅",
        description: `As alterações do protocolo ${id} foram salvas.`,
        type: "success",
        duration: 6000,
    });
}

export function DeleteSuccessToaster(numeroOcorrencia: string) {
    toaster.create({
        title: "Registro excluído",
        description: `A ocorrência ${numeroOcorrencia} foi removida definitivamente.`,
        type: "success",
        duration: 6000,
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