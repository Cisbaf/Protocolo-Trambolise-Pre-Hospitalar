import React from "react";
import { useAvcForm } from "../hooks/useAvcForm";
import { LoadingOverlay } from "../componentes/ui/loading";
import { usePost } from "../hooks/usePost";
import { usePut } from "../hooks/usePut";
import { BaseURL } from "../settings";
import { fakeAvcFormInitialValues } from "../forms/formRegisterAvc/utils/fakeInitialValues";
import { ErrorToaster, FormErrorToaster, SuccessToaster, UpdateSuccessToaster } from "../forms/formRegisterAvc/utils/toasters";
import { defaultAvcFormInitialValues } from "../forms/formRegisterAvc/utils/defaultInitialValues";
import type { AvcFormValues } from "../forms/formRegisterAvc/schemas/AvcFormSchema";

interface AvcFormType {
  form: ReturnType<typeof useAvcForm>
}

interface AvcFormProps {
  children: any;
  fakeInitialValues?: boolean;
  /** Abre o formulário já preenchido, para edição de um registro existente. */
  initialValues?: Partial<AvcFormValues>;
  /** Quando informado, o submit vira PUT /protocolo/{id} em vez de POST. */
  protocoloId?: string;
  /** Chamado depois de salvar com sucesso. */
  onSaved?: (data: any) => void;
}

const AvcFormContext = React.createContext<AvcFormType | null>(null);

export function AvcFormProvider(props: AvcFormProps) {
    const isEdicao = !!props.protocoloId;

    const valoresIniciais =
      props.initialValues ??
      (props.fakeInitialValues ? fakeAvcFormInitialValues : defaultAvcFormInitialValues);

    const avcForm = useAvcForm(valoresIniciais);

    const { post, loading: enviando } = usePost({
      url: `${BaseURL}/protocolo`,
      onSuccess(data) {
        SuccessToaster(data.id);
        avcForm.reset();
        props.onSaved?.(data);
      },
      onError: (error)=>ErrorToaster(error.message),
    })

    const { put, loading: salvando } = usePut({
      url: `${BaseURL}/protocolo/${props.protocoloId}`,
      onSuccess(data) {
        UpdateSuccessToaster(data.id);
        // mantém na tela o que foi gravado, mas zera o estado "sujo" do formulário
        avcForm.reset(avcForm.getValues());
        props.onSaved?.(data);
      },
      onError: (error)=>ErrorToaster(error.message),
    })

    const cleanForm = (data: AvcFormValues) => {
      const { naoSoubeInformarLKW, ...rest } = data.LinhaDoTempoSection;
      const payload = {
        ...data,
        LinhaDoTempoSection: rest,
      };

      if (isEdicao) put(payload);
      else post(payload);
    }

    return (
       <AvcFormContext.Provider value={{form: avcForm}}>
         <form onSubmit={avcForm.handleSubmit(cleanForm, FormErrorToaster)}>
            <LoadingOverlay isOpen={enviando || salvando} />
            {props.children}
        </form>
       </AvcFormContext.Provider>
    )
}

export function useAvcFormContext() {
  const context = React.useContext(AvcFormContext)

  if (!context) {
    throw new Error(
      "useAvcForm must be used inside AvcFormProvider"
    )
  }

  return context
}
